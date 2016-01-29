package stock.selector.dao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import stock.selector.model.Stock;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;

public class StockDAO4FileSystem implements IStockDAO {
	private String root;

	public StockDAO4FileSystem(String root) {
		this.root = root;
	}

	public void storeAllSymbols(List<Stock> list) {
		String allSymbols = JSON.toJSONString(list);
		File to = new File(root, "allSymbols");
		try {
			Files.write(allSymbols, to, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void storeStock(Stock stock) {
		String jsonStock = JSON.toJSONString(stock);
		File to = new File(root, stock.getCode());
		try {
			Files.write(jsonStock, to, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stock getStock(String code) {
		File from = new File(root, code);
		String rs;
		try {
			rs = Files.readFirstLine(from, Charset.forName("UTF-8"));
			Stock stock = JSON.parseObject(rs.toString(), Stock.class);
			return stock;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("code error" + code);

		}
		return null;

	}

	public List<Stock> getAllSymbols() {
		File from = new File(root, "allSymbols");
		String rs;
		try {
			rs = Files.readFirstLine(from, Charset.forName("UTF-8"));
			List<Stock> stocks = JSON.parseArray(rs.toString(), Stock.class);
			return stocks;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public Date getStockUpdateTime(String code) {
		File file = new File(root, code);
		long time = file.lastModified();
		Date lastDate = new Date(time);
		return lastDate;
	}

	public Date getAllSymbolsUpdateTime() {
		File file = new File(root, "allSymbols");
		long time = file.lastModified();
		Date lastDate = new Date(time);
		return lastDate;
	}

	public static void main(String[] args) {
		IStockDAO dao = new StockDAO4FileSystem(
				"/Users/moneyice/code/stock-cache/");
		List<Stock> list = dao.getAllSymbols();

		for (Stock stock : list) {
			long start = System.currentTimeMillis();
			stock = dao.getStock(stock.getCode());
			long end = System.currentTimeMillis();
			System.out.println("cost " + (end - start));
		}
	}

}
