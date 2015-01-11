package stock.selector.process.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import stock.selector.model.SelectResult;
import stock.selector.util.Utils;

public class FileResultWriter implements IResultWriter {
	PrintWriter pw =null;
	
	public FileResultWriter(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		 pw = new PrintWriter(new FileWriter(file,true),true);
	}

	@Override
	public void write(String content) {
		pw.println(content);
	}

	@Override
	public void end() {
		pw.close();
	}
}
