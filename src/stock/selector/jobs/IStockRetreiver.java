package stock.selector.jobs;

import java.io.IOException;
import java.util.List;

import stock.selector.model.Stock;

public interface IStockRetreiver {
	public List<Stock> getAllStockSymbols() throws IOException;

	public Stock getStockInfo(Stock stock) throws IOException;
}