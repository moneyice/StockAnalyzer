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

import com.nhefner.main.StockFetcher;

public class NeteaseWebStockRetreiver extends WebStockRetreiver {
	String URL = "http://quotes.money.163.com/service/chddata.html?code=#{symbol}&start=20140101&&fields=TOPEN;HIGH;LOW;TCLOSE;VOTURNOVER";

	public Stock getStockInfo(Stock stock) throws IOException {
		String symbol = null;
		if(stock.getMarket().equals("SH")){
			symbol = "0" +stock.getCode();
		}else{
			symbol = "1" +stock.getCode();
		}
		
		String url = URL.replace("#{symbol}", symbol);
		BufferedReader br = null;
		InputStreamReader is = null;
		try {
			// Retrieve CSV File
			URL neteaseUrl = new URL(url);
			URLConnection connection = neteaseUrl.openConnection();
			is = new InputStreamReader(connection.getInputStream());
			br = new BufferedReader(is);
			// pass the first head line
			//日期	股票代码	名称	开盘价	最高价	最低价	收盘价	成交量
			// 2015-1-8	'600000	浦发银行	15.87	15.88	15.2	15.25	330627172
			String line = br.readLine();
			// Parse CSV Into Array
			while ((line = br.readLine()) != null) {
				DailyInfo daily = new DailyInfo();
				try {
					line = line.replaceAll("\"", "");
					String[] result = line.split(",");
					double open = Utils.handleDouble(result[3]);
					if (open < 0.01) {
						continue;
					}
					double high = Utils.handleDouble(result[4]);
					double low = Utils.handleDouble(result[5]);
					double close = Utils.handleDouble(result[6]);
					long volume = Utils.handleLong(result[7]);
					Date date = Utils.parseDate(result[0]);
					daily.setOpen(open);
					daily.setHigh(high);
					daily.setLow(low);
					daily.setClose(close);
					daily.setTime(date);
					daily.setVolume(volume);
				} catch (Exception e) {
					System.out.println("163 process error " + line);
					throw new RuntimeException("163 process error " + line);
				}
				// by asc order
				stock.getDailyinfo().add(daily);
			}
		} catch (IOException e) {
			Logger log = Logger.getLogger(StockFetcher.class.getName());
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
		NeteaseWebStockRetreiver scr = new NeteaseWebStockRetreiver();
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
