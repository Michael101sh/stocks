package stocksPortfoliosAPI;

import java.util.Map;
import stocksPortfoliosSrc.StocksDB;

/**
 * The interface Profile.
 * Define the operations which customer object should have. 
 * @author Michael Shachar. */
public interface Profile {
	
	public String getID();
	
	public PortfolioAPI getPortfolio();
	
	public void replacePortfolio(PortfolioAPI portfolio,
			StocksDB stocksDB);
	
	public void updateStocks(Map<String, Integer> stocks);
}