package stocksPortfoliosSrc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import stocksPortfoliosAPI.PortfolioAPI;

/**
 * The class Portfolio - define stocks Portfolio.
 * @author Michael Shachar. */
public class Portfolio implements Serializable,
			PortfolioAPI {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> stocksAndAmounts;
		
	public Portfolio(HashMap<String, Integer> stocksAndAmounts) {
		this.stocksAndAmounts =
			new HashMap<String, Integer>(stocksAndAmounts);
	}
	
    public Map<String, Integer> getStocksAndAmounts() {
		return new HashMap<String, Integer>(this.stocksAndAmounts);
	}
	
	public void setStockAndAmount(String stockName, int amount) {
		this.stocksAndAmounts.put(stockName, amount);
	}
	
	public int getStockAmount(String stockName) {
		 return this.stocksAndAmounts.get(stockName);
	}
	
	public boolean isStockExist(String name) {
		return (this.stocksAndAmounts.containsKey(name));
	}
	

	public double portfolioValue(StocksDB stocksDB) {
		double sum = 0;
		int amount = 0;
		double value = 0;
		/* value is the sum of all its stock values, where a
		 * stock value is its amount multiplied by its value. */
	    for (HashMap.Entry<String, Integer> entry : 
				this.stocksAndAmounts.entrySet()) {
			amount = entry.getValue().intValue();
			value = stocksDB.getStock(entry.getKey()).getValue();
			sum = sum + (amount * value);
		}
		return sum;
	}
}