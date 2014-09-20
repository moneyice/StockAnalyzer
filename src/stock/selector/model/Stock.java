package stock.selector.model;

import java.util.ArrayList;
import java.util.List;

public class Stock {
	List<DailyInfo> dailyinfo = new ArrayList<DailyInfo>();

	String code;
	String symbol;

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

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
