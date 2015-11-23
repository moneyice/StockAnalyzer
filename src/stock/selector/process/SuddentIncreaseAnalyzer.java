package stock.selector.process;

import java.util.List;

import stock.selector.model.DailyInfo;
import stock.selector.model.SelectResult;
import stock.selector.model.Stock;
import stock.selector.process.io.HtmlFileResultWriter;
import stock.selector.util.Utils;

//n天内有涨停，现价跌幅在m 的股票
public class SuddentIncreaseAnalyzer extends AbstractStockAnalyzer {

	//consider how many days before now.
	int daysToNow = 10;
	
	//increase range
	double increase=9;
	
	//decrease now
	double decrease=-3.5;
	
	public SuddentIncreaseAnalyzer() {
	}

	public void analyze(Stock stock) {
		//setStock(stock);
		List<DailyInfo> infos = stock.getDailyinfo();
		if (infos.size() <= daysToNow) {
			// 元数据天数要求大于考察天数
			return;
		}
		
		DailyInfo current= infos.get(infos.size()-1);
		
		for (int i = infos.size() - daysToNow - 1; i < infos.size(); i++) {
			DailyInfo toCheck = infos.get(i);
			
			double increaseReage= (toCheck.getClose()-toCheck.getOpen())/toCheck.getOpen();
			double percent=((double)(current.getClose()-toCheck.getClose()))/toCheck.getClose();
			
			
			boolean condition1 = increaseReage*100 >=  increase;
			
			boolean condition2 = percent * 100 <=decrease;

			if (condition1 && condition2) {
				SelectResult result = new SelectResult();
				result.setStock(stock);
				String msg=format(stock,current,toCheck);
				result.setMsg(msg);
				results.add(result);
				break;
			}
		}
	}

	public void outPutResults(){
			getResultwriter().write(results);
	}
	
	public String format(Stock stock, DailyInfo current, DailyInfo check  ) {
		StringBuilder sb = new StringBuilder();
		sb.append(stock.getCode()).append("  ")
				.append(stock.getName()).append("\n");
		sb.append("时间：").append(check.getTime()).append("\n");
		sb.append(
				"跌幅："
						+ (current.getClose() - check
								.getClose()) / check.getClose()
						* 100).append("%\n");
		sb.append("现价: ").append(current.getClose()).append("\n\r");
		return (sb.toString());
	}
	
	public String getDescription() {
		return daysToNow+"天之内有近似涨停，目前跌幅大于 "+decrease + "% \n\r" ;
	}

	public int getDaysToNow() {
		return daysToNow;
	}

	public void setDaysToNow(int daysToNow) {
		this.daysToNow = daysToNow;
	}
	
	
	public static void main(String[] args) {
		double decrease=-2;
		double decreaseReage=-2.7;
		boolean condition1 = decreaseReage *100 <=  decrease;
		System.out.println(condition1);
	}
}
