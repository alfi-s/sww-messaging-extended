import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A class which handles inputting and outputting clientTable data.
 * @author alfis
 *
 */
public class FileKeeper {
	
	/**
	 * Writes the ClientTable data to the file data.ctable
	 * @param clientTable The client table to save
	 */
	public void writeData(ClientTable clientTable) {
		FileWriter fileStream = null;
		PrintWriter printer = null;
		
		try {
			fileStream = new FileWriter("data.ctable");
			printer = new PrintWriter(fileStream);
			
			printer.print(clientTable.saveTable());
			
			fileStream.close();
			printer.close();
		}
		catch(IOException e) {
			Report.error("Error: There was a problem trying to save account data " + e.getMessage());
		}
	}
	
	/**
	 * Reads a file and loads the client table data.
	 * @param filename The name of the file to read. 
	 * @return A client table with the information stored in the file.
	 */
	public ClientTable readData(String filename){
		ClientTable clientTable = null;
		FileReader fileStream = null;
		BufferedReader reader = null;
		
		try {
			fileStream = new FileReader(filename);
			reader = new BufferedReader(fileStream);
			
			StringBuilder data = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				data.append(line);
				data.append("\r\n");
			}
			
			clientTable = new ClientTable();
			clientTable.loadTable(data.toString());
			
			fileStream.close();
			reader.close();
		} 
		catch(FileNotFoundException e) {
			return new ClientTable();
		}
		catch(IOException e) {
			Report.error("I/O Exception " + e.getMessage());
		}
		
		return clientTable;
	}
}
