package stock.selector.dao;

import java.util.List;

import stock.selector.model.Stock;

public interface IStockDAO {
	public void storeAllSymbols(List<Stock> list);

	public void storeStock(Stock stock);

	public Stock getStock(String code);

	public List<Stock> getAllSymbols();
}
