package core;

public class MemoryAnalyzer implements Runnable {

	private long totalMemoryUsed;
	private boolean stops;

	public MemoryAnalyzer(){
		this.totalMemoryUsed = 0L;
		this.stops = false;
	}
	
	@Override
	public void run() {
		
		while(!stops){
			
			Runtime runtime = Runtime.getRuntime();
			long totalMemory = runtime.totalMemory();
			long freeMemory = runtime.freeMemory();
			long usedMemory = totalMemory - freeMemory; 
			
			if(usedMemory > totalMemoryUsed){
				totalMemoryUsed = usedMemory; 
			}
			
		}
		
	}
	
	public void stop(){
		this.stops = true;
		Thread.currentThread().interrupt();
	}
	
	public long maxUsedMemory(){
		return totalMemoryUsed;
	}

}
