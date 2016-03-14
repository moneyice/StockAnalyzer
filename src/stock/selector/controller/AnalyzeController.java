package stock.selector.controller;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import stock.selector.dao.IStockDAO;
import stock.selector.model.ResultInfo;
import stock.selector.process.PriceLimitAnalyzer;
import stock.selector.process.StockSelectService;
import stock.selector.process.SuddentHighVolumeAnalyzer;
import stock.selector.process.YesterdayOnceMoreAnalyzer;
import stock.selector.process.io.ConsoleResultWriter;
import stock.selector.process.io.HtmlFileResultWriter;
import stock.selector.process.io.IResultWriter;
import stock.selector.util.SystemEnv;

import com.alibaba.fastjson.JSON;

@RestController
public class AnalyzeController {
	@Resource(name = "systemEnv")
	SystemEnv systemEnv = null;

	@Resource(name = "stockDAO4FileSystem")
	private IStockDAO stockDAO;

	public AnalyzeController() {
		System.out.println("sdf");
	}

	@RequestMapping("/yesterday")
	public String runYOMAnalyzer(
			@RequestParam(value = "name", defaultValue = "World") String name) {

		YesterdayOnceMoreAnalyzer analyzer = new YesterdayOnceMoreAnalyzer();
		analyzer.setDaysToNow(systemEnv.getInt("YOM.analyzer.daysToNow"));
		analyzer.setRangeDays(systemEnv.getInt("YOM.analyzer.rangeDays"));
		analyzer.setRange(systemEnv.getDouble("YOM.analyzer.range"));
		analyzer.setCloseRangeDown(systemEnv
				.getDouble("YOM.analyzer.closeRangeDown"));
		analyzer.setCloseRangeTop(systemEnv
				.getDouble("YOM.analyzer.closeRangeTop"));

		StockSelectService hs = new StockSelectService();

		hs.setStockDAO(stockDAO);

		hs.addAnalyzer(analyzer);

		hs.startAnalyze();

		List<ResultInfo> result = hs.getSelectResultList();

		StringBuilder sb = new StringBuilder();
		for (ResultInfo selectResult : result) {
			sb.append(selectResult.getMsg()).append("<br/><br/>");
		}

		return sb.toString();

		// try {
		// getResultWriter().write(result);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	@RequestMapping("/high_volume")
	public String runSuddentHighVolumeAnalyzer(
			@RequestParam(value = "name", defaultValue = "World") String name) {
		StockSelectService hs = new StockSelectService();
		hs.setStockDAO(stockDAO);
		{
			SuddentHighVolumeAnalyzer analyzer = new SuddentHighVolumeAnalyzer();
			hs.addAnalyzer(analyzer);
		}
		{
			PriceLimitAnalyzer analyzer = new PriceLimitAnalyzer();
			hs.addAnalyzer(analyzer);
		}

		hs.startAnalyze();

		List<ResultInfo> result = hs.getSelectResultList();

		String json = JSON.toJSONString(result);

		StringBuilder sb = new StringBuilder();
		for (ResultInfo selectResult : result) {
			sb.append(selectResult.getMsg()).append("<br/><br/>");
		}

		return sb.toString();

	}

	public IResultWriter getResultWriter() throws IOException {
		Calendar cal = Calendar.getInstance();
		String filename = "" + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH)
				+ cal.get(Calendar.DAY_OF_MONTH) + "_"
				+ cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE)
				+ cal.get(Calendar.SECOND) + ".html";
		File root = new File("result");
		IResultWriter writer = new HtmlFileResultWriter(
				new File(root, filename));
		writer = new ConsoleResultWriter();
		return writer;
	}
}
