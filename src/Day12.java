import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Day12 {

	
	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day12.txt"));

		// Part 1
		long startTime = System.nanoTime();

		String json = lines.get(0);
		int result = 0;
		Integer tmp = null;
		boolean negative = false;
		for (char c : json.toCharArray()) {
			if (c == '-') {
				negative = true;
			}
			else if (Character.isDigit(c)) {
				tmp = (tmp == null ? 0 : tmp * 10) + (c-'0');
			}
			else {
				if (negative) tmp = -tmp;
				result += tmp == null ? 0 : tmp;
				negative = false;
				tmp = null;
			}
		}			
		
		System.out.println("Result part 1 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		
		AtomicInteger index = new AtomicInteger(0);
		Node rootNode = parseNode(json, index);
		AtomicInteger resultCounter = new AtomicInteger(0);
		rootNode.visit(resultCounter);
		
		System.out.println("Result part 2 : " + resultCounter + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	private static Node parseNode(String json, AtomicInteger index) {
		char c = json.charAt(index.get());
		switch (c) {
			case '{': return parseObject(json, index);
			case '[': return parseList(json, index);
			case '"': return parseString(json, index);
		}
		if (Character.isDigit(c) || c == '-') {
			return parseInteger(json, index);
		}
		throw new IllegalStateException();
	}

	private static StringNode parseString(String json, AtomicInteger index) {
		String text = "";
		char c = json.charAt(index.getAndIncrement());
		if (c != '"') throw new IllegalStateException();
		c = json.charAt(index.getAndIncrement());
		while (c != '"') {
			text = text + c;
			 c = json.charAt(index.getAndIncrement());
		}
		return new StringNode(text);
	}

	private static ObjectNode parseObject(String json, AtomicInteger index) {
		ObjectNode node = new ObjectNode();
		char c = json.charAt(index.getAndIncrement());
		if (c != '{')  throw new IllegalStateException();
		c = json.charAt(index.get());
		while (c != '}') {
			StringNode parseString = parseString(json, index);
			c = json.charAt(index.getAndIncrement());
			if (c != ':') throw new IllegalStateException();
			Node value = parseNode(json, index);
			node.addAttribute(parseString.getValue(), value);
			c = json.charAt(index.getAndIncrement());
			while (c == ',') c = json.charAt(index.getAndIncrement());
			index.decrementAndGet();
		}
		index.incrementAndGet();
		return node;
	}

	private static Node parseList(String json, AtomicInteger index) {
		ListNode listNode = new ListNode();
		char c = json.charAt(index.get());
		if (c != '[') throw new IllegalStateException();
		c = json.charAt(index.incrementAndGet());
		while (c != ']') {
			listNode.add(parseNode(json, index));
			c = json.charAt(index.getAndIncrement());
			while (c == ',') c = json.charAt(index.getAndIncrement());
			index.decrementAndGet();
		}
		index.incrementAndGet();
		return listNode;
	}

	private static Node parseInteger(String json, AtomicInteger index) {
		int result = 0;
		char c = json.charAt(index.getAndIncrement());
		if (c != '-' && !Character.isDigit(c)) throw new IllegalStateException();
		boolean negative = false;
		if (c == '-') {
			negative = true;
			c = json.charAt(index.getAndIncrement());
		}
		while (Character.isDigit(c)) {
			result = result * 10 + (c-'0');
			c = json.charAt(index.getAndIncrement());
		}
		index.decrementAndGet();
		return new IntegerNode(result * (negative ? -1 : 1));
	}

	public static abstract class Node {

		public abstract void visit(AtomicInteger resultCounter);

		protected boolean isRed() { return false; }

	}
	
	public static class StringNode extends Node {

		private String text;

		public StringNode(String text) {
			this.text = text;
		}

		public String getValue() {
			return text;
		}

		@Override
		public String toString() {
			return "\""+text+"\"";
		}

		@Override
		public void visit(AtomicInteger resultCounter) {
		}

		@Override
		protected boolean isRed() {
			return "red".equals(text);
		}
	}

	public static class ObjectNode extends Node {
		
		Map<String, Node> attributes = new LinkedHashMap<String, Node>();

		public void addAttribute(String value, Node node) {
			attributes.put(value, node);
		}
		
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("{");
			List<String> names = new ArrayList<String>(attributes.keySet());
			for (int i=0;i<names.size();i++) {
				if (i > 0) buffer.append(",");
				buffer.append("\"");
				buffer.append(names.get(i));
				buffer.append("\"");
				buffer.append(":");
				buffer.append(attributes.get(names.get(i)));
			}
			buffer.append("}");
			return buffer.toString();
		}

		@Override
		public void visit(AtomicInteger resultCounter) {
			boolean hasRed = false;
			for (Node node : attributes.values()) {
				hasRed |= node.isRed();
			}
			
			if (!hasRed) {
				for (Node node : attributes.values()) {
					node.visit(resultCounter);
				}
			}
		}
	}

	
	public static class ListNode extends Node {

		private List<Node> children = new ArrayList<Node>();

		public void add(Node child) {
			children.add(child);
		}
		
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("[");
			for (int i=0;i<children.size();i++) {
				if (i > 0) buffer.append(",");
				buffer.append(children.get(i));
			}
			buffer.append("]");
			return buffer.toString();
		}

		@Override
		public void visit(AtomicInteger resultCounter) {
			for (Node node : children) {
				node.visit(resultCounter);
			}
		}
	}

	public static class IntegerNode extends Node {

		private int value;

		public IntegerNode(int value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.valueOf(value);
		}

		@Override
		public void visit(AtomicInteger resultCounter) {
			resultCounter.set(resultCounter.get()+value);
		}
	}
}
