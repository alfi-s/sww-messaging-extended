import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileKeeper {
	
	public void writeData(ClientTable clientTable) {
		FileOutputStream fileStream = null;
		ObjectOutputStream outputStream = null;
		
		try {
			fileStream = new FileOutputStream("table.ser");
			outputStream = new ObjectOutputStream(fileStream);
			
			outputStream.writeObject(clientTable);
			
			fileStream.close();
			outputStream.close();
		}
		catch(IOException e) {
			Report.error("Error: There was a problem trying to save account data " + e.getMessage());
		}
	}
	
	public ClientTable readData(String filename){
		ClientTable clientTable = null;
		FileInputStream fileStream = null;
		ObjectInputStream inputStream = null;
		
		try {
			fileStream = new FileInputStream(filename);
			inputStream = new ObjectInputStream(fileStream);
			
			clientTable = (ClientTable) inputStream.readObject();
			
			fileStream.close();
			inputStream.close();
		} 
		catch(FileNotFoundException e) {
			return new ClientTable();
		}
		catch(IOException e) {
			Report.error("I/O Exception " + e.getMessage());
		}
		catch(ClassNotFoundException e) {
			Report.errorAndGiveUp("Class not found: " + e.getMessage());
		}
		
		return clientTable;
	}
}
