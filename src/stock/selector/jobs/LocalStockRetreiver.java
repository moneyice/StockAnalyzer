package stock.selector.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stock.selector.model.DailyInfo;
import stock.selector.model.Stock;
import stock.selector.util.Utils;

public class LocalStockRetreiver implements IStockRetreiver {
	String originalFilesRoot = "c:\\zd_zszq\\T0002\\export";
	String stockDataFolder = null;

	public String getStockDataFolder() {
		return stockDataFolder;
	}

	public void setStockDataFolder(String stockDataFolder) {
		this.stockDataFolder = stockDataFolder;
	}

	public void run() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter("./stock_codes.txt"));
			File[] files = new File(originalFilesRoot).listFiles();
			for (File file : files) {
				String fileName = file.getName();
				fileName = fileName.replace("SH", "SS");
				String stockCode = fileName.substring(2, 8) + "."
						+ fileName.substring(0, 2);

				pw.println(stockCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	public static void main(String[] args) {
		LocalStockRetreiver scr = new LocalStockRetreiver();
		scr.run();
	}

	@Override
	public List<Stock> getAllStockSymbols() throws IOException {
		return null;
	}

	@Override
	public Stock getStockInfo(Stock stock) throws IOException {
		// TODO Auto-generated method stub
		return null;
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
			// pass the line 数据来源：通达信
			if (input.indexOf(":") > 0) {
				continue;
			}
			result = input.split(",");
			DailyInfo daily = new DailyInfo();
			try {
				daily.setClose(Double.parseDouble(result[4]));
				daily.setLow(Double.parseDouble(result[3]));
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

	public File[] getFiles() {
		File root = new File(stockDataFolder);
		if (!root.isDirectory()) {
			return new File[0];
		}

		return root.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.indexOf("SH") > -1 || arg1.indexOf("SZ") > -1;
			}
		});
	}

	private String[] getAllSymbols() {
		List<String> list = new ArrayList<String>();
		String[] symbols = new String[] { "000001.SZ", "000002.SZ" };
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"./stock_codes.txt"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("6") || line.startsWith("0")
						|| line.startsWith("3")) {
					list.add(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.toArray(new String[list.size()]);
	}
}
