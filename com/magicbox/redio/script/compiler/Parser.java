package com.magicbox.redio.script.compiler;

import java.util.ArrayList;
import java.util.Arrays;

import javax.script.ScriptException;

public class Parser
{
	public class If
	{
		public Object positive = null;
		public Object negative = null;
		public Expression expression = null;
	}

	public class For
	{
		public Object body = null;
		public Object init = null;
		public Object stepper = null;
		public Expression condition = null;
	}

	public class While
	{
		public Object body = null;
		public Expression expression = null;
	}

	public class Case
	{
		public Object body = null;
		public Constant value = new Constant();
	}

	public class Switch
	{
		public Object defaultCase = null;
		public Expression expression = null;
		public ArrayList<Case> cases = new ArrayList<Case>();
	}

	public class Function
	{
		public String name = "";
		public Object body = null;
		public ArrayList<String> args = new ArrayList<String>();
	}

	public class CompondStatement
	{
		public ArrayList<Object> statements = new ArrayList<Object>();
	}

	public class Break
	{
		// Nothing to implement
	}

	public class Return
	{
		public Expression expression = null;
	}

	public class Continue
	{
		// Nothing to implement
	}

	public class Name
	{
		public String name = "";
	}

	public class Constant
	{
		public int type = 0;
		public int intValue = 0;
		public String stringValue = "";
	}

	public class Unit
	{
		public Object name = null;
		public String operator = "";
	}

	public class Assign
	{
		public Object name = null;
		public Expression expression = null;
	}

	public class Augment
	{
		public Object name = null;
		public String operator = "";
		public Expression expression = null;
	}

	public class Index
	{
		public Object name = null;
		public Expression item = null;
	}

	public class Invoke
	{
		public Object name = null;
		public boolean discard = false;
		public ArrayList<Expression> args = new ArrayList<Expression>();
	}

	public class Attribute
	{
		public Object name = null;
		public String attribute = "";
	}

	public class Expression
	{
		public Object left = null;
		public Object right = null;
		public String operator = "";
	}

	public class FactorValue
	{
		public Object value = null;
		public boolean isInvoke = false;
	}

	private boolean inFunc = false;
	private boolean inLoop = false;
	private Tokenizer tokenizer = null;

	public Parser(Tokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
	}

	private boolean maybe(int... tokens) throws ScriptException
	{
		Token tk = tokenizer.peek();

		for (int token : tokens)
			if (token == tk.token)
				return true;

		return false;
	}

	private boolean assume(int token, String value) throws ScriptException
	{
		Token tk = tokenizer.peek();
		return tk.token == token ? tk.stringValue.equals(value) : false;
	}

	private String expect(int token) throws ScriptException
	{
		Token tk = tokenizer.next();

		if (tk.token != token)
			throw new ScriptException("Unexpected token", tokenizer.filename(), tokenizer.y(), tokenizer.x());

		return tk.stringValue;
	}

	private void expect(int token, String string) throws ScriptException
	{
		Token tk = tokenizer.next();
		if (tk.token != token || !tk.stringValue.equals(string))
			throw new ScriptException("Unexpected token", tokenizer.filename(), tokenizer.y(), tokenizer.x());
	}

	private boolean expectOperators(Expression expression, String... operators) throws ScriptException
	{
		Token token = tokenizer.peek();

		if (token.token != Token.OPERATOR)
			return false;

		for (String operator : Arrays.asList(operators))
		{
			if (token.stringValue.equals(operator))
			{
				expression.operator = operator;
				return true;
			}
		}

		return false;
	}

	private If parseIf() throws ScriptException
	{
		If result = new If();

		expect(Token.KEYWORD, "if");
		expect(Token.OPERATOR, "(");
		result.expression = parseExpression();
		expect(Token.OPERATOR, ")");
		result.positive = parseStatement();

		if (assume(Token.KEYWORD, "else"))
		{
			tokenizer.next();
			result.negative = parseStatement();
		}

		return result;
	}

	private For parseFor() throws ScriptException
	{
		For result = new For();

		inLoop = true;
		expect(Token.KEYWORD, "for");
		expect(Token.OPERATOR, "(");
		result.init = parseSimpleStatement(true);
		result.condition = parseExpression();
		expect(Token.OPERATOR, ";");
		result.stepper = parseSimpleStatement(false);
		expect(Token.OPERATOR, ")");
		result.body = parseStatement();
		inLoop = false;
		return result;
	}

