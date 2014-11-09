package com.magicbox.redio.script.objects;

import java.util.HashMap;
import java.util.Stack;

import com.magicbox.redio.script.engine.Bytecode;
import com.magicbox.redio.script.engine.BytecodeBuffer;
import com.magicbox.redio.script.engine.Constant;
import com.magicbox.redio.script.engine.Interpreter;
import com.magicbox.redio.script.objects.string.RedStringObject;

public class RedCodeObject extends RedObject
{
	public static final int HALT = 0;
	public static final int RETURN = 1;
	public static final int CONTINUE = 2;

	private BytecodeBuffer buffer = new BytecodeBuffer();
	private Stack<RedObject> evalStack = new Stack<RedObject>();
	private HashMap<String, RedObject> context = null;

	private int nestingLevel = 0;
	private boolean isRootObject = false;
	private Interpreter interpreter = null;
	private RedFunctionObject functionGenerating = null;

	public static RedCodeObject fromBytecodes(BytecodeBuffer buffer)
	{
		RedCodeObject result = new RedCodeObject();

		result.buffer = buffer;
		return result;
	}

	private RedObject lookupObject(String name)
	{
		if (context.containsKey(name))
			return context.get(name);

		throw new RuntimeException("\"" + name + "\" could not be resolved.");
	}

	public BytecodeBuffer getBuffer()
	{
		return buffer;
	}

	public Stack<RedObject> getStack()
	{
		return evalStack;
	}

	public void setRootObject(boolean rootObject)
	{
		isRootObject = rootObject;
	}

	public void setInterpreter(Interpreter interpreter)
	{
		this.interpreter = interpreter;
	}

	public void eval()
	{
		buffer.pushIP();
		context = interpreter.pushContext(isRootObject);

		while (true)
		{
			Bytecode bytecode = buffer.nextBytecode();

			if (bytecode == null)
				throw new RuntimeException("Unexpected end of bytecode.");

			switch (bytecode.opcode)
			{
				case Bytecode.MAKE_FUNC:
					nestingLevel--;
					break;

				case Bytecode.START_FUNC:
					nestingLevel++;
					break;
			}

			if (nestingLevel == 0 && bytecode.opcode == Bytecode.MAKE_FUNC)
			{
				if (functionGenerating == null)
					throw new RuntimeException("MAKE_FUNC out of function.");

				context.put(bytecode.stringAccum, functionGenerating);

				functionGenerating.setName(bytecode.stringAccum);
				functionGenerating = null;
			}
			else if (functionGenerating != null)
			{
				functionGenerating.getCodeObject().getBuffer().emit(bytecode);
			}
			else
			{
				switch (evalBytecode(bytecode))
				{
					case HALT:
					case RETURN:
					{
						buffer.popIP();
						context = interpreter.popContext();
						return;
					}

					case CONTINUE:
						break;
				}
			}
		}
	}

	private int evalBytecode(Bytecode bytecode) throws RuntimeException
	{
		switch (bytecode.opcode)
		{
			case Bytecode.LOAD_CONST:
			{
				switch (bytecode.constAccum.type)
				{
					case Constant.STRING:
						evalStack.push(new RedStringObject(bytecode.constAccum.stringValue));
						break;

					case Constant.INTEGER:
						evalStack.push(RedIntObject.fromInteger(bytecode.constAccum.intValue));
						break;

					default:
						throw new RuntimeException("Invalid constant type");
				}

				break;
			}

			case Bytecode.LOAD_OBJECT:
				evalStack.push(lookupObject(bytecode.stringAccum));
				break;

			case Bytecode.STOR_OBJECT:
				context.put(bytecode.stringAccum, evalStack.pop());
				break;

			case Bytecode.GET_ATTR:
			{
				RedObject a = evalStack.pop();
				evalStack.push(a.invokeMethod("__getattr__", new RedStringObject(bytecode.stringAccum)));
				break;
			}

			case Bytecode.GET_ITEM:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__getitem__", a));
				break;
			}

			case Bytecode.SET_ATTR:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				RedObject c = evalStack.pop();
				c.invokeMethod("__setattr__", b, a);
				break;
			}

