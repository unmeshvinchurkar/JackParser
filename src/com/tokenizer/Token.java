package com.tokenizer;

public class Token {

	private String tokenType;
	private String tokenSubType;
	private String value;
	private int columnNum = 0;

	public Token(String tokenType, String value) {
		this.tokenType = tokenType;
		this.value = value;
	}

	public Token(String tokenType, String tokenSubType, String value) {
		this.tokenType = tokenType;
		this.tokenSubType = tokenSubType;
		this.value = value;
	}

	public Token(String tokenType, String tokenSubType, String value, int columnNun) {
		this.tokenType = tokenType;
		this.tokenSubType = tokenSubType;
		this.value = value;
		this.columnNum = columnNun;
	}

	public Token(String tokenType, String value, int columnNun) {
		this.tokenType = tokenType;
		this.value = value;
		this.columnNum = columnNun;
	}

	public String getTokenSubType() {
		return tokenSubType;
	}

	public void setTokenSubType(String tokenSubType) {
		this.tokenSubType = tokenSubType;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Token [tokenType=" + tokenType + ", value=" + value + ", columnNum=" + columnNum + "]";
	}

}
