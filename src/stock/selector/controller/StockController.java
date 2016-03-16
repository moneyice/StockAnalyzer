package stock.selector.controller;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import stock.selector.dao.IStockDAO;
import stock.selector.jobs.StockInfoSpider;
import stock.selector.model.DailyInfo;
import stock.selector.model.Stock;
import stock.selector.util.SystemEnv;
import stock.selector.util.Utils;

@RestController
@RequestMapping("/stocks")
public class StockController {
	@Resource(name = "systemEnv")
	SystemEnv systemEnv = null;

	@Resource(name = "stockInfoSpider")
	private StockInfoSpider stockInfoSpider = null;

	@Resource(name = "stockDAO4FileSystem")
	private IStockDAO stockDAO;

	public StockController() {
	}

	@RequestMapping("/stock")
	public String getStock(
			@RequestParam(value = "code", defaultValue = "000001") String code) {
		Stock stock = stockDAO.getStock(code);
		DailyInfo info = stock.getDailyinfo().get(
				stock.getDailyinfo().size() - 1);

		return stock.getCode() + "\n " + stock.getName() + "\n "
				+ info.getClose() + "\n " + Utils.format(info.getTime());
	}

	@RequestMapping("/force_refresh_stocks")
	public void retrieveStockDailyData() {
		stockInfoSpider.setCheckOutOfDate(false);
		stockInfoSpider.run();
	}
}
