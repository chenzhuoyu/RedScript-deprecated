package com.magicbox.redio.script.objects.string;

import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedStringStripMethod extends RedStringBaseMethod
{
	public RedStringStripMethod(RedStringObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 0)
			throw new RuntimeException("string.strip() takes no arguments.");

		return getParent().strip();
	}
}
