package stock.selector.process.formater;

import java.util.ArrayList;
import java.util.List;

import stock.selector.model.DemarkSelectResult;
import stock.selector.model.SelectResult;
import stock.selector.model.Stock;
import stock.selector.util.Utils;

public class DemarkResultFormater implements IResultFormater {

	public String format(List<? extends SelectResult> list, Stock stock) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		List<DemarkSelectResult> results = (List<DemarkSelectResult>) list;
		StringBuilder sb = new StringBuilder();
		sb.append(stock.getCode()).append("  ").append(stock.getSymbol())
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
