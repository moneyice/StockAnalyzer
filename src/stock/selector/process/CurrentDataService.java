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
import java.util.logging.Level;
import java.util.logging.Logger;

import stock.selector.model.DailyInfo;
import stock.selector.model.Stock;
import stock.selector.util.Utils;

import com.nhefner.main.StockFetcher;

public class CurrentDataService {
	List<IStockAnalyzer> analyzers = new ArrayList<IStockAnalyzer>();

	public void addAnalyzer(IStockAnalyzer analyzer) {
		analyzers.add(analyzer);
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
				stock = getStockCurrentData(symbol);
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

	private Stock getStockCurrentData(String symbol) throws IOException,
			ParseException {
		int i = 0;
		Stock stock = null;
		while (i < 10) {
			i++;
			try {
				stock = getStockCurrentDataOnce(symbol);
			} catch (Exception e) {
				continue;
			}
			if (stock == null) {
				continue;
			}
		}
		return stock;
	}

	private Stock getStockCurrentDataOnce(String symbol) throws IOException,
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
		CurrentDataService s = new CurrentDataService();

		for (int i = 0; i < 100; i++) {
			Stock stock = null;
			try {
				stock = s.getStockCurrentData("300319.SZ");
			} catch (Exception e) {
				// TODO: handle exception
			}
			System.out.println(stock + "  " + i);
		}

	}
}
