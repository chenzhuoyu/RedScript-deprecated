package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;

public class RedArrayAppendMethod extends RedArrayBaseMethod
{
	public RedArrayAppendMethod(RedArrayObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 1)
			throw new RuntimeException("array.append() takes exactly 1 argument.");

		getParent().add(args.get(0));
		return RedNullObject.nullObject;
	}
}
