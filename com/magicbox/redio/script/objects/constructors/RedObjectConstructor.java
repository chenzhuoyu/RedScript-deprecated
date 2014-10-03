package com.magicbox.redio.script.objects.constructors;

import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedObjectConstructor extends RedObject
{
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 0)
			throw new RuntimeException("object() takes no arguments.");

		return new RedObject();
	}
}
