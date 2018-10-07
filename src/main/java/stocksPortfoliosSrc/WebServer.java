package stocksPortfoliosSrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import stocksPortfoliosAPI.IO;
import stocksPortfoliosAPI.PortfolioAPI;
import stocksPortfoliosAPI.Profile;

/**
 * The class Server. 
 * Handling the API requests (=Operations - register, update, value)
 * which accepted from the client.
 * @author Michael Shachar. */
public class WebServer {
	private static final int PORT = 8080;
	private static ArrayList<Profile> customers = null;
	private static final String STOCKS_STATE_FILE =
			"stocks_states.txt";
	private static final String CUSTOMERS_FILE = "customers.txt";
	private static final String STOCKS_AND_VALUES_FILE = 
			"stocks_and_values.txt";
	private static String basePath = "";
	private static final String OUTPUT_PATH = "output/";
	private static final String IDS_FILE_NAME = "ids.txt";
	private static StocksDB stocksDB;
	private static HashMap<String, Integer> stocks;

	public static void main(String[] args) {
		System.out.println("The server is up");
	    if (args.length != 0) {
	        basePath = args[0];
		    HttpServer server = null;
			try {
				server = HttpServer.create(new InetSocketAddress(PORT), 0);
				// declare on handler for the API requests.
				server.createContext("/register", new registerHandler());
			    server.createContext("/update", new updateHandler());
			    server.createContext("/replace", new replaceHandler());
			    server.createContext("/value", new valueHandler());
			    server.setExecutor(null); // creates a default executor
			    server.start(); // start binding for requests.
			    // build the initial stocks database.
				IO stkIO = new StocksIO();
				String path = "";
	        	File outputFolder = new File(OUTPUT_PATH);
	        	if (!outputFolder.exists()) { 
	        		/* there is no an output folder, so let's
	        		 * create it. */
	        		boolean result = outputFolder.mkdirs();
		        	if (!result) {
		        		System.err.println("Failed creating " + 
		        				outputFolder.getAbsolutePath());
						 System.exit(1);
		        	}
	        	}
    			path = OUTPUT_PATH + STOCKS_AND_VALUES_FILE;
        		File dbFile = new File(path);
        		HashMap<String, Double> stocks; 
	        	if (dbFile.exists()) {
        			stocks = new HashMap<String, Double>
        				(stkIO.readStocks(path, false));
        		} else {
        			// the db file doesn't created yet, so let's create it
		        	path = basePath + STOCKS_STATE_FILE;
        			stocks = new HashMap<String, Double>
            			(stkIO.readStocks(path, true));
    				stkIO.saveStocks(dbFile, stocks);		        		
        		}
	        	stocksDB = new StocksDB(stocks);
        		
				/* reading from file the customers which 
					created in the register requests. */	        	
		        path = OUTPUT_PATH + CUSTOMERS_FILE;
		        File customersFile = new File(path);
				 if (customersFile.exists()) {
					 customers = new ArrayList<Profile>
					 	(stkIO.loadCustomers(customersFile));
				 }
	        } catch (IOException e) {
	            System.err.println("Failed to start the server");
				e.printStackTrace();
			}
	    } else {
	    	System.err.println("Please supply the base url");
	    }
	}

	/**
	 * Handling in register API request - register new Customer
	 * which consists of unique id and stocks (names and amounts). */
	private static class registerHandler implements HttpHandler {
		public void handle(HttpExchange he) {
			/* reading the stocks data for registering from
				the register request params. */
			String buffer = helper(he);
			HashMap<String, Double> tempStocksAndAmounts =
					new HashMap<String, Double>();
			tempStocksAndAmounts = new Gson().
					 fromJson(buffer, HashMap.class);
			stocks = new HashMap<String, Integer>
				(doubleToIntStocks(tempStocksAndAmounts));
			// save the stocks data in file.
			IO stkIO = new StocksIO();
			String path = OUTPUT_PATH + CUSTOMERS_FILE;
			File customersFile = new File(path);
			HashMap<String, Integer> stocksAndAmounts = 
					new HashMap<String, Integer>();
			ArrayList<String> stocksNamesAdded = new ArrayList<String>();
			for (HashMap.Entry<String, Integer> entry : 
					stocks.entrySet()) {
				String stockName = entry.getKey();
				int amount = entry.getValue();
				if (!stocksNamesAdded.contains(stockName)) {
					stocksAndAmounts.put(stockName, amount);
					stocksNamesAdded.add(stockName);
				}
			}
			PortfolioAPI portfolio = new Portfolio(stocksAndAmounts);
			Profile customer = new Customer(portfolio);
			if (customers == null) {
				customers = new ArrayList<Profile>();
			}
			customers.add(customer);
			stkIO.saveCustomers(customersFile, customers);
			// send the response to the request to the client.
			String id = customer.getID();
			String idsFilePath = OUTPUT_PATH + IDS_FILE_NAME; 
			File idsFile = new File(idsFilePath);
	        stkIO.saveID(idsFile, id);
			byte[] response = id.getBytes
					(StandardCharsets.UTF_8); // Java 7+ only
			sendResponse(he, response);
		}
	}	
		
	/**
	 * Handling in update API request - update specific stocks
	 * of an exist Customer by id.  */
	private static class updateHandler implements HttpHandler {
		public void handle(HttpExchange he) {
			changePortfolioHelper(he, 1);
		}
	}
	
