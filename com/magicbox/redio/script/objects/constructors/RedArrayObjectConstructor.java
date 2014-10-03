package com.magicbox.redio.script.objects.constructors;

import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public class RedArrayObjectConstructor extends RedObject
{
	public RedObject __call__(RedArrayObject elements)
	{
		return elements;
	}
}
