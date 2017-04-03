package com.tokenizer;

public class TestTokenizer {

	public static void main(String[] args) {
		String exp = "(--2+++3)*(566)--\"123abc";

		Tokenizer tn = new Tokenizer(exp);
		tn.parse();

		while (tn.hasMoreTokens()) {
			System.out.println(tn.getToken());
			tn.advance();
		}

	}

}
