package com.magicbox.redio.script.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.magicbox.redio.script.engine.Bytecode;
import com.magicbox.redio.script.engine.BytecodeBuffer;
import com.magicbox.redio.script.engine.Constant;

public class Generator
{
	private HashMap<String, Integer> AUGMENT_MAP = new HashMap<String, Integer>();
	private HashMap<String, Integer> OPERATOR_MAP = new HashMap<String, Integer>();

	private int programCounter = 0;
	private BytecodeBuffer bytecodes = new BytecodeBuffer();

	private ArrayList<Bytecode> pendingBreaks = new ArrayList<Bytecode>();
	private ArrayList<Bytecode> pendingContinues = new ArrayList<Bytecode>();

	public Generator()
	{
		AUGMENT_MAP.put("+=", Bytecode.AUG_ADD);
		AUGMENT_MAP.put("-=", Bytecode.AUG_SUB);
		AUGMENT_MAP.put("*=", Bytecode.AUG_MUL);
		AUGMENT_MAP.put("/=", Bytecode.AUG_DIV);
		AUGMENT_MAP.put("%=", Bytecode.AUG_MOD);
		AUGMENT_MAP.put("**=", Bytecode.AUG_POW);

		AUGMENT_MAP.put("|=", Bytecode.AUG_BIT_OR);
		AUGMENT_MAP.put("&=", Bytecode.AUG_BIT_AND);
		AUGMENT_MAP.put("^=", Bytecode.AUG_BIT_XOR);
		AUGMENT_MAP.put("<<=", Bytecode.AUG_LSHIFT);
		AUGMENT_MAP.put(">>=", Bytecode.AUG_RSHIFT);

		OPERATOR_MAP.put("<", Bytecode.LE);
		OPERATOR_MAP.put(">", Bytecode.GE);
		OPERATOR_MAP.put("<=", Bytecode.LEQ);
		OPERATOR_MAP.put(">=", Bytecode.GEQ);
		OPERATOR_MAP.put("==", Bytecode.EQ);
		OPERATOR_MAP.put("!=", Bytecode.NEQ);

		OPERATOR_MAP.put("+", Bytecode.ADD);
		OPERATOR_MAP.put("-", Bytecode.SUB);
		OPERATOR_MAP.put("*", Bytecode.MUL);
		OPERATOR_MAP.put("/", Bytecode.DIV);
		OPERATOR_MAP.put("%", Bytecode.MOD);
		OPERATOR_MAP.put("**", Bytecode.POW);

		OPERATOR_MAP.put("|", Bytecode.BIT_OR);
		OPERATOR_MAP.put("&", Bytecode.BIT_AND);
		OPERATOR_MAP.put("^", Bytecode.BIT_XOR);
		OPERATOR_MAP.put("<<", Bytecode.LSHIFT);
		OPERATOR_MAP.put(">>", Bytecode.RSHIFT);

		OPERATOR_MAP.put("or", Bytecode.BOOL_OR);
		OPERATOR_MAP.put("and", Bytecode.BOOL_AND);
		OPERATOR_MAP.put("xor", Bytecode.BOOL_XOR);
	}

	private Bytecode pushCode(int opcode)
	{
		Bytecode bytecode = new Bytecode(opcode);

		programCounter++;
		bytecodes.emit(bytecode);
		return bytecode;
	}

	private void generate_If(Parser.If node)
	{
		generate(node.expression);
		Bytecode branch = pushCode(Bytecode.BRFALSE);
		generate(node.positive);

		if (node.negative == null)
		{
			branch.intAccum = programCounter;
			return;
		}

		Bytecode skip = pushCode(Bytecode.BR);
		branch.intAccum = programCounter;
		generate(node.negative);
		skip.intAccum = programCounter;
	}

	private void generate_For(Parser.For node)
	{
		generate(node.init);
		int start = programCounter;
		generate(node.condition);
		Bytecode branch = pushCode(Bytecode.BRFALSE);

		pendingBreaks.clear();
		pendingContinues.clear();
		generate(node.body);

		for (Bytecode bytecode : pendingContinues)
			bytecode.intAccum = programCounter;

		generate(node.stepper);
		pushCode(Bytecode.BR).intAccum = start;
		branch.intAccum = programCounter;

		for (Bytecode bytecode : pendingBreaks)
			bytecode.intAccum = programCounter;
	}

