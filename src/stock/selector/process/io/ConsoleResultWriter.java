package stock.selector.process.io;

import java.util.List;

import stock.selector.model.SelectResult;


public class ConsoleResultWriter implements IResultWriter {

	@Override
	public void write(String content) {
		System.out.print(content);
	}

	@Override
	public void end() {
	}

	@Override
	public void write(List<SelectResult> list) {
		for (SelectResult selectResult : list) {
			write(selectResult.getMsg());
		}
		
	}
	
	
}
