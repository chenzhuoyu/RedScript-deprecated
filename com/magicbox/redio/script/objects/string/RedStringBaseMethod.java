package com.magicbox.redio.script.objects.string;

import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.array.RedArrayObject;

public abstract class RedStringBaseMethod extends RedObject
{
	private RedStringObject parent = null;

	public RedStringBaseMethod(RedStringObject parent)
	{
		this.parent = parent;
	}

	public RedStringObject getParent()
	{
		return parent;
	}

	public abstract RedObject __call__(RedArrayObject args);
}
