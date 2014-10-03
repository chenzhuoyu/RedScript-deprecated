package com.magicbox.redio.script.compiler;

import java.util.ArrayList;

import javax.script.ScriptException;

public class Tokenizer
{
	private int x = 1;
	private int y = 1;
	private int pos = 0;
	private Token cached = null;

	private String source = "";
	private String filename = "";

	private ArrayList<String> boolOps = new ArrayList<String>();
	private ArrayList<String> keywords = new ArrayList<String>();

	public Tokenizer(String filename, String source)
	{
		this.source = source;
		this.filename = filename;

		boolOps.add("or");
		boolOps.add("and");
		boolOps.add("xor");
		boolOps.add("not");

		keywords.add("if");
		keywords.add("for");
		keywords.add("case");
		keywords.add("else");
		keywords.add("func");
		keywords.add("break");
		keywords.add("while");
		keywords.add("return");
		keywords.add("switch");
		keywords.add("default");
		keywords.add("continue");
	}

	private char peekChar()
	{
		return pos < source.length() ? source.charAt(pos) : 0;
	}

	private char readChar()
	{
		if (pos > source.length())
			return 0;

		char result = source.charAt(pos);

		if (result == '\n')
		{
			y++;
			x = 0;
		}

		x++;
		pos++;
		return result;
	}

	private void skipSpace()
	{
		char ch = peekChar();

		while (" \t\r\n".indexOf(ch) >= 0)
		{
			readChar();
			ch = peekChar();
		}
	}

	private void skipComment()
	{
		while (peekChar() == '#')
		{
			char ch = readChar();

			while (ch != 0 && ch != '\n')
				ch = readChar();

			skipSpace();
		}
	}

	private Token scanString() throws ScriptException
	{
		Token result = new Token(Token.STRING);

		char start = readChar();
		char remains = readChar();

		while (start != remains)
		{
			switch (remains)
			{
				case 0:
					throw new ScriptException("EOF seen but string not ternimated", filename, y, x);

				case '\\':
				{
					remains = readChar();

					switch (remains)
					{
						case 0:
							throw new ScriptException("EOF seen but string not ternimated", filename, y, x);

						case '0':
							remains = 0;
							break;

						case 't':
							remains = '\t';
							break;

						case 'r':
							remains = '\r';
							break;

						case 'n':
							remains = '\n';
							break;
					}
				}
			}

			result.stringValue += remains;
			remains = readChar();
		}

		return result;
	}

	private Token scanInteger()
	{
		int base = 10;
		char number = readChar();
		String charset = Token.NUMBER_CONSTANT;

		Token result = new Token(Token.INTEGER);
		String buffer = String.valueOf(number);

		if (number == '0')
		{
			int follow = peekChar();

			switch (follow)
			{
				case 'b':
				{
					base = 2;
					charset = Token.BINARY_CONSTANT;
					break;
				}

				case 'x':
				{
					base = 16;
					charset = Token.HEX_NUMBER_CONSTANT;
					break;
				}

				default:
				{
					result.intValue = 0;
					return result;
				}
			}
		}

		char follow = peekChar();

		while (charset.indexOf(follow) >= 0)
		{
			readChar();
			buffer += follow;
			follow = peekChar();
		}

		result.intValue = Integer.parseInt(buffer, base);
		return result;
	}

	private Token scanOperator() throws ScriptException
	{
		char op = readChar();
		Token result = new Token(Token.OPERATOR);

		result.stringValue = String.valueOf(op);

		if (op == '!')
		{
			char follow = peekChar();

			if (follow != '=')
				throw new ScriptException("Invalid operator '" + follow + "'", filename, y, x);

			result.stringValue += readChar();
			return result;
		}
		else if ("~.,()[]{}:;".indexOf(op) >= 0)
		{
			return result;
		}
		else if ("*<>".indexOf(op) >= 0)
		{
			if (peekChar() == op)
				result.stringValue += readChar();
		}

		if (peekChar() == '=')
			result.stringValue += readChar();

		return result;
	}

	private Token scanIdentifier()
	{
		char first = readChar();
		char follow = peekChar();
		Token result = new Token(Token.IDENTIFIER);
		String charset = Token.NUMBER_CONSTANT + Token.IDENTIFIER_CONSTANT;

		result.stringValue = String.valueOf(first);

		while (charset.indexOf(follow) >= 0)
		{
			result.stringValue += readChar();
			follow = peekChar();
		}

		if (boolOps.contains(result.stringValue))
			result.token = Token.OPERATOR;
		else if (keywords.contains(result.stringValue))
			result.token = Token.KEYWORD;

		return result;
	}

	private Token scan() throws ScriptException
	{
		skipSpace();
		skipComment();

		char first = peekChar();

		if (first == 0)
			return null;
		else if (first == '"' || first == '\'')
			return scanString();
		else if (Token.NUMBER_CONSTANT.indexOf(first) >= 0)
			return scanInteger();
		else if (Token.OPERATOR_CONSTANT.indexOf(first) >= 0)
			return scanOperator();
		else if (Token.IDENTIFIER_CONSTANT.indexOf(first) >= 0)
			return scanIdentifier();
		else
			throw new ScriptException("Illegal character '" + first + "'", filename, y, x);
	}

	public int x()
	{
		return x;
	}

	public int y()
	{
		return y;
	}

	public String filename()
	{
		return filename;
	}

	public boolean eof() throws ScriptException
	{
		return peek() == null;
	}

	public Token next() throws ScriptException
	{
		Token result = cached;

		if (result == null)
			result = scan();

		cached = null;
		return result;
	}

	public Token peek() throws ScriptException
	{
		if (cached == null)
			cached = scan();

		return cached;
	}
}
