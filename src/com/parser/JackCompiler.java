package com.parser;

import com.assembler.MemoryMap;
import com.parser.tokenizer.TokenUtils;
import com.parser.tokenizer.Tokenizer;

import symboltable.Symbol;
import symboltable.SymbolTable;
import symboltable.SymbolTableManager;

public class JackCompiler {

	private SymbolTable cst = null;
	private int classVarIndex = 0;
	private static int staticIndex = 0;
	private static int labelNameIndex = 0;
	private String className = null;
	private StringBuffer code = new StringBuffer(500);

	private int codeLine = 0;

	private Tokenizer t;

	public JackCompiler(Tokenizer t) {
		this.t = t;
	}

	private void print(String s) {
		code.append(s);
		// code.append("\n");
	}

	private void printLine(String s) {
		code.append("\n");
		code.append(s);
		codeLine++;

	}

	private void push(String s) {
		printLine("push " + s);
		MemoryMap.incrementSP();
	}

	private void pop(String s) {
		printLine("pop " + s);
		MemoryMap.decrementSP();
	}

	public void compileClass() {

		if (eat("class")) {
			className = t.getToken().getValue();

			cst = new SymbolTable();
			SymbolTableManager.addSymbolTable(className, cst);
			cst.addSymbol(new Symbol("this", className, "pointer"));

			String className = t.getToken().getValue();

			compileIdentifier(); // class name

			cst.setParentName(className);

			eatHard("{");

			compileClassVariables();

			compileSubRoutines();

			eatHard("}");

		}
	}

	public void compileClassVariables() {

		String fieldType = t.getToken().getValue();
		int classVarCount = 0;

		while (eat("static") || (eat("field"))) {

			String kindType = fieldType.equals("field") ? "this" : "static";

			String type = t.getToken().getValue();

			compileType();

			String varName = t.getToken().getValue();

			compileIdentifier(); // variable name

			String kind = null;

			if (kindType.equals("this")) {
				kind = kindType + " " + classVarIndex++;
				classVarCount++;
			} else {
				kind = kindType + " " + staticIndex++;
			}

			Symbol s = new Symbol(varName, type, kind);
			cst.addSymbol(s);

			while (eat(",")) {
				varName = t.getToken().getValue();
				compileIdentifier(); // variable name

				s = new Symbol(varName, type, kind);
				cst.addSymbol(s);
			}

			eatHard(";");
		}

		CompilerCache.setClassVarCount(cst.getParentName(), classVarCount);

	}

	public void compileSubRoutines() {

		String methodType = t.getToken().getValue();
		boolean isConstructor = false;

		while (eat("constructor") || (eat("function")) || (eat("method"))) {

			if (methodType.equals("constructor")) {
				isConstructor = true;
			}

			int numArgs = 0;
			String returnType = "void";

			if (!eat("void")) {
				returnType = t.getToken().getValue();
				compileType();
			}

			String methodName = t.getToken().getValue();

			compileIdentifier(); // method name

			SymbolTable mst = SymbolTableManager.createChildSymbolTable(className);

			String mName = className + "." + methodName;

			mst.setParentName(mName);

			printLine("function " + mName + " ");

			eatHard("(");

			t.mark();
			// if next token is not closing bracket
			if (!eat(")")) {
				numArgs = compileParameterList(mst);
				CompilerCache.setMethodArgCount(mName, numArgs);
			} else {
				CompilerCache.setMethodArgCount(mName, 0);
				t.reset();
			}

			eatHard(")");

			compileMethodBody(mst, returnType, isConstructor);

		}

	}

	public int compileParameterList(SymbolTable mst) {

		int argIndex = 0;
		do {
			String type = t.getToken().getValue();
			compileType();
			String argName = t.getToken().getValue();
			compileIdentifier(); // parameter name

			Symbol s = new Symbol(argName, type, "argument " + argIndex++);
			mst.addSymbol(s);
		} while (eat(","));

		return argIndex;

	}

	public void compileMethodBody(SymbolTable mst, String returnType, boolean isConstructor) {

		// If it is a constructor create object in memory
		if (isConstructor) {
			printLine("push " + CompilerCache.getClassVarCount(cst.getParentName()));
			printLine("goto Memory.alloc 1");
			printLine("pop pointer 0");
		}

		eatHard("{");

		t.mark();

		if (eat("var")) {
			t.reset();
			int localVarNum = compileLocalVariables(mst);

			CompilerCache.setMethodLCLCount(mst.getParentName(), localVarNum);

			print(localVarNum + "");
		}
		compileStatements(mst, returnType, isConstructor);
		
		printLine("endFrame=LCL");
		printLine("returnAddress=*(endFrame-5)");
		printLine("argument 0 =*(SP-1))"); // copy last value in stack to argument 0
		printLine("SP=ARG+1");
		printLine("THAT=*(endFrame-1)");
		printLine("THIS=*(endFrame-2)");
		printLine("ARG=*(endFrame-3)");
		printLine("LCL=*(endFrame-4)");
		printLine("goto returnAddress");

		eatHard("}");

	}

