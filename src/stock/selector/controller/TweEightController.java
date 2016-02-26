package stock.selector.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import stock.selector.util.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;

@RestController
public class TweEightController {
	static String historyURL = "http://quotes.money.163.com/service/chddata.html?code=#{symbol}&start=20140101&&fields=TOPEN;HIGH;LOW;TCLOSE;VOTURNOVER";
	static String currentURL = "http://api.money.126.net/data/feed/#{symbol},money.api";

	@RequestMapping("/28")
	public String greeting(
			@RequestParam(value = "name", defaultValue = "World") String name) {
		String result = null;
		DecimalFormat df = new DecimalFormat("######0.00");
		try {
			// 沪深300
			double current300 = getCurrentIndex("0000300");
			double history300 = getIndexOf4WeeksAgo("0000300");

			// 中证500
			double current500 = getCurrentIndex("0000905");
			double history500 = getIndexOf4WeeksAgo("0000905");

			result = "300" + "     "
					+ df.format((current300 / history300 - 1) * 100) + "%";
			result = result + "-------------------------";
			result = result + "500" + "      "
					+ df.format((current500 / history500 - 1) * 100) + "%";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("163 process error ");
		}
		return result;
	}

	private double getCurrentIndex(String symbol) throws IOException {
		String url = currentURL.replace("#{symbol}", symbol);
		String json = Resources
				.toString(new URL(url), Charset.forName("UTF-8"));
		json = json.replaceAll("_ntes_quote_callback\\(", "").replaceAll(
				"\\);", "");

		System.out.println(json);
		JSONObject jsonObj = JSON.parseObject(json);
		jsonObj = jsonObj.getJSONObject(symbol);

		System.out.println(jsonObj);
		double index = jsonObj.getDouble("price");
		return index;
	}

	private double getIndexOf4WeeksAgo(String symbol) throws IOException {
		String url = historyURL.replace("#{symbol}", symbol);
		List<String> list = Resources.readLines(new URL(url),
				Charset.forName("UTF-8"));

		String info = list.get(20);
		String[] result = info.split(",");
		double close = Utils.handleDouble(result[3]);
		return close;
	}

	public static void main(String[] args) {
		try {
			String url = currentURL.replace("#{symbol}", "0000300");
			String json = Resources.toString(new URL(url),
					Charset.forName("UTF-8"));
			json = json.replaceAll("_ntes_quote_callback\\(", "").replaceAll(
					"\\);", "");

			System.out.println(json);
			JSONObject jsonObj = JSON.parseObject(json);
			System.out.println(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
