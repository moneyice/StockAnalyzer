package stock.selector.dao;

import java.util.Date;
import java.util.List;

import stock.selector.model.Stock;

public interface IStockDAO {
	public void storeAllSymbols(List<Stock> list);
	public Date getStockUpdateTime(String code);
	public void storeStock(Stock stock);

	public Stock getStock(String code);

	public List<Stock> getAllSymbols();
	public Date getAllSymbolsUpdateTime();
}
