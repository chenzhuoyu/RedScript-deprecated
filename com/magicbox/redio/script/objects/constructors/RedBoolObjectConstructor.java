package com.magicbox.redio.script.objects.constructors;

import com.magicbox.redio.script.objects.RedBoolObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedBoolObjectConstructor extends RedObject
{
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 1)
			throw new RuntimeException("bool() takes exactly 1 argument.");

		return RedBoolObject.fromBoolean(args.get(0).isTrue());
	}
}
