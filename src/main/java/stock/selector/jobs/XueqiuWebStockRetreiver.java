package stock.selector.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import stock.selector.model.DailyInfo;
import stock.selector.model.Stock;
import stock.selector.util.Utils;

public class XueqiuWebStockRetreiver extends WebStockRetreiver {
	String codeListHTML = "http://quote.eastmoney.com/stocklist.html";

	public Stock getStockInfo(Stock stock) throws IOException {
		String symbol = stock.getMarket() + stock.getCode();
		String url = "http://xueqiu.com/S/#{symbol}/historical.csv";
		url = url.replace("#{symbol}", symbol);
		BufferedReader br = null;
		InputStreamReader is = null;
		try {
			// Retrieve CSV File
			URL xueqiuurl = new URL(url);
			URLConnection connection = xueqiuurl.openConnection();
			is = new InputStreamReader(connection.getInputStream());
			br = new BufferedReader(is);
			// pass the first head line
			// symbol, date, open, high, low, close, volume
			// SH600340 2003-12-30 12:00 AM 8.7 9.16 8.68 8.92 16666958
			String line = br.readLine();
			// Parse CSV Into Array
			while ((line = br.readLine()) != null) {
				DailyInfo daily = new DailyInfo();
				try {
					line = line.replaceAll("\"", "");
					String[] result = line.split(",");
					double open = Utils.handleDouble(result[2]);
					if (open < 0.01) {
						continue;
					}
					double high = Utils.handleDouble(result[3]);
					double low = Utils.handleDouble(result[4]);
					double close = Utils.handleDouble(result[5]);
					long volume = Utils.handleLong(result[6]);
					Date date = Utils.parseDate(result[1]);
					daily.setOpen(open);
					daily.setHigh(high);
					daily.setLow(low);
					daily.setClose(close);
					daily.setTime(date);
					daily.setVolume(volume);
				} catch (Exception e) {
					System.out.println("process error " + line);
					throw new RuntimeException("process error " + line);
				}
				// by asc order
				stock.getDailyinfo().add(daily);
			}
		} catch (IOException e) {
			Logger log = Logger.getLogger("sa");
			log.log(Level.SEVERE, e.toString() + "  " + symbol);
			throw e;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return stock;
	}

	public static void main(String[] args) throws IOException {
		XueqiuWebStockRetreiver scr = new XueqiuWebStockRetreiver();
		Stock t = new Stock();
		t.setCode("600000");
		t.setMarket("SH");
		Stock list = scr.getStockInfo(t);
		System.out.println(list);
		// scr.run();
		// scr.store();
		// scr.getAllSymbols();
	}
}