	private While parseWhile() throws ScriptException
	{
		While result = new While();

		inLoop = true;
		expect(Token.KEYWORD, "while");
		expect(Token.OPERATOR, "(");
		result.expression = parseExpression();
		expect(Token.OPERATOR, ")");
		result.body = parseStatement();
		inLoop = false;
		return result;
	}

	private Switch parseSwitch() throws ScriptException
	{
		Switch result = new Switch();
		ArrayList<Constant> values = new ArrayList<Constant>();

		expect(Token.KEYWORD, "switch");
		expect(Token.OPERATOR, "(");
		result.expression = parseExpression();
		expect(Token.OPERATOR, ")");
		expect(Token.OPERATOR, "{");

		while (!assume(Token.OPERATOR, "}"))
		{
			Token token = tokenizer.next();

			if (token == null || token.token != Token.KEYWORD)
				throw new ScriptException("'case' or 'default' expected", tokenizer.filename(), tokenizer.y(), tokenizer.x());

			if (token.stringValue.equals("default"))
			{
				if (result.defaultCase != null)
					throw new ScriptException("Duplicated 'default'", tokenizer.filename(), tokenizer.y(), tokenizer.x());

				result.defaultCase = parseStatement();
			}
			else if (token.stringValue.equals("case"))
			{
				Case node = new Case();
				Token next = tokenizer.next();

				if (next == null)
					throw new ScriptException("EOF seen while parsing 'case'", tokenizer.filename(), tokenizer.y(), tokenizer.x());

				for (Constant value : values)
					if (value.type == token.token && (value.intValue == token.intValue || value.stringValue.equals(token.stringValue)))
						throw new ScriptException("Duplicated 'case' value", tokenizer.filename(), tokenizer.y(), tokenizer.x());

				node.value.type = token.token;
				node.value.intValue = token.intValue;
				node.value.stringValue = token.stringValue;

				values.add(node.value);
				expect(Token.OPERATOR, ":");

				node.body = parseStatement();
				result.cases.add(node);
			}
			else
				throw new ScriptException(
					"Unexpected keyword '" + token.stringValue + "'",
					tokenizer.filename(),
					tokenizer.y(),
					tokenizer.x());
		}

		expect(Token.OPERATOR, "}");
		return result;
	}

	private Function parseFunction() throws ScriptException
	{
		Function result = new Function();

		inFunc = true;
		expect(Token.KEYWORD, "func");
		result.name = expect(Token.IDENTIFIER);
		expect(Token.OPERATOR, "(");

		while (!assume(Token.OPERATOR, ")"))
		{
			result.args.add(expect(Token.IDENTIFIER));

			if (!assume(Token.OPERATOR, ")"))
				expect(Token.OPERATOR, ",");
		}

		expect(Token.OPERATOR, ")");
		result.body = parseStatement();
		inFunc = false;
		return result;
	}

	private Assign parseAssign() throws ScriptException
	{
		Assign result = new Assign();

		expect(Token.OPERATOR, "=");
		result.expression = parseExpression();
		return result;
	}

	private Augment parseAugment() throws ScriptException
	{
		Token token = tokenizer.next();
		Augment result = new Augment();

		if (token.token != Token.OPERATOR)
			throw new ScriptException("Operator expected", tokenizer.filename(), tokenizer.y(), tokenizer.x());

		if ("+= -= *= /= %= **= |= &= ^= <<= >>=".indexOf(token.stringValue) == -1)
			throw new ScriptException("Invalid operator '" + token.stringValue + "'", tokenizer.filename(), tokenizer.y(), tokenizer.x());

		result.operator = token.stringValue;
		result.expression = parseExpression();
		return result;
	}

	private Unit parseUnit() throws ScriptException
	{
		Unit result = new Unit();
		Expression expr = new Expression();

		if (expectOperators(expr, "+", "-", "~", "not"))
		{
			tokenizer.next();
			result.name = parseUnit();
			result.operator = expr.operator;
		}
		else if (assume(Token.OPERATOR, "("))
		{
			tokenizer.next();
			result.name = parseExpression();
			expect(Token.OPERATOR, ")");
		}
		else
		{
			result.name = parseFactorValue().value;
		}

		return result;
	}

