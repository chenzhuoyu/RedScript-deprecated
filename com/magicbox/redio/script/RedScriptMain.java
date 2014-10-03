package com.magicbox.redio.script;

import javax.script.ScriptException;

import com.magicbox.redio.script.compiler.Compiler;
import com.magicbox.redio.script.engine.Interpreter;
import com.magicbox.redio.script.objects.console.RedConsoleObject;
import com.magicbox.redio.script.objects.constructors.RedArrayObjectConstructor;
import com.magicbox.redio.script.objects.constructors.RedBoolObjectConstructor;
import com.magicbox.redio.script.objects.constructors.RedIntObjectConstructor;
import com.magicbox.redio.script.objects.constructors.RedObjectConstructor;
import com.magicbox.redio.script.objects.constructors.RedStringObjectConstructor;

public class RedScriptMain
{
	private Interpreter interpreter = new Interpreter();

	public static void main(String [] args) throws ScriptException
	{
		new RedScriptMain().run();
	}

	public void run() throws ScriptException
	{
		String src =
			"func partition(a, l, r)\n{\n	i = l;\n	j = r;\n	pv = a[i];\n\n	while (i < j)\n	{\n		while (i < j and a[j] >= pv) j -= 1;\n\n		if (i < j)\n		{\n			a[i] = a[j];\n			i += 1;\n		}\n\n		while (i < j and a[i] < pv) i += 1;\n\n		if (i < j)\n		{\n			a[j] = a[i];\n			j -= 1;\n		}\n	}\n\n	a[i] = pv;\n	return i;\n}\n\nfunc quickSort(a, l, r)\n{\n	if (r > l)\n	{\n		pv = partition(a, l, r);\n		quickSort(a, l, pv - 1);\n		quickSort(a, pv + 1, r);\n	}\n}\n\ndata = array(312, 2, 123, 5, 12, 776);\nquickSort(data, 0, data.length - 1);\nConsole.print(data);\nConsole.print('done');";

		interpreter.addBuiltins("Console", new RedConsoleObject());
		interpreter.addBuiltins("int", new RedIntObjectConstructor());
		interpreter.addBuiltins("bool", new RedBoolObjectConstructor());
		interpreter.addBuiltins("array", new RedArrayObjectConstructor());
		interpreter.addBuiltins("object", new RedObjectConstructor());
		interpreter.addBuiltins("string", new RedStringObjectConstructor());

		interpreter.setBytecodes(Compiler.compile("<string>", src));
		interpreter.run();
	}
}
