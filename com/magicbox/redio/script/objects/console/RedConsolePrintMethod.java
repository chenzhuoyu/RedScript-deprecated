package com.magicbox.redio.script.objects.console;

import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedConsolePrintMethod extends RedObject
{
	public RedObject __call__(RedArrayObject args)
	{
		for (int i = 0; i < args.size(); i++)
			System.out.print(args.get(i).toString() + " ");

		System.out.println();
		return RedNullObject.nullObject;
	}
}
