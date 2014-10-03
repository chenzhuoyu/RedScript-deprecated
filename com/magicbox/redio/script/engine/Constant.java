package com.magicbox.redio.script.engine;

import java.io.Serializable;

public class Constant implements Serializable
{
	private static final long serialVersionUID = 20141003L;

	public int type = 0;
	public int intValue = 0;
	public String stringValue = "";

	public static final int STRING = 0;
	public static final int INTEGER = 1;
}
