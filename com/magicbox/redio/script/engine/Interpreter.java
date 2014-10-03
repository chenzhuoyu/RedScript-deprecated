package com.magicbox.redio.script.engine;

import java.util.HashMap;
import java.util.Stack;

import com.magicbox.redio.script.objects.RedBoolObject;
import com.magicbox.redio.script.objects.RedCodeObject;
import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;

public class Interpreter
{
	private boolean running = true;
	private RedCodeObject codeObject = null;

	private HashMap<String, RedObject> context = new HashMap<String, RedObject>();
	private Stack<HashMap<String, RedObject>> callstack = new Stack<HashMap<String, RedObject>>();

	public Interpreter()
	{
		addBuiltins("null", RedNullObject.nullObject);
		addBuiltins("true", RedBoolObject.trueObject);
		addBuiltins("false", RedBoolObject.falseObject);
	}

	public void run()
	{
		running = true;
		codeObject.eval();
	}

	public void halt()
	{
		running = false;
	}

	public boolean isRunning()
	{
		return running;
	}

	public void setBytecodes(BytecodeBuffer buffer)
	{
		codeObject = RedCodeObject.fromBytecodes(buffer);
		codeObject.setInterpreter(this);
	}

	public void popContext()
	{
		context = callstack.pop();
	}

	public HashMap<String, RedObject> pushContext()
	{
		callstack.push(context);
		context = (HashMap<String, RedObject>)context.clone();
		return context;
	}

	public void addBuiltins(String name, RedObject value)
	{
		context.put(name, value);
	}
}