	private Expression parseTerm() throws ScriptException
	{
		Expression result = new Expression();

		result.left = parseFactor();

		if (expectOperators(result, "+", "-"))
		{
			Expression node = new Expression();

			tokenizer.next();
			result.right = parseFactor();

			while (expectOperators(result, "+", "-"))
			{
				tokenizer.next();
				node.left = result;
				node.right = parseFactor();
				result = node;
				node = new Expression();
			}
		}

		return result;
	}

	private Expression parsePower() throws ScriptException
	{
		Expression result = new Expression();

		result.left = parseUnit();

		if (expectOperators(result, "**"))
		{
			Expression node = new Expression();

			tokenizer.next();
			result.right = parseUnit();

			while (expectOperators(result, "**"))
			{
				tokenizer.next();
				node.left = result;
				node.right = parseUnit();
				result = node;
				node = new Expression();
			}
		}

		return result;
	}

	private Expression parseFactor() throws ScriptException
	{
		Expression result = new Expression();

		result.left = parsePower();

		if (expectOperators(result, "*", "/", "%"))
		{
			Expression node = new Expression();

			tokenizer.next();
			result.right = parsePower();

			while (expectOperators(result, "*", "/", "%"))
			{
				tokenizer.next();
				node.left = result;
				node.right = parsePower();
				result = node;
				node = new Expression();
			}
		}

		return result;
	}

	private Expression parseBitwise() throws ScriptException
	{
		Expression result = new Expression();

		result.left = parseTerm();

		if (expectOperators(result, "|", "&", "^", "<<", ">>"))
		{
			Expression node = new Expression();

			tokenizer.next();
			result.right = parseTerm();

			while (expectOperators(result, "|", "&", "^", "<<", ">>"))
			{
				tokenizer.next();
				node.left = result;
				node.right = parseTerm();
				result = node;
				node = new Expression();
			}
		}

		return result;
	}

	private Expression parseRelations() throws ScriptException
	{
		Expression result = new Expression();

		result.left = parseBitwise();

		if (expectOperators(result, "<", ">", "<=", ">=", "==", "!="))
		{
			Expression node = new Expression();

			tokenizer.next();
			result.right = parseBitwise();

			while (expectOperators(result, "<", ">", "<=", ">=", "==", "!="))
			{
				tokenizer.next();
				node.left = result;
				node.right = parseBitwise();
				result = node;
				node = new Expression();
			}
		}

		return result;
	}

	private Expression parseExpression() throws ScriptException
	{
		Expression result = new Expression();

		result.left = parseRelations();

		if (expectOperators(result, "and", "or", "xor"))
		{
			Expression node = new Expression();

			tokenizer.next();
			result.right = parseRelations();

			while (expectOperators(result, "and", "or", "xor"))
			{
				tokenizer.next();
				node.left = result;
				node.right = parseRelations();
				result = node;
				node = new Expression();
			}
		}

		return result;
	}

	private Constant parseConstant() throws ScriptException
	{
		Token token = tokenizer.next();
		Constant result = new Constant();

		if (token == null)
			throw new ScriptException("Constant expected", tokenizer.filename(), tokenizer.y(), tokenizer.x());

		result.type = token.token;
		result.intValue = token.intValue;
		result.stringValue = token.stringValue;
		return result;
	}

	private Invoke parseInvoke() throws ScriptException
	{
		Invoke result = new Invoke();

		expect(Token.OPERATOR, "(");

		while (!assume(Token.OPERATOR, ")"))
		{
			result.args.add(parseExpression());

			if (!assume(Token.OPERATOR, ")"))
				expect(Token.OPERATOR, ",");
		}

		expect(Token.OPERATOR, ")");
		return result;
	}

	private Index parseIndexer() throws ScriptException
	{
		Index result = new Index();

		expect(Token.OPERATOR, "[");
		result.item = parseExpression();
		expect(Token.OPERATOR, "]");
		return result;
	}

	private Attribute parseProperty() throws ScriptException
	{
		Attribute result = new Attribute();

		expect(Token.OPERATOR, ".");
		result.attribute = expect(Token.IDENTIFIER);
		return result;
	}

