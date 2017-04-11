package com.assembler.instruction;

abstract class Instruction {
	int type;

	public Instruction(int type) {
		this.type = type;
	}
}