package stock.selector.process.io;

import java.io.File;
import java.io.IOException;

import stock.selector.model.SelectResult;
import stock.selector.util.Utils;

public class HtmlFileResultWriter extends FileResultWriter {

	public HtmlFileResultWriter(File file) throws IOException {
		super(file);
		write("<html><head></head><body> <table>");
	}


	public void write(SelectResult result) {
		if (result != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("<tr><td>");
			sb.append(result.getStock().getCode());
			sb.append("</td>");
			
			sb.append("<td>");
			sb.append(result.getStock().getName());
			sb.append("</td>");
			
			sb.append("<td>");
			sb.append(result.getMsg());
			sb.append("</td></tr>");
			write(sb.toString());
		}

	}
	
	public void end() {
		write(" </table></body></html>");
	}
}
