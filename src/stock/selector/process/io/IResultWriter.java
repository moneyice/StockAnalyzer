package stock.selector.process.io;

import java.util.List;

import stock.selector.model.ResultInfo;


public interface IResultWriter {
	public void write(String info);
	
	public void write (List<ResultInfo> list);
	
	public void end();
}
