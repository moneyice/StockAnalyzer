package stock.selector.process;

import org.junit.Test;

import stock.selector.model.Stock;

public class HistoryDataServiceTest {

	@Test
	public void testRealGetStockHistory() {
		AbstractStockAnalyzer analyzer=new YesterdayOnceMoreAnalyzer();
		HistoryDataService hs=new HistoryDataService();
		hs.addAnalyzer(analyzer);
		hs.startAnalyze();
	}
}
