package stock.selector.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import stock.selector.dao.IStockDAO;
import stock.selector.model.ResultInfo;
import stock.selector.model.Stock;

public class StockSelectService {
	List<IStockAnalyzer> analyzers = new ArrayList<IStockAnalyzer>();
	IStockDAO stockDAO = null;
	List<ResultInfo> selectResultList = null;

	public List<ResultInfo> getSelectResultList() {
		return selectResultList;
	}

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
		// startAnalyzeInParallel();

		long now = System.currentTimeMillis();
		System.out.println("collapse " + ((now - mark) / 1000) + " seconds.");
	}

	private void startAnalyzeInParallel() {
		selectResultList = new CopyOnWriteArrayList<ResultInfo>();

		ExecutorService es = Executors.newFixedThreadPool(15);

		List<Stock> allSymbols = stockDAO.getAllSymbols();
		for (Stock stock : allSymbols) {
			Task task = new Task(stock.getCode(), selectResultList);
			es.execute(task);
		}
		es.shutdown();
		try {
			es.awaitTermination(60, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	class Task implements Runnable {
		String code = null;
		List<ResultInfo> list = null;

		public Task(String code, List<ResultInfo> selectResultList) {
			this.code = code;
			this.list = selectResultList;
		}

		public void run() {
			Stock stock = null;
			stock = stockDAO.getStock(code);
			if (stock != null) {
				ResultInfo resultInfo = analyze(stock);
				if (resultInfo != null) {
					selectResultList.add(resultInfo);
				}
			}
		}
	}

	public void startAnalyzeInSequence() {
		// for (IStockAnalyzer analyzer : analyzers) {
		// analyzer.getResultwriter().write(analyzer.getDescription());
		// }

		selectResultList = new ArrayList<ResultInfo>();
		List<Stock> allSymbols = getAllSymbols();
		for (Stock stock : allSymbols) {
			stock = stockDAO.getStock(stock.getCode());
			if (stock != null) {
				ResultInfo resultInfo = analyze(stock);
				if (resultInfo != null) {
					selectResultList.add(resultInfo);
				}
			}
		}
	}

	private ResultInfo analyze(Stock stock) {
		ResultInfo resultInfo = new ResultInfo();
		for (IStockAnalyzer analyzer : analyzers) {
			if (!analyzer.analyze(resultInfo, stock)) {
				resultInfo = null;
				break;
			}
		}
		if (resultInfo != null) {
			// shrink the transferred data
			stock.setDailyinfo(null);
			resultInfo.setStock(stock);
		}
		return resultInfo;
	}

	public List<Stock> getAllSymbols() {
		return stockDAO.getAllSymbols();
	}
}
