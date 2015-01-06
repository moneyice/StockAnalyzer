package stock.selector.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import stock.selector.model.DailyInfo;
import stock.selector.model.Stock;
import stock.selector.util.Utils;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.alibaba.fastjson.JSON;
import com.nhefner.main.StockFetcher;

public class StockCodeRetreiver2 {
	String codeListHTML = "http://quote.eastmoney.com/stocklist.html";
	private AerospikeClient client;
	List<Stock> list = new ArrayList<Stock>();
	private InputStreamReader is;

	public void run() throws IOException {
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
	}

	private void store() {
		String allSymbols = JSON.toJSONString(list);
		ClientPolicy cPolicy = new ClientPolicy();
		cPolicy.timeout = 500;
		this.client = new AerospikeClient(cPolicy, "172.20.2.19", 3000);

		Key key = new Key("test", "Symbols", "allSymbols");
		Bin bin = new Bin(null, allSymbols);

		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		client.put(wPolicy, key, bin);
		if (this.client != null) {
			this.client.close();
		}
	}

	private void getAllSymbols() {
		ClientPolicy cPolicy = new ClientPolicy();
		cPolicy.timeout = 500;

		this.client = new AerospikeClient(cPolicy, "172.20.2.19", 3000);
		Key key = new Key("test", "Symbols", "allSymbols");
		Record record = client.get(cPolicy.readPolicyDefault, key);
		String rs = record.getValue("").toString();
		System.out.println(rs);

		List<Stock> stocks = JSON.parseArray(rs, Stock.class);

		System.out.println(stocks);

		if (this.client != null) {
			this.client.close();
		}
	}

	public Stock getStock(Stock stock) throws IOException {
		String symbol = stock.getMarket() + stock.getCode();
		String url = "http://xueqiu.com/S/#{symbol}/historical.csv";
		url = url.replace("#{symbol}", symbol);
		BufferedReader br = null;
		try {
			// Retrieve CSV File
			URL xueqiuurl = new URL(url);
			URLConnection connection = xueqiuurl.openConnection();
			 is = new InputStreamReader(
					connection.getInputStream());
			br = new BufferedReader(is);
			// pass the first head line
			// symbol, date, open, high, low, close, volume
			// SH600340 2003-12-30 12:00 AM 8.7 9.16 8.68 8.92 16666958
			String line = br.readLine();
			// Parse CSV Into Array
			while ((line = br.readLine()) != null) {
				line=line.replaceAll("\"", "");
				String[] result = line.split(",");
				DailyInfo daily = new DailyInfo();
				double open = Utils.handleDouble(result[2]);
				double high = Utils.handleDouble(result[3]);
				double low = Utils.handleDouble(result[4]);
				double close = Utils.handleDouble(result[5]);
				int volume = Utils.handleInt(result[6]);
				Date date = null;
				try {
					date = Utils.parseDate(result[1]);
				} catch (Exception e) {
					throw new RuntimeException("process " + symbol
							+ " error, wrong data format " + result[0]);
				}
				daily.setOpen(open);
				daily.setHigh(high);
				daily.setLow(low);
				daily.setClose(close);
				daily.setTime(date);
				daily.setVolume(volume);
				// by asc order
				stock.getDailyinfo().add(daily);
			}
		} catch (IOException e) {
			Logger log = Logger.getLogger(StockFetcher.class.getName());
			log.log(Level.SEVERE, e.toString() + "  " + symbol);
			throw e;
		}finally{
			if(is!=null){
				is.close();
			}
		}
		return stock;
	}

	public static void main(String[] args) throws IOException {
		StockCodeRetreiver2 scr = new StockCodeRetreiver2();
		// scr.run();
		// scr.store();
		// scr.getAllSymbols();
	}

}
