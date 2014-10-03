package com.magicbox.redio.script.objects.string;

import com.magicbox.redio.script.objects.RedIntObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedStringObject extends RedObject
{
	private String string = "";

	public RedStringObject(String string)
	{
		this.string = string;
		setAttribute("find", new RedStringFindMethod(this));
		setAttribute("join", new RedStringJoinMethod(this));
		setAttribute("strip", new RedStringStripMethod(this));
		setAttribute("lower", new RedStringLowerMethod(this));
		setAttribute("upper", new RedStringUpperMethod(this));
		setAttribute("split", new RedStringSplitMethod(this));
		setAttribute("isdigit", new RedStringIsDigitMethod(this));
		setAttribute("replace", new RedStringReplaceMethod(this));
		setAttribute("endswith", new RedStringEndsWithMethod(this));
		setAttribute("startswith", new RedStringStartsWithMethod(this));
	}

	@Override
	public String toString()
	{
		return string;
	}

	public void setString(String string)
	{
		this.string = string;
	}

	public RedArrayObject split(RedStringObject sep)
	{
		RedArrayObject result = new RedArrayObject();

		for (String s : string.split(sep.toString()))
			result.add(new RedStringObject(s));

		return result;
	}

	public RedArrayObject split(RedStringObject sep, int limit)
	{
		RedArrayObject result = new RedArrayObject();

		for (String s : string.split(sep.toString(), limit))
			result.add(new RedStringObject(s));

		return result;
	}

	public RedStringObject join(RedArrayObject array)
	{
		String result = "";

		for (int i = 0; i < array.size(); i++)
			if (i == array.size() - 1)
				result += array.get(i).toString();
			else
				result += array.get(i).toString() + string;

		return new RedStringObject(result);
	}

	public RedStringObject strip()
	{
		return new RedStringObject(string.trim());
	}

	public RedStringObject lower()
	{
		return new RedStringObject(string.toLowerCase());
	}

	public RedStringObject upper()
	{
		return new RedStringObject(string.toUpperCase());
	}

	public RedStringObject replace(RedStringObject a, RedStringObject b)
	{
		return new RedStringObject(string.replaceAll(a.toString(), b.toString()));
	}

	public int find(RedStringObject other)
	{
		return string.indexOf(other.toString());
	}

	public int find(RedStringObject other, int start)
	{
		return string.indexOf(other.toString(), start);
	}

	public boolean isDigit()
	{
		return string.matches("-?\\d+");
	}

	public boolean endsWith(RedStringObject suffix)
	{
		return string.endsWith(suffix.toString());
	}

	public boolean startsWith(RedStringObject prefix)
	{
		return string.startsWith(prefix.toString());
	}

	@Override
	public RedObject __getattr__(RedObject name)
	{
		if (name instanceof RedStringObject)
			if (name.toString().equals("length"))
				return RedIntObject.fromInteger(string.length());

		return super.__getattr__(name);
	}

	@Override
	public RedObject __setattr__(RedObject name, RedObject object)
	{
		if (name instanceof RedStringObject)
			if (name.toString().equals("length"))
				throw new RuntimeException("Attribute 'length' of string objects is readonly.");

		return super.__setattr__(name, object);
	}

	public RedObject __getitem__(RedObject item)
	{
		if (!(item instanceof RedIntObject))
			throw new RuntimeException("String index must be integers.");

		int index = ((RedIntObject)item).getValue();

		if (index >= string.length())
			throw new RuntimeException("String index out of bounds.");

		if (index < 0)
			index = (string.length() + index) % string.length();

		return new RedStringObject(String.valueOf(string.charAt(index)));
	}

	public RedObject __setitem__(RedObject item, RedObject value)
	{
		throw new RuntimeException("Strings doesn't support item assignment.");
	}
}
