package com.magicbox.redio.script.engine;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Stack;

public class BytecodeBuffer
{
	private int ip = 0;
	private int offset = 0;

	private Stack<Integer> callstack = new Stack<Integer>();
	private ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();

	public int getIP()
	{
		return ip;
	}

	public void emit(Bytecode bytecode)
	{
		bytecodes.add(bytecode);
	}

	public void popIP()
	{
		branchTo(callstack.pop());
	}

	public void pushIP()
	{
		callstack.push(ip);
		branchTo(offset);
	}

	public void branchTo(int ip)
	{
		this.ip = ip;
	}

	public void setOffset(int offset)
	{
		branchTo(offset);
		this.offset = offset;
	}

	public Bytecode nextBytecode()
	{
		int index = ip++ - offset;
		return index < 0 || index >= bytecodes.size() ? null : bytecodes.get(index);
	}

	public boolean load(String filename)
	{
		try
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
			Object object = in.readObject();

			if (!(object instanceof ArrayList<?>))
			{
				in.close();
				return false;
			}

			bytecodes = (ArrayList<Bytecode>)object;
			in.close();
			return true;
		} catch (IOException | ClassNotFoundException e)
		{
			return false;
		}
	}

	public boolean save(String filename)
	{
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));

			out.writeObject(bytecodes);
			out.close();
			return true;
		} catch (IOException e)
		{
			return true;
		}
	}
}
