package stock.selector.model;

public class SelectResult {
	Stock stock;

	DailyInfo from;
	DailyInfo to;
	DailyInfo now;

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public DailyInfo getFrom() {
		return from;
	}

	public void setFrom(DailyInfo from) {
		this.from = from;
	}

	public DailyInfo getTo() {
		return to;
	}

	public void setTo(DailyInfo to) {
		this.to = to;
	}

	public DailyInfo getNow() {
		return now;
	}

	public void setNow(DailyInfo now) {
		this.now = now;
	}

}
