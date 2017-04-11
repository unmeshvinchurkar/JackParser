package com.assembler.instruction;

public class AInstruction extends Instruction {
	String value;
	boolean constant;

	public AInstruction(String value, boolean constant) {
		super(1);
		this.value = constant ? to16(Integer.parseInt(value)) : value;
		this.constant = constant;
	}

	@Override
	public String toString() {
		//return constant ? value : to16(Main.symbolTable.get(value));
		
		return "";
	}

	private String to16(int n) {
		String num = Integer.toBinaryString(n);
		StringBuffer sb = new StringBuffer(16);
		int len = num.length();

		for (int i = 1; len + i <= 16; ++i)
			sb.append('0');
		sb.append(num);

		return sb.toString();
	}
}
