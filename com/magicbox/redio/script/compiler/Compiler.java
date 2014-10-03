package com.magicbox.redio.script.compiler;

import javax.script.ScriptException;

import com.magicbox.redio.script.engine.BytecodeBuffer;

public class Compiler
{
	public static BytecodeBuffer compile(String filename, String source) throws ScriptException
	{
		return new Generator().generate(new Parser(new Tokenizer(filename, source)).parse());
	}
}
