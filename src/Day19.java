import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Day19 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day19.txt"));

		List<String> rules = lines.subList(0, lines.size()-2);
		String molecule = lines.get(lines.size()-1);
		
		Map<String, List<Rule>> rulesMap = rules.stream().map(l -> new Rule(l)).collect(Collectors.groupingBy(r -> r.lhs));
		
		List<Rule> allRules = rules.stream().map(l -> new Rule(l)).collect(Collectors.toList());
		
		// Part 1
		long startTime = System.nanoTime();
		int result = 0;
		
		Set<String> newMolecules = new HashSet<String>();
		for (int i=molecule.length();i>=0;i--) {
			for (String atom : rulesMap.keySet()) {
				if (molecule.substring(i).startsWith(atom)) {
					for (Rule rule : rulesMap.get(atom)) {
						StringBuffer buffer = new StringBuffer();
						buffer.append(molecule.substring(0, i));
						buffer.append(rule.rhs);
						buffer.append(molecule.substring(i+atom.length()));
						newMolecules.add(buffer.toString());
					}
				}
			}
		}
		result = newMolecules.size();
		
		System.out.println("Result part 1 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();

		result = beamSearch(molecule, allRules, "e");

		System.out.println("Result part 2 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	private static int beamSearch(String currentMolecule, List<Rule> allRules, String targetMolecule) {

		int stepsCounter = 0;
		List<String> allReductions = Arrays.asList(currentMolecule);
				
		while (true) {
			if (allReductions.contains(targetMolecule)) return stepsCounter;

			List<String> allNewReductions = new ArrayList<String>();
			for (String molecule : allReductions) {
				allNewReductions.addAll(findAllReductions(molecule, allRules));
			}
			allReductions = allNewReductions.stream().distinct().sorted((s1,s2) -> Integer.compare(s1.length(), s2.length())).collect(Collectors.toList());
			allReductions = allReductions.subList(0,  Math.min(10, allReductions.size()));
			stepsCounter++;
		}
	}
		
	private static List<String> findAllReductions(String molecule, List<Rule> allRules) {
		allRules = allRules.stream().filter(rule -> molecule.contains(rule.rhs)).collect(Collectors.toList());
		List<String> result = new ArrayList<String>();
		for (int i=0;i<molecule.length()-1;i++) {
			for (Rule rule : allRules) {
				if (molecule.substring(i).startsWith(rule.rhs)) {
					StringBuffer buffer = new StringBuffer(molecule.length());
					buffer.append(molecule.substring(0, i));
					buffer.append(rule.lhs);
					buffer.append(molecule.substring(i+rule.rhs.length()));
					result.add(buffer.toString());
				}
			}
		}
		return result;
	}

	public static class Rule {

		public String lhs;
		public String rhs;

		public Rule(String l) {
			String[] split = l.split("=>");
			this.lhs = split[0].trim();
			this.rhs = split[1].trim();
		}

	}
}
