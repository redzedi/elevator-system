package impl1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ElevatorSystem implements ElevatorSystemIntf {
	
	private int numOfFloors;
	private ExecutorService svc;
	private List<Elevator> es;
	
	private volatile boolean isExited;
	
	private ReentrantReadWriteLock[] eLocks;
	private Condition[] emptyConds;
	private Condition[] reachedFloorConds;
	
	public ElevatorSystem(int numOfFloors, int numOfElevators , int elevatorCapacity) {
		
		this.svc = Executors.newCachedThreadPool();
		this.numOfFloors = numOfFloors;
		es = new ArrayList<>();
		
		eLocks = new ReentrantReadWriteLock[numOfElevators];
		emptyConds = new Condition[numOfElevators];
		reachedFloorConds = new Condition[numOfElevators];
		
		for (int i = 0; i < numOfElevators ; i++) {
			Elevator curr = new Elevator("e"+i, elevatorCapacity);
			eLocks[i] = new ReentrantReadWriteLock();
			emptyConds[i] = eLocks[i].writeLock().newCondition();
			reachedFloorConds[i] = eLocks[i].writeLock().newCondition();
			
			final int ii = i;
			this.svc.submit(()->{
				
				eLocks[ii].writeLock().lock();
				while(!isExited) {
					try {
						emptyConds[ii].await(1, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(!curr.getReqs().isEmpty()) {
						curr.processRequest();
						reachedFloorConds[ii].signalAll();
					}
					
					
					
				}
				eLocks[ii].writeLock().unlock();
				
			});
			this.es.add(curr);
		}
		
	}
	
	
	
		
	@Override
	public  Future<String> enterElevator( int currentFloor, int destinationFloor , int count) throws ElevatorOverCapacityException, InterruptedException {
		int minDistElevatorIdx = -1; 
		
		CompletableFuture<String> completionWatcher = null;
		  // it has to wait for the elevator to arrive 
		  
		  try {
			int minDistance = Integer.MAX_VALUE;
			minDistElevatorIdx = -1;
			minDistElevatorIdx = findMatchingElevator(currentFloor,  minDistance);
			eLocks[minDistElevatorIdx].writeLock().lock();
			es.get(minDistElevatorIdx).addRequest(currentFloor);
			emptyConds[minDistElevatorIdx].signal();
			try {
				reachedFloorConds[minDistElevatorIdx].await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (es.get(minDistElevatorIdx).getCurrentFloor() == currentFloor) {
				es.get(minDistElevatorIdx).admitPassenger(currentFloor, destinationFloor, count);
				final int minIdx = minDistElevatorIdx;
			completionWatcher = 	CompletableFuture.supplyAsync(()->{
					try {
						eLocks[minIdx].writeLock().lock();
						try {
							reachedFloorConds[minIdx].await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return es.get(minIdx).getElevatorId();
					}finally {
						
						eLocks[minIdx].writeLock().unlock();
					}
					
					
				});
				emptyConds[minDistElevatorIdx].signal();
			} else {
				throw new RuntimeException("requested elevator didn't reach the floor");
			}
		} finally {
			// TODO: handle finally clause
			if(minDistElevatorIdx != -1)
			eLocks[minDistElevatorIdx].writeLock().unlock();
		}
		
		
		
		return completionWatcher;
		//return CompletableFuture.completedFuture(es.get(minDistElevatorIdx).getElevatorId());
	}




	private int findMatchingElevator(int currentFloor,  int minDistance) {
		int minDistElevatorIdx = -1;
		for (int i = 0; i < es.size(); i++) {
			eLocks[i].readLock().lock();
			int currDist = Math.abs(currentFloor - es.get(i).getCurrentFloor());
			eLocks[i].readLock().unlock();
			if (currDist < minDistance) {
				minDistance = currDist;
				minDistElevatorIdx = i;
			}
		}
		return minDistElevatorIdx;
	}
	
	
	@Override
	public int getCurrentFloorOfElevator(String eId) {
		
		Optional<Elevator> optE = this.es.stream().filter(e->e.getElevatorId().equals(eId)).findFirst();
		
		return optE.orElseThrow().getCurrentFloor();
		
	}
	
	@Override
	public synchronized void shutdownElevatorSystem() throws InterruptedException {
		
		if(isExited)
			return;
		this.isExited = true;
		
		for(Elevator e:es) {
			e.exit();
		}
		
		this.svc.awaitTermination(2, TimeUnit.SECONDS);
		
		
		
	}




	

}
