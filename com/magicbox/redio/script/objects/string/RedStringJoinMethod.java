package com.magicbox.redio.script.objects.string;

import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedStringJoinMethod extends RedStringBaseMethod
{
	public RedStringJoinMethod(RedStringObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 1)
			throw new RuntimeException("string.join() takes exactly 1 argument.");

		RedObject arg0 = args.get(0);

		if (!(arg0 instanceof RedArrayObject))
			throw new RuntimeException("string.join() only accepts arrays as its first argument.");

		return getParent().join((RedArrayObject)arg0);
	}
}
