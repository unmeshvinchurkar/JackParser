package com.parser;

import java.util.HashMap;
import java.util.Map;

public class CompilerCache {

	private static Map<String, Integer> methodArgNo = new HashMap<>();
	private static Map<String, Integer> methodLCLNo = new HashMap<>();
	private static Map<String, Integer> classVarNo = new HashMap<>();

	public static void setMethodArgCount(String mName, Integer noOfArgs) {
		methodArgNo.put(mName, noOfArgs);
	}

	public static Integer getMethodArgCount(String mName) {
		return methodArgNo.get(mName);
	}

	public static void setMethodLCLCount(String mName, Integer noOfLcl) {
		methodLCLNo.put(mName, noOfLcl);
	}

	public static Integer getMethodLCLCount(String mName) {
		return methodLCLNo.get(mName);
	}

	public static void setClassVarCount(String className, Integer count) {
		classVarNo.put(className, count);
	}

	public static Integer getClassVarCount(String className) {
		return classVarNo.get(className);
	}

}
