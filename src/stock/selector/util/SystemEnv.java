package stock.selector.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component("systemEnv")
public class SystemEnv {
	Properties props = null;

	public SystemEnv() {
		try {
			props = new Properties();
			props.load(new FileReader("analyzer.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int getInt(String key) {
		return Integer.parseInt(props.getProperty(key));
	}

	public double getDouble(String key) {
		return Double.parseDouble(props.getProperty(key));
	}

	public String getString(String key) {
		return props.getProperty(key);
	}
}
