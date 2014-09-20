package stock.selector.process.formater;

import java.util.List;

import stock.selector.model.SelectResult;
import stock.selector.model.Stock;

public interface IResultFormater {

	public String format(List<? extends SelectResult> results, Stock stock);
}
