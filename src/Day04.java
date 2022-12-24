import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Day04 {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		List<String> lines = Files.readAllLines(Path.of("input_day04.txt"));

		String input = lines.get(0);

		int index = 1;

		MessageDigest md = MessageDigest.getInstance("MD5");

		while (true) {
			String line = input + (index);
			md.reset();
			md.update(line.getBytes());
			byte[] digest = md.digest();
			String hexString = encodeUsingBigIntegerStringFormat(digest);
			if (hexString.startsWith("00000")) {
				System.out.println(index);
				break;
			}
			index++;
		}

		index = 1;
		while (true) {
			String line = input + (index);
			md.reset();
			md.update(line.getBytes());
			byte[] digest = md.digest();
			String hexString = encodeUsingBigIntegerStringFormat(digest);
			if (hexString.startsWith("000000")) {
				System.out.println(index);
				break;
			}
			index++;
		}
	}

	public static String encodeUsingBigIntegerStringFormat(byte[] bytes) {
		BigInteger bigInteger = new BigInteger(1, bytes);
		return String.format("%0" + (bytes.length << 1) + "x", bigInteger);
	}

}
