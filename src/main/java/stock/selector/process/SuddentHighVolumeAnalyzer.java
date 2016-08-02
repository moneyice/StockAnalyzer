package stock.selector.process;

import java.util.List;

import stock.selector.model.DailyInfo;
import stock.selector.model.ResultInfo;
import stock.selector.model.Stock;
import stock.selector.process.io.HtmlFileResultWriter;
import stock.selector.util.Utils;

//n天之内某一天成交量是前一天的  x 倍
public class SuddentHighVolumeAnalyzer extends AbstractStockAnalyzer {

	// consider how many days before now.
	int daysToNow = 5;

	// volume increase in times
	double volumeIncrease = 2.5;

	double[] riseRange = { 0.001, 0.06 };

	public SuddentHighVolumeAnalyzer() {
	}

	public boolean analyze(ResultInfo resultInfo, Stock stock) {
		boolean ok = false;
		List<DailyInfo> infos = stock.getDailyinfo();
		if (infos.size() < daysToNow + 1) {
			// 元数据天数要求大于考察天数+1
			return ok;
		}
		for (int i = infos.size() - daysToNow; i < infos.size(); i++) {
			DailyInfo toCheck = infos.get(i);

			DailyInfo compare = infos.get(i - 1);
			double times = ((double) toCheck.getVolume() / compare.getVolume());
			times = Utils.get2Double(times);
			boolean condition = times >= volumeIncrease;
			double risePercentage = (toCheck.getClose() - compare.getClose())
					/ toCheck.getClose();

			boolean condition2 = risePercentage >= riseRange[0]
					&& risePercentage <= riseRange[1];
			if (condition && condition2) {
				String msg = format(stock, toCheck, times);
				resultInfo.appendMessage(msg);
				ok = true;
				break;
			}
		}
		return ok;
	}

	private String format(Stock stock, DailyInfo check, double times) {
		StringBuilder sb = new StringBuilder();
		sb.append(stock.getCode()).append("  ").append(stock.getName())
				.append("\n");
		sb.append("时间：").append(check.getTime()).append("\n");
		sb.append("成交量放大倍数：" + times).append("\n");
		sb.append("现价: ").append(getCurrentPrice(stock)).append("\n\r");
		return (sb.toString());
	}

	public String getDescription() {
		return daysToNow + "天之内成交量是前一天的";
	}

	public int getDaysToNow() {
		return daysToNow;
	}

	public void setDaysToNow(int daysToNow) {
		this.daysToNow = daysToNow;
	}

	public static void main(String[] args) {
		double decrease = -2;
		double decreaseReage = -2.7;
		boolean condition1 = decreaseReage * 100 <= decrease;
		System.out.println(condition1);
	}
}
