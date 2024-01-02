import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Day22 {

	public static boolean DEBUG = false;
	
	public static void debug(String txt) {
		if (DEBUG) System.out.println(txt);
	}
	
	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day22.txt"));

		// Part 1
		long startTime = System.nanoTime();

		int bossHP = Integer.parseInt(lines.get(0).split(":")[1].trim());
		int bossDamagePoints = Integer.parseInt(lines.get(1).split(":")[1].trim());

		Boss boss = new Boss(bossHP, bossDamagePoints, 0);
		Hero hero = new Hero(50, 500);
		
		List<Spell> allSpells = Arrays.asList(new MagicMissile(), new Drain(), new Shield(), new Poison(), new Recharge());

		int lowestMana = recurseFight(hero, boss, allSpells, Integer.MAX_VALUE, false);

		System.out.println("Result part 1 : " + lowestMana + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		lowestMana = recurseFight(hero, boss, allSpells, Integer.MAX_VALUE, true);
		
		System.out.println("Result part 2 : " + lowestMana + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	private static int recurseFight(Hero hero, Boss boss, List<Spell> allSpells, int lowesManaCost, boolean part2) {
		
		if (hero.usedMana >= lowesManaCost) return lowesManaCost;

		if (part2) hero.hitPoints--;
		if (hero.hitPoints <= 0) {
			return lowesManaCost;
		}

		debug("-- Player turn --");
		debug("- Player has " + hero.hitPoints +" hit points, " + hero.armorPoints + " armor, " + hero.mana + " mana");
		debug("- Boss has " + boss.hitPoints +" hit points.");
		
		// hero turn, process current spells
		hero.processSpells(boss);

		// if boss has no more HP, hero wins
		if (boss.hitPoints <= 0) {
			debug("This kills the boss, and the player wins.");
			debug("");
			if (hero.usedMana == 900) {
				Thread.yield();
			}
			return hero.usedMana;
		}
		
		// On each of your turns, you must select one of your spells to cast. 
		// If you cannot afford to cast any spell, you lose.
		final Hero tmpHero = hero;
		List<Spell> spells = allSpells.stream().filter(s -> s.cost <= tmpHero.mana && !tmpHero.activeSpells.contains(s)).map(s -> s.createNew()).collect(Collectors.toList());
		if (spells.isEmpty()) {
			debug("Hero cannot casts spell, and the boss wins.");
			debug("");
			return Integer.MAX_VALUE;
		}
		
		for (Spell spell : spells) {
			
			Hero newHero = hero.getCopy();
			Boss newBoss = boss.getCopy();
			
			newHero.castSpell(spell, newBoss);
			debug("");
			
			// Boss turn
			// spells effects are applied also on boss turn
			debug("-- Boss turn --");
			debug("- Player has " + newHero.hitPoints +" hit points, " + newHero.armorPoints + " armor, " + newHero.mana + " mana");
			debug("- Boss has " + newBoss.hitPoints +" hit points.");
			newHero.processSpells(newBoss);
			
			// if boss has no more HP, hero wins
			if (newBoss.hitPoints <= 0) {
				debug("This kills the boss, and the player wins.");
				debug("");
				if (newHero.usedMana == 900) {
					Thread.yield();
				}
				return newHero.usedMana;
			}
			
			// Compute damages dealt by the boss
			int delta = newBoss.getDamagePoints() - newHero.getArmorPoints();
			// As before, if armor (from a spell, in this case) would reduce damage below 1, it becomes 1 instead - that is, the boss' attacks always deal at least 1 damage.
			int damage = Math.max(1, delta);
			newHero.hitPoints -= damage;
			debug("Boss attacks for " + damage + " damage." + ((newHero.hitPoints <= 0) ? " This kills the hero, and the boss wins." : ""));
			debug("");
			if (newHero.hitPoints > 0) {
				
				int cost = recurseFight(newHero, newBoss, allSpells, lowesManaCost, part2);
				if (cost < lowesManaCost) {
					lowesManaCost = cost;
				}
			}
		}
		
		return lowesManaCost;
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
		if (damage <= 0)
			defender.hitPoints--;
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

		public int mana;
		public int armorPoints;
		public int damagePoints;
		public int usedMana;
		
		private List<Spell> activeSpells = new ArrayList<Spell>();

		public Hero(int hitPoints, int mana) {
			this.hitPoints = hitPoints;
			this.mana = mana;
		}

		public Hero getCopy() {
			Hero copy = new Hero(hitPoints, mana);
			copy.armorPoints = armorPoints;
			copy.damagePoints = damagePoints;
			copy.usedMana = usedMana;
			copy.activeSpells = activeSpells.stream().map(s -> s.getCopy()).collect(Collectors.toList());
			return copy;
		}

		public int getDamagePoints() {
			return damagePoints;
		}

		public int getArmorPoints() {
			return armorPoints;
		}

		public void processSpells(Boss boss) {
			for (Spell spell : new ArrayList<Spell>(activeSpells)) {
				spell.apply(this, boss);
			}
		}
		
		public void castSpell(Spell spell, Boss boss) {
			spell.turns = spell.initialTurn;
			mana -= spell.cost;
			usedMana += spell.cost;
			activeSpells.add(spell);
			spell.applyAtCast(this, boss);
		}

		public void spellExpired(Spell spell) {
			activeSpells.remove(spell);
		}
	}

	public abstract static class Spell {

		protected int cost;
		protected int initialTurn;
		protected int turns;

		public Spell(int cost, int turns) {
			this.cost = cost;
			this.initialTurn = turns;
		}
		
		protected Spell createNew() {
			try {
				return getClass().getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}
		
		protected Spell getCopy() {
			try {
				Spell copy = getClass().getDeclaredConstructor().newInstance();
				copy.turns = turns;
				copy.initialTurn = initialTurn;
				return copy;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}

		public void apply(Hero hero, Boss boss) {
			turns--;
			applyTurn(hero, boss);
			if (turns == 0) {
				applyLastTurn(hero, boss);
				hero.spellExpired(this);
			}
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + cost;
			result = prime * result + initialTurn;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Spell other = (Spell) obj;
			if (cost != other.cost)
				return false;
			if (initialTurn != other.initialTurn)
				return false;
			return true;
		}

		protected abstract void applyAtCast(Hero hero, Boss boss);
		protected abstract void applyTurn(Hero hero, Boss boss);
		protected abstract void applyLastTurn(Hero hero, Boss boss);
	}
	
	public static class MagicMissile extends Spell {
		
		public MagicMissile() {
			super(53, 1);
		}

		@Override
		protected void applyAtCast(Hero hero, Boss boss) {
			debug("Player casts Magic Missile, dealing 4 damage.");
			boss.hitPoints -= 4;
			hero.spellExpired(this);
		}

		@Override
		protected void applyLastTurn(Hero hero, Boss boss) {}

		@Override
		protected void applyTurn(Hero hero, Boss boss) {}
	}

	public static class Drain extends Spell {
		
		public Drain() {
			super(73, 1);
		}

		@Override
		protected void applyAtCast(Hero hero, Boss boss) {
			debug("Player casts Drain, dealing 2 damage, and healing 2 hit points.");
			boss.hitPoints -= 2;
			hero.hitPoints += 2;
			hero.spellExpired(this);
		}

		@Override
		protected void applyLastTurn(Hero hero, Boss boss) {}
		
		@Override
		protected void applyTurn(Hero hero, Boss boss) {}
	}

	public static class Shield extends Spell {
		
		public Shield() {
			super(113, 6);
		}

		@Override
		protected void applyAtCast(Hero hero, Boss boss) {
			debug("Player casts Shield, increasing armor by 7.");
			hero.armorPoints += 7;
		}

		@Override
		protected void applyLastTurn(Hero hero, Boss boss) {
			hero.armorPoints -= 7;
		}
		
		@Override
		protected void applyTurn(Hero hero, Boss boss) {
			debug("Shield's timer is now " + turns + ".");
		}
	}

	private static final Map<Object, Long> map = new IdentityHashMap<>();

	private static long nextId;

	public static class Poison extends Spell {
		
		private long id;

		public Poison() {
			super(173, 6);
			this.id = nextId++;
		}

		@Override
		protected void applyAtCast(Hero hero, Boss boss) {
			debug("Player casts Poison.");
		}

		@Override
		protected void applyLastTurn(Hero hero, Boss boss) {}
		
		@Override
		protected void applyTurn(Hero hero, Boss boss) {
			debug("Poison deals 3 damage; its timer is now " + turns + ".");
			boss.hitPoints -= 3;
		}
	}

	public static class Recharge extends Spell {
		
		public Recharge() {
			super(229, 5);
		}

		@Override
		protected void applyAtCast(Hero hero, Boss boss) {
			debug("Player casts Recharge.");
		}

		@Override
		protected void applyLastTurn(Hero hero, Boss boss) {}
		
		@Override
		protected void applyTurn(Hero hero, Boss boss) {
			debug("Recharge provides 101 mana; its timer is now " + turns + ".");
			hero.mana += 101;
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

		public Boss getCopy() {
			Boss copy = new Boss(hitPoints, damagePoints, armorPoints);
			return copy;
		}

		@Override
		public int getDamagePoints() {
			return damagePoints;
		}

		@Override
		public int getArmorPoints() {
			return armorPoints;
		}
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
