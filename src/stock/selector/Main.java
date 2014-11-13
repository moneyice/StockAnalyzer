package stock.selector;

import java.io.IOException;

import stock.selector.process.StockSelector;

public class Main {
	public static void main(String[] args) {

		StockSelector selector = null;
		try {
			selector = new StockSelector();
			//昨日重现选股
			selector.runYOMAnalyzer();
			
			//Demark选股
//			selector.runDemarkAnalyzer();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				selector.getResultWriter().write(e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
