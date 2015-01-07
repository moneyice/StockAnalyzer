package stock.selector.jobs;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import stock.selector.dao.IStockDAO;
import stock.selector.dao.StockDAO4Aerospike;
import stock.selector.model.DailyInfo;
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
				Stock currentStock=dao.getStock(stock.getCode());
				if(currentStock==null||isOutOfDate(currentStock.getDailyinfo())){
					System.out.println("get " + stock.getCode());
					Stock info = stockRetreiver.getStockInfo(stock);
					dao.storeStock(info);
				}
				
				
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean isOutOfDate(List<DailyInfo> dailyinfo) {
		if(dailyinfo==null||dailyinfo.isEmpty()){
			return false;
		}
		Calendar  lastDay=Calendar.getInstance();
		lastDay.setTime(dailyinfo.get(dailyinfo.size()-1).getTime());
		
		Calendar today=Calendar.getInstance();
		
		lastDay.get(Calendar.YEAR);
		
//		每天18:00 以前，最新数据是昨天的， 每天18:00 以后，最新数据是今天的，就什么都不做。
//		否则更新数据
		
		
		
		
		
		return false;
	}

	public static void main(String[] args) {
		StockInfoSpider spider = new StockInfoSpider();
		spider.run();
	}

}
