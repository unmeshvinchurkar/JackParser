package com.parser;

import java.util.Stack;

import com.tokenizer.TokenUtils;
import com.tokenizer.Tokenizer;

public class ExpressionSolver {

	Tokenizer t;
	Stack<Float> stack = new Stack<Float>();

	public ExpressionSolver(Tokenizer t) {
		this.t = t;
	}

	public void printResult() {
		System.out.println(stack.pop());
	}

	public void processStatement() {
		processExp();

		if (t.hasMoreTokens()) {
			throw new RuntimeException("Error at token: " + t.getLastToken());
		}
	}

	public void processExp() {

		processTerm();

		if (t.hasMoreTokens()) {

			String op = t.getToken().getValue();

			if (eat("+") || eat("-")) {
				processExp();

				float num2 = stack.pop();
				float num1 = stack.pop();

				if (op.equals("+")) {
					stack.push(num1 + num2);
				} else {
					stack.push(num1 - num2);
				}
			}
		}
	}

	public void processTerm() {

		processFactor();

		if (t.hasMoreTokens()) {

			String op = t.getToken().getValue();

			if (eat("*") || eat("/")) {
				processTerm();

				float num2 = stack.pop();
				float num1 = stack.pop();

				if (op.equals("*")) {
					stack.push(num1 * num2);
				} else {
					stack.push(num1 / num2);
				}

			}
		}
	}

	public void processFactor() {
		if (eat("(")) {
			processExp();
			eat(")");
		} else if (TokenUtils.isNumber(t.getToken().getValue())) {
			stack.push(Float.valueOf(t.getToken().getValue()));
			t.advance();
		} else {
			throw new RuntimeException("Error at token: " + t.getToken());
		}

	}

	public boolean eat(String s) {

		if (!t.hasMoreTokens()) {
			throw new RuntimeException("No more tokens available");
		}

		if (t.getToken().getValue().equals(s)) {
			t.advance();
			return true;
		} else {
			return false;
		}
	}

}
