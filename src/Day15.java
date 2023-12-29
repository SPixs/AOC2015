import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day15.txt"));

		// Part 1
		long startTime = System.nanoTime();
		
		Map<String, Ingredient> ingredients = new HashMap<String, Ingredient>();

		Pattern pattern = Pattern.compile("([a-zA-Z]+): capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)");
		
		for (String line : lines) {
			final Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				String name = matcher.group(1);
				Ingredient ingredient = ingredients.computeIfAbsent(name, n -> {
					int capacity = Integer.parseInt(matcher.group(2));
					int durability = Integer.parseInt(matcher.group(3));
					int flavor = Integer.parseInt(matcher.group(4));
					int texture = Integer.parseInt(matcher.group(5));
					int calories = Integer.parseInt(matcher.group(6));
					return new Ingredient(n, capacity, durability, flavor, texture, calories);
				});
			}
		}
		
		AtomicInteger bestScore = new AtomicInteger(0);
		recurseTeaspoons(new ArrayList<Ingredient>(ingredients.values()), 100, new Cookie(), bestScore, false);
		
		System.out.println("Result part 1 : " + bestScore + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		bestScore = new AtomicInteger(0);
		recurseTeaspoons(new ArrayList<Ingredient>(ingredients.values()), 100, new Cookie(), bestScore, true);
		System.out.println("Result part 2 : " + bestScore + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	private static class Cookie {
		
		public Map<Ingredient, Integer> teaSpoons = new HashMap<Day15.Ingredient, Integer>();
		
		public int computeScore() {
			List<Ingredient> ingredients = new ArrayList<Ingredient>(teaSpoons.keySet());
			int capacity = ingredients.stream().mapToInt(ingredient -> teaSpoons.get(ingredient) * ingredient.capacity).sum();
			int durability = ingredients.stream().mapToInt(ingredient -> teaSpoons.get(ingredient) * ingredient.durability).sum();
			int flavor = ingredients.stream().mapToInt(ingredient -> teaSpoons.get(ingredient) * ingredient.flavor).sum();
			int texture = ingredients.stream().mapToInt(ingredient -> teaSpoons.get(ingredient) * ingredient.texture).sum();
			capacity = Math.max(capacity, 0);
			durability = Math.max(durability, 0);
			flavor = Math.max(flavor, 0);
			texture = Math.max(texture, 0);
			return capacity * durability * flavor * texture;
		}

		public int getCalories() {
			List<Ingredient> ingredients = new ArrayList<Ingredient>(teaSpoons.keySet());
			return ingredients.stream().mapToInt(ingredient -> teaSpoons.get(ingredient) * ingredient.calories).sum();
		}
		
	}
	
	public static void recurseTeaspoons(List<Ingredient> ingredients, int remainingTeaspoons, Cookie cookie, AtomicInteger bestScore, boolean useCalories) {
        if (ingredients.isEmpty() && remainingTeaspoons == 0) {
            int score = cookie.computeScore();
            if (useCalories && cookie.getCalories() != 500) return;
            bestScore.set(Math.max(bestScore.get(), score));
        } 
        else if (ingredients.size() > 0) {
            for (int i = 1; i <= 100; i++) {
                if (i <= remainingTeaspoons) {
                	Ingredient ingredient = ingredients.get(0);
                	ingredients.remove(ingredient);
                	cookie.teaSpoons.put(ingredient, i);
                	recurseTeaspoons(ingredients, remainingTeaspoons - i, cookie, bestScore, useCalories);
                	ingredients.add(0, ingredient);
                }
            }
        }
    }
	
	public static class Ingredient {

		private String name;
		private int capacity;
		private int durability;
		private int flavor;
		private int texture;
		private int calories;

		public Ingredient(String n, int capacity, int durability, int flavor, int texture, int calories) {
			this.name = n;
			this.capacity = capacity;
			this.durability = durability;
			this.flavor = flavor;
			this.texture = texture;
			this.calories = calories;
		}

	}

}
