package stock.selector.process.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import stock.selector.model.SelectResult;

public class FileResultWriter implements IResultWriter {
	PrintWriter pw =null;
	
	public FileResultWriter(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		 pw = new PrintWriter(new FileWriter(file,true),true);
	}

	public void write(String content) {
		pw.println(content);
	}

	public void end() {
		pw.close();
	}

	public void write(List<SelectResult> list) {
		for (SelectResult selectResult : list) {
			write(selectResult.getMsg());
		}
		
	}
}
