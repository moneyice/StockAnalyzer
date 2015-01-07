package stock.selector.jobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import stock.selector.model.Stock;

public class LocalStockRetreiver implements IStockRetreiver {
	String originalFilesRoot = "c:\\zd_zszq\\T0002\\export";

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

}
