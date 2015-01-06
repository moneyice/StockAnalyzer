package stock.selector.process.formater;

import java.util.List;

import stock.selector.model.SelectResult;
import stock.selector.model.Stock;
import stock.selector.util.Utils;

public class YesterdayOnceMoreFormater implements IResultFormater {

	@Override
	public String format(List<? extends SelectResult> results, Stock stock) {
		if (results == null || results.isEmpty()) {
			return null;
		}
		SelectResult result = results.get(0);

		StringBuilder sb = new StringBuilder();
		sb.append(result.getStock().getCode()).append("  ")
				.append(result.getStock().getName()).append("\n");
		sb.append(Utils.format(result.getFrom().getTime())).append("  ")
				.append(result.getFrom().getClose()).append("  --->   ");
		sb.append(Utils.format(result.getTo().getTime())).append("  ")
				.append(result.getTo().getClose());
		sb.append("\n");
		sb.append(
				"涨幅："
						+ (result.getTo().getClose() - result.getFrom()
								.getClose()) / result.getFrom().getClose()
						* 100).append("%\n");
		sb.append("现价: ").append(result.getNow().getClose()).append("\n\r");
		return (sb.toString());
	}
}
