package com.magicbox.redio.script.objects;

import com.magicbox.redio.script.objects.array.RedArrayObject;


public class RedFunctionObject extends RedObject
{
	private int argc = 0;
	private String name = null;
	private RedCodeObject codeObject = new RedCodeObject();

	public RedFunctionObject(int argc)
	{
		this.argc = argc;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public RedCodeObject getCodeObject()
	{
		return codeObject;
	}

	public RedObject __call__(RedArrayObject args)
	{
		if (args.size() != argc)
			throw new RuntimeException("Function \"" + name + "\" takes exactly " + argc + "arguments but " + args.size() + " passed.");

		for (int i = args.size() - 1; i >= 0; i--)
			codeObject.getStack().push(args.get(i));

		codeObject.eval();
		return codeObject.getStack().pop();
	}
}
