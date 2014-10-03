package com.magicbox.redio.script.objects;

public class RedBoolObject extends RedObject
{
	public static final RedBoolObject trueObject = new RedBoolObject();
	public static final RedBoolObject falseObject = new RedBoolObject();

	public static RedBoolObject fromBoolean(boolean value)
	{
		return value ? trueObject : falseObject;
	}

	@Override
	public String toString()
	{
		return isTrue() ? "true" : "false";
	}
}
