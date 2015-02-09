package stock.selector.process;

import java.util.ArrayList;
import java.util.List;

import stock.selector.model.DailyInfo;
import stock.selector.model.DemarkSelectResult;
import stock.selector.model.SelectResult;
import stock.selector.model.Stock;
import stock.selector.util.Utils;

//1、Buy-Setup：买入结构的条件是连续9T或以上的收盘价低于先前第4个T的收盘价，视为一个完整的买入结构。
//（注：这里的限定条件是“连续”9T或以上，期间不能中断）
//2、Buy-Countdown：每当某个收盘价低于先前第2个T的最低价时计数增加1，计数可以不连续，当计数增加到13个交易日意味着卖盘动能已经耗尽，往往是下跌趋势的反转点。
//（注：这里的限定条件是累计13个T，不要求是“连续”的，期间可以中断）
public class DemarkAnalyzer extends AbstractStockAnalyzer {
	int daysToNow = 100;
	// 连续9T或以上
	int buySetupDays = 9;
	// 收盘价低于先前第X个T的收盘价
	int buySetupBeforeDay = 4;

	//demark is 13
	int countDownNumber=13;
	private boolean setupReady = false;

	private List<DemarkSelect> selectList = null;

	public DemarkAnalyzer() {
	}

	@Override
	public void analyze(Stock stock) {
		setupReady = false;
		selectList = new ArrayList<DemarkSelect>();
		setStock(stock);

		List<DailyInfo> infos = stock.getDailyinfo();
		if (infos.size() <= daysToNow + buySetupBeforeDay) {
			// 元数据天数要求大于考察天数
			return;
		}

		int setupTimes = 0;

		for (int i = infos.size() - daysToNow - 1; i < infos.size(); i++) {
			if (infos.get(i).getClose() < infos.get(i - buySetupBeforeDay)
					.getClose()) {
				setupTimes++;
			} else {
				if (setupReady) {
					DemarkSelect sr = new DemarkSelect();
					sr.setStock(stock);
					// use now field to store the setup point info
					sr.setSetupPoint(infos.get(i - 1));
					sr.setSetupNumber(setupTimes);
					selectList.add(sr);
					setupReady = false;
				}
				setupTimes = 0;
			}
			if (setupTimes >= buySetupDays) {
				setupReady = true;
			}

		}

		prepareCountDownData();
		if(selectList.isEmpty()){
			return;
		}
		SelectResult sr = new SelectResult();
		sr.setStock(getStock());
		String msg = format(selectList, getStock());
		sr.setMsg(msg);
		results.add(sr);
	}

	private void prepareCountDownData() {
		if (selectList.isEmpty()) {
			return;
		}
		List<DailyInfo> infos = getStock().getDailyinfo();
		for (int i = 0; i < selectList.size(); i++) {
			findCountDownResult(selectList.get(i), infos);
		}
	}

	private void findCountDownResult(DemarkSelect demarkSelect,
			List<DailyInfo> infos) {
		int index = infos.indexOf(demarkSelect.getSetupPoint());
		int number = 0;
		DailyInfo countDownPoint = null;
		for (int i = index + 1; i < infos.size(); i++) {
			if (number >= countDownNumber) {
				break;
			}
			if (infos.get(i).getClose() < infos.get(i - 2).getLow()) {
				number++;
				countDownPoint = infos.get(i);
			}
		}
		demarkSelect.setCountDownNumber(number);
		// use to field to store the count down point info
		demarkSelect.setCountDownPoint(countDownPoint);
	}
	
	public String getDescription() {
		return "Demark 指标\n";
	}

	public String format(List<DemarkSelect> selectList, Stock stock) {
		StringBuilder sb = new StringBuilder();
		sb.append(stock.getCode()).append("  ").append(stock.getName())
				.append("\n");
		// 000001 平安银行
		// 2012-2-12 (9) --- 2012-3-30 (13)
		// 2013-1-12 (9) --- 2013-2-28 (13)
		for (int i = 0; i < selectList.size(); i++) {
			sb.append(Utils.format(selectList.get(i).getSetupPoint().getTime()))
					.append("(").append(selectList.get(i).getSetupNumber())
					.append(")  --- ");
			if (selectList.get(i).getCountDownPoint() != null) {
				sb.append(
						Utils.format(selectList.get(i).getCountDownPoint()
								.getTime())).append("(")
						.append(selectList.get(i).getCountDownNumber())
						.append(") ");
			}
			sb.append(" \n");
		}
		sb.append("\n");
		return sb.toString();
	}


	public class DemarkSelect {
		private int setupNumber = 0;
		private DailyInfo setupPoint = null;

		private DailyInfo countDownPoint = null;

		public DailyInfo getCountDownPoint() {
			return countDownPoint;
		}

		public void setCountDownPoint(DailyInfo countDownPoint) {
			this.countDownPoint = countDownPoint;
		}

		public DailyInfo getSetupPoint() {
			return setupPoint;
		}

		public void setSetupPoint(DailyInfo setupPoint) {
			this.setupPoint = setupPoint;
		}

		private int countDownNumber = 0;

		public int getCountDownNumber() {
			return countDownNumber;
		}

		public void setCountDownNumber(int countDownNumber) {
			this.countDownNumber = countDownNumber;
		}

		public int getSetupNumber() {
			return setupNumber;
		}

		public void setSetupNumber(int setupNumber) {
			this.setupNumber = setupNumber;
		}

		Stock stock;

		String msg;

		public Stock getStock() {
			return stock;
		}

		public void setStock(Stock stock) {
			this.stock = stock;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

	}
}
