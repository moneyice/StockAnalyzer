package stock.selector.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import stock.selector.dao.IStockDAO;
import stock.selector.dao.StockDAO4FileSystem;
import stock.selector.process.io.ConsoleResultWriter;
import stock.selector.process.io.HtmlFileResultWriter;
import stock.selector.process.io.IResultWriter;

public class StockSelector {

	Properties props = null;

	public StockSelector() throws FileNotFoundException, IOException {
		props = new Properties();
		props.load(new FileReader("analyzer.properties"));
	}

	public void runYOMAnalyzer() throws IOException {
		YesterdayOnceMoreAnalyzer analyzer = new YesterdayOnceMoreAnalyzer();
		analyzer.setDaysToNow(getInt("YOM.analyzer.daysToNow"));
		analyzer.setRangeDays(getInt("YOM.analyzer.rangeDays"));
		analyzer.setRange(getDouble("YOM.analyzer.range"));
		analyzer.setCloseRangeDown(getDouble("YOM.analyzer.closeRangeDown"));
		analyzer.setCloseRangeTop(getDouble("YOM.analyzer.closeRangeTop"));

		IResultWriter writer = getResultWriter();

		HistoryDataService hs = new HistoryDataService();
		IStockDAO stockDAO=new StockDAO4FileSystem(getString("local.data.cache.folder"));
		hs.setStockDAO(stockDAO);

		analyzer.setResultwriter(writer);

		hs.addAnalyzer(analyzer);

		// hs.startAnalyzeFromYahoo();
		hs.startAnalyze();
	}

	public void runDemarkAnalyzer() throws IOException {
		AbstractStockAnalyzer analyzer = new DemarkAnalyzer();
		IResultWriter writer = getResultWriter();

		HistoryDataService hs = new HistoryDataService();
		IStockDAO stockDAO=new StockDAO4FileSystem(getString("stock.data.folder"));
		hs.setStockDAO(stockDAO);
		analyzer.setResultwriter(writer);

		hs.addAnalyzer(analyzer);

		// hs.startAnalyzeFromYahoo();
		hs.startAnalyze();
	}

	public IResultWriter getResultWriter() throws IOException {
		Calendar cal = Calendar.getInstance();
		String filename = "" + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH)
				+ cal.get(Calendar.DAY_OF_MONTH) + "_"
				+ cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE)+cal.get(Calendar.SECOND)
				+ ".html";
		File root = new File("result");
		IResultWriter writer = new HtmlFileResultWriter(
				new File(root, filename));

//		writer = new ConsoleResultWriter();

		return writer;
	}

	public int getInt(String key) {
		return Integer.parseInt(props.getProperty(key));
	}

	public double getDouble(String key) {
		return Double.parseDouble(props.getProperty(key));
	}
	
	public String getString(String key){
		return props.getProperty(key);
	}

}
