package stocksPortfoliosSrc;

import java.io.Serializable;

/**
 * The class Stock - define Stock in the system.
 * @author Michael Shachar. */
public class Stock implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MAX_ID = 1000000;
	private int id;
	private String name = "";
	private double value = 0;
	
	public Stock(String name, double value) {
		// define the range
        int max = MAX_ID;
        int min = 1;
        int range = max - min + 1;
        this.id = (int)(Math.random() * range) + min;
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
    public boolean equals(Object obj) {
		Stock stk = (Stock) obj;
        return (this.name.equals(stk.name));
    }
	
	 @Override
	 public int hashCode() {
		 return this.id;
	 }
}
