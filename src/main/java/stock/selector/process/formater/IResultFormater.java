package stock.selector.process.formater;

import java.util.List;

import stock.selector.model.ResultInfo;
import stock.selector.model.Stock;

public interface IResultFormater {

	public String format(List<? extends ResultInfo> results, Stock stock);
}