	private void generate_While(Parser.While node)
	{
		int start = programCounter;
		generate(node.expression);
		Bytecode branch = pushCode(Bytecode.BRFALSE);

		pendingBreaks.clear();
		pendingContinues.clear();
		generate(node.body);

		for (Bytecode bytecode : pendingContinues)
			bytecode.intAccum = programCounter;

		pushCode(Bytecode.BR).intAccum = start;
		branch.intAccum = programCounter;

		for (Bytecode bytecode : pendingBreaks)
			bytecode.intAccum = programCounter;
	}

	private void generate_Switch(Parser.Switch node)
	{
		Bytecode pending = null;
		ArrayList<Bytecode> patch = new ArrayList<Bytecode>();

		generate(node.expression);

		for (Parser.Case caseItem : node.cases)
		{
			if (pending != null)
				pending.intAccum = programCounter;

			pushCode(Bytecode.DUP);
			generate(caseItem.value);
			pushCode(Bytecode.EQ);
			pending = pushCode(Bytecode.BRFALSE);
			generate(caseItem.body);
			patch.add(pushCode(Bytecode.BR));
		}

		if (pending != null)
			pending.intAccum = programCounter;

		if (node.defaultCase != null)
			generate(node.defaultCase);

		for (Bytecode bytecode : patch)
			bytecode.intAccum = programCounter;

		pushCode(Bytecode.DROP);
	}

	private void generate_Function(Parser.Function node)
	{
		pushCode(Bytecode.START_FUNC).intAccum = node.args.size();

		for (String arg : node.args)
			pushCode(Bytecode.STOR_OBJECT).stringAccum = arg;

		generate(node.body);
		pushCode(Bytecode.LOAD_OBJECT).stringAccum = "null";
		pushCode(Bytecode.RETURN);
		pushCode(Bytecode.MAKE_FUNC).stringAccum = node.name;
	}

	private void generate_CompondStatement(Parser.CompondStatement node)
	{
		generate(node.statements.toArray());
	}

	private void generate_Break(Parser.Break node)
	{
		pendingBreaks.add(pushCode(Bytecode.BR));
	}

	private void generate_Return(Parser.Return node)
	{
		generate(node.expression);
		pushCode(Bytecode.RETURN);
	}

	private void generate_Continue(Parser.Continue node)
	{
		pendingContinues.add(pushCode(Bytecode.BR));
	}

	private void generate_Name(Parser.Name node)
	{
		pushCode(Bytecode.LOAD_OBJECT).stringAccum = node.name;
	}

	private void generate_Constant(Parser.Constant node)
	{
		Bytecode bytecode = pushCode(Bytecode.LOAD_CONST);

		switch (node.type)
		{
			case Token.STRING:
				bytecode.constAccum.type = Constant.STRING;
				break;

			case Token.INTEGER:
				bytecode.constAccum.type = Constant.INTEGER;
				break;

			default:
				throw new RuntimeException("Invalid constant type.");
		}

		bytecode.constAccum.intValue = node.intValue;
		bytecode.constAccum.stringValue = node.stringValue;
	}

	private void generate_Unit(Parser.Unit node)
	{
		generate(node.name);

		switch (node.operator)
		{
			case "+":
				pushCode(Bytecode.POS);
				break;

			case "-":
				pushCode(Bytecode.NEG);
				break;

			case "~":
				pushCode(Bytecode.BIT_NOT);
				break;

			case "not":
				pushCode(Bytecode.BOOL_NOT);
				break;
		}
	}

