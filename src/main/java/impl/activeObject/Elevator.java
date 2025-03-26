package impl.activeObject;

import java.util.HashMap;
import java.util.Map;

import impl1.ElevatorOverCapacityException;

public class Elevator {
	
	private String elevatorId;
	private int currentFloor;
	private int capacity;
	private int currentNumOfPpl;
	
	volatile private boolean isExited;
	
	private Map<Integer,Integer> floorToPassengerCount;
	
	public Elevator(String elevatorId, int capacity) {
		super();
		this.elevatorId = elevatorId;
		this.capacity = capacity;
		floorToPassengerCount = new HashMap<>();
	}

	
	
	public void admitPassenger(int currentFloor, int destinationFloor , int count) throws ElevatorOverCapacityException, InterruptedException {
		
		if(currentFloor != this.currentFloor) {
			throw new RuntimeException("Elevator can't admit passenger from floor it is not currently located in . Requested from -- "+currentFloor+" current floor of the elevator "+this.currentFloor);
		}
		
		if(this.currentNumOfPpl+count > this.capacity) {
			throw new ElevatorOverCapacityException(String.format("Elevator id - %s currently has - %d passengers , total capacity - %d can't admit - %d more", elevatorId, currentNumOfPpl, capacity, count));
		}
		
		if( currentFloor == destinationFloor) {
			throw new RuntimeException("Elevator can't be requested for current floor");
		}
		
		
		floorToPassengerCount.put(destinationFloor, floorToPassengerCount.getOrDefault(destinationFloor, 0)+count);
		
	}
	
	public void exit() {
		isExited=true;
		
	}


	
	public int gotoFloor(int requestFromFloor , int destinationFloor) {
		
		if(requestFromFloor != currentFloor) {
			throw new RuntimeException("gotoFloor can't be invoked from a floor that the elevator is not currently in ");
		}
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.currentFloor = destinationFloor;
		
		floorToPassengerCount.computeIfPresent(destinationFloor, (k,ov)-> 0);
		
		return currentFloor;
		
	}

	public String getElevatorId() {
		return elevatorId;
	}

	public int getCurrentFloor() {
		return currentFloor;
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