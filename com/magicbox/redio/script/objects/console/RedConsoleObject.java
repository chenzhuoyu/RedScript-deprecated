package com.magicbox.redio.script.objects.console;

import com.magicbox.redio.script.objects.RedObject;

public class RedConsoleObject extends RedObject
{
	public RedConsoleObject()
	{
		setAttribute("print", new RedConsolePrintMethod());
		setAttribute("println", new RedConsolePrintLnMethod());
	}
}
