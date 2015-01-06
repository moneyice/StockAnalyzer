package stock.selector.calculator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import stock.selector.model.DailyInfo;
import stock.selector.model.Stock;

public class BenefitCalculator {
	public static void main(String[] args) {
		try {
			Class.forName("org.relique.jdbc.csv.CsvDriver");
			Connection conn = DriverManager
					.getConnection("jdbc:relique:csv:./");

			// create a scrollable Statement so we can move forwards and
			// backwards
			// through ResultSets
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT code,name,inout,money FROM qlzq_benefit1 order by code");

			Map<String, Stock> stocks = new HashMap<String, Stock>();
			while (rs.next()) {
				String name = rs.getString("name");
				String code = rs.getString("code");

				Stock stock = stocks.get(code);
				if (stock == null) {
					stock = new Stock();
					stock.setCode(code);
					stock.setName(name);
					stocks.put(code, stock);
				}
				DailyInfo di = new DailyInfo();
				Double money=Double.valueOf(rs.getString("money"));
				if(rs.getString("inout").equals("证券买入")){
					money=money*-1;
				}
				di.setClose(money);
				stock.getDailyinfo().add(di);
			}
			// clean up
			rs.close();
			stmt.close();
			conn.close();
			
			double sum=0;
			for (Map.Entry<String, Stock> entry : stocks.entrySet()) {
				Stock stock=entry.getValue();
				if(stock.getDailyinfo().size()==1){
					continue;
				}
				for (DailyInfo di : stock.getDailyinfo()) {
					System.out.println(stock.getName() + "  "+ di.getClose());
					sum+=di.getClose();
				}
			}
			
			System.out.println("Benefit is "+sum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
