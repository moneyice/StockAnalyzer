package stock.selector.jobs;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StockCodeRetreiver2 {
	String codeListHTML = "http://quote.eastmoney.com/stocklist.html";

	public void run() throws IOException {
		Document doc = Jsoup.connect(codeListHTML).get();
		Elements codeList = doc.select("#quotesearch ul li a");
		System.out.println(codeList.size());
		for (Element element : codeList) {
			String text = element.text();
			String linkHref = element.attr("href"); // http://quote.eastmoney.com/sz300409.html
			String market = linkHref.substring(27, 29);
			String symbol = linkHref.substring(29, 35);
			String name = text.substring(0,text.length() - 8);
			System.out.println(market+"  "+ symbol+"  "+name);
		}

	}

	public static void main(String[] args) throws IOException {
		StockCodeRetreiver2 scr = new StockCodeRetreiver2();
		scr.run();
	}

}
