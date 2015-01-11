package stock.selector.process;

import java.util.List;

import stock.selector.model.DailyInfo;
import stock.selector.model.DemarkSelectResult;
import stock.selector.model.SelectResult;
import stock.selector.model.Stock;
import stock.selector.process.formater.DemarkResultFormater;
import stock.selector.util.Utils;

public class DemarkAnalyzer extends AbstractStockAnalyzer {
	int daysToNow = 10;
	// 连续9T或以上
	int buySetupDays = 9;
	// 收盘价低于先前第X个T的收盘价
	int buySetupBeforeDay = 4;

	private boolean setupReady = false;

	public DemarkAnalyzer() {
	}

	@Override
	public void analyze(Stock stock) {
		setupReady = false;
		results.clear();
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
					DemarkSelectResult sr = new DemarkSelectResult();
					sr.setStock(stock);
					// use now field to store the setup point info
					sr.setNow(infos.get(i - 1));
					sr.setSetupNumber(setupTimes);
					results.add(sr);
					setupReady = false;
				}
				setupTimes = 0;
			}
			if (setupTimes >= buySetupDays) {
				setupReady = true;
			}

		}

		prepareCountDownData();

		String data = getFormater().format(results, stock);
		if (data != null) {
			getResultwriter().write(data);
		}
	}

	private void prepareCountDownData() {
		if (results.isEmpty()) {
			return;
		}
		List<DailyInfo> infos = getStock().getDailyinfo();
		for (int i = 0; i < results.size(); i++) {
			findCountDownResult((DemarkSelectResult) results.get(i), infos);
		}

	}

	private void findCountDownResult(DemarkSelectResult demarkSelectResult,
			List<DailyInfo> infos) {
		int index = infos.indexOf(demarkSelectResult.getNow());
		int number = 0;
		DailyInfo to = null;
		for (int i = index + 1; i < infos.size(); i++) {
			if (number >= 13) {
				break;
			}
			if (infos.get(i).getClose() < infos.get(i - 2).getLow()) {
				number++;
				to = infos.get(i);
			}
		}
		demarkSelectResult.setCountDownNumber(number);
		// use to field to store the count down point info
		demarkSelectResult.setTo(to);
	}

	public String getDescription() {
		return "Demark 指标\n";
	}
	public String format(List<? extends SelectResult> list, Stock stock) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		List<DemarkSelectResult> results = (List<DemarkSelectResult>) list;
		StringBuilder sb = new StringBuilder();
		sb.append(stock.getCode()).append("  ").append(stock.getName())
				.append("\n");
		// 000001 平安银行
		// 2012-2-12 (9) --- 2012-3-30 (13)
		// 2013-1-12 (9) --- 2013-2-28 (13)
		for (int i = 0; i < results.size(); i++) {
			sb.append(Utils.format(results.get(i).getNow().getTime()))
					.append("(").append(results.get(i).getSetupNumber())
					.append(")  --- ");
			if (results.get(i).getTo() != null) {
				sb.append(Utils.format(results.get(i).getTo().getTime()))
						.append("(")
						.append(results.get(i).getCountDownNumber())
						.append(") ");
			}
			sb.append(" \n");
		}
		sb.append("\n");
		return sb.toString();
	}
}
