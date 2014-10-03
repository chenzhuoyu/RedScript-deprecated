package com.magicbox.redio.script.objects;

import java.util.HashMap;

import com.magicbox.redio.script.objects.string.RedStringObject;

public class RedIntObject extends RedObject
{
	private int value = 0;
	private static HashMap<Integer, RedIntObject> integerCache = new HashMap<Integer, RedIntObject>();

	private RedIntObject(int value)
	{
		this.value = value;
	}

	public static RedIntObject fromInteger(int value)
	{
		if (integerCache.containsKey(value))
			return integerCache.get(value);

		RedIntObject object = new RedIntObject(value);

		integerCache.put(value, object);
		return object;
	}

	private int convert(RedObject other)
	{
		if (other instanceof RedIntObject)
			return ((RedIntObject)other).getValue();
		else if (other == RedNullObject.nullObject)
			return 0;
		else if (other == RedBoolObject.trueObject)
			return 1;
		else if (other == RedBoolObject.falseObject)
			return 0;
		else
			throw new RuntimeException("Invalid operation with 'int' object.");
	}

	public int getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return value + "";
	}

	public RedObject __pos__()
	{
		return this;
	}

	public RedObject __neg__()
	{
		return RedIntObject.fromInteger(-value);
	}

	public RedObject __add__(RedObject other)
	{
		if (other instanceof RedStringObject)
			return new RedStringObject(value + other.toString());

		int otherValue = convert(other);
		return RedIntObject.fromInteger(value + otherValue);
	}

	public RedObject __sub__(RedObject other)
	{
		int otherValue = convert(other);
		return RedIntObject.fromInteger(value - otherValue);
	}

	public RedObject __mul__(RedObject other)
	{
		int otherValue = convert(other);
		return RedIntObject.fromInteger(value - otherValue);
	}

	public RedObject __div__(RedObject other)
	{
		int otherValue = convert(other);

		if (otherValue == 0)
			throw new RuntimeException("Divide by zero.");

		return RedIntObject.fromInteger(value / otherValue);
	}

	public RedObject __mod__(RedObject other)
	{
		int otherValue = convert(other);

		if (otherValue == 0)
			throw new RuntimeException("Divide by zero.");

		return RedIntObject.fromInteger(value % otherValue);
	}

	public RedObject __power__(RedObject other)
	{
		int otherValue = convert(other);
		return RedIntObject.fromInteger((int)Math.pow(value, otherValue));
	}

	public RedObject __bit_or__(RedObject other)
	{
		int otherValue = convert(other);
		return RedIntObject.fromInteger(value | otherValue);
	}

	public RedObject __bit_and__(RedObject other)
	{
		int otherValue = convert(other);
		return RedIntObject.fromInteger(value & otherValue);
	}

	public RedObject __bit_xor__(RedObject other)
	{
		int otherValue = convert(other);
		return RedIntObject.fromInteger(value ^ otherValue);
	}

	public RedObject __bit_not__()
	{
		return RedIntObject.fromInteger(~value);
	}

	public RedObject __lshift__(RedObject other)
	{
		int otherValue = convert(other);
		return RedIntObject.fromInteger(value << otherValue);
	}

	public RedObject __rshift__(RedObject other)
	{
		int otherValue = convert(other);
		return RedIntObject.fromInteger(value >> otherValue);
	}

	public RedObject __eq__(RedObject other)
	{
		return RedBoolObject.fromBoolean(other instanceof RedIntObject && value == ((RedIntObject)other).getValue());
	}

	public RedObject __le__(RedObject other)
	{
		return RedBoolObject.fromBoolean(other instanceof RedIntObject && value < ((RedIntObject)other).getValue());
	}

	public RedObject __ge__(RedObject other)
	{
		return RedBoolObject.fromBoolean(other instanceof RedIntObject && value > ((RedIntObject)other).getValue());
	}

	public RedObject __neq__(RedObject other)
	{
		return RedBoolObject.fromBoolean(!(other instanceof RedIntObject) || value != ((RedIntObject)other).getValue());
	}

	public RedObject __leq__(RedObject other)
	{
		return RedBoolObject.fromBoolean(other instanceof RedIntObject && value <= ((RedIntObject)other).getValue());
	}

	public RedObject __geq__(RedObject other)
	{
		return RedBoolObject.fromBoolean(other instanceof RedIntObject && value >= ((RedIntObject)other).getValue());
	}

	public RedObject __cmp__(RedObject other)
	{
		if (!(other instanceof RedIntObject))
			throw new RuntimeException("Object int and " + other.getClass().getName() + " are not comparable.");

		int otherValue = ((RedIntObject)other).getValue();
		return RedIntObject.fromInteger(value == otherValue ? 0 : value > otherValue ? 1 : -1);
	}

	public RedObject __inplace_add__(RedObject other)
	{
		return __add__(other);
	}

	public RedObject __inplace_sub__(RedObject other)
	{
		return __sub__(other);
	}

	public RedObject __inplace_mul__(RedObject other)
	{
		return __mul__(other);
	}

	public RedObject __inplace_div__(RedObject other)
	{
		return __div__(other);
	}

	public RedObject __inplace_mod__(RedObject other)
	{
		return __mod__(other);
	}

	public RedObject __inplace_power__(RedObject other)
	{
		return __power__(other);
	}

	public RedObject __inplace_bit_or__(RedObject other)
	{
		return __bit_or__(other);
	}

	public RedObject __inplace_bit_and__(RedObject other)
	{
		return __bit_and__(other);
	}

	public RedObject __inplace_bit_xor__(RedObject other)
	{
		return __bit_xor__(other);
	}

	public RedObject __inplace_lshift__(RedObject other)
	{
		return __lshift__(other);
	}

	public RedObject __inplace_rshift__(RedObject other)
	{
		return __rshift__(other);
	}
}
