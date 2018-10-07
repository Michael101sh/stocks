package stocksPortfoliosSrc;

import java.util.HashMap;
import java.util.Map;

/**
 * The class PortfolioParams.
 * Binds customer id and it's stocks portfolios.
 * @author Michael Shachar. */
public class PortfolioParams {
    private String customerId;
    private HashMap<String, Double> portfolio;

    public PortfolioParams(String customerId, 
    		Map<String, Double> portfolio) {
    	this.customerId = customerId;
        this.portfolio = new HashMap<String, Double>(portfolio);
    }

    public String getCustomerID() {
        return this.customerId;
    }
    
    public Map<String, Double> getPortfolio() {
        return this.portfolio;
    }
}