package stock.benefitter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StockBenefitter {
	String dataFile = "C:\\security_flow\\对账单汇总.csv";

	public void run() {
		caculate();
	}

	public void caculate() {
		List<Benefit> list = getData();
		Map<String, List<Benefit>> map = new LinkedHashMap<String, List<Benefit>>();
		for (Benefit benefit : list) {
			List<Benefit> bs = map.get(benefit.getCode());
			if (bs == null) {
				bs = new ArrayList<Benefit>();
				map.put(benefit.getCode(), bs);
			}
			bs.add(benefit);
		}
		printResult(map);
	}

	private void printResult(Map<String, List<Benefit>> map) {
		for (Entry<String, List<Benefit>> entry : map.entrySet()) {
			System.out.println("========================================");
			for (Benefit b : entry.getValue()) {
				System.out.println(b);
			}
			printProfit(entry.getValue());
		}
	}

	private void printProfit(List<Benefit> value) {
		double profit = 0;
		double direction = 1;
		for (Benefit benefit : value) {
			if (benefit.getTransaction().equals("证券买入")
					|| benefit.getTransaction().equals("申购中签")) {
				direction = -1;
			} else if (benefit.getTransaction().equals("证券卖出")) {
				direction = 1;
			} else {
				continue;
			}
			profit += (Double.parseDouble(benefit.getTotal()) * direction);
		}
		System.out.println("利润：" + profit);
	}

	public List<Benefit> getData() {
		List<Benefit> list = new ArrayList<Benefit>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(dataFile));
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				Benefit b = new Benefit();
				b.setDate(data[0]);
				b.setTransaction(data[1]);
				b.setCode(data[2]);
				b.setName(data[3]);
				b.setPrice(data[4]);
				b.setTotal(data[6]);
				list.add(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static void main(String[] args) {
		StockBenefitter sbf = new StockBenefitter();
		sbf.run();
	}
}
