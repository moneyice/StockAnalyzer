package stock.selector.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import stock.selector.model.DailyInfo;
import stock.selector.model.Stock;
import stock.selector.util.Utils;

import com.nhefner.main.StockFetcher;

public class HistoryDataService {
	String stockDataFolder = null;

	public String getStockDataFolder() {
		return stockDataFolder;
	}

	public void setStockDataFolder(String stockDataFolder) {
		this.stockDataFolder = stockDataFolder;
	}

	// IResultWriter resultwriter=null;

	List<IStockAnalyzer> analyzers = new ArrayList<IStockAnalyzer>();

	public void addAnalyzer(IStockAnalyzer analyzer) {
		analyzers.add(analyzer);
	}

	public void startAnalyze() {
		long mark=System.currentTimeMillis();
		//startAnalyzeInSequence();
		 startAnalyzeInParallel();
		
		long now=System.currentTimeMillis();
		System.out.println("collapse "+((now-mark)/1000) + " seconds." );
	}

	class Task implements Runnable {
		File file;

		public Task(File file) {
			this.file = file;
		}

		@Override
		public void run() {
			Stock stock = null;
			try {
				stock = getStockHistory(file);
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

		File[] files = getFiles();
		for (File file : files) {
			Task task = new Task(file);
			es.execute(task);
		}
		es.shutdown();
		try {
			es.awaitTermination(60, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void startAnalyzeInSequence() {
		for (IStockAnalyzer analyzer : analyzers) {
			analyzer.getResultwriter().write(analyzer.getDescription());
		}

		File[] files = getFiles();
		for (File file : files) {
			Stock stock = null;
			try {
				stock = getStockHistory(file);
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

	public void startAnalyzeFromYahoo() {
		for (IStockAnalyzer analyzer : analyzers) {
			analyzer.getResultwriter().write(analyzer.getDescription());
		}

		String[] symbols = getAllSymbols();
		for (String symbol : symbols) {
			sleep();
			Stock stock = null;
			try {
				stock = getStockHistory(symbol);
				// System.out.println("succeed: " + symbol);
			} catch (Exception e) {
				// e.printStackTrace();
				continue;
			}
			if (stock != null) {
				for (IStockAnalyzer analyzer : analyzers) {
					analyzer.analyze(stock);
				}
			}
		}
	}

	private void sleep() {
		try {
			Thread.sleep(400);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private String[] getAllSymbols() {
		List<String> list = new ArrayList<String>();
		String[] symbols = new String[] { "000001.SZ", "000002.SZ" };
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"./stock_codes.txt"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("6") || line.startsWith("0")
						|| line.startsWith("3")) {
					list.add(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.toArray(new String[list.size()]);
	}

	public Stock getStockHistory(File file) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "GBK"));
		String input = null;
		input = br.readLine();
		// first line
		if (input == null) {
			return null;
		}
		String[] result = input.split(" ");
		Stock stock = new Stock();
		stock.setCode(result[0]);
		stock.setName(result[1]);

		br.readLine();// pass the second line

		while ((input = br.readLine()) != null) {
			// pass the line 数据来源：通达信
			if (input.indexOf(":") > 0) {
				continue;
			}
			result = input.split(",");
			DailyInfo daily = new DailyInfo();
			try {
				daily.setClose(Double.parseDouble(result[4]));
				daily.setLow(Double.parseDouble(result[3]));
				Date time = Utils.format(result[0]);
				daily.setTime(time);
				stock.getDailyinfo().add(daily);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		br.close();
		return stock;
	}

	public File[] getFiles() {
		File root = new File(stockDataFolder);
		if (!root.isDirectory()) {
			return new File[0];
		}

		return root.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.indexOf("SH") > -1 || arg1.indexOf("SZ") > -1;
			}
		});
	}

	private Stock getStockHistory(String symbol) throws IOException,
			ParseException {
		int i = 0;
		Stock stock = null;
		while (i < 24) {
			i++;
			try {
				stock = getStockHistoryOnce(symbol);
			} catch (Exception e) {
				continue;
			}
			if (stock == null) {
				continue;
			}
		}
		return stock;
	}

	private Stock getStockHistoryOnce(String symbol) throws IOException,
			ParseException {
		Stock stock = new Stock();
		stock.setCode(symbol);
		stock.setName(symbol);
		BufferedReader br = null;
		try {
			// Retrieve CSV File
			URL yahoo = new URL(
					"http://ichart.yahoo.com/table.csv?&a=06&b=01&c=2013&g=d&s="
							+ symbol);
			URLConnection connection = yahoo.openConnection();
			InputStreamReader is = new InputStreamReader(
					connection.getInputStream());
			br = new BufferedReader(is);
			// pass the first head line
			// Date,Open,High,Low,Close,Volume,Adj Close
			String line = br.readLine();
			// Parse CSV Into Array
			while ((line = br.readLine()) != null) {
				String[] result = line.split(",");
				DailyInfo daily = new DailyInfo();
				double open = Utils.handleDouble(result[1]);
				double high = Utils.handleDouble(result[2]);
				double low = Utils.handleDouble(result[3]);
				double close = Utils.handleDouble(result[4]);
				double volume = Utils.handleDouble(result[5]);
				double adjClose = Utils.handleDouble(result[6]);
				Date date = null;
				try {
					date = Utils.parseDate(result[0]);
				} catch (Exception e) {
					throw new RuntimeException("process " + symbol
							+ " error, wrong data format " + result[0]);
				}

				daily.setTime(date);
				daily.setClose(adjClose);
				stock.getDailyinfo().add(0, daily);
			}
		} catch (IOException e) {
			Logger log = Logger.getLogger(StockFetcher.class.getName());
			log.log(Level.SEVERE, e.toString() + "  " + symbol);
			throw e;
		}
		return stock;
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

		for (int i = 0; i < 100; i++) {
			Stock stock = null;
			try {
				stock = s.getStockHistory("300319.SZ");
			} catch (Exception e) {
				// TODO: handle exception
			}
			System.out.println(stock + "  " + i);
		}

	}
}