	public int compileLocalVariables(SymbolTable mst) {

		int localVar = 0;

		while (eat("var")) {
			String type = t.getToken().getValue();
			compileType();

			String varName = t.getToken().getValue();
			compileIdentifier(); // variable name

			Symbol s = new Symbol(varName, type, "local " + localVar++);
			mst.addSymbol(s);

			while (eat(",")) {

				varName = t.getToken().getValue();
				compileIdentifier();

				s = new Symbol(varName, type, "local " + localVar++);
				mst.addSymbol(s);
			}

			eatHard(";");
		}

		return localVar;
	}

	public void compileStatements(SymbolTable mst) {
		compileStatements(mst, null, false);
	}

	public void compileStatements(SymbolTable mst, String returnType, boolean isConstructor) {

		t.mark();

		if (eat("let")) {
			t.reset();
			compileLetStmt(mst);
		} else if (eat("if")) {
			t.reset();
			compileIfStmt(mst);
		} else if (eat("while")) {
			t.reset();
			compileWhileStmt(mst);
		} else if (eat("do")) {
			t.reset();
			compileDoStmt();
		} else if (eat("return")) {
			t.reset();
			compileReturnStmt(mst, returnType, isConstructor);
		}

	}

	public void compileWhileStmt(SymbolTable mst) {

		String startLabel = "whileStartLabel" + labelNameIndex++;
		String endLabel = "whileEndLabel" + labelNameIndex++;

		eatHard("while");
		eatHard("(");

		printLine("Label: " + startLabel);

		compileLogicalExp(mst);
		printLine("not");

		printLine("IF-NOT_TRUE:" + endLabel);

		eatHard(")");
		eatHard("{");

		compileStatements(mst);
		eatHard("}");

		printLine("go-to:" + startLabel);

		printLine("Label: " + endLabel);

	}

	public void compileMethodCall(SymbolTable cst) {

		compileIdentifier();

		if (eat("(")) {
			if (!eat(")")) {
				compileMethodExpressionList(null);
				eatHard(")");
			}
		} else if (eat(".")) {
			compileIdentifier();
			eatHard("(");

			if (!eat(")")) {
				compileMethodExpressionList(null);
				eatHard(")");
			}
		} else {
			new RuntimeException("No method call available");
		}

	}

	/**
	 * This method automatically pushes arguments on the stack.
	 * 
	 * @param mst
	 */
	public void compileMethodExpressionList(SymbolTable mst) {

		compileExpression(mst);

		while (eat(",")) {
			compileExpression(mst);
		}
	}

	public void compileDoStmt() {
		eatHard("do");

		String methodName = t.getToken().getValue();

		// printLine("push " + (codeLine + 1)); // return address
		// printLine("push " + MemoryMap.getMemorySegValue(MemoryMap.LCL));
		compileMethodCall(cst);

		printLine("push RETURN_ADDRESS"); // return address
		printLine("push LCL");
		printLine("push ARG");
		printLine("push  THIS");
		printLine("push THAT");
		printLine("ARG= SP-" + (5 - CompilerCache.getMethodArgCount(methodName)));
		printLine("LCL=SP");
		printLine("goto " + methodName);
		eatHard(";");
	}

	public void compileReturnStmt(SymbolTable mst, String returnType, boolean isConstructor) {

		eatHard("return");
		t.mark();

		if (!eat(";")) {
			if (returnType != null && returnType.equals("boolean")) {
				compileLogicalExp(mst);
			} else {
				compileExpression(mst);
			}
		} else if (isConstructor) {
			printLine("push pointer 0");
			t.reset();
		} else {
			printLine("push 0");
			t.reset();
		}
		eatHard(";");

	}

	public void compileLetStmt(SymbolTable mst) {

		eatHard("let");

		String name = t.getToken().getValue();
		compileIdentifier();

		String type = mst.findSymbol(name).getType();

		if (eat("[")) {
			compileExpression(mst);
			eatHard("]");
		}

		eatHard("=");

		if (type.equals("boolean")) {
			compileLogicalExp(mst);
		} else {
			compileExpression(mst);
		}

		eatHard(";");
		printLine("pop " + mst.findSymbol(name).getKind());

	}