	private void generate_Assign(Parser.Assign node)
	{
		if (node.name instanceof Parser.Name)
		{
			generate(node.expression);
			pushCode(Bytecode.STOR_OBJECT).stringAccum = ((Parser.Name)node.name).name;
		}
		else if (node.name instanceof Parser.Index)
		{
			generate(((Parser.Index)node.name).name);
			generate(((Parser.Index)node.name).item);
			generate(node.expression);
			pushCode(Bytecode.SET_ITEM);
		}
		else if (node.name instanceof Parser.Attribute)
		{
			generate(((Parser.Attribute)node.name).name);
			generate(node.expression);
			pushCode(Bytecode.SET_ATTR).stringAccum = ((Parser.Attribute)node.name).attribute;
		}
		else
		{
			throw new RuntimeException("Invalid name class " + node.name + ".");
		}
	}

	private void generate_Augment(Parser.Augment node)
	{
		if (node.name instanceof Parser.Name)
		{
			pushCode(Bytecode.LOAD_OBJECT).stringAccum = ((Parser.Name)node.name).name;
		}
		else if (node.name instanceof Parser.Index)
		{
			generate(((Parser.Index)node.name).name);
			generate(((Parser.Index)node.name).item);
			pushCode(Bytecode.DUP2);
			pushCode(Bytecode.GET_ITEM);
		}
		else if (node.name instanceof Parser.Attribute)
		{
			generate(((Parser.Attribute)node.name).name);
			pushCode(Bytecode.DUP);
			pushCode(Bytecode.GET_ATTR).stringAccum = ((Parser.Attribute)node.name).attribute;
		}
		else
		{
			throw new RuntimeException("Internal error.");
		}

		generate(node.expression);
		pushCode(AUGMENT_MAP.get(node.operator));

		if (node.name instanceof Parser.Name)
		{
			pushCode(Bytecode.STOR_OBJECT).stringAccum = ((Parser.Name)node.name).name;
		}
		else if (node.name instanceof Parser.Index)
		{
			pushCode(Bytecode.SET_ITEM);
		}
		else if (node.name instanceof Parser.Attribute)
		{
			pushCode(Bytecode.SET_ATTR).stringAccum = ((Parser.Attribute)node.name).attribute;
		}
	}

	private void generate_Index(Parser.Index node)
	{
		generate(node.name);
		generate(node.item);
		pushCode(Bytecode.GET_ITEM);
	}

	private void generate_Invoke(Parser.Invoke node)
	{
		for (int i = node.args.size() - 1; i >= 0; i--)
			generate(node.args.get(i));

		generate(node.name);
		pushCode(Bytecode.INVOKE).intAccum = node.args.size();

		if (node.discard)
			pushCode(Bytecode.DROP);
	}

	private void generate_Attribute(Parser.Attribute node)
	{
		generate(node.name);
		pushCode(Bytecode.GET_ATTR).stringAccum = node.attribute;
	}

	private void generate_Expression(Parser.Expression node)
	{
		generate(node.left);

		if (!node.operator.isEmpty())
		{
			int operator = OPERATOR_MAP.get(node.operator);
			Bytecode branch = null;

			switch (operator)
			{
				case Bytecode.BOOL_OR:
				{
					pushCode(Bytecode.DUP);
					branch = pushCode(Bytecode.BRTRUE);
					break;
				}

				case Bytecode.BOOL_AND:
				{
					pushCode(Bytecode.DUP);
					branch = pushCode(Bytecode.BRFALSE);
					break;
				}
			}

			generate(node.right);
			pushCode(operator);

			if (branch != null)
				branch.intAccum = programCounter;
		}
	}

	private void generate(Object... nodes)
	{
		try
		{
			for (Object node : nodes)
			{
				String classPath = node.getClass().getName();
				String className = node.getClass().getSimpleName();
				Method generator = getClass().getDeclaredMethod("generate_" + className, Class.forName(classPath));

				generator.invoke(this, node);
			}
		} catch (InvocationTargetException e)
		{
			throw new RuntimeException(e.getCause().getLocalizedMessage());
		} catch (SecurityException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException e)
		{
			throw new RuntimeException("Internal error " + e.getCause() + ".");
		}
	}

	public BytecodeBuffer generate(ArrayList<Object> ast)
	{
		generate(ast.toArray());
		pushCode(Bytecode.STOP);
		return bytecodes;
	}
}
