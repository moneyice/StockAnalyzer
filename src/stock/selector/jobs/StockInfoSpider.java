package stock.selector.jobs;

import java.io.IOException;
import java.util.List;

import stock.selector.dao.IStockDAO;
import stock.selector.dao.StockDAO4Aerospike;
import stock.selector.model.Stock;

public class StockInfoSpider {

	IStockDAO dao = new StockDAO4Aerospike();
	IStockRetreiver stockRetreiver = new WebStockRetreiver();

	public void run() {
		try {
			List<Stock> stockSymbols = stockRetreiver.getAllStockSymbols();
			dao.storeAllSymbols(stockSymbols);

			stockSymbols = dao.getAllSymbols();
			for (Stock stock : stockSymbols) {
				System.out.println("get " + stock.getCode());
				Stock info = stockRetreiver.getStockInfo(stock);
				dao.storeStock(info);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		StockInfoSpider spider = new StockInfoSpider();
		spider.run();
	}

}
