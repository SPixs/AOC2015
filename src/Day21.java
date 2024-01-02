import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Day21 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day21.txt"));

		// Part 1
		long startTime = System.nanoTime();
		
		int bossHP = Integer.parseInt(lines.get(0).split(":")[1].trim());
		int bossDamagePoints = Integer.parseInt(lines.get(1).split(":")[1].trim());
		int bossArmorPoints = Integer.parseInt(lines.get(2).split(":")[1].trim());

		Boss boss = new Boss(bossHP, bossDamagePoints, bossArmorPoints);
		Hero hero = new Hero(100);

		List<Item> weapons = Arrays.asList( 
				new Item("Dagger", 8, 4, 0),
				new Item("Shortsword", 10, 5, 0),
				new Item("Warhammer", 25, 6, 0),
				new Item("Longsword", 40, 7, 0),
				new Item("Greataxe", 74, 8, 0)
				);
		
		List<Item> armors = Arrays.asList( 
				new Item("Leather", 13, 0, 1),
				new Item("Chainmail", 31, 0, 2),
				new Item("Splintmail", 53, 0, 3),
				new Item("Bandedmail", 75, 0, 4),
				new Item("Platemail", 102, 0, 5)
				);

		List<Item> rings = Arrays.asList( 
				new Item("Damage +1", 25, 1, 0),
				new Item("Damage +2", 50, 2, 0),
				new Item("Damage +3", 100, 3, 0),
				new Item("Defense +1", 20, 0, 1),
				new Item("Defense +2", 40, 0, 2),
				new Item("Defense +3", 80, 0, 3)
				);
		
		int lowestCost = Integer.MAX_VALUE;
		for (Item weapon : weapons) {
			List<Item> equipment = new ArrayList<Item>(Collections.singleton(weapon));
			for (int i=0;i<=armors.size();i++) {
				if (i<armors.size()) equipment.add(armors.get(i));
				for (int j=0;j<=rings.size();j++) {
					if (j<rings.size()) {
						equipment.add(rings.get(j));
					}
					for (int k=0;k<=rings.size();k++) {
						if (k!=j && k<rings.size()) {
							equipment.add(rings.get(k));
						}
						hero.setEquipment(equipment);
						if (getFightOutcome(hero, boss)) {
							lowestCost = Math.min(lowestCost, hero.getEquipmentCost());
						}
						if (k!=j && k<rings.size()) {
							equipment.remove(rings.get(k));
						}
					}
					if (j<rings.size()) {
						equipment.remove(rings.get(j));
					}
				}
				if (i<armors.size()) equipment.remove(armors.get(i));
			}
		}

		System.out.println("Result part 1 : " + lowestCost + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();

		int highestCost = Integer.MIN_VALUE;
		for (Item weapon : weapons) {
			List<Item> equipment = new ArrayList<Item>(Collections.singleton(weapon));
			for (int i=0;i<=armors.size();i++) {
				if (i<armors.size()) equipment.add(armors.get(i));
				for (int j=0;j<=rings.size();j++) {
					if (j<rings.size()) {
						equipment.add(rings.get(j));
					}
					for (int k=0;k<=rings.size();k++) {
						if (k!=j && k<rings.size()) {
							equipment.add(rings.get(k));
						}
						hero.setEquipment(equipment);
						if (!getFightOutcome(hero, boss)) {
							highestCost = Math.max(highestCost, hero.getEquipmentCost());
						}
						if (k!=j && k<rings.size()) {
							equipment.remove(rings.get(k));
						}
					}
					if (j<rings.size()) {
						equipment.remove(rings.get(j));
					}
				}
				if (i<armors.size()) equipment.remove(armors.get(i));
			}
		}
		
		
		System.out.println("Result part 2 : " + highestCost + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	public static boolean getFightOutcome(Hero hero, Boss boss) {
		
		int heroHP = hero.hitPoints;
		int bossHP = boss.hitPoints;
		
		while (true) {
			simulateTurn(hero, boss);
			if (boss.hitPoints <= 0) {
				hero.hitPoints = heroHP;
				boss.hitPoints = bossHP;
				return true;
			}
			simulateTurn(boss, hero);
			if (hero.hitPoints <= 0) {
				hero.hitPoints = heroHP;
				boss.hitPoints = bossHP;
				return false;
			}
		}
	}
	
	private static void simulateTurn(Character attacker, Character defender) {
		int damage = attacker.getDamagePoints() - defender.getArmorPoints();
		if (damage <= 0) defender.hitPoints--;
		else {
			defender.hitPoints -= damage;
		}
	}

	public static abstract class Character {
		
		public int hitPoints;
		
		public abstract int getDamagePoints();
		public abstract int getArmorPoints();
	}
	
	public static class Hero extends Character {
		
		public List<Item> equipment = new ArrayList<Item>();
		
		public Hero(int hitPoints) {
			this.hitPoints = hitPoints;
		}
		
		public int getEquipmentCost() {
			return equipment.stream().mapToInt(w -> w.cost).sum();
		}

		public void setEquipment(List<Item> equipment) {
			this.equipment = equipment;
		}

		public int getDamagePoints() { 
			return equipment.stream().mapToInt(w -> w.damagePoints).sum();
		}
		
		public int getArmorPoints() {
			return equipment.stream().mapToInt(w -> w.armorPoints).sum();
		}
	}

	public static class Boss extends Character { 
		
		private int damagePoints;
		private int armorPoints;

		public Boss(int hitPoints, int damagePoints, int armorPoints) {
			this.hitPoints = hitPoints;
			this.damagePoints = damagePoints;
			this.armorPoints = armorPoints;
		}

		@Override
		public int getDamagePoints() { return damagePoints; }

		@Override
		public int getArmorPoints() { return armorPoints; }
	}
	
	public static class Item {

		public String name;
		public int cost;
		public int damagePoints;
		public int armorPoints;
		
		public Item(String name, int cost, int damagePoints, int armorPoints) {
			this.name = name;
			this.cost = cost;
			this.damagePoints = damagePoints;
			this.armorPoints = armorPoints;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
