package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedIntObject;
import com.magicbox.redio.script.objects.RedObject;

public class RedArrayPopMethod extends RedArrayBaseMethod
{
	public RedArrayPopMethod(RedArrayObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		switch (args.size())
		{
			case 0:
				return getParent().pop();

			case 1:
			{
				RedObject arg0 = args.get(0);

				if (!(arg0 instanceof RedIntObject))
					throw new RuntimeException("array.pop() only accepts integers as its first argument.");

				return getParent().pop(((RedIntObject)arg0).getValue());
			}

			default:
				throw new RuntimeException("array.pop() takes at most 1 argument.");
		}
	}
}
