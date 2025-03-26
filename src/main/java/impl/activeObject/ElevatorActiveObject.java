package impl.activeObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ElevatorActiveObject {
	
	private ElevatorAO e;
	
	private ThreadPoolExecutor taskExecutor;
	
	private class ElevatorCommand implements Callable<ElevatorInternalResponse>{
		
		private int frmFloor;
		private int toFloor;
		private int cnt;
		
		

		public ElevatorCommand(int frmFloor, int toFloor, int cnt) {
			super();
			this.frmFloor = frmFloor;
			this.toFloor = toFloor;
			this.cnt = cnt;
		}



		@Override
		public ElevatorInternalResponse call() {
			// TODO Auto-generated method stub
			try {
				if(e.getCurrentFloor() != frmFloor) {
					e.gotoFloor(e.getCurrentFloor(), frmFloor);
				}
				e.admitPassenger(frmFloor, toFloor, cnt);
				return new ElevatorInternalResponse( ElevatorActiveObject.this, frmFloor, e.gotoFloor(frmFloor, toFloor));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				throw new RuntimeException(e);
			} 
			
		}
		
	}

	public ElevatorActiveObject(String id , int capacity) {
		super();
		this.e = new ElevatorAO(id,capacity);
		taskExecutor = new ThreadPoolExecutor(1, 1, 100, TimeUnit.MILLISECONDS	, new ArrayBlockingQueue<Runnable>(10, true));
		
	}
	
	public Future<ElevatorInternalResponse> requestElevator(int frmFloor, int toFloor ,  int cnt) {
		
		return taskExecutor.submit(new ElevatorCommand(frmFloor , toFloor, cnt));
		
	}
	
	public int getCurrentFloor() {
		return e.getCurrentFloor();
	}
	
	public String getElevatorId() {
		return e.getElevatorId();
	}
	
	

}