			case Bytecode.SET_ITEM:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				RedObject c = evalStack.pop();
				c.invokeMethod("__setitem__", b, a);
				break;
			}

			case Bytecode.INVOKE:
			{
				RedObject func = evalStack.pop();
				RedObject [] args = new RedObject [bytecode.intAccum];

				for (int i = 0; i < args.length; i++)
					args[i] = evalStack.pop();

				evalStack.push(func.invoke(args));
				break;
			}

			case Bytecode.RETURN:
				return RETURN;

			case Bytecode.ADD:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__add__", a));
				break;
			}

			case Bytecode.SUB:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__sub__", a));
				break;
			}

			case Bytecode.MUL:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__mul__", a));
				break;
			}

			case Bytecode.DIV:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__div__", a));
				break;
			}

			case Bytecode.MOD:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__mod__", a));
				break;
			}

			case Bytecode.POW:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__pow__", a));
				break;
			}

			case Bytecode.BIT_OR:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__bit_or__", a));
				break;
			}

			case Bytecode.BIT_AND:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__bit_and__", a));
				break;
			}

			case Bytecode.BIT_XOR:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__bit_xor__", a));
				break;
			}

			case Bytecode.BIT_NOT:
			{
				RedObject a = evalStack.pop();
				evalStack.push(a.invokeMethod("__bit_not__"));
				break;
			}

			case Bytecode.LSHIFT:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__lshift__", a));
				break;
			}

			case Bytecode.RSHIFT:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__rshift__", a));
				break;
			}

			case Bytecode.AUG_ADD:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_add__", a));
				break;
			}

			case Bytecode.AUG_SUB:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_sub__", a));
				break;
			}

			case Bytecode.AUG_MUL:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_mul__", a));
				break;
			}

			case Bytecode.AUG_DIV:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_div__", a));
				break;
			}

			case Bytecode.AUG_MOD:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_mod__", a));
				break;
			}

			case Bytecode.AUG_POW:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_pow__", a));
				break;
			}

			case Bytecode.AUG_BIT_OR:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_bit_or__", a));
				break;
			}

			case Bytecode.AUG_BIT_AND:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_bit_and__", a));
				break;
			}

			case Bytecode.AUG_BIT_XOR:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_bit_xor__", a));
				break;
			}

			case Bytecode.AUG_LSHIFT:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_lshift__", a));
				break;
			}

			case Bytecode.AUG_RSHIFT:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__inplace_rshift__", a));
				break;
			}

			case Bytecode.BOOL_OR:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__bool_or__", a));
				break;
			}

			case Bytecode.BOOL_AND:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__bool_and__", a));
				break;
			}

			case Bytecode.BOOL_XOR:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__bool_xor__", a));
				break;
			}

			case Bytecode.BOOL_NOT:
			{
				RedObject a = evalStack.pop();
				evalStack.push(a.invokeMethod("__bool_not__"));
				break;
			}

			case Bytecode.EQ:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__eq__", a));
				break;
			}

			case Bytecode.LE:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__le__", a));
				break;
			}

			case Bytecode.GE:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__ge__", a));
				break;
			}

			case Bytecode.NEQ:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__neq__", a));
				break;
			}

			case Bytecode.LEQ:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__leq__", a));
				break;
			}

			case Bytecode.GEQ:
			{
				RedObject a = evalStack.pop();
				RedObject b = evalStack.pop();
				evalStack.push(b.invokeMethod("__geq__", a));
				break;
			}

			case Bytecode.POS:
			{
				RedObject a = evalStack.pop();
				evalStack.push(a.invokeMethod("__pos__"));
				break;
			}

			case Bytecode.NEG:
			{
				RedObject a = evalStack.pop();
				evalStack.push(a.invokeMethod("__neg__"));
				break;
			}

			case Bytecode.DUP:
				evalStack.push(evalStack.elementAt(evalStack.size() - 1));
				break;

			case Bytecode.DUP2:
			{
				RedObject top = evalStack.elementAt(evalStack.size() - 1);
				RedObject second = evalStack.elementAt(evalStack.size() - 2);

				evalStack.push(second);
				evalStack.push(top);
				break;
			}

			case Bytecode.DROP:
				evalStack.pop();
				break;

			case Bytecode.START_FUNC:
			{
				functionGenerating = new RedFunctionObject(bytecode.intAccum);
				functionGenerating.getCodeObject().setInterpreter(interpreter);
				functionGenerating.getCodeObject().getBuffer().setOffset(buffer.getIP());
				break;
			}

			case Bytecode.BR:
				buffer.branchTo(bytecode.intAccum);
				break;

			case Bytecode.BRTRUE:
			{
				if (evalStack.pop().isTrue())
					buffer.branchTo(bytecode.intAccum);
				break;
			}

			case Bytecode.BRFALSE:
			{
				if (!evalStack.pop().isTrue())
					buffer.branchTo(bytecode.intAccum);
				break;
			}

			case Bytecode.STOP:
				return HALT;

			default:
				throw new RuntimeException("Invalid opcode.");
		}

		return CONTINUE;
	}
}
