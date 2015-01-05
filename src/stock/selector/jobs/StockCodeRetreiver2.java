package stock.selector.jobs;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class StockCodeRetreiver2 {
	String codeListHTML = "http://quote.eastmoney.com/stocklist.html";

	public void run() throws IOException {
		Document doc = Jsoup.connect(codeListHTML).get();
		System.out.println(doc);
	}

	public static void main(String[] args) throws IOException {
		StockCodeRetreiver2 scr = new StockCodeRetreiver2();
		scr.run();
	}

}
