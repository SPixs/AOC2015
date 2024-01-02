import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day16 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day16.txt"));

		// Part 1
		long startTime = System.nanoTime();

		AtomicInteger index = new AtomicInteger(1);
		List<Sue> allSues = lines.stream().map(l -> {
			String[] items = l.substring(l.indexOf(":")+1).trim().split(",");
			Map<String, Integer> itemsMap = new HashMap<String, Integer>();
			for (String item : items) {
				String name = item.split(":")[0].trim();
				int quantity = Integer.parseInt(item.split(":")[1].trim());
				itemsMap.put(name, quantity);
			}
			return new Sue(index.getAndIncrement(), itemsMap);
		}).collect(Collectors.toList());
		
		int result = 0;

		Map<String, Integer> tickerTape = new HashMap<String, Integer>();
		tickerTape.put("children", 3);
		tickerTape.put("cats", 7);
		tickerTape.put("samoyeds", 2);
		tickerTape.put("pomeranians", 3);
		tickerTape.put("akitas", 0);
		tickerTape.put("vizslas", 0);
		tickerTape.put("goldfish", 5);
		tickerTape.put("trees", 3);
		tickerTape.put("cars", 2);
		tickerTape.put("perfumes", 1);

		Sue sue = allSues.stream().filter(s -> s.isCompatible(tickerTape)).findAny().orElseThrow();
		result = sue.index;
		
		System.out.println("Result part 1 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		sue = allSues.stream().filter(s -> s.isCompatiblePart2(tickerTape)).findAny().orElseThrow();
		result = sue.index;
		
		System.out.println("Result part 2 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	public static class Sue {

		private int index;
		private Map<String, Integer> items;

		public Sue(int i, Map<String, Integer> items) {
			this.index = i;
			this.items = items;
		}

		public boolean isCompatiblePart2(Map<String, Integer> tickerTape) {
			for (String itemName : tickerTape.keySet()) {
				if (itemName.equals("cats") || itemName.equals("trees")) {
					if (items.containsKey(itemName) && items.get(itemName) <= tickerTape.get(itemName)) return false;
				}
				else if (itemName.equals("pomeranians") || itemName.equals("goldfish")) {
					if (items.containsKey(itemName) && items.get(itemName) >= tickerTape.get(itemName)) return false;
				}
				else {
					if (items.containsKey(itemName) && items.get(itemName) != tickerTape.get(itemName)) return false;
				}
			}
			return true;
		}

		public boolean isCompatible(Map<String, Integer> tickerTape) {
			for (String itemName : tickerTape.keySet()) {
				if (items.containsKey(itemName) && items.get(itemName) != tickerTape.get(itemName)) return false;
			}
			return true;
		}

	}
}
