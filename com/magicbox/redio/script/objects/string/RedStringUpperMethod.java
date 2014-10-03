package com.magicbox.redio.script.objects.string;

import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedStringUpperMethod extends RedStringBaseMethod
{
	public RedStringUpperMethod(RedStringObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 0)
			throw new RuntimeException("string.upper() takes no arguments.");

		return getParent().upper();
	}
}
