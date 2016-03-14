package stock.selector.process.io;

import java.util.List;

import stock.selector.model.ResultInfo;


public class ConsoleResultWriter implements IResultWriter {

	public void write(String content) {
		System.out.print(content);
	}

	public void end() {
	}

	public void write(List<ResultInfo> list) {
		for (ResultInfo selectResult : list) {
			write(selectResult.getMsg());
		}
		
	}
	
	
}
