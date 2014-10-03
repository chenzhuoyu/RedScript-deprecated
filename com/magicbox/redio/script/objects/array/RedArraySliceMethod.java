package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedIntObject;
import com.magicbox.redio.script.objects.RedObject;

public class RedArraySliceMethod extends RedArrayBaseMethod
{
	public RedArraySliceMethod(RedArrayObject parent)
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

				if (!(arg0 instanceof RedIntObject))
					throw new RuntimeException("array.slice() only accepts integers as its first argument.");

				return getParent().slice(((RedIntObject)arg0).getValue());
			}

			case 2:
			{
				RedObject arg0 = args.get(0);
				RedObject arg1 = args.get(1);

				if (!(arg0 instanceof RedIntObject))
					throw new RuntimeException("array.slice() only accepts integers as its first argument.");

				if (!(arg1 instanceof RedIntObject))
					throw new RuntimeException("array.slice() only accepts integers as its second argument.");

				return getParent().slice(((RedIntObject)arg0).getValue(), ((RedIntObject)arg1).getValue());
			}

			case 3:
			{
				RedObject arg0 = args.get(0);
				RedObject arg1 = args.get(1);
				RedObject arg2 = args.get(2);

				if (!(arg0 instanceof RedIntObject))
					throw new RuntimeException("array.slice() only accepts integers as its first argument.");

				if (!(arg1 instanceof RedIntObject))
					throw new RuntimeException("array.slice() only accepts integers as its second argument.");

				if (!(arg2 instanceof RedIntObject))
					throw new RuntimeException("array.slice() only accepts integers as its third argument.");

				return getParent().slice(((RedIntObject)arg0).getValue(), ((RedIntObject)arg1).getValue(), ((RedIntObject)arg2).getValue());
			}

			default:
				throw new RuntimeException("array.slice() takes 1 ~ 3 arguments.");
		}
	}
}
