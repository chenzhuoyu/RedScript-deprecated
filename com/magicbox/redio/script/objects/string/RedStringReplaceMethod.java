package com.magicbox.redio.script.objects.string;

import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedStringReplaceMethod extends RedStringBaseMethod
{
	public RedStringReplaceMethod(RedStringObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 2)
			throw new RuntimeException("string.replace() takes exactly 2 argument.");

		RedObject arg0 = args.get(0);
		RedObject arg1 = args.get(1);

		if (!(arg0 instanceof RedStringObject))
			throw new RuntimeException("string.replace() only accepts strings as its first argument.");

		if (!(arg1 instanceof RedStringObject))
			throw new RuntimeException("string.replace() only accepts strings as its second argument.");

		return getParent().replace((RedStringObject)arg0, (RedStringObject)arg1);
	}
}
