package stock.selector.process;

import java.util.ArrayList;
import java.util.List;

import stock.selector.model.DailyInfo;
import stock.selector.model.SelectResult;
import stock.selector.model.Stock;
import stock.selector.process.formater.YesterdayOnceMoreFormater;
import stock.selector.process.io.IResultWriter;

public class YesterdayOnceMoreAnalyzer extends AbstractStockAnalyzer {

	int daysToNow = 110;
	double range = 0.5;
	int rangeDays = 20;
	double highest = 0d;
	double lowest = 0.0d;

	DailyInfo current;
	double closeRangeTop = 0.3;
	double closeRangeDown = -0.5;

	public YesterdayOnceMoreAnalyzer() {
		setFormater(new YesterdayOnceMoreFormater());
	}

	@Override
	public void analyze(Stock stock) {
		results.clear();
		setStock(stock);

		if (daysToNow <= rangeDays) {
			throw new RuntimeException("考察天数要大于涨幅天数");
		}

		List<DailyInfo> infos = stock.getDailyinfo();
		if (infos.size() <= daysToNow) {
			// 元数据天数要求大于考察天数
			return;
		}
		current = infos.get(infos.size() - 1);

		for (int i = infos.size() - daysToNow - 1; i + rangeDays < infos.size(); i++) {
			if (infos.size() <= daysToNow) {
				continue;
			}

			DailyInfo from = infos.get(i);
			DailyInfo end = infos.get(i + rangeDays);

			boolean condition1 = (end.getClose() - from.getClose())
					/ from.getClose() >= range;

			double r = (current.getClose() - from.getClose()) / from.getClose();
			boolean condition2 = r >= closeRangeDown && r <= closeRangeTop;

			if (condition1 && condition2) {
				SelectResult result = new SelectResult();
				result.setStock(stock);
				result.setFrom(from);
				result.setTo(end);
				result.setNow(current);
				results.add(result);
				break;
			}
		}

		String data = getFormater().format(results, stock);
		if (data != null) {
			getResultwriter().write(data);
		}
	}

	public String getDescription() {
		return "只考虑" + daysToNow + "天内的股票，曾经在" + rangeDays + "天内涨幅超过"
				+ (range * 100) + "%，现值比前期低点涨幅大于 " + (closeRangeDown * 100)
				+ "%， 小于 " + (closeRangeTop * 100) + "%\n";
	}

	public int getDaysToNow() {
		return daysToNow;
	}

	public void setDaysToNow(int daysToNow) {
		this.daysToNow = daysToNow;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public int getRangeDays() {
		return rangeDays;
	}

	public void setRangeDays(int rangeDays) {
		this.rangeDays = rangeDays;
	}

	public double getCloseRangeTop() {
		return closeRangeTop;
	}

	public void setCloseRangeTop(double closeRangeTop) {
		this.closeRangeTop = closeRangeTop;
	}

	public double getCloseRangeDown() {
		return closeRangeDown;
	}

	public void setCloseRangeDown(double closeRangeDown) {
		this.closeRangeDown = closeRangeDown;
	}
}
