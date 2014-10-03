package com.magicbox.redio.script.objects.constructors;

import com.magicbox.redio.script.objects.RedBoolObject;
import com.magicbox.redio.script.objects.RedIntObject;
import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;
import com.magicbox.redio.script.objects.string.RedStringObject;

public class RedIntObjectConstructor extends RedObject
{
	public RedObject __call__(RedArrayObject args)
	{
		int base = 10;

		switch (args.size())
		{
			case 1:
				break;

			case 2:
			{
				RedObject arg0 = args.get(0);
				RedObject arg1 = args.get(1);

				if (!(arg0 instanceof RedStringObject))
					throw new RuntimeException("int() accepts second argument only when its first argument is string.");

				if (!(arg1 instanceof RedIntObject))
					throw new RuntimeException("Argument 2 of int() must be integer.");

				base = ((RedIntObject)arg1).getValue();
				break;
			}

			default:
				throw new RuntimeException("int() takes at most 2 arguments.");
		}

		RedObject first = args.get(0);

		if (first instanceof RedIntObject)
			return first;
		else if (first == RedNullObject.nullObject)
			return RedIntObject.fromInteger(0);
		else if (first == RedBoolObject.trueObject)
			return RedIntObject.fromInteger(1);
		else if (first == RedBoolObject.falseObject)
			return RedIntObject.fromInteger(0);
		else if (first instanceof RedStringObject)
			return RedIntObject.fromInteger(Integer.parseInt(first.toString(), base));
		else
			throw new RuntimeException("int() does't accept " + first.getClass().getName() + " as first argument.");
	}
}
