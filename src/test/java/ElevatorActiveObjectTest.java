import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

import impl.activeObject.ElevatorActiveObject;
import impl.activeObject.ElevatorResponse;
import impl.activeObject.ElevatorSystem;

public class ElevatorActiveObjectTest {
	
	
	@Test
	public void testElevatorActiveObject() {
		
		ElevatorActiveObject eao = new ElevatorActiveObject( "1", 1 );
		Future<?> f = eao.requestElevator(4, 5, 1);
		
		try {
			Object res = f.get();
			System.out.println(res);
			System.out.println(f.isDone());
			System.out.println(f.isCancelled());
			
			assertEquals(5, eao.getCurrentFloor());
			
			
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		
	}
	
	@Test
	public void testElevatorSystem() {
		
		ElevatorSystem eSys = new ElevatorSystem(2, 1);
		Future<ElevatorResponse> req1 = eSys.requestElevator(4, 5, 1);
		Future<ElevatorResponse> req2 = eSys.requestElevator(3, 1, 1);
		
		try {
			assertEquals("0" , req1.get().elevatorId());
			assertEquals("0" , req2.get().elevatorId());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testElevatorSystem2() {
		
		ElevatorSystem eSys = new ElevatorSystem(2, 1);
		Future<ElevatorResponse> req1 = eSys.requestElevator(4, 5, 1);
		Future<ElevatorResponse> req2 = eSys.requestElevator(1, 5, 1);
		
		try {
			assertEquals("0" , req1.get().elevatorId());
			assertEquals("1" , req2.get().elevatorId());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
	}

}
