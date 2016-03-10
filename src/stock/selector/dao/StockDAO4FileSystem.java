package stock.selector.dao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import stock.selector.jobs.StockInfoSpider;
import stock.selector.model.Stock;
import stock.selector.util.SystemEnv;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;

@Repository("stockDAO4FileSystem")
public class StockDAO4FileSystem implements IStockDAO {
	
	@Resource(name = "systemEnv")
	SystemEnv systemEnv = null;

	private String root;

	@Resource(name = "stockInfoSpider")
	private StockInfoSpider stockInfoSpider = null;
	
	public StockDAO4FileSystem() {
		System.out.println("tet");
	}

	public void storeAllSymbols(List<Stock> list) {
		this.root = systemEnv.getString("local.data.cache.folder");
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
}
