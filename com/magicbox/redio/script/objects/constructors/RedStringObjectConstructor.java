package com.magicbox.redio.script.objects.constructors;

import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;
import com.magicbox.redio.script.objects.string.RedStringObject;

public class RedStringObjectConstructor extends RedObject
{
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 1)
			throw new RuntimeException("string() takes exactly 1 argument.");

		return new RedStringObject(args.get(0).toString());
	}
}
