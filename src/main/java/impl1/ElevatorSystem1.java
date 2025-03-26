package impl1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Predicate;

public class ElevatorSystem1 implements ElevatorSystemIntf{
	
	private int numOfFloors;
	private List<ElevatorTask> ets;
	private List<Thread> eThreads;
	private volatile boolean isExited;
	
	
	private class ElevatorTask implements Runnable{
		
		private Elevator e;

		public ElevatorTask(Elevator e) {
			super();
			this.e = e;
		}
		
		public String getElevatorId() {
			return e.getElevatorId();
		}
		
		public synchronized int getCurrentFloor() {
			return e.getCurrentFloor();
		}
		
		public synchronized void callElevatorAndAdmitPassenger(int fromFloorNum, int toFloorNum , int count) throws ElevatorOverCapacityException, InterruptedException {
			e.addRequest(fromFloorNum);
			notifyAll();
			//block until elevator reaches the floor
		     System.out.println("added request going to wait ");
			waitUntilCondition((o)->e.getCurrentFloor()!=fromFloorNum);
			System.out.println("elevator arrived at the floor "+e.getElevatorId());
			
			e.admitPassenger(fromFloorNum, toFloorNum, count);
			notifyAll();
			System.out.println("passengers entered waiting to reach the destination floor  "+e.getElevatorId());

			waitUntilCondition((o)->e.getCurrentFloor()!=toFloorNum);
			
			
		
		}
		
		

		private void waitUntilCondition(Predicate<Void> p) {
			do {
				try {
					wait(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}while(p.test(null));
		}
		
		


		@Override
		public void run() {
			synchronized(this) {
				
				while(!isExited) {
					
					e.processRequest();
					notifyAll();
				    
					try {
						wait();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				
			}
		}
	}
	
	
	public ElevatorSystem1(int numOfFloors, int numOfElevators , int elevatorCapacity) {
		
		this.numOfFloors = numOfFloors;
		
		ets = new ArrayList<>();
		eThreads = new ArrayList<>();
		
		for (int i = 0; i < numOfElevators; i++) {
			ElevatorTask currTask = new ElevatorTask(new Elevator(i+"", elevatorCapacity));
			eThreads.add(new Thread(currTask));
			ets.add(currTask);
		}
		
		for(Thread currT:eThreads) {
			currT.start();
		}
		
		
	}
	

	@Override
	public Future<String> enterElevator(int currentFloor, int destinationFloor, int count)
			throws ElevatorOverCapacityException, InterruptedException {

		//find closest to the current El
		
		int closestElevatorIdx = -1;
		int minDistance = Integer.MAX_VALUE;
		
		for (int i = 0; i < ets.size(); i++) {
			int currD = Math.abs(currentFloor-ets.get(i).getCurrentFloor());
			if(currD<minDistance) {
				closestElevatorIdx = i;
				minDistance = currD;
			}
		}
		
	   //request the elevator to come to the current Floor	
		ets.get(closestElevatorIdx).callElevatorAndAdmitPassenger(currentFloor, destinationFloor, count);
		
		return CompletableFuture.completedFuture(ets.get(closestElevatorIdx).getElevatorId());
	}

	@Override
	public int getCurrentFloorOfElevator(String eId) {
		
		return ets.stream().filter((et)-> et.getElevatorId().equals(eId)).findFirst().map(et->et.getCurrentFloor()).orElseThrow();
		
	}

	@Override
	public void shutdownElevatorSystem() throws InterruptedException {

		synchronized(this) {
			isExited = true;
		}
		
		for(Thread currT:eThreads) {
			currT.interrupt();
			currT.join();
		}
		
		
		
	}

	

}
