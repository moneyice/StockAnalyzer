package stock.selector.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.sun.xml.internal.ws.api.PropertySet.Property;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import stock.selector.model.Stock;

@Repository("stockDAO4Redis")
public class StockDAO4Redis implements IStockDAO {
	private static final String K = "k";
	private static final String UPDATE_TIME = "update_time";
	public static String HOST = "192.168.56.102";
	public static int PORT = 6379;

	String ALL_SYMBOLS = "ALL_SYMBOLS";

	private JedisPool pool = new JedisPool(HOST, PORT);

	public StockDAO4Redis() {

	}

	public void storeAllSymbols(List<Stock> list) {
		String symbols = JSON.toJSONString(list);
		Jedis jedis = pool.getResource();
		String updateTime = JSON.toJSONString(new Date());
		Map<String, String> map = new HashMap<String, String>();
		map.put(ALL_SYMBOLS, symbols);
		map.put(UPDATE_TIME, updateTime);
		jedis.hmset(ALL_SYMBOLS, map);
		jedis.close();
	}

	public List<Stock> getAllSymbols() {
		Jedis jedis = pool.getResource();
		String symbols = jedis.hget(ALL_SYMBOLS, ALL_SYMBOLS);
		List<Stock> stocks = JSON.parseArray(symbols, Stock.class);
		jedis.close();
		return stocks;
	}

	public void storeStock(Stock stock) {
		String json = JSON.toJSONString(stock);
		Jedis jedis = pool.getResource();
		String updateTime = JSON.toJSONString(new Date());

		Map<String, String> map = new HashMap<String, String>();
		map.put(K, json);
		map.put(UPDATE_TIME, updateTime);
		jedis.hmset(stock.getCode(), map);
		jedis.close();
	}

	public Date getStockUpdateTime(String code) {
		Jedis jedis = pool.getResource();
		String updateTime = jedis.hget(code, UPDATE_TIME);
		Date date = JSON.parseObject(updateTime, Date.class);
		jedis.close();
		return date;
	}

	public Stock getStock(String code) {
		Jedis jedis = pool.getResource();
		String json = jedis.hget(code, K);

		Stock stock = JSON.parseObject(json, Stock.class);
		jedis.close();
		return stock;
	}

	public Date getAllSymbolsUpdateTime() {
		Jedis jedis = pool.getResource();
		String updateTime = jedis.hget(ALL_SYMBOLS, UPDATE_TIME);
		Date date = JSON.parseObject(updateTime, Date.class);
		jedis.close();
		return date;
	}
}
