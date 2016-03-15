package stock.selector.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import stock.selector.dao.IStockDAO;
import stock.selector.model.Stock;
import stock.selector.util.SystemEnv;

@RestController
@RequestMapping("/stocks")
public class StockController {
	@Resource(name = "systemEnv")
	SystemEnv systemEnv = null;

	@Resource(name = "stockDAO4FileSystem")
	private IStockDAO stockDAO;

	public StockController() {
		System.out.println("sdf");
	}

	@RequestMapping("/getstock")
	public Stock getStock(
			@RequestParam(value = "code", defaultValue = "000001") String code) {
		Stock stock = stockDAO.getStock(code);
		return stock;
	}
}
