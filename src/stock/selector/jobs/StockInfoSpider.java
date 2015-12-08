package stock.selector.jobs;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stock.selector.dao.IStockDAO;
import stock.selector.dao.StockDAO4FileSystem;
import stock.selector.dao.StockDAO4Redis;
import stock.selector.model.Stock;

public class StockInfoSpider {

	private static final int MAX = 20000;
	// IStockDAO dao = new StockDAO4Aerospike();
	// IStockDAO dao = new
	// StockDAO4FileSystem("/Users/moneyice/code/stock-cache/");
	IStockDAO dao = new StockDAO4Redis();
	private NeteaseWebStockRetreiver stockRetreiver;
	

	public StockInfoSpider() {
		 stockRetreiver = new NeteaseWebStockRetreiver();
	}

	public void run() {
		try {
			Date lastUpdateTime = dao.getAllSymbolsUpdateTime();
			if (lastUpdateTime == null || isAllSymbosOutOfDate(lastUpdateTime)) {
				List<Stock> stockSymbols = stockRetreiver.getAllStockSymbols();
				dao.storeAllSymbols(stockSymbols);

			}

			List<Stock> stockSymbols = dao.getAllSymbols();

			int loop = 0;
			for (Stock stock : stockSymbols) {

				if (loop > MAX) {
					break;
				}

				String code = stock.getCode();
				if (code.startsWith("0") || code.startsWith("3") || code.startsWith("6")) {
					lastUpdateTime = dao.getStockUpdateTime(stock.getCode());
					if (lastUpdateTime == null || isStockOutOfDate(lastUpdateTime)) {
						System.out.println("get " + stock.getCode());
						Stock info = stockRetreiver.getStockInfo(stock);
						dao.storeStock(info);
						loop++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isAllSymbosOutOfDate(Date lastUpdateTime) {
		if (lastUpdateTime == null) {
			return true;
		}
		Calendar lastDay = Calendar.getInstance();
		lastDay.setTime(lastUpdateTime);
		// 超过30天没有更新过 算过期
		lastDay.add(Calendar.DAY_OF_YEAR, 30);
		Calendar today = Calendar.getInstance();
		return lastDay.before(today);
	}

	private boolean isStockOutOfDate(Date lastUpdateTime) {
		if (lastUpdateTime == null) {
			return true;
		}
		Calendar lastDay = Calendar.getInstance();
		lastDay.setTime(lastUpdateTime);

		Calendar today = Calendar.getInstance();
		int todayHour = today.get(Calendar.HOUR_OF_DAY);
		// 每天18:00 以前，最新数据是昨天的， 每天18:00 以后，最新数据是今天的，就什么都不做。
		// 否则更新数据
		if (todayHour < 18) {
			today.add(Calendar.DAY_OF_YEAR, -1);
		}

		int lastDayTime = lastDay.get(Calendar.YEAR) * 10000 + lastDay.get(Calendar.MONTH)
				+ lastDay.get(Calendar.DAY_OF_MONTH);
		int wantedTime = today.get(Calendar.YEAR) * 10000 + today.get(Calendar.MONTH)
				+ today.get(Calendar.DAY_OF_MONTH);

		return lastDayTime < wantedTime;
	}

	public void testPerf() {
		List<Stock> stockSymbols = dao.getAllSymbols();
		long start = System.currentTimeMillis();
		System.out.println(stockSymbols.size());
		for (Stock stock : stockSymbols) {
			String code = stock.getCode();
			if (code.startsWith("0") || code.startsWith("3") || code.startsWith("6")) {

				Stock t = dao.getStock(stock.getCode());

			}
		}
		System.out.println((System.currentTimeMillis() - start) / 1000);
	}

	public static void main(String[] args) {
		StockInfoSpider spider = new StockInfoSpider();
		spider.run();
		// spider.testPerf();
	}

}
