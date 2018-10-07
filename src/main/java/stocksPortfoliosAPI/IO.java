package stocksPortfoliosAPI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import stocksPortfoliosSrc.Customer;

/**
 * The interface IO.
 * Define the IO (input/output) capabilities which IO object should have. 
 * @author Michael Shachar. */
public interface IO {
	public void saveStocks(File file, Map<String, Double> stocks);

	/**
     * Reading stocks data from file.
 	 * @param flag - whether to read the first line in 
 	 * 	special way (for build the initial database). */
	public Map<String, Double> readStocks(String filename,
			boolean flag);
	
 	 public void saveCustomers(File customersFile,
    		ArrayList<Profile> customers);
    
	public ArrayList<Customer> loadCustomers(File file) 
    		throws IOException;	
	
	public void saveID(File file, String id);

	/**
     * Reading id from ids file by index.
 	 * @param idIndex - index of the id of customer
 	 *  from the ids file (all of the ids in the DB
 	 *  of the customers). */
    public String readID(String idsFile, int idIndex);

    public ArrayList<String> readPaths(String pathsFile);
    
    public ArrayList<Integer> readIDsIndexes(String idsFile);
}