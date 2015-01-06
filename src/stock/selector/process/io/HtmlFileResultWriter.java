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
			sb.append(result.getFrom().getClose()).append("(").append(Utils.format(result.getFrom().getTime())).append(")");
			sb.append("</td>");
			
			sb.append("<td>");
			sb.append(result.getTo().getClose()).append("(").append(Utils.format(result.getTo().getTime())).append(")");
			sb.append("</td>");
			
			sb.append("<td>");
			sb.append(
					"涨幅："
							+ (result.getTo().getClose() - result.getFrom()
									.getClose()) / result.getFrom().getClose()
							* 100);
			sb.append("</td>");
			
			sb.append("<td>");
			sb.append("现价: ").append(result.getNow());
			sb.append("</td></tr>");
			write(sb.toString());
		}

	}
	
	public void end() {
		write(" </table></body></html>");
	}
}
