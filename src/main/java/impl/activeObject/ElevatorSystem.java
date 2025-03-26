package impl.activeObject;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class ElevatorSystem {
	
	private TreeMap<Integer,LinkedList<ElevatorActiveObject>> currFloorToElevatorMap;
	
	private ReentrantLock lk = new ReentrantLock();
	
	
	public ElevatorSystem(int numOfElevators, int capacity) {
		LinkedList<ElevatorActiveObject> es = new LinkedList<>();
		
		for (int i = 0; i < numOfElevators; i++) {
            es.add(new ElevatorActiveObject("i", capacity));			
		}
		
		currFloorToElevatorMap = new TreeMap<>();
		currFloorToElevatorMap.put(0, es);
	}
	
	
	public Future<ElevatorResponse> requestElevator(int frmFloor, int toFloor ,  int cnt) {
		
		ElevatorActiveObject selectedElev =  currFloorToElevatorMap.floorEntry(frmFloor) != null ?  currFloorToElevatorMap.floorEntry(frmFloor).getValue().getFirst():  currFloorToElevatorMap.ceilingEntry(frmFloor).getValue().getFirst() ;
		
		if(selectedElev == null) {
			throw new RuntimeException("No suitable elevator can be found ");
		}
		
		Future<ElevatorInternalResponse> eResp =  selectedElev.requestElevator(frmFloor, toFloor, cnt);
		
		CompletableFuture<ElevatorInternalResponse> cf1 = CompletableFuture.supplyAsync(()->{
			try {
				return eResp.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} 
		});
		
		return cf1.whenComplete((res,t)-> {
			if(t==null) {
				lk.lock();
				try {
					currFloorToElevatorMap.compute(res.destinationFloor(), (k,ov)-> {
						LinkedList<ElevatorActiveObject> eaos = ov;
						if(eaos == null) {
							eaos = new LinkedList<>();
						}
						eaos.add(res.eao());
						return eaos;
					});
					currFloorToElevatorMap.computeIfPresent(res.requestedFromFloor() , (k, eaos)->{
						eaos.removeIf((eao)-> res.eao().getElevatorId().equals(eao.getElevatorId()));
						return eaos;
					});
					if(currFloorToElevatorMap.get(res.requestedFromFloor()).isEmpty()) {
						currFloorToElevatorMap.remove(res.requestedFromFloor());
					}
				}finally {
					lk.unlock();
				}
				
				
				
				
			}
		})
		.thenApply((esInt)-> new ElevatorResponse(esInt.eao().getElevatorId(),esInt.requestedFromFloor(),esInt.destinationFloor()));
		
		
	}
	
	
	
	

}
