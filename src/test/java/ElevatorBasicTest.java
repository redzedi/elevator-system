import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import impl1.Elevator;
import impl1.ElevatorOverCapacityException;

public class ElevatorBasicTest {
	
	@Test
	public void testSimpleElevatorRequest() throws InterruptedException {
		
		Elevator e = new Elevator("e1", 1);
		
		ExecutorService svc = Executors.newCachedThreadPool();
		
		try {
			svc.submit(e);
			e.addRequest(1);
			Thread.sleep(200);
			assertEquals(1, e.getCurrentFloor());
			e.addRequest(5);
			Thread.sleep(200);
			assertEquals(5, e.getCurrentFloor());
		} finally {
			e.exit();
			
			svc.awaitTermination(2, TimeUnit.SECONDS);
		}
		
	}
	
	
	@Test
	public void testSimpleElevatorRequestAndAdmitPassendger() throws InterruptedException, ElevatorOverCapacityException {
		
		Elevator e = new Elevator("e1", 1);
		
		ExecutorService svc = Executors.newCachedThreadPool();
		
		try {
			//passenger1 requests elevator 
			svc.submit(e);
			e.addRequest(1);
			Thread.sleep(200);
			assertEquals(1, e.getCurrentFloor());
			//passenger1 enters elevator
			e.admitPassenger(1, 2, 1);
			
			Thread.sleep(200);
			
			//passenger1 has reached destination
			assertEquals(2, e.getCurrentFloor());
			
			//passenger2 enters elevator
			e.admitPassenger(2, 5, 1);
			
			//passenger2 reaches destination
			Thread.sleep(200);
			assertEquals(5, e.getCurrentFloor());
			
			
			
			
		} finally {
			e.exit();
			
			svc.awaitTermination(2, TimeUnit.SECONDS);
		}
		
	}
	
	
	

}
