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
		// @formatter:off
		String src =
			"func fac(n)" +
			"{" +
			"	if (n <= 1)" +
			"		return 1;" +
			"	else" +
			"		return n * fac(n - 1);" +
			"}" +
			"" +
			"Console.println('test:', fac(10));";
		// @formatter:on

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
