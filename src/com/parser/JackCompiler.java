package com.parser;

import com.tokenizer.TokenUtils;
import com.tokenizer.Tokenizer;

public class JackCompiler {

	Tokenizer t;

	public JackCompiler(Tokenizer t) {
		this.t = t;
	}

	public void compileClass() {

		if (eat("class")) {
			compileIdentifier(); // class name

			eatHard("{");

			compileClassVariables();

			compileSubRoutines();

			eatHard("}");

		}
	}

	public void compileClassVariables() {

		while (eat("static") || (eat("field"))) {

			compileType();
			compileIdentifier(); // variable name

			while (eat(",")) {
				compileIdentifier(); // variable name
			}

			eatHard(";");
		}
	}

	public void compileSubRoutines() {

		while (eat("constructor") || (eat("function")) || (eat("method"))) {

			if (!eat("void")) {
				compileType();
			} else {
				eatHard("void");
			}
			compileIdentifier(); // method name

			eatHard("(");

			t.mark();
			t.advance();
			// if next token is not closing bracket
			if (!eat(")")) {
				t.reset();
				compileParameterList();
			}
			t.reset();

			eatHard(")");
			compileMethodBody();

		}

	}

	public void compileParameterList() {

		compileType();
		compileIdentifier(); // parameter name

		while (eat(",")) {
			compileType();
			compileIdentifier(); // parameter name
		}

	}

	public void compileMethodBody() {

		eatHard("{");

		t.mark();
		t.advance();

		if (eat("var")) {
			t.reset();
			compileLocalVariables();
		}
		compileStatements();

		eatHard("}");

	}

	public void compileLocalVariables() {

		eatHard("var");
		compileType();
		compileIdentifier(); // variable name

		while (eat(",")) {
			compileIdentifier();
		}
		eatHard(",");

	}

	public void compileStatements() {

		t.mark();
		t.advance();

		if (eat("let")) {
			t.reset();
			compileLetStmt();
		} else if (eat("if")) {
			t.reset();
			compileIfStmt();
		} else if (eat("while")) {
			t.reset();
			compileWhileStmt();
		} else if (eat("do")) {
			t.reset();
			compileDoStmt();
		} else if (eat("return")) {
			t.reset();
			compileReturnStmt();
		}

		t.reset();

	}

	public void compileWhileStmt() {
		eatHard("while");
		eatHard("(");
		compileExpression();
		eatHard(")");
		eatHard("{");
		compileStatements();
		eatHard("}");

	}

	public void compileMethodCall() {

		compileIdentifier();

		if (eat("(")) {
			if (!eat(")")) {
				compileExpressionList();
				eatHard(")");
			}
		} else if (eat(".")) {
			compileIdentifier();
			eatHard("(");

			if (!eat(")")) {
				compileExpressionList();
				eatHard(")");
			}
		} else {
			new RuntimeException("No method call available");
		}

	}

	public void compileExpressionList() {
		compileExpression();

		while (eat(",")) {
			compileExpression();
		}
	}

	public void compileDoStmt() {
		eatHard("do");
		compileMethodCall();
		eatHard(";");
	}

	public void compileReturnStmt() {

		eatHard("return");

		if (!eat(";")) {
			compileExpression();
		}

		eatHard(";");

	}

	public void compileLetStmt() {

		eatHard("let");
		compileIdentifier();

		if (eat("[")) {
			compileExpression();
			eatHard("]");
		}

		eatHard("=");
		compileExpression();
		eatHard(";");

	}

	public void compileIfStmt() {

		eatHard("if");
		eatHard("(");
		compileExpression();
		eatHard(")");

		eatHard("{");
		compileStatements();
		eatHard("}");

		if (eat("else")) {
			eatHard("{");
			compileStatements();
			eatHard("}");
		}
	}

	public void compileType() {

		if (!(eat("int") || eat("boolean") || eat("char") || eat("float") || eat("String"))) {
			compileIdentifier();
		}

	}

	public void compileIdentifier() {

		if (TokenUtils.isIdentifier(t.getToken().getValue())) {
			t.advance();
		} else {
			throw new RuntimeException("Not an identifier: " + t);
		}

	}

	public void compileExpression() {

		compileTerm();

		if (t.hasMoreTokens()) {
			String op = t.getToken().getValue();

			if (eat("+") || eat("-")) {
				compileExpression();

				if (op.equals("+")) {
					// stack.push(num1 + num2);
				} else {
					// stack.push(num1 - num2);
				}
			}
		}
	}

	public void compileTerm() {

		compileFactor();

		if (t.hasMoreTokens()) {

			String op = t.getToken().getValue();

			if (eat("*") || eat("/")) {
				compileTerm();

				if (op.equals("*")) {
					// stack.push(num1 * num2);
				} else {
					// stack.push(num1 / num2);
				}

			}
		}
	}

	public void compileFactor() {
		if (eat("(")) {
			compileExpression();
			eat(")");
		} else if (TokenUtils.isInteger(t.getToken().getValue())) {
			// stack.push(Float.valueOf(t.getToken().getValue()));
			t.advance();
		} else if (TokenUtils.isNumber(t.getToken().getValue())) {
			t.advance();
		} else if (TokenUtils.isBoolean(t.getToken().getValue())) {
			t.advance();
		} else if (TokenUtils.isString(t.getToken().getValue())) {
			t.advance();
		} else if (TokenUtils.isUraniaryOperator(t.getToken().getValue())) {
			t.advance();
			compileTerm();
		} else {

			t.mark();
			compileIdentifier();

			if (eat("[")) {
				compileExpression();
				eatHard("]");
			} else {
				t.reset();
				compileMethodCall();
			}
			/// throw new RuntimeException("Error at token: " + t.getToken());
		}

	}

	public void eatHard(String s) {

		if (!t.hasMoreTokens()) {
			throw new RuntimeException("No more tokens available");
		}

		if (t.getToken().getValue().equals(s)) {
			t.advance();

		} else {
			throw new RuntimeException("Token " + t + "  doesn't match with string: " + s);
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
