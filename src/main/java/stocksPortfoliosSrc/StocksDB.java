package stocksPortfoliosSrc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The class StocksDB - define stocks DB in the system.
 * @author Michael Shachar. */
public class StocksDB {
	private ArrayList<Stock> stocks;
	
	public StocksDB(Map<String, Double> stocks) {
		List<Stock> stocksData = 
				new ArrayList<Stock>();
		for (Map.Entry<String, Double> entry :
				stocks.entrySet()) {
			stocksData.add(new Stock(entry.getKey(),
					entry.getValue()));
		}
		this.stocks = new ArrayList<Stock>(stocksData);
	}
	
	public ArrayList<Stock> getDB() {
		return this.stocks;
	}
	
	public void setDB(ArrayList<Stock> stocks) {
		this.stocks = new ArrayList<Stock>(stocks);
	}
	
	public Stock getStock(String name) {
		for (Stock stk : stocks) {
			if (stk.getName().equals(name)) {
				return stk;
			}
		}
		System.err.println("There is no such a stock");
		return null;
	}
	
	public void addStock(Stock stk) {
		this.stocks.add(stk);
	}
	
	public void setStockValue(String name, double value) {
		if (isStockExist(name)) {
			for (Stock stk : stocks) {
				if (stk.getName().equals(name)) {
					stk.setValue(value);
				}
			}
		}
	}
	
	public ArrayList<String> getStocksNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Stock stk : this.stocks) {
			names.add(stk.getName());
		}
		return names;
	}
	
	public boolean isStockExist(String name) {
		ArrayList<String> names = 
				new ArrayList<String>(this.getStocksNames());
		if (names.contains(name)) {
			return true;
		}
		return false;
	}
}
