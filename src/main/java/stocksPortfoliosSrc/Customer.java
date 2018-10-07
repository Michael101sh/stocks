package stocksPortfoliosSrc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import stocksPortfoliosAPI.PortfolioAPI;
import stocksPortfoliosAPI.Profile;

/**
 * The class Customer - define customer in the system.
 * @author Michael Shachar. */
public class Customer implements Serializable, Profile {
	private static final long serialVersionUID = 1L;
	private String id;
	private PortfolioAPI portfolio;
	
	
	public Customer(PortfolioAPI portfolio) {
		this.id = UUID.randomUUID().toString();  // Unique random ID
		this.portfolio = portfolio;			
	}

	public String getID() {
		return this.id;
	}

	public PortfolioAPI getPortfolio() {
		return this.portfolio;
	}
	
	public void replacePortfolio(PortfolioAPI newPortfolio,
			StocksDB stocksDB) {
		HashMap<String, Integer> stocksToAdd = 
					new HashMap<String, Integer>();
		Map<String, Integer> newStocks = 
				newPortfolio.getStocksAndAmounts();
		for (HashMap.Entry<String, Integer> entry : 
				newStocks.entrySet()) {
			String stockName = entry.getKey();
			if (stocksDB.isStockExist(stockName)) {
				int amount = entry.getValue();
				stocksToAdd.put(stockName, amount);
			} else {
				System.err.println("There is no " + stockName +
						" stock in stocks database, try again.");
			}
		}
		this.portfolio = new Portfolio(stocksToAdd);
	}
	
	public void updateStocks(Map<String, Integer> stocks) {
		for (Map.Entry<String, Integer> entry : 
				stocks.entrySet()) {
			String stockName = entry.getKey();
			// only update existing stocks, don't add new stocks.
			if (this.portfolio.isStockExist(stockName)) {
				this.portfolio.setStockAndAmount(stockName, entry.getValue());
			} else {
				System.err.println("The portfolio doesn't contatin " +
						stockName + " stock");
			}
		}
	}
}