package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedIntObject;
import com.magicbox.redio.script.objects.RedObject;

public class RedArrayIndexMethod extends RedArrayBaseMethod
{
	public RedArrayIndexMethod(RedArrayObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 1)
			throw new RuntimeException("array.index() takes exactly 1 argument.");

		return RedIntObject.fromInteger(getParent().index(args.get(0)));
	}
}
