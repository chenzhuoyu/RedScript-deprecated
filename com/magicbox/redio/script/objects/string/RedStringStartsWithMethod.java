package com.magicbox.redio.script.objects.string;

import com.magicbox.redio.script.objects.RedBoolObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedStringStartsWithMethod extends RedStringBaseMethod
{
	public RedStringStartsWithMethod(RedStringObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 1)
			throw new RuntimeException("string.startswith() takes exactly 1 argument.");

		RedObject arg0 = args.get(0);

		if (!(arg0 instanceof RedStringObject))
			throw new RuntimeException("string.startswith() only accepts strings as its first argument.");

		return RedBoolObject.fromBoolean(getParent().startsWith((RedStringObject)arg0));
	}
}
