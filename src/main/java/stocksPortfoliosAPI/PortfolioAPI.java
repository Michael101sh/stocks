package stocksPortfoliosAPI;

import java.util.Map;

import stocksPortfoliosSrc.StocksDB;

/**
 * The interface PortfolioAPI.
 * Define the API for portfolio of stocks.
 * @author Michael Shachar. */
public interface PortfolioAPI {
	public Map<String, Integer> getStocksAndAmounts();

	public void setStockAndAmount(String stockName, int amount);
		
	// return the amount of a specific stock.
	public int getStockAmount(String stockName);
	
	public boolean isStockExist(String name);
	
	public double portfolioValue(StocksDB stocksDB);
}
