package stocksPortfoliosSrc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import stocksPortfoliosAPI.IO;
import stocksPortfoliosAPI.Operations;

/**
 * The class Client which implements the Operations interface.
 * Sending API requests (=Operations - register, update, replace, value)
 * to the server.
 * @author Michael Shachar. */
public class Client implements Operations {
	private static final String IP = "http://localhost:";
	private static final int PORT = 8080;
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String IDS_FILE_NAME = "ids.txt";
	private static final String OUTPUT_PATH = "output/";
	private IO stkIO = new StocksIO();
	
	// Handling in register API request.
	public void register(String inpuFilePath) {
	    // urlParams are the stocks portfolios
		HashMap<String, Double> stocks = (HashMap<String, Double>) 
				this.stkIO.readStocks(inpuFilePath, false);
		if (stocks != null) {
			HashMap<String, Double> urlParams = stocks;
			if (urlParams != null) {
				 String url = IP + String.valueOf(PORT) + "/register";
				    String jsonMap = new Gson().toJson(urlParams);
				    String id = sendPost(url, jsonMap);
					System.out.println("Server response is: \n" +
							"Your Portfoilo was successfully created. "
							+ "Portfoilo id is: " + id + ".");	
			}
		}
	}

	/**
     * This function handling in update API request. */
	public void update(int idIndex, String inpuFilePath) {
		String url = IP + String.valueOf(PORT) + "/update";
		helper(url, idIndex, inpuFilePath);
	}

	// The function handling in replace API request.
	public void replace(int idIndex, String inputFilePath) {
		String url = IP + String.valueOf(PORT) + "/replace";
		helper(url, idIndex, inputFilePath);
	}
	
	// Handling in value API request.
	public void value(int idIndex) {
		String idsFilePath = OUTPUT_PATH + IDS_FILE_NAME;
		String id = this.stkIO.readID(idsFilePath, idIndex);
		if (id != null) {
			String url = IP + String.valueOf(PORT) + "/value";
			String response = sendPost(url, id);
		    System.out.println("Server response is: " + response + ".");
		}
	}
	
	// Send to the server the post requests.
	private String sendPost(String url, String params) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		// add header - parameters and other properties.
		post.setHeader("User-Agent", USER_AGENT);
	    StringEntity entity;
	    String responseAsString = "";
		try {
			entity = new StringEntity(params);
			post.setEntity(entity);
		    HttpResponse response = null;
			try {
				response = client.execute(post);
			} catch (IOException e) {
                System.err.println("Failed execute post request");
				e.printStackTrace();
			}
			System.out.println("Sending 'POST' request to URL : "
								+ url);
			System.out.println("Post parameters : " + 
									post.getEntity());
			System.out.println("Response Code : " + 
					response.getStatusLine().getStatusCode());
			try {
				responseAsString = EntityUtils.toString(
						response.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return responseAsString;
	}
	
	private void helper(String url, int idIndex, String inpuFilePath) {
		String idsFilePath = OUTPUT_PATH + IDS_FILE_NAME;
		String id = this.stkIO.readID(idsFilePath, idIndex);
		if (id != null) {
			PortfolioParams urlParams = null;
			HashMap<String, Double> stocks = (HashMap<String, Double>)
					this.stkIO.readStocks(inpuFilePath, false);
			if (stocks != null) {
				HashMap<String, Double> stocksAndAmounts = stocks;
				if (stocksAndAmounts != null) {
					String jsonMap = null;
					urlParams = new PortfolioParams
							(id, stocksAndAmounts);
					jsonMap = new Gson().toJson(urlParams);
					String response = sendPost(url, jsonMap);
				    System.out.println("Server response is: " + response + ".");
				}
			}
		}
	}
}