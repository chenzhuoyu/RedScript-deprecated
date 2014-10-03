package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedIntObject;
import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;

public class RedArrayInsertMethod extends RedArrayBaseMethod
{
	public RedArrayInsertMethod(RedArrayObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != 2)
			throw new RuntimeException("array.insert() takes exactly 2 argument.");

		RedObject arg0 = args.get(0);
		RedObject arg1 = args.get(1);

		if (!(arg0 instanceof RedIntObject))
			throw new RuntimeException("array.insert() only accepts integers as its first argument.");

		getParent().insert(((RedIntObject)arg0).getValue(), arg1);
		return RedNullObject.nullObject;
	}
}
