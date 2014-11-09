package com.magicbox.redio.script.objects;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.magicbox.redio.script.objects.array.RedArrayObject;
import com.magicbox.redio.script.objects.string.RedStringObject;

public class RedObject
{
	private HashMap<String, RedObject> attrs = new HashMap<String, RedObject>();

	public boolean isNull()
	{
		return this == RedNullObject.nullObject;
	}

	public boolean isTrue()
	{
		return !isNull() && this != RedBoolObject.falseObject && this != RedIntObject.fromInteger(0);
	}

	@Override
	public String toString()
	{
		return "<type '" + getClass().getSimpleName() + "'>";
	}

	// Object protocol

	public RedObject __getattr__(RedObject name)
	{
		if (name instanceof RedStringObject)
			return getAttribute(name.toString());

		throw new RuntimeException("Invalid argument of '__setattr__'.");
	}

	public RedObject __setattr__(RedObject name, RedObject object)
	{
		if (!(name instanceof RedStringObject))
			throw new RuntimeException("Invalid argument of '__setattr__'.");

		setAttribute(name.toString(), object);
		return RedNullObject.nullObject;
	}

	public RedObject __bool_or__(RedObject other)
	{
		return RedBoolObject.fromBoolean(isTrue() || other.isTrue());
	}

	public RedObject __bool_and__(RedObject other)
	{
		return RedBoolObject.fromBoolean(isTrue() && other.isTrue());
	}

	public RedObject __bool_xor__(RedObject other)
	{
		return RedBoolObject.fromBoolean(isTrue() != other.isTrue());
	}

	public RedObject __bool_not__()
	{
		return RedBoolObject.fromBoolean(!isTrue());
	}

	// RedObject __call__(RedArrayObject args);
	// RedObject __getitem__(RedObject item);
	// RedObject __setitem__(RedObject item, RedObject value);

	// RedObject __pos__();
	// RedObject __neg__();

	// RedObject __add__(RedObject other);
	// RedObject __sub__(RedObject other);
	// RedObject __mul__(RedObject other);
	// RedObject __div__(RedObject other);
	// RedObject __mod__(RedObject other);
	// RedObject __power__(RedObject other);

	// RedObject __bit_or__(RedObject other);
	// RedObject __bit_and__(RedObject other);
	// RedObject __bit_xor__(RedObject other);
	// RedObject __bit_not__();

	// RedObject __lshift__(RedObject other);
	// RedObject __rshift__(RedObject other);

	// RedObject __eq__(RedObject other);
	// RedObject __le__(RedObject other);
	// RedObject __ge__(RedObject other);
	// RedObject __neq__(RedObject other);
	// RedObject __leq__(RedObject other);
	// RedObject __geq__(RedObject other);

	// RedObject __cmp__(RedObject other);

	// RedObject __inplace_add__(RedObject other);
	// RedObject __inplace_sub__(RedObject other);
	// RedObject __inplace_mul__(RedObject other);
	// RedObject __inplace_div__(RedObject other);
	// RedObject __inplace_mod__(RedObject other);
	// RedObject __inplace_power__(RedObject other);

	// RedObject __inplace_bit_or__(RedObject other);
	// RedObject __inplace_bit_and__(RedObject other);
	// RedObject __inplace_bit_xor__(RedObject other);

	// RedObject __inplace_lshift__(RedObject other);
	// RedObject __inplace_rshift__(RedObject other);

	// RedObject __bool_or__(RedObject other);
	// RedObject __bool_and__(RedObject other);
	// RedObject __bool_xor__(RedObject other);
	// RedObject __bool_not__();

	// Protocol wrapper

	public RedObject getAttribute(String name)
	{
		return attrs.containsKey(name) ? attrs.get(name) : RedNullObject.nullObject;
	}

	public void setAttribute(String name, RedObject object)
	{
		attrs.put(name, object);
	}

	public RedObject invoke(RedObject... args)
	{
		RedObject object = __getattr__(new RedStringObject("__call__"));

		if (!object.isNull())
		{
			if (object instanceof RedFunctionObject)
				return object.invoke(args);

			throw new RuntimeException("Object " + object.getClass().getName() + " is not callable.");
		}
		else
		{
			try
			{
				Object result = getClass().getMethod("__call__", RedArrayObject.class).invoke(this, new RedArrayObject(args));

				if (result instanceof RedObject)
					return (RedObject)result;

				throw new RuntimeException("Object " + getClass().getName() + " is not callable.");
			} catch (InvocationTargetException e)
			{
				throw new RuntimeException(e.getCause().getLocalizedMessage());
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e)
			{
				throw new RuntimeException("Object " + getClass().getName() + " is not callable.");
			}

		}
	}

	public RedObject invokeMethod(String method, RedObject... args)
	{
		RedObject object = __getattr__(new RedStringObject(method));

		if (!object.isNull())
		{
			if (object instanceof RedFunctionObject)
				return object.invoke(args);

			throw new RuntimeException("Object " + object.getClass().getName() + " doesn't support " + method + ".");
		}
		else
		{
			try
			{
				Class<?> [] classes = new Class<?> [args.length];

				for (int i = 0; i < args.length; i++)
					classes[i] = RedObject.class;

				Object result = getClass().getMethod(method, classes).invoke(this, (Object [])args);

				if (result instanceof RedObject)
					return (RedObject)result;

				throw new RuntimeException("Object " + getClass().getName() + " doesn't support " + method + ".");
			} catch (InvocationTargetException e)
			{
				throw new RuntimeException(e.getCause().getLocalizedMessage());
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e)
			{
				throw new RuntimeException("Object " + getClass().getName() + " doesn't support " + method + ".");
			}
		}
	}
}
