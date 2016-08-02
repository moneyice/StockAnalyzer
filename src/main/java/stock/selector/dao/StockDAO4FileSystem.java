package stock.selector.dao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import stock.selector.model.Stock;
import stock.selector.util.SystemEnv;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;

@Repository("stockDAO4FileSystem")
public class StockDAO4FileSystem implements IStockDAO {

	@Resource(name = "systemEnv")
	SystemEnv systemEnv = null;

	private String root;

	public StockDAO4FileSystem() {
	}

	private String getRootPath() {
		if (root == null) {
			root = systemEnv.getString("local.data.cache.folder");
		}
		return root;
	}

	public void storeAllSymbols(List<Stock> list) {
		String allSymbols = JSON.toJSONString(list);
		File to = new File(getRootPath(), "allSymbols");
		try {
			Files.write(allSymbols, to, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void storeStock(Stock stock) {
		String jsonStock = JSON.toJSONString(stock);
		File to = new File(getRootPath(), stock.getCode());
		try {
			Files.write(jsonStock, to, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stock getStock(String code) {
		File from = new File(getRootPath(), code);
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
		File from = new File(getRootPath(), "allSymbols");
		if(!from.exists()){
			from.mkdirs();
		}
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
		File file = new File(getRootPath(), code);
		long time = file.lastModified();
		Date lastDate = new Date(time);
		return lastDate;
	}

	public Date getAllSymbolsUpdateTime() {
		File file = new File(getRootPath(), "allSymbols");
		long time = file.lastModified();
		Date lastDate = new Date(time);
		return lastDate;
	}
}
