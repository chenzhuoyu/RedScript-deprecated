package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;

public class RedArrayReverseMethod extends RedArrayBaseMethod
{
	public RedArrayReverseMethod(RedArrayObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 0)
			throw new RuntimeException("array.append() takes no arguments.");

		getParent().reverse();
		return RedNullObject.nullObject;
	}
}
