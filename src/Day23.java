import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Day23 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day23.txt"));

		// Part 1
		long startTime = System.nanoTime();

		Cpu cpu = new Cpu(lines);
		while (cpu.execute()) {}
		
		System.out.println("Result part 1 : " + cpu.b + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		cpu.reset();
		cpu.a = 1;
		while (cpu.execute()) {}
		
		startTime = System.nanoTime();
		System.out.println("Result part 2 : " + cpu.b + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	public static class Cpu {

		private long a;
		private long b;

		private int pc;

		private List<String> ram;
		
		public Cpu(List<String> ram) {
			this.ram = ram;
		}
		
		public void reset() {
			a = 0;
			b = 0;
			pc = 0;
		}

		public boolean execute() {
			// fetch
			if (pc < 0 || pc >= ram.size()) return false;
			String instruction = ram.get(pc++);
			String opcode = instruction.substring(0, instruction.indexOf(" ")).trim();
			String operand = instruction.substring(instruction.indexOf(" ")).trim();

			// execute
			getInstruction(opcode).execute(this, operand);
			return true;
		}

		private Instruction getInstruction(String opcode) {
			switch (opcode) {
				case "hlf": return new InstructionHLF();
				case "tpl": return new InstructionTPL();
				case "inc": return new InstructionINC();
				case "jmp": return new InstructionJMP();
				case "jie": return new InstructionJIE();
				case "jio": return new InstructionJIO();
				default:
					throw new IllegalArgumentException();
			}
		}
	}
	
	public static abstract class Instruction {

		public abstract void execute(Cpu cpu, String operand);
	}
	
	public static class InstructionHLF extends Instruction {

		@Override
		public void execute(Cpu cpu, String operand) {
			switch (operand) {
				case "a": cpu.a = (cpu.a & 0x0FFFFFFFFl) >> 1; break;
				case "b": cpu.b = (cpu.b & 0x0FFFFFFFFl) >> 1; break;
				default: throw new IllegalArgumentException();
			}
		}
	}

	public static class InstructionTPL extends Instruction {

		@Override
		public void execute(Cpu cpu, String operand) {
			switch (operand) {
				case "a": cpu.a = (cpu.a * 3) & 0x0FFFFFFFFl; break;
				case "b": cpu.b = (cpu.b * 3) & 0x0FFFFFFFFl; break;
				default: throw new IllegalArgumentException();
			}
		}
	}

	public static class InstructionINC extends Instruction {

		@Override
		public void execute(Cpu cpu, String operand) {
			switch (operand) {
				case "a": cpu.a = (cpu.a + 1) & 0x0FFFFFFFFl; break;
				case "b": cpu.b = (cpu.b + 1) & 0x0FFFFFFFFl; break;
				default: throw new IllegalArgumentException();
			}
		}
	}

	public static class InstructionJMP extends Instruction {

		@Override
		public void execute(Cpu cpu, String operand) {
			int offset = Integer.parseInt(operand);
			cpu.pc += (offset - 1);
		}
	}

	public static class InstructionJIE extends Instruction {

		@Override
		public void execute(Cpu cpu, String operand) {
			String register = operand.split(",")[0];
			int offset = Integer.parseInt(operand.split(",")[1].trim());
			long registerValue = 0;
			switch (register) {
				case "a": registerValue = cpu.a; break;
				case "b": registerValue = cpu.b; break;
				default: throw new IllegalArgumentException();
			}
			if ((registerValue & 0x01) == 0) cpu.pc += (offset - 1);
		}
	}

	public static class InstructionJIO extends Instruction {

		@Override
		public void execute(Cpu cpu, String operand) {
			String register = operand.split(",")[0];
			int offset = Integer.parseInt(operand.split(",")[1].trim());
			long registerValue = 0;
			switch (register) {
				case "a": registerValue = cpu.a; break;
				case "b": registerValue = cpu.b; break;
				default: throw new IllegalArgumentException();
			}
			if ((registerValue & 0x0FFFFFFFFl) == 1) cpu.pc += (offset - 1);
		}
	}
}
