package com.tokenizer;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenUtils {

	
	private static Pattern identifierPttern = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]{0,30}$");
	private static Pattern stringPttern1 = Pattern.compile("^\".*\"$");
	private static Pattern stringPttern2 = Pattern.compile("^'.*'$");

	public static boolean isKeyword(String s) {
		String keywords[] = TokenConstants.KEYWORDS;
		for (String k : keywords) {
			if (s.equals(k)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBoolean(String s) {

		if (s.equals("true") || s.equals("false")) {
			return true;
		}
		return false;
	}

	public static boolean isString(String s) {
		return isString1(s) || isString2(s);
	}

	private static boolean isString1(String s) {
		Matcher m = stringPttern1.matcher(s);
		if (m.find()) {
			return true;
		}
		return false;
	}

	private static boolean isString2(String s) {
		Matcher m = stringPttern2.matcher(s);
		if (m.find()) {
			return true;
		}
		return false;
	}

	public static boolean isIdentifier(String s) {

		Matcher m = identifierPttern.matcher(s);

		if (m.find()) {
			return true;
		}

		return false;
	}

	public static boolean isBracket(String s) {

		if (s.equals(TokenConstants.CLOSE_B) || s.equals(TokenConstants.OPEN_B)) {
			return true;
		}

		if (s.equals(TokenConstants.CLOSE_CURLY_B) || s.equals(TokenConstants.OPEN_CURLY_B)) {
			return true;
		}

		if (s.equals(TokenConstants.CLOSE_SQUARE_B) || s.equals(TokenConstants.OPEN_SQUARE_B)) {
			return true;
		}

		return false;
	}

	public static boolean isInteger(String s) {

		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	public static boolean isNumber(String s) {

		try {
			Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	public static boolean isOperator(String s) {

		if (isUraniaryOperator(s)) {
			return true;
		}

		else if (isLogicalOperator(s)) {
			return true;
		}

		else if (isArithmaticOperator(s)) {
			return true;
		}

		return false;
	}

	public static boolean isUraniaryOperator(String s) {

		String operators[] = TokenConstants.U_OPERATOR;

		for (String op : operators) {

			if (s.equals(op)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLogicalOperator(String s) {

		String operators[] = TokenConstants.L_OPERATOR;

		for (String op : operators) {

			if (s.equals(op)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isArithmaticOperator(String s) {

		String operators[] = TokenConstants.A_OPERATOR;

		for (String op : operators) {

			if (s.equals(op)) {
				return true;
			}
		}
		return false;
	}
}
