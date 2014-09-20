package stock.selector.jobs;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class StockCodeRetreiver {
	String originalFilesRoot = "c:\\zd_zszq\\T0002\\export";

	public void run() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter("./stock_codes.txt"));
			File[] files = new File(originalFilesRoot).listFiles();
			for (File file : files) {
				String fileName = file.getName();
				fileName = fileName.replace("SH", "SS");
				String stockCode = fileName.substring(2, 8)+"."
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
		StockCodeRetreiver scr = new StockCodeRetreiver();
		scr.run();
	}

}
