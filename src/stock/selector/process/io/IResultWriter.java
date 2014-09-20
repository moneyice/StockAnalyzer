package stock.selector.process.io;


public interface IResultWriter {
	public void write(String info);
	
	public void end();
}
