package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;

public class RedArraySortMethod extends RedArrayBaseMethod
{
	public RedArraySortMethod(RedArrayObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		switch (args.size())
		{
			case 0:
				getParent().sort();
				return RedNullObject.nullObject;

			case 1:
				getParent().sort(args.get(0));
				return RedNullObject.nullObject;

			default:
				throw new RuntimeException("array.sort() takes at most 1 argument.");
		}
	}
}
