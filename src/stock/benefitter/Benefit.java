package stock.benefitter;

public class Benefit {
	String date;
	String name;
	String code;
	String transaction;
	String price;
	String total;

	@Override
	public String toString() {
		return "date=" + date + ", name=" + name + ", code=" + code
				+ ", transaction=" + transaction + ", price=" + price
				+ ", total=" + total;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
}
