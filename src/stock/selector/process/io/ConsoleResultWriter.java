package stock.selector.process.io;

import java.util.List;

import stock.selector.model.SelectResult;


public class ConsoleResultWriter implements IResultWriter {

	public void write(String content) {
		System.out.print(content);
	}

	public void end() {
	}

	public void write(List<SelectResult> list) {
		for (SelectResult selectResult : list) {
			write(selectResult.getMsg());
		}
		
	}
	
	
}
