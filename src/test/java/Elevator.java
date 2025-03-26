import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class Elevator implements Runnable{
	
	private String elevatorId;
	private int currentFloor;
	private LinkedBlockingQueue<Integer> reqs;
	private int capacity;
	private int currentNumOfPpl;
	
	volatile private boolean isExited;
	
	private Map<Integer,Integer> floorToPassengerCount;
	
	
	
	
	
	
	
	public Elevator(String elevatorId, int capacity) {
		super();
		this.elevatorId = elevatorId;
		this.capacity = capacity;
		reqs = new LinkedBlockingQueue<Integer>();
		floorToPassengerCount = new HashMap<>();
	}

	public void addRequest(int floor) throws InterruptedException {
		
		reqs.put(floor);
		
	}
	
	public void admitPassenger(int currentFloor, int destinationFloor , int count) throws ElevatorOverCapacityException, InterruptedException {
		
		if(currentFloor != this.currentFloor) {
			throw new RuntimeException("Elevator can't admit passenger from floor it is not currently located in ");
		}
		
		if(this.currentNumOfPpl+count > this.capacity) {
			throw new ElevatorOverCapacityException(String.format("Elevator id - %s currently has - %d passengers , total capacity - %d can't admit - %d more", elevatorId, currentNumOfPpl, capacity, count));
		}
		
		if( currentFloor == destinationFloor) {
			throw new RuntimeException("Elevator can't be requested for current floor");
		}
		
		reqs.put(destinationFloor);
		
		floorToPassengerCount.put(destinationFloor, floorToPassengerCount.getOrDefault(destinationFloor, 0)+count);
		
	}
	
	public void exit() {
		isExited=true;
		
	}

	@Override
	public void run() {
		while(!isExited) {
			try {
				//reached floor
				currentFloor = reqs.poll(1, TimeUnit.SECONDS);
				//door opens passengers exit
				floorToPassengerCount.remove(currentFloor);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public String getElevatorId() {
		return elevatorId;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public LinkedBlockingQueue<Integer> getReqs() {
		return reqs;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getCurrentNumOfPpl() {
		return currentNumOfPpl;
	}

	public boolean isExited() {
		return isExited;
	}
	
	
	
	
	
}