package com.assembler;

import java.util.HashMap;
import java.util.Map;

public class MemoryMap {

	private static Map<String, Integer> memorySegValue = new HashMap<>();
	private static Map<String, Integer> memoryMap = new HashMap<>();

	public static String SP = "SP";
	public static String LCL = "LCL";
	public static String ARG = "ARG";
	public static String THIS = "THIS";
	public static String THAT = "THAT";

	// 15 to 255 static variables
	// @256 SP starts

	static {
		memorySegValue.put(SP, 256);
		memorySegValue.put(LCL, 8000);

		memoryMap.put("SP", 0);
		memoryMap.put("LCL", 1);
		memoryMap.put("ARG", 2);
		memoryMap.put("THIS", 3);
		memoryMap.put("THAT", 4);
		memoryMap.put("R0", 0);
		memoryMap.put("R1", 1);
		memoryMap.put("R2", 2);
		memoryMap.put("R3", 3);
		memoryMap.put("R4", 4);
		memoryMap.put("R5", 5);
		memoryMap.put("R6", 6);
		memoryMap.put("R7", 7);
		memoryMap.put("R8", 8);
		memoryMap.put("R9", 9);
		memoryMap.put("R10", 10);
		memoryMap.put("R11", 11);
		memoryMap.put("R12", 12);
		memoryMap.put("R13", 13);
		memoryMap.put("R14", 14);
		memoryMap.put("R15", 15);
		memoryMap.put("SCREEN", 16384);
		memoryMap.put("KBD", 24576);

	}

	public static void assignMemory(String symbol, int address) {
		memoryMap.put(symbol, address);
	}

	public static boolean containsMemory(String name) {
		return memoryMap.containsKey(name);
	}

	public static int getAddress(String symbol) {
		return memoryMap.get(symbol);
	}

	public static void incrementSP() {
		memorySegValue.put(SP, memorySegValue.get(SP) + 1);
	}

	public static void decrementSP() {
		memorySegValue.put(SP, memorySegValue.get(SP) - 1);
	}

	public static void setMemorySegValue(String segName, Integer value) {
		memorySegValue.put(segName, value);
	}

	public static Integer getMemorySegValue(String segName) {
		return memorySegValue.get(segName);
	}

}
