package com.magicbox.redio.script.objects.string;

import com.magicbox.redio.script.objects.RedIntObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedStringSplitMethod extends RedStringBaseMethod
{
	public RedStringSplitMethod(RedStringObject parent)
	{
		super(parent);
	}

	@Override
	public RedObject __call__(RedArrayObject args)
	{
		switch (args.size())
		{
			case 1:
			{
				RedObject arg0 = args.get(0);

				if (!(arg0 instanceof RedStringObject))
					throw new RuntimeException("string.split() only accepts strings as its first argument.");

				return getParent().split((RedStringObject)arg0);
			}

			case 2:
			{
				RedObject arg0 = args.get(0);
				RedObject arg1 = args.get(1);

				if (!(arg0 instanceof RedStringObject))
					throw new RuntimeException("string.split() only accepts strings as its first argument.");

				if (!(arg1 instanceof RedIntObject))
					throw new RuntimeException("string.split() only accepts integers as its second argument.");

				return getParent().split((RedStringObject)arg0, ((RedIntObject)arg1).getValue());
			}

			default:
				throw new RuntimeException("string.split() takes 1 ~ 2 argument.");
		}
	}
}
