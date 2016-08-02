package stock.selector.process;

import java.util.List;

import stock.selector.model.DailyInfo;
import stock.selector.model.ResultInfo;
import stock.selector.model.Stock;
import stock.selector.process.io.HtmlFileResultWriter;
import stock.selector.util.Utils;

//n天之内某一天成交量是前一天的  x 倍
public class PriceLimitAnalyzer extends AbstractStockAnalyzer {

	private double maxPrice = 10;
	private double minPrice = 0;

	public PriceLimitAnalyzer() {
	}

	public boolean analyze(ResultInfo resultInfo, Stock stock) {
		boolean ok = false;
		DailyInfo toCheck = stock.getDailyinfo().get(
				stock.getDailyinfo().size() - 1);

		if (toCheck.getClose() >= minPrice && toCheck.getClose() <= maxPrice) {
			ok = true;
			resultInfo.appendMessage("");
		}
		return ok;
	}

	private String format(Stock stock, DailyInfo check, double times) {
		return "";
	}

	public static void main(String[] args) {
		double decrease = -2;
		double decreaseReage = -2.7;
		boolean condition1 = decreaseReage * 100 <= decrease;
		System.out.println(condition1);
	}

	@Override
	public String getDescription() {
		return null;
	}
}
