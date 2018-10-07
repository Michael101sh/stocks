package stocksPortfoliosAPI;

/**
 * The interface Operations.
 * Define the API of the requests (operations) which 
 *  the client should be able to send to the server.
 * @author Michael Shachar. */
public interface Operations {
	// This function handling in register API request.
	public void register(String inpuFilePath);
	
	/**
     * This function handling in update API request.
     * @param idIndex - index of the id of customer, which it's
     *  portfolio need to be updated, from the ids file
     *  (all of the ids in the DB of the customers). */
	public void update(int idIndex, String inpuFilePath);	
	/**
     * The function handling in replace API request.
     * @param idIndex - index of the id of customer, which it's
     *  portfolio need to be updated, from the ids file. */
	public void replace(int idIndex, String inputFilePath);
	
	/**
     * This function handling in value API request.
 	 * @param idIndex - index of the id, of the customer which we
 	 * want to calculate it's value, from the ids file. */
	public void value(int idIndex);
}