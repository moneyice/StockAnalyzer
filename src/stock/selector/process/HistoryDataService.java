package stock.selector.process;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import stock.selector.dao.IStockDAO;
import stock.selector.dao.StockDAO4Aerospike;
import stock.selector.model.Stock;

public class HistoryDataService {
	List<IStockAnalyzer> analyzers = new ArrayList<IStockAnalyzer>();
	IStockDAO stockDAO = null;
	

	public IStockDAO getStockDAO() {
		return stockDAO;
	}

	public void setStockDAO(IStockDAO stockDAO) {
		this.stockDAO = stockDAO;
	}

	public void addAnalyzer(IStockAnalyzer analyzer) {
		analyzers.add(analyzer);
	}

	public void startAnalyze() {
		long mark = System.currentTimeMillis();
		startAnalyzeInSequence();
//		startAnalyzeInParallel();

		long now = System.currentTimeMillis();
		System.out.println("collapse " + ((now - mark) / 1000) + " seconds.");
	}

	class Task implements Runnable {
		String code = null;

		public Task(String code) {
			this.code = code;
		}

		public void run() {
			Stock stock = null;
			try {
				stock = stockDAO.getStock(code);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (stock != null) {
				for (IStockAnalyzer analyzer : analyzers) {
					analyzer.analyze(stock);
				}
			}
		}
	}

	private void startAnalyzeInParallel() {
		for (IStockAnalyzer analyzer : analyzers) {
			analyzer.getResultwriter().write(analyzer.getDescription());
		}

		ExecutorService es = Executors.newFixedThreadPool(15);

		List<Stock> allSymbols = stockDAO.getAllSymbols();
		for (Stock stock : allSymbols) {
			Task task = new Task(stock.getCode());
			es.execute(task);
		}
		es.shutdown();
		try {
			es.awaitTermination(60, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (IStockAnalyzer analyzer : analyzers) {
			analyzer.outPutResults();
		}
	}

	public void startAnalyzeInSequence() {
		for (IStockAnalyzer analyzer : analyzers) {
			analyzer.getResultwriter().write(analyzer.getDescription());
		}

		List<Stock> allSymbols = stockDAO.getAllSymbols();
		for (Stock stock : allSymbols) {
			try {
				stock = stockDAO.getStock(stock.getCode());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (stock != null) {
				for (IStockAnalyzer analyzer : analyzers) {
					analyzer.analyze(stock);
				}
			}
		}
		for (IStockAnalyzer analyzer : analyzers) {
			analyzer.outPutResults();
		}
	}

	public List<Stock> getAllSymbols() {
		return stockDAO.getAllSymbols();
	}

	// public IResultWriter getResultwriter() {
	// return resultwriter;
	// }
	//
	// public void setResultwriter(IResultWriter resultwriter) {
	// this.resultwriter = resultwriter;
	// }
	public static void main(String[] args) throws IOException, ParseException {
		HistoryDataService s = new HistoryDataService();

	}
}
