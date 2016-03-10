package stock.selector.process;

import java.util.List;

import stock.selector.model.SelectResult;
import stock.selector.model.Stock;
import stock.selector.process.io.IResultWriter;

public interface IStockAnalyzer {

	public void analyze(Stock stock);

	public String getDescription();

	public void setResultwriter(IResultWriter resultwriter);

	public IResultWriter getResultwriter();

	public void outPutResults();

	public List<SelectResult> getResults();
}
