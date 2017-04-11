package com.parser.tokenizer;

public class TokenConstants {

	public static String OPERATOR_TYPE = "operator";
	public static String INTEGER_TYPE = "integer";
	public static String NUMBER_TYPE = "number";
	public static String IDENTIFIER_TYPE = "identifier";
	public static String KEYWORD_TYPE = "keyword";
	public static String STRING_TYPE = "string";

	public static String ARITHMATIC_OPERATOR_TYPE = "arithmatic";
	
	public static String LOGICAL_OPERATOR_TYPE = "logical";
	
	public static String URINARY_OPERATOR_TYPE = "urinary";

	public static String SINGLE_QUOTE_TYPE = "'";
	public static String DOUBLE_QUOTE_TYPE = "\"";
	public static String COMMA_TYPE = ",";

	public static String OPEN_B_TYPE = "(";
	public static String CLOSE_B_TYPE = ")";

	public static String OPEN_CURLY_B_TYPE = "{";
	public static String CLOSE_CURLY_B_TYPE = "}";

	public static String OPEN_SQUARE_B_TYPE = "[";
	public static String CLOSE_SQUARE_B_TYPE = "]";

	public static String SPACE = " ";
	public static String SINGLE_Q = "'";
	public static String DOUBLE_Q = "\"";

	public static String OPEN_B = "(";
	public static String CLOSE_B = ")";

	public static String OPEN_CURLY_B = "{";
	public static String CLOSE_CURLY_B = "}";

	public static String OPEN_SQUARE_B = "[";
	public static String CLOSE_SQUARE_B = "]";

	public static String SEMICOLON = ";";
	public static String COMMA = ",";

	public static String EQUALS = "=";
	public static String NOT = "!";

	public static String KEYWORDS[] = { "boolean", "int", "float", "class", "static", "char", "if", "for", "while",
			"else", "elseif", "switch", "return", "void", "function", "constructor", "method", "let", "null", "this",
			"do", "true", "false", "var" };

	public static String A_OPERATOR[] = { "-", "+", "*", "/", "%" };
	public static String L_OPERATOR[] = { "&&", "||", "==","!=",">",">=","<","<="};
	public static String U_OPERATOR[] = { "++", "--", "!" };

}
