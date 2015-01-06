package stock.selector.model;

import java.util.ArrayList;
import java.util.List;

public class Stock {
	List<DailyInfo> dailyinfo = new ArrayList<DailyInfo>();

	String code;
	String name;
	String market;

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public List<DailyInfo> getDailyinfo() {
		return dailyinfo;
	}

	public void setDailyinfo(List<DailyInfo> dailyinfo) {
		this.dailyinfo = dailyinfo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Stock [code=" + code + ", name=" + name + ", market=" + market
				+ "]";
	}
	
}
