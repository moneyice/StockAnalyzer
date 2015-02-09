package stock.selector.process;

import java.util.ArrayList;
import java.util.List;

import stock.selector.model.SelectResult;
import stock.selector.model.Stock;
import stock.selector.process.formater.IResultFormater;
import stock.selector.process.io.IResultWriter;

public abstract class AbstractStockAnalyzer implements IStockAnalyzer{

	private Stock stock;
	private IResultWriter resultwriter;
	private IResultFormater formater;
	
	protected List<SelectResult> results = new ArrayList<SelectResult>();
	
	public IResultFormater getFormater() {
		return formater;
	}

	public void setFormater(IResultFormater formater) {
		this.formater = formater;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}
	public IResultWriter getResultwriter() {
		return resultwriter;
	}

	public void setResultwriter(IResultWriter resultwriter) {
		this.resultwriter = resultwriter;
	}
	public void outPutResults(){
		getResultwriter().write(results);
}
}
