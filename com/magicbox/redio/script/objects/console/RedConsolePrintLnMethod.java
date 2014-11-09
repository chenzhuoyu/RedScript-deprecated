package com.magicbox.redio.script.objects.console;

import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedConsolePrintLnMethod extends RedObject
{
	public RedObject __call__(RedArrayObject args)
	{
		for (int i = 0; i < args.size(); i++)
			if (i == args.size() - 1)
				System.out.print(args.get(i).toString());
			else
				System.out.print(args.get(i).toString() + " ");

		System.out.println();
		return RedNullObject.nullObject;
	}
}
