import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

import impl1.ElevatorOverCapacityException;
import impl1.ElevatorSystem;
import impl1.ElevatorSystem1;
import impl1.ElevatorSystemIntf;

public class ElevatorSystemTest {
	
	/**
	 * assumes elevators are standing in floors when request made .
	 * No dynamic request addition to elevators in flight
	 * @throws InterruptedException 
	 * @throws ElevatorOverCapacityException 
	 * @throws ExecutionException 
	 */
	
	@Test
	public void testElevatorRequest() throws ElevatorOverCapacityException, InterruptedException, ExecutionException {
		ElevatorSystemIntf eSys = null;
		try {
			eSys = new ElevatorSystem(5, 2, 1);
			Future<String> elevatorIdFut = eSys.enterElevator(1, 4, 1);
			assertEquals("e0", elevatorIdFut.get());
			//Thread.sleep(500);
			assertEquals(4, eSys.getCurrentFloorOfElevator(elevatorIdFut.get()));
			assertEquals(0, eSys.getCurrentFloorOfElevator("e1"));
		
			Future<String> elevatorIdFut1 = eSys.enterElevator(3, 2, 1);
			assertEquals("e0", elevatorIdFut1.get());
			assertEquals(2, eSys.getCurrentFloorOfElevator(elevatorIdFut1.get()));
			assertEquals(0, eSys.getCurrentFloorOfElevator("e1"));
			
			Future<String> elevatorIdFut2 = eSys.enterElevator(0, 4, 1);
			assertEquals("e1", elevatorIdFut2.get());
			
			
		} finally {
			if(eSys != null) {
				eSys.shutdownElevatorSystem();
			}
		}
		
	}
	
	
	@Test
	public void testElevatorRequest1() throws ElevatorOverCapacityException, InterruptedException, ExecutionException {
		ElevatorSystemIntf eSys = null;
		try {
			eSys = new ElevatorSystem1(5, 2, 1);
			Future<String> elevatorIdFut = eSys.enterElevator(1, 4, 1);
			assertEquals("e0", elevatorIdFut.get());
			//Thread.sleep(500);
			assertEquals(4, eSys.getCurrentFloorOfElevator(elevatorIdFut.get()));
			assertEquals(0, eSys.getCurrentFloorOfElevator("e1"));
		
			Future<String> elevatorIdFut1 = eSys.enterElevator(3, 2, 1);
			assertEquals("e0", elevatorIdFut1.get());
			assertEquals(2, eSys.getCurrentFloorOfElevator(elevatorIdFut1.get()));
			assertEquals(0, eSys.getCurrentFloorOfElevator("e1"));
			
			Future<String> elevatorIdFut2 = eSys.enterElevator(0, 4, 1);
			assertEquals("e1", elevatorIdFut2.get());
			
			
		} finally {
			if(eSys != null) {
				eSys.shutdownElevatorSystem();
			}
		}
		
	}
	
	

}
