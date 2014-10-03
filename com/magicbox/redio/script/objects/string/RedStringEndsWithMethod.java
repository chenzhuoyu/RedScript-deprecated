package com.magicbox.redio.script.objects.string;

import com.magicbox.redio.script.objects.RedBoolObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedStringEndsWithMethod extends RedStringBaseMethod
{
	public RedStringEndsWithMethod(RedStringObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 1)
			throw new RuntimeException("string.endswith() takes exactly 1 argument.");

		RedObject arg0 = args.get(0);

		if (!(arg0 instanceof RedStringObject))
			throw new RuntimeException("string.endswith() only accepts strings as its first argument.");

		return RedBoolObject.fromBoolean(getParent().endsWith((RedStringObject)arg0));
	}
}
