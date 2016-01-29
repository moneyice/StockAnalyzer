package stock.selector.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import stock.selector.jobs.StockInfoSpider;

@Component
public class ScheduledController {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss");

	@Resource(name = "stockInfoSpider")
	private StockInfoSpider stockInfoSpider = null;

	@Scheduled(fixedRate = 500000)
	public void reportCurrentTime() {
		System.out.println("The time is now " + dateFormat.format(new Date()));
		stockInfoSpider.run();
	}
}