	/**
	 * The function replaceHandler.
	 * Handling in replace API request - setting a new portfolio
	 * to an exist Customer by id. */
	private static class replaceHandler implements HttpHandler {
		public void handle(HttpExchange he) {
			changePortfolioHelper(he, 2);
		}
	}

	/**
	 * Handling in value API request - calculating the portfolio
	 * value of the customer. */
	private static class valueHandler implements HttpHandler {
		public void handle(HttpExchange he) {
			/* reading the id of the customer which we want to
			 * calculate it's portfolio value from the value
			 * request params. */
			String id = helper(he);
			boolean isExist = false;
			double value = 0;
			// if id exist, calculate value
			for (Profile cust : customers) {
				if (cust.getID().equals(id)) {
					 isExist = true;
					 value = cust.getPortfolio().
							 portfolioValue(stocksDB);
					 break;
				}
			}	
			String str = null;
			byte[] response = null;
			if (!isExist) {
				str = "You gave a wrong portfolio id,"
						+ " try again.";
			} else {
					str = "Your portfolio value is: " +
							String.valueOf(value);
			}
			// send the response to the request to the client.
			response = str.getBytes(StandardCharsets.UTF_8); // Java 7+ only
			sendResponse(he, response);
		}
	}

	/**
	 * The function doubleToIntStocks.
	 * A helper function for converting double value to int. */
	private static Map<String, Integer> doubleToIntStocks
			(Map<String, Double> doubleStocks) {
		HashMap<String, Integer> intStocks = 
				new HashMap<String, Integer>();
		for (HashMap.Entry<String, Double> entry : 
			doubleStocks.entrySet()) {
			double d = entry.getValue().doubleValue();
			int x = (int) d;
			if (x <= 0) {
				System.err.println("You need to choose a"
						+ " postive number of stock."
						+ " Check the stock " + entry.getKey());
			} else {
				Integer value = new Integer(x);
				intStocks.put(entry.getKey(), value);
			}
		}
		return intStocks;
	}


	 /**
	 * The function helper.
	 * A helper function for the register, update and replace handlers,
	 * which preventing duplicate code. */
	private static String helper(HttpExchange he) {
		BufferedReader in = null;
		StringBuffer buf = null;
		try {
			InputStreamReader isr =  
				new InputStreamReader(he.getRequestBody(),"utf-8");
			in = new BufferedReader(isr);
			buf = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				buf.append(line);
			}
			String path = OUTPUT_PATH + CUSTOMERS_FILE;
			File customersFile = new File(path);
			// if file doesnt exists, then create it
			if (!customersFile.exists()) {
				try {
					customersFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (customers == null) {
				customers = new ArrayList<Profile>();
			}
		} catch (IOException e) {
			System.err.println("Failed register new customer");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.err.println("Failed closing reader");
				}
			}
		}
		return buf.toString();
	}

	private static void changePortfolioHelper(HttpExchange he, int option) {
		/* reading the stocks data for updating from
		the update request params. */
		String buffer = helper(he);
		Map<String, Double> tempStocksAndAmounts =
				new HashMap<String, Double>();
		PortfolioParams changePortfolioParams
			= new Gson().fromJson(buffer, 
					PortfolioParams.class);
		String id = changePortfolioParams.getCustomerID();
		tempStocksAndAmounts = changePortfolioParams.getPortfolio();
		HashMap<String, Integer> stocksAndAmounts
			= new HashMap<String, Integer>
			(doubleToIntStocks(tempStocksAndAmounts));
		HashMap<String, Integer> stocks =
				new HashMap<String, Integer>();
		for (HashMap.Entry<String, Integer> entry :
				stocksAndAmounts.entrySet()) {
			String stockName = entry.getKey();
			int amount = entry.getValue();
			stocks.put(stockName, amount);
		}
		PortfolioAPI portfolio = new Portfolio(stocks); 
		// if id exist in DB, update
		boolean isExist = false;
		for (Profile cust : customers) {
			 if (cust.getID().equals(id)) {
				 isExist = true;
				 if (option == 1) {
					 cust.updateStocks(stocksAndAmounts);
				 } else if (option == 2) {
					 cust.replacePortfolio(portfolio, stocksDB);						 
				 }
			 }
		}
		String str = null;
		byte[] response = null;
		if (!isExist) {
			str = "You gave a wrong portfolio id,"
					+ " try again.";
		} else {
			// save the updated customers to file
			IO stkIO = new StocksIO();
			String path = OUTPUT_PATH + CUSTOMERS_FILE;
			File customersFile = new File(path);
			stkIO.saveCustomers(customersFile, customers);	
			if (option == 1) {
				str = "Portfoilo of id " + id
						+ " successfully updated with new stocks"; 
			} else if (option == 2) {
				str = "Portfoilo stocks of id " + id
						+ " successfully updated"; 
			}
		}
		// send the response to the request to the client.
		response = str.getBytes(StandardCharsets.UTF_8); // Java 7+ only
		sendResponse(he, response);
	}

	/**
	 * Returns to client the response for his request. */
	private static void sendResponse(HttpExchange he, 
				byte[] response) {
		 try {
			he.sendResponseHeaders(200, response.length);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    OutputStream os = he.getResponseBody();
	    try {
			os.write(response);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
		    if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
		        	 System.err.println("Failed closing "
		        	 		+ "output stream");
				}
			}
		}
	}
}