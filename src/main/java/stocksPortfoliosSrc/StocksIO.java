package stocksPortfoliosSrc;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import stocksPortfoliosAPI.IO;
import stocksPortfoliosAPI.Profile;

/**
 * The class StocksIO which implements the IO interface.
 * Responsible for all handling of IO requests.
 * @author Michael Shachar. */
public class StocksIO implements IO {
	
	public void saveStocks(File file, Map<String, Double> stocks) {
        BufferedWriter writer = null;
        // create output stream with writer wrappers
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (Map.Entry<String, Double> entry : 
    			stocks.entrySet()) {
            	writer.write(entry.getKey() + " " +
            			String.valueOf(entry.getValue()) + '\n');
            }
        } catch (IOException e) {
            System.err.println("Failed saving to file" + file.getName());
            e.printStackTrace(System.err);
        } finally {
            if (writer != null) {
	            try {
	            	writer.close();
	            } catch (IOException e) {
	                System.err.println("Failed closing writer");
	            }
            }
        }
    }
	
	/**
 	 * @param flag - whether to read the first line in 
 	 * special way (for build the initial database). */
	public Map<String, Double> readStocks
				(String path, boolean flag) {
		HashMap<String, Double> stocks = new HashMap<String, Double>();
		InputStream is = null;
		LineNumberReader lineNumberReader = null;
        try {
        	is = new FileInputStream(path);
        	lineNumberReader = new LineNumberReader(
                    new InputStreamReader(is));
            String line = lineNumberReader.readLine();
            if (flag) {
            	this.extractDataFirstInput(line, "$", stocks);
            	line = lineNumberReader.readLine();
            }
            while (line != null) {
            	this.extractData(line, stocks);
            	line = lineNumberReader.readLine();
            }
	    } catch (IOException e) {
            System.err.println("Failed reading file: "
                    + path);
            stocks = null;
	    } finally {
            if (lineNumberReader != null) {
            	try {
                    lineNumberReader.close();
                } catch (IOException e) {
                	System.err.println("Failed closing file: "
                       + path);
                }
            }
        }
		return stocks;
	}
	
    public void saveCustomers(File customersFile, 
    		ArrayList<Profile> customers) {
    	ObjectOutputStream writer = null;
        try {
        	writer = new ObjectOutputStream(
        					new FileOutputStream(customersFile));
        	writer.writeObject(customers);
        } catch (IOException e) {
            System.err.println("Failed saving to file " +
            		customersFile);
            e.printStackTrace(System.err);
        } finally {
            if (writer != null) {
	            try {
	            	writer.close(); 
	            } catch (IOException e) {
	            	System.err.println("Failed closing writer");
	            }
            }
        }
    }
    
    public ArrayList<Customer> loadCustomers(File file) {
        ObjectInputStream reader = null;
        ArrayList<Customer> customers = null;
        try {
        	reader = new ObjectInputStream(
                    new FileInputStream(file));
        	while(true) {
    	        try {    	            
    	        	customers = new ArrayList<Customer>
    	        		((ArrayList<Customer>) 
    	        				reader.readObject());	
    	        } catch (EOFException e) {
    	        	break;
    	        } catch (FileNotFoundException e) {
    	        	// Can't find file to open
    	            System.err.println("Unable to find file: " 
    	            					+ file);
    	        } catch (ClassNotFoundException e) { 
    	        	// The class in the stream is unknown to the JVM
    				System.err.println("Unable to find class "
    						+ "for object in file: " + file);
    	        } catch (IOException e) { // Some other problem
    	            System.err.println("Failed read from file:" 
    	            					+ file);
    	            e.printStackTrace();
    	        }
        	}
        } catch (IOException e) {
        		System.err.println("Failed read from file:"
        							+ file);
	            e.printStackTrace();
        } finally {
            if (reader != null) {	
	            try {
	            	reader.close();
	            } catch (IOException e) {
	                System.err.println("Failed closing file: " + file);
	            }
            }
        }
        return customers;
    }
    
   public void saveID(File idsFile, String id) {
        BufferedWriter writer = null;
        // create output stream with writer wrappers
        try {
            writer = new BufferedWriter(new FileWriter(idsFile, true));
            writer.write(id);
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Failed saving to file" + 
            		idsFile.getName());
            e.printStackTrace(System.err);
        } finally {
            if (writer != null) {
	            try {
	            	writer.close();
	            } catch (IOException e) {
	                System.err.println("Failed closing writer");
	            }
            }
        }
    }
    
   /**
	* @param idIndex - index of the id of customer.
	*  from the ids file (all of the ids in the DB
	*  of the customers). */
   public String readID(String idsFile, int idIndex) {
	   InputStream is = null;
	   LineNumberReader lineNumberReader = null;
	   String id = null;
       try {
	       is = new FileInputStream(idsFile);
	       lineNumberReader = new LineNumberReader(
	       new InputStreamReader(is));
	       for (int i = 0; i <= idIndex; i++) {
	    	   id = lineNumberReader.readLine();
	       }
	   } catch (IOException e) {
		   System.err.println("Failed reading file: "+ idsFile);
		   id = null;
	   } finally {
		   if (lineNumberReader != null) {
			   try {
                   lineNumberReader.close();
               } catch (IOException e) {
            	   System.err.println("Failed closing file: " 
            			   + idsFile);
               }
           }
       }
       return id;
	}
   
   public ArrayList<String> readPaths(String pathsFile) {
	   return this.readHelper(pathsFile);
   }
   
   public ArrayList<Integer> readIDsIndexes(String idsFile) {
	   ArrayList<Integer> ids = new ArrayList<Integer>();
	   ArrayList<String> lines = this.readHelper(idsFile);
	   if (lines != null) {
		   int id = -1;
		   for (String line: lines) {
			   id = Integer.parseInt(line);
			   ids.add(id);
		   }
	   }
	   return ids;
   }
   
   /**
    * A helper function which extract data from the first line
    * of the file (special syntax) which we build by it the
    * initial stocks database. */
   private void extractDataFirstInput(String data, String ch,
		   Map<String, Double> stocks) {
	   String[] parts = data.split(" ");
       double value = Double.parseDouble(parts[parts.length - 1]);
       for (String str : parts) {
    	   if (str.equals(ch)) {
    		   break;
    	   }
    	   stocks.put(str, value);
       } 
	}
    
    private void extractData(String data, Map<String, Double> stocks) {
    	String[] parts = data.split(" ");
        String name = parts[0];
    	double value = Double.parseDouble(parts[1]);
    	stocks.put(name, value); 
	}
    
    /**
     * A helper function for reading from files, which
     * preventing duplicate code. */
    private ArrayList<String> readHelper(String path) {
    	ArrayList<String> lines = new ArrayList<String>();
    	InputStream is = null;
		LineNumberReader lineNumberReader = null;
        try {
        	is = new FileInputStream(path);
        	lineNumberReader = new LineNumberReader(
                    new InputStreamReader(is));
            String line = lineNumberReader.readLine();
            while (line != null) {
            	lines.add(line);
            	line = lineNumberReader.readLine();
            }
	    } catch (IOException e) {
            System.err.println("Failed reading file: "
                    + path);
            lines = null;
	    } finally {
            if (lineNumberReader != null) {
            	try {
                    lineNumberReader.close();
                } catch (IOException e) {
                	System.err.println("Failed closing file: "
                       + path);
                }
            }
        }
		return lines;
    }
}