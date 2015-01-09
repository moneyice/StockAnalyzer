package stock.selector.dao;

import java.util.Date;
import java.util.List;

import stock.selector.model.Stock;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.alibaba.fastjson.JSON;

public class StockDAO4Aerospike implements IStockDAO {
	public static String HOST = "172.20.2.19";
	public static int PORT = 3000;
	private AerospikeClient client;

	public StockDAO4Aerospike() {
		ClientPolicy cPolicy = new ClientPolicy();
		cPolicy.timeout = 500;
		this.client = new AerospikeClient(cPolicy, HOST, PORT);
	}

	public void storeAllSymbols(List<Stock> list) {
		String allSymbols = JSON.toJSONString(list);

		Key key = new Key("test", "Symbols", "allSymbols");
		Bin bin = new Bin(null, allSymbols);
		String update_time=JSON.toJSONString(new Date());
		Bin bin2=new Bin("update_time",update_time);
		
		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		client.put(wPolicy, key, bin,bin2);
	}

	public void storeStock(Stock stock) {
		String jsonStock = JSON.toJSONString(stock);

		Key key = new Key("test", "Stocks", stock.getCode());
		Bin bin = new Bin("k", jsonStock);
		String update_time=JSON.toJSONString(new Date());
		Bin bin2=new Bin("update_time",update_time);

		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		client.put(wPolicy, key, bin,bin2);

	}

	public Date getStockUpdateTime(String code){
		Key key = new Key("test", "Stocks", code);
		Policy readPolicyDefault = new Policy();
		Record record = client.get(readPolicyDefault, key);
		if(record==null){
			return null;
		}
		Object rs = record.getValue("update_time");
		if (rs == null) {
			return null;
		}

		Date date = JSON.parseObject(rs.toString(), Date.class);
		return date;
	}
	
	public Stock getStock(String code) {
		Key key = new Key("test", "Stocks", code);
		Policy readPolicyDefault = new Policy();
		Record record = client.get(readPolicyDefault, key);
		if(record==null){
			return null;
		}
		Object rs = record.getValue("k");
		if (rs == null) {
			return null;
		}

		Stock stock = JSON.parseObject(rs.toString(), Stock.class);

		return stock;
	}

	public List<Stock> getAllSymbols() {
		Key key = new Key("test", "Symbols", "allSymbols");
		Policy readPolicyDefault = new Policy();
		Record record = client.get(readPolicyDefault, key);
		if(record==null){
			return null;
		}
		Object rs = record.getValue("");
		if(rs==null){
			return null;
		}
		
		List<Stock> stocks = JSON.parseArray(rs.toString(), Stock.class);

		return stocks;
	}

	public Date getAllSymbolsUpdateTime(){
		Key key = new Key("test", "Symbols", "allSymbols");
		Policy readPolicyDefault = new Policy();
		Record record = client.get(readPolicyDefault, key);
		if(record==null){
			return null;
		}
		Object rs = record.getValue("update_time");
		if (rs == null) {
			return null;
		}

		Date date = JSON.parseObject(rs.toString(), Date.class);
		return date;
		
	}
	
	public void close() {
		if (this.client != null) {
			this.client.close();
		}
	}
}
