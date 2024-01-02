import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Day20 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day20.txt"));

		// Part 1
		long startTime = System.nanoTime();
		int targetPresentCount = Integer.parseInt(lines.get(0));

		int result = findHouseNumber(targetPresentCount);

		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		result = findHouseNumberPart2(targetPresentCount);
		System.out.println("Result part 2 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
    private static int sumOfFactors(int house) {
        int factorsSum = 0;
        for (int i = 1; i <= Math.sqrt(house); i++) {
            if (house % i == 0) {
                factorsSum += i;
                // Don't count divisor twice if it is the square root of house
                if (i*i != house) {
                    factorsSum += house / i;
                }
            }
        }
        return factorsSum;
    }

    // Find the lowest house number based on the target input
    private static int findHouseNumber(int target) {
        int houseNumber = 1;
        while (true) {
            int factorsSum = sumOfFactors(houseNumber) * 10;
            if (factorsSum >= target) {
                return houseNumber;
            }
            houseNumber++;
        }
    }
    
    // Find the lowest house number based on the target input
    private static int findHouseNumberPart2(int target) {
        int houseNumber = 1;
        while (true) {
            int factorsSum = sumOfFactorsPart2(houseNumber) * 11;
            if (factorsSum >= target) {
                return houseNumber;
            }
            houseNumber++;
        }
    }
    
    private static int sumOfFactorsPart2(int house) {
    	 int factorsSum = 0;
         for (int i = 1; i <= Math.sqrt(house); i++) {
             if (house % i == 0) {
                 if (house / i <= 50) {
                     factorsSum += i;
                 }
                 int complement = house / i;
                 if (complement != i && house / complement <= 50) {
                     factorsSum += complement;
                 }
             }
         }
         return factorsSum;
    }
}
