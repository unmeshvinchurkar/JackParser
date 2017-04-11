package com.learn;

import com.parser.tokenizer.Tokenizer;

public class Main {

	public static void main(String[] args) {
		
		String exp = "(-4-2*7)+10";

		Tokenizer tn = new Tokenizer(exp);
		tn.parse();

		while (tn.hasMoreTokens()) {
			System.out.println(tn.getToken());
			tn.advance();
		}
		
		tn.reset();
		
		
		ExpressionSolver solver = new ExpressionSolver(tn);
		solver.processStatement();
		
		System.out.println();
		System.out.println("_________________________________");
		solver.printResult();
		
		

	}

}
