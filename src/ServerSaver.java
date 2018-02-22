
public class ServerSaver extends Thread {
	
	private final int TIME_TO_SLEEP;
	FileKeeper keeper;
	ClientTable clientTable;
	
	public ServerSaver(FileKeeper keeper, ClientTable clientTable, int minutesToSleep) {
		TIME_TO_SLEEP = minutesToSleep * 60 * 1000;
		this.keeper = keeper;
		this.clientTable = clientTable;
	}
	
	
	@Override 
	public void run() {
		try {
			while(true) {
				keeper.writeData(clientTable);
				sleep(TIME_TO_SLEEP);
			}
		}
		catch(InterruptedException e) {
			//end the thread.
		}
	}
}
