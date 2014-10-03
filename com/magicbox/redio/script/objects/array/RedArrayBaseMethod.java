package com.magicbox.redio.script.objects.array;

import com.magicbox.redio.script.objects.RedObject;

public abstract class RedArrayBaseMethod extends RedObject
{
	private RedArrayObject parent = null;

	public RedArrayBaseMethod(RedArrayObject parent)
	{
		this.parent = parent;
	}

	public RedArrayObject getParent()
	{
		return parent;
	}

	public abstract RedObject __call__(RedArrayObject args);
}