	private FactorValue parseFactorItem() throws ScriptException
	{
		Token token = tokenizer.peek();
		FactorValue result = new FactorValue();

		if (token.token != Token.OPERATOR)
			throw new ScriptException("Operator expected", tokenizer.filename(), tokenizer.y(), tokenizer.x());

		if (token.stringValue.equals("("))
		{
			result.value = parseInvoke();
			result.isInvoke = true;
		}
		else if (token.stringValue.equals("["))
		{
			result.value = parseIndexer();
			result.isInvoke = false;
		}
		else if (token.stringValue.equals("."))
		{
			result.value = parseProperty();
			result.isInvoke = false;
		}
		else
		{
			result = null;
		}

		return result;
	}

	private FactorValue parseFactorValue() throws ScriptException
	{
		Object result = null;
		boolean invoke = false;

		if (!maybe(Token.IDENTIFIER))
		{
			result = parseConstant();
		}
		else
		{
			result = new Name();
			((Name)result).name = expect(Token.IDENTIFIER);
		}

		while (true)
		{
			FactorValue item = parseFactorItem();

			if (item == null)
				break;

			try
			{
				item.value.getClass().getField("name").set(item.value, result);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
			{
				throw new ScriptException("Internal exception", tokenizer.filename(), tokenizer.y(), tokenizer.x());
			}

			result = item.value;
			invoke = item.isInvoke;
		}

		FactorValue value = new FactorValue();

		value.value = result;
		value.isInvoke = invoke;
		return value;
	}

	private Break parseBreak() throws ScriptException
	{
		if (!inLoop)
			throw new ScriptException("'break' outside loop", tokenizer.filename(), tokenizer.y(), tokenizer.x());

		expect(Token.KEYWORD, "break");
		expect(Token.OPERATOR, ";");
		return new Break();
	}

	private Return parseReturn() throws ScriptException
	{
		Return result = new Return();

		if (!inFunc)
			throw new ScriptException("'return' outside function", tokenizer.filename(), tokenizer.y(), tokenizer.x());

		expect(Token.KEYWORD, "return");
		result.expression = parseExpression();
		expect(Token.OPERATOR, ";");
		return result;
	}

	private Continue parseContinue() throws ScriptException
	{
		if (!inLoop)
			throw new ScriptException("'continue' outside loop", tokenizer.filename(), tokenizer.y(), tokenizer.x());

		expect(Token.KEYWORD, "continue");
		expect(Token.OPERATOR, ";");
		return new Continue();
	}

	private Object parseStatement() throws ScriptException
	{
		if (assume(Token.OPERATOR, "{"))
			return parseCompondStatement();
		else if (assume(Token.KEYWORD, "if"))
			return parseIf();
		else if (assume(Token.KEYWORD, "for"))
			return parseFor();
		else if (assume(Token.KEYWORD, "while"))
			return parseWhile();
		else if (assume(Token.KEYWORD, "switch"))
			return parseSwitch();
		else if (assume(Token.KEYWORD, "break"))
			return parseBreak();
		else if (assume(Token.KEYWORD, "return"))
			return parseReturn();
		else if (assume(Token.KEYWORD, "continue"))
			return parseContinue();
		else
			return parseSimpleStatement(true);
	}

	private Object parseSimpleStatement(boolean requireComma) throws ScriptException
	{
		Object result = null;
		FactorValue fv = parseFactorValue();

		if (fv.isInvoke)
		{
			result = fv.value;
			((Invoke)result).discard = true;
		}
		else if (assume(Token.OPERATOR, "="))
		{
			result = parseAssign();
			((Assign)result).name = fv.value;
		}
		else
		{
			result = parseAugment();
			((Augment)result).name = fv.value;
		}

		if (requireComma)
			expect(Token.OPERATOR, ";");

		return result;
	}

	private CompondStatement parseCompondStatement() throws ScriptException
	{
		CompondStatement result = new CompondStatement();

		expect(Token.OPERATOR, "{");

		while (!assume(Token.OPERATOR, "}"))
			result.statements.add(parseStatement());

		expect(Token.OPERATOR, "}");
		return result;
	}

	public ArrayList<Object> parse() throws ScriptException
	{
		ArrayList<Object> nodes = new ArrayList<Object>();

		inFunc = false;
		inLoop = false;

		while (!tokenizer.eof())
		{
			if (assume(Token.KEYWORD, "func"))
				nodes.add(parseFunction());
			else
				nodes.add(parseStatement());
		}

		return nodes;
	}
}
