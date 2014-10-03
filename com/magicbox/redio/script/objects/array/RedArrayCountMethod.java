package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedIntObject;
import com.magicbox.redio.script.objects.RedObject;

public class RedArrayCountMethod extends RedArrayBaseMethod
{
	public RedArrayCountMethod(RedArrayObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 1)
			throw new RuntimeException("array.append() takes exactly 1 argument.");

		return RedIntObject.fromInteger(getParent().count(args.get(0)));
	}
}
