package stock.selector.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stock.selector.model.DailyInfo;
import stock.selector.model.Stock;
import stock.selector.util.Utils;

public class StockDAO4FileSystem implements IStockDAO {
	private String root;

	public StockDAO4FileSystem(String root){
		this.root=root;
	}
	
	public void storeAllSymbols(List<Stock> list) {
		
	}

	public void storeStock(Stock stock) {
		
	}

	public Stock getStock(String code) {
		if(code.startsWith("6")){
			code="SH"+code+".txt";
		}else{
			code="SZ"+code+".txt";
		}
		File file=new File(root,code);
		
		 Stock stock=null;
		try {
			stock = getStockHistory(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return stock;
		
		
		
	}
	
	public Stock getStockHistory(File file) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "GBK"));
		String input = null;
		input = br.readLine();
		// first line
		if (input == null) {
			return null;
		}
		String[] result = input.split(" ");
		Stock stock = new Stock();
		stock.setCode(result[0]);
		stock.setName(result[1]);

		br.readLine();// pass the second line

		while ((input = br.readLine()) != null) {
			// pass the line 日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
			if (input.indexOf(":") > 0) {
				continue;
			}
			result = input.split(",");
			DailyInfo daily = new DailyInfo();
			try {
				daily.setOpen(Double.parseDouble(result[1]));
				daily.setHigh(Double.parseDouble(result[2]));
				daily.setClose(Double.parseDouble(result[4]));
				daily.setLow(Double.parseDouble(result[3]));
				daily.setVolume(Long.parseLong(result[5]));
				Date time = Utils.format(result[0]);
				daily.setTime(time);
				stock.getDailyinfo().add(daily);
			} catch (Exception e) {
				e.printStackTrace();
			}

			
		}
		br.close();
		return stock;
	}
	public List<Stock> getAllSymbols() {
		File base=new File(root);
		File[] files=base.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.indexOf("SH") > -1 || arg1.indexOf("SZ") > -1;
			}
		});
		List<Stock> list=new ArrayList<Stock>();
		
		for (File file : files) {
			String name=file.getName();
			Stock stock=new Stock();
			stock.setMarket(name.substring(0,2));
			stock.setCode(name.substring(2,8));
			list.add(stock	);
		}
		return list;
	}

}
