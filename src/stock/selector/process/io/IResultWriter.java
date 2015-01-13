package stock.selector.process.io;

import java.util.List;

import stock.selector.model.SelectResult;


public interface IResultWriter {
	public void write(String info);
	
	public void write (List<SelectResult> list);
	
	public void end();
}
