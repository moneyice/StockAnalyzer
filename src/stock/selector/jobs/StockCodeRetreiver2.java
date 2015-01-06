package stock.selector.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import stock.selector.model.Stock;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.alibaba.fastjson.JSON;

public class StockCodeRetreiver2 {
	String codeListHTML = "http://quote.eastmoney.com/stocklist.html";
	private AerospikeClient client;
	List<Stock> list = new ArrayList<Stock>();

	public void run() throws IOException {
		Document doc = Jsoup.connect(codeListHTML).get();
		Elements codeList = doc.select("#quotesearch ul li a");
		System.out.println(codeList.size());

		for (Element element : codeList) {
			String text = element.text();
			String linkHref = element.attr("href"); // http://quote.eastmoney.com/sz300409.html
			String market = linkHref.substring(27, 29);
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
		String allSymbols = JSON.toJSONString(list);
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

	public static void main(String[] args) throws IOException {
		StockCodeRetreiver2 scr = new StockCodeRetreiver2();
		scr.run();
		scr.store();
		scr.getAllSymbols();
	}

}
