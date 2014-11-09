package com.magicbox.redio.script.engine;

import java.util.HashMap;
import java.util.Stack;

import com.magicbox.redio.script.objects.RedBoolObject;
import com.magicbox.redio.script.objects.RedCodeObject;
import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.constructors.RedArrayObjectConstructor;
import com.magicbox.redio.script.objects.constructors.RedBoolObjectConstructor;
import com.magicbox.redio.script.objects.constructors.RedIntObjectConstructor;
import com.magicbox.redio.script.objects.constructors.RedStringObjectConstructor;

public class Interpreter
{
	private RedCodeObject codeObject = null;
	private HashMap<String, RedObject> context = new HashMap<String, RedObject>();
	private Stack<HashMap<String, RedObject>> callstack = new Stack<HashMap<String, RedObject>>();

	public Interpreter()
	{
		addBuiltins("null", RedNullObject.nullObject);
		addBuiltins("true", RedBoolObject.trueObject);
		addBuiltins("false", RedBoolObject.falseObject);

		addBuiltins("int", new RedIntObjectConstructor());
		addBuiltins("bool", new RedBoolObjectConstructor());
		addBuiltins("array", new RedArrayObjectConstructor());
		addBuiltins("string", new RedStringObjectConstructor());
	}

	public void run()
	{
		codeObject.eval();
	}

	public void setBytecodes(BytecodeBuffer buffer)
	{
		codeObject = RedCodeObject.fromBytecodes(buffer);
		codeObject.setRootObject(true);
		codeObject.setInterpreter(this);
	}

	public HashMap<String, RedObject> popContext()
	{
		context = callstack.pop();
		return context;
	}

	public HashMap<String, RedObject> pushContext(boolean isRootObject)
	{
		callstack.push(context);

		if (!isRootObject)
			context = (HashMap<String, RedObject>)context.clone();

		return context;
	}

	public void addBuiltins(String name, RedObject value)
	{
		context.put(name, value);
	}

	public RedObject getObject(String name)
	{
		return context.containsKey(name) ? context.get(name) : null;
	}
}
