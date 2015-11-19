package stock.selector.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import stock.selector.model.Stock;

public abstract class WebStockRetreiver implements IStockRetreiver {
	String codeListHTML = "http://quote.eastmoney.com/stocklist.html";

	@Override
	public List<Stock> getAllStockSymbols() throws IOException {
		List<Stock> list = new ArrayList<Stock>();
		Document doc = Jsoup.connect(codeListHTML).get();
		Elements codeList = doc.select("#quotesearch ul li a");
		System.out.println(codeList.size());

		for (Element element : codeList) {
			String text = element.text();
			String linkHref = element.attr("href"); // http://quote.eastmoney.com/sz300409.html
			String market = linkHref.substring(27, 29).toUpperCase();
			String symbol = linkHref.substring(29, 35);
			String name = text.substring(0, text.length() - 8);
			System.out.println(market + "  " + symbol + "  " + name);
			Stock stock = new Stock();
			stock.setName(name);
			stock.setCode(symbol);
			stock.setMarket(market);
			list.add(stock);
		}
		return list;
	}

	// public void startAnalyzeFromYahoo() {
	// for (IStockAnalyzer analyzer : analyzers) {
	// analyzer.getResultwriter().write(analyzer.getDescription());
	// }
	//
	// String[] symbols = getAllSymbols();
	// for (String symbol : symbols) {
	// sleep();
	// Stock stock = null;
	// try {
	// stock = getStockHistory(symbol);
	// // System.out.prinahtln("succeed: " + symbol);
	// } catch (Exception e) {
	// // e.printStackTrace();
	// continue;
	// }
	// if (stock != null) {
	// for (IStockAnalyzer analyzer : analyzers) {
	// analyzer.analyze(stock);
	// }
	// }
	// }
	// }
	//
	// private void sleep() {
	// try {
	// Thread.sleep(400);
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// }
	//
	// private Stock getStockHistory(String symbol) throws IOException,
	// ParseException {
	// int i = 0;
	// Stock stock = null;
	// while (i < 24) {
	// i++;
	// try {
	// stock = getStockHistoryOnce(symbol);
	// } catch (Exception e) {
	// continue;
	// }
	// if (stock == null) {
	// continue;
	// }
	// }
	// return stock;
	// }
	//
	// private Stock getStockHistoryOnce(String symbol) throws IOException,
	// ParseException {
	// Stock stock = new Stock();
	// stock.setCode(symbol);
	// stock.setName(symbol);
	// BufferedReader br = null;
	// try {
	// // Retrieve CSV File
	// URL yahoo = new URL(
	// "http://ichart.yahoo.com/table.csv?&a=06&b=01&c=2013&g=d&s="
	// + symbol);
	// URLConnection connection = yahoo.openConnection();
	// InputStreamReader is = new InputStreamReader(
	// connection.getInputStream());
	// br = new BufferedReader(is);
	// // pass the first head line
	// // Date,Open,High,Low,Close,Volume,Adj Close
	// String line = br.readLine();
	// // Parse CSV Into Array
	// while ((line = br.readLine()) != null) {
	// String[] result = line.split(",");
	// DailyInfo daily = new DailyInfo();
	// double open = Utils.handleDouble(result[1]);
	// double high = Utils.handleDouble(result[2]);
	// double low = Utils.handleDouble(result[3]);
	// double close = Utils.handleDouble(result[4]);
	// double volume = Utils.handleDouble(result[5]);
	// double adjClose = Utils.handleDouble(result[6]);
	// Date date = null;
	// try {
	// date = Utils.parseDate(result[0]);
	// } catch (Exception e) {
	// throw new RuntimeException("process " + symbol
	// + " error, wrong data format " + result[0]);
	// }
	//
	// daily.setTime(date);
	// daily.setClose(adjClose);
	// stock.getDailyinfo().add(0, daily);
	// }
	// } catch (IOException e) {
	// Logger log = Logger.getLogger(StockFetcher.class.getName());
	// log.log(Level.SEVERE, e.toString() + "  " + symbol);
	// throw e;
	// }
	// return stock;
	// }

}