	public void compileIfStmt(SymbolTable mst) {

		String endLabel = "endLabel" + labelNameIndex++;
		String elseLabel = "elseLabel" + labelNameIndex++;

		eatHard("if");
		eatHard("(");
		compileLogicalExp(mst);
		eatHard(")");

		eatHard("{");

		printLine("not");
		printLine("if-not-goto:" + elseLabel);

		compileStatements(mst);
		eatHard("}");

		printLine("go-to:" + endLabel);

		printLine("Label:" + elseLabel);

		if (eat("else")) {
			eatHard("{");
			compileStatements(mst);
			eatHard("}");
		}

		printLine("Label:" + endLabel);
	}

	public void compileLogicalExp(SymbolTable mst) {

		compileLogicalTerm(mst);

		if (t.hasMoreTokens()) {
			String op = t.getToken().getValue();

			if (eat("||")) {
				compileLogicalExp(mst);

				if (op.equals("||")) {
					printLine("or");
				}
			}
		}
	}

	public void compileLogicalTerm(SymbolTable mst) {

		if (eat("(")) {
			compileLogicalExp(mst);
			eatHard(")");
		} else {
			compileLogicalFactor(mst);

			if (t.hasMoreTokens()) {

				String op = t.getToken().getValue();

				if (eat("&&")) {
					compileLogicalTerm(mst);

					if (op.equals("&&")) {
						printLine("and");
					}
				}
			}
		}
	}

	public void compileLogicalFactor(SymbolTable mst) {

		if (t.getToken().getValue().equals("true") || t.getToken().getValue().equals("false")) {
			compileLogicalFactorFactor(mst);
		}

		else {

			compileExpression(mst);

			String op = t.getToken().getValue();

			if (eat(">=") || eat("<=") || eat("<") || eat(">")) {
				compileExpression(mst);

				if (op.equals("<")) {
					printLine("lt");
				} else if (op.equals(".")) {
					printLine("gt");
				} else if (op.equals("<=")) {
					printLine("lte");
				} else if (op.equals(">=")) {
					printLine("gte");
				}
			}
		}
	}

	public void compileLogicalFactorFactor(SymbolTable mst) {

		if (eat("(")) {
			compileLogicalExp(mst);
			eat(")");
		} else if (t.getToken().getValue().equals("true")) {
			printLine("push 1");
			t.advance();
		} else if (t.getToken().getValue().equals("false")) {
			printLine("push 0");
			t.advance();
		} else {

			t.mark();
			compileIdentifier();

			if (eat("[")) {
				compileExpression(mst);
				eatHard("]");
			} else {
				t.reset();
				compileMethodCall(cst);
			}
			/// throw new RuntimeException("Error at token: " + t.getToken());
		}
	}

	public void compileExpression(SymbolTable mst) {

		compileTerm(mst);

		if (t.hasMoreTokens()) {
			String op = t.getToken().getValue();

			if (eat("+") || eat("-")) {
				compileExpression(mst);

				if (op.equals("+")) {
					printLine("add");
				} else {
					printLine("sub");
				}
			}
		}
	}

	public void compileTerm(SymbolTable mst) {

		compileFactor(mst);

		if (t.hasMoreTokens()) {

			String op = t.getToken().getValue();

			if (eat("*") || eat("/")) {
				compileTerm(mst);

				if (op.equals("*")) {
					printLine("multiply");
				} else {
					printLine("divide");
				}

			}
		}
	}

	public void compileFactor(SymbolTable mst) {
		if (eat("(")) {
			compileExpression(mst);
			eat(")");
		} else if (TokenUtils.isInteger(t.getToken().getValue())) {
			printLine("push " + t.getToken().getValue());
			t.advance();
		} else if (TokenUtils.isNumber(t.getToken().getValue())) {
			printLine("push " + t.getToken().getValue());
			t.advance();
		} else if (TokenUtils.isBoolean(t.getToken().getValue())) {
			printLine("push " + (t.getToken().getValue().equals("true") ? "1" : "0"));
			t.advance();
		} else if (TokenUtils.isString(t.getToken().getValue())) {
			printLine("push " + t.getToken().getValue());
			t.advance();
		} else if (TokenUtils.isUraniaryOperator(t.getToken().getValue())) {
			String operator = t.getToken().getValue();

			if (operator.equals("--")) {
				printLine("push 1");
				printLine("sub");
			} else if (operator.equals("++")) {
				printLine("push 1");
				printLine("add");
			} else if (operator.equals("!")) {
				printLine("not");
			}

			t.advance();
			compileTerm(mst);
		} else {

			t.mark();
			compileIdentifier();

			if (eat("[")) {
				compileExpression(mst);
				eatHard("]");
			} else {
				t.reset();
				compileMethodCall(cst);
			}
			/// throw new RuntimeException("Error at token: " + t.getToken());
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
