package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;

public class RedArrayExtendMethod extends RedArrayBaseMethod
{
	public RedArrayExtendMethod(RedArrayObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 1)
			throw new RuntimeException("array.extend() takes exactly 1 argument.");

		RedObject arg0 = args.get(0);

		if (!(arg0 instanceof RedArrayObject))
			throw new RuntimeException("array.extend() only accepts arrays as its first argument.");

		getParent().extend((RedArrayObject)arg0);
		return RedNullObject.nullObject;
	}
}
