package stock;

import java.util.HashMap;
import java.util.Map;

public class HalfCal {
	static String info = "平胜,平胜,平平,负负,负负,平负,平胜,胜胜,负负,平负,负负,平平,平平,胜胜,平胜,平平,平负,平胜,负负,平负,负负,平胜,胜胜,平平,平胜,胜胜,平负,胜胜,平平,平平,平平,平平,胜胜,负胜,平胜,平平,平负,胜平,胜胜,负平,平平,负胜,平负,胜平,胜负,平负,胜平,负平,平负,平平,负胜,平平,平胜,负胜,平负,负胜";

	public static void main(String[] args) {

		String[] infos = info.split(",");
		double total = infos.length;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String string : infos) {
			Integer number = map.get(string);
			if (number == null) {
				map.put(string, 1);
			} else {
				number++;
				map.put(string, number);
			}
		}
		System.out.println(map);
		for (String key : map.keySet()) {
			System.out.println(key + "  " + map.get(key) * 100 / total + "%");
		}
		
	}
}
