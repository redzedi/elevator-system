package impl1;

import java.util.List;
import java.util.concurrent.Future;

public interface ElevatorSystemIntf {

	Future<String> enterElevator(int currentFloor, int destinationFloor, int count)
			throws ElevatorOverCapacityException, InterruptedException;

	int getCurrentFloorOfElevator(String eId);

	void shutdownElevatorSystem() throws InterruptedException;
	
	//List<Elevator> getElevators();

}