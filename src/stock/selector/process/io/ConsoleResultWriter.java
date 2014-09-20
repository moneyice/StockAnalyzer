package stock.selector.process.io;


public class ConsoleResultWriter implements IResultWriter {

	@Override
	public void write(String content) {
		System.out.print(content);
	}

	@Override
	public void end() {
	}
}
