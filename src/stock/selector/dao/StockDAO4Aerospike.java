package stock.selector.dao;

import java.io.InputStreamReader;
import java.util.List;

import stock.selector.model.Stock;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.alibaba.fastjson.JSON;

public class StockDAO4Aerospike implements IStockDAO {
	public static String HOST = "172.20.2.19";
	public static int PORT = 3000;
	private AerospikeClient client;

	public void storeAllSymbols(List<Stock> list) {
		String allSymbols = JSON.toJSONString(list);
		ClientPolicy cPolicy = new ClientPolicy();
		cPolicy.timeout = 500;
		this.client = new AerospikeClient(cPolicy, HOST, PORT);

		Key key = new Key("test", "Symbols", "allSymbols");
		Bin bin = new Bin(null, allSymbols);

		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		client.put(wPolicy, key, bin);
		if (this.client != null) {
			this.client.close();
		}
	}

	public void storeStock(Stock stock) {
		String jsonStock = JSON.toJSONString(stock);
		ClientPolicy cPolicy = new ClientPolicy();
		cPolicy.timeout = 500;
		this.client = new AerospikeClient(cPolicy, HOST, PORT);

		Key key = new Key("test", "Stocks", stock.getCode());
		Bin bin = new Bin(null, jsonStock);

		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		client.put(wPolicy, key, bin);
		if (this.client != null) {
			this.client.close();
		}
	}

	public Stock getStock(String code) {
		ClientPolicy cPolicy = new ClientPolicy();
		cPolicy.timeout = 500;

		this.client = new AerospikeClient(cPolicy, HOST, PORT);
		Key key = new Key("test", "Symbols", code);
		Record record = client.get(cPolicy.readPolicyDefault, key);
		String rs = record.getValue("").toString();
		System.out.println(rs);

		Stock stock = JSON.parseObject(rs, Stock.class);

		if (this.client != null) {
			this.client.close();
		}
		return stock;
	}

	public List<Stock> getAllSymbols() {
		ClientPolicy cPolicy = new ClientPolicy();
		cPolicy.timeout = 500;

		this.client = new AerospikeClient(cPolicy, HOST, PORT);
		Key key = new Key("test", "Symbols", "allSymbols");
		Record record = client.get(cPolicy.readPolicyDefault, key);
		String rs = record.getValue("").toString();
		System.out.println(rs);

		List<Stock> stocks = JSON.parseArray(rs, Stock.class);

		System.out.println(stocks);

		if (this.client != null) {
			this.client.close();
		}
		return stocks;
	}

}
