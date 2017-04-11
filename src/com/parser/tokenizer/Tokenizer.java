package com.parser.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

	private int storedIndex = 0;
	private int index = 0;
	private String text = "";
	private List<Token> tokens = new ArrayList<>();
	private Token lastToken = null;

	public Tokenizer(String text) {
		this.text = text;
	}

	public Token getLastToken() {
		return lastToken;
	}

	public boolean hasMoreTokens() {
		return index < tokens.size();
	}

	public void mark() {
		storedIndex = index;
	}

	public void reset() {
		index = storedIndex;
		storedIndex = 0;

		if (index > 0) {
			lastToken = tokens.get(index - 1);
		} else {
			lastToken = null;
		}
	}

	public void advance() {

		if (index < tokens.size()) {
			lastToken = tokens.get(index);
		}

		index++;

		if (index == text.length()) {
			throw new RuntimeException("No more token available");
		}
	}

	public Token getToken() {
		return tokens.get(index);
	}

	private void processCollectedChars(StringBuffer sb, int index) {
		String s = sb.toString();

		if (s != null && s.length() > 0) {
			if (TokenUtils.isInteger(s)) {
				tokens.add(new Token(TokenConstants.INTEGER_TYPE, s, index));
			} else if (TokenUtils.isNumber(s)) {
				tokens.add(new Token(TokenConstants.NUMBER_TYPE, s, index));
			} else if (TokenUtils.isKeyword(s)) {
				tokens.add(new Token(TokenConstants.KEYWORD_TYPE, s, index));
			} else if (TokenUtils.isIdentifier(s)) {
				tokens.add(new Token(TokenConstants.IDENTIFIER_TYPE, s, index));
			} else if (TokenUtils.isString(s)) {
				tokens.add(new Token(TokenConstants.STRING_TYPE, s, index));
			} else {

				throw new RuntimeException("Cannot determine type of string: " + s);
			}
		}

		sb.setLength(0);
	}

	private String getOpSubType(String op) {

		if (TokenUtils.isArithmaticOperator(op)) {
			return TokenConstants.ARITHMATIC_OPERATOR_TYPE;
		} else if (TokenUtils.isLogicalOperator(op)) {
			return TokenConstants.LOGICAL_OPERATOR_TYPE;
		} else if (TokenUtils.isUraniaryOperator(op)) {
			return TokenConstants.URINARY_OPERATOR_TYPE;
		}
		return "";
	}

	public void parse() {

		StringBuffer sb = new StringBuffer();

		int i = 0;

		while (i < text.length()) {
			String ch = text.substring(i, i + 1);

			if (TokenUtils.isOperator(ch)) {

				processCollectedChars(sb, i - sb.length());

				String nextCh = null;
				String prevCh = null;

				if (i + 1 < text.length()) {
					nextCh = text.substring(i + 1, i + 2);
				}

				if (i - 1 > 0) {
					prevCh = text.substring(i - 1, i);
				}

				if (nextCh != null) {
					if (TokenUtils.isOperator(ch + nextCh)) {
						tokens.add(new Token(TokenConstants.OPERATOR_TYPE, getOpSubType(ch + nextCh), ch + nextCh, i));
						i++;
					}
					// for negative integers like -2, -23
					else if ((prevCh == null || !TokenUtils.isBracket(prevCh) && !TokenUtils.isOperator(prevCh)
							&& !TokenUtils.isNumber(prevCh)) && ch.equals("-") && TokenUtils.isNumber(nextCh)) {
						sb.append(ch);
					} else {
						tokens.add(new Token(TokenConstants.OPERATOR_TYPE, getOpSubType(ch), ch, i));
					}

				} else {
					tokens.add(new Token(TokenConstants.OPERATOR_TYPE, getOpSubType(ch), ch, i));
				}
			} else if (TokenUtils.isBracket(ch)) {
				processCollectedChars(sb, i - sb.length());
				tokens.add(new Token(ch, ch, i));
			}

			else if (ch.equals(TokenConstants.COMMA)) {
				processCollectedChars(sb, i - sb.length());
				tokens.add(new Token(TokenConstants.COMMA_TYPE, ch, i));
			}

			else if (ch.equals("\n")) {
				processCollectedChars(sb, i - sb.length());
			}

			else if (ch.equals("/")) {
				String nextCh = text.substring(i + 1, i + 2);
				sb.append(nextCh);
				i++;
			}

			else if (ch.equals(TokenConstants.EQUALS)) {
				processCollectedChars(sb, i - sb.length());

				String nextCh = text.substring(i + 1, i + 2);

				if (TokenUtils.isOperator(ch + nextCh)) {
					tokens.add(new Token(TokenConstants.OPERATOR_TYPE, getOpSubType(ch + nextCh), ch + nextCh, i + 1));
					i++;
				} else {
					tokens.add(new Token(ch, ch, i));
				}
			}

			else if (ch.equals("&") || ch.equals("|")) {
				processCollectedChars(sb, i - sb.length());

				String nextCh = text.substring(i + 1, i + 2);

				if (TokenUtils.isOperator(ch + nextCh)) {
					tokens.add(new Token(TokenConstants.OPERATOR_TYPE, getOpSubType(ch + nextCh), ch + nextCh, i + 1));
					i++;
				} else {
					// tokens.add(new Token(ch, ch, i));
				}
			}

			else if (ch.equals(TokenConstants.SPACE) || ch.equals(TokenConstants.SEMICOLON)) {
				processCollectedChars(sb, i - sb.length());
			} else {
				sb.append(ch);
			}

			i++;
		}

		processCollectedChars(sb, i - sb.length());

	}

}
