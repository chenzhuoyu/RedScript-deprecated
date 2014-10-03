package com.magicbox.redio.script.compiler;

public class Token
{
	public int token = 0;
	public int intValue = 0;
	public String stringValue = "";

	public Token(int token)
	{
		this.token = token;
	}

	public static final int STRING = 0;
	public static final int KEYWORD = 1;
	public static final int INTEGER = 2;
	public static final int OPERATOR = 3;
	public static final int IDENTIFIER = 4;

	public static final String BINARY_CONSTANT = "01";
	public static final String NUMBER_CONSTANT = "0123456789";
	public static final String HEX_NUMBER_CONSTANT = "0123456789abcdefABCDEF";

	public static final String OPERATOR_CONSTANT = "+-*/%|&^~=<>!,.()[]{}:;";
	public static final String IDENTIFIER_CONSTANT = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
}
