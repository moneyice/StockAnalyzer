package stock.selector.process;

import java.util.List;

import stock.selector.model.DailyInfo;
import stock.selector.model.DemarkSelectResult;
import stock.selector.model.Stock;
import stock.selector.process.formater.DemarkResultFormater;

public class DemarkAnalyzer extends AbstractStockAnalyzer {
	int daysToNow = 10;
	// 连续9T或以上
	int buySetupDays = 9;
	// 收盘价低于先前第X个T的收盘价
	int buySetupBeforeDay = 4;

	private boolean setupReady = false;

	public DemarkAnalyzer() {
		setFormater(new DemarkResultFormater());
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

}
