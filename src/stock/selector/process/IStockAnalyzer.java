package stock.selector.process;

import java.util.List;

import stock.selector.model.ResultInfo;
import stock.selector.model.Stock;
import stock.selector.process.io.IResultWriter;

public interface IStockAnalyzer {

	/**
	 * @param ri
	 * @param stock
	 * @return selected or not
	 */
	public boolean analyze(ResultInfo ri, Stock stock);

	public String getDescription();

	public void setResultwriter(IResultWriter resultwriter);

	public IResultWriter getResultwriter();

	public void outPutResults();
}
