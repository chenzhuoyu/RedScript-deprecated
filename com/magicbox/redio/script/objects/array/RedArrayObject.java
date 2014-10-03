package com.magicbox.redio.script.objects.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.magicbox.redio.script.objects.RedIntObject;
import com.magicbox.redio.script.objects.RedNullObject;
import com.magicbox.redio.script.objects.RedObject;
import com.magicbox.redio.script.objects.string.RedStringObject;

public class RedArrayObject extends RedObject
{
	private ArrayList<RedObject> elements = new ArrayList<RedObject>();

	public RedArrayObject(RedObject... items)
	{
		elements.addAll(Arrays.asList(items));
		setAttribute("pop", new RedArrayPopMethod(this));
		setAttribute("sort", new RedArraySortMethod(this));
		setAttribute("count", new RedArrayCountMethod(this));
		setAttribute("index", new RedArrayIndexMethod(this));
		setAttribute("slice", new RedArraySliceMethod(this));
		setAttribute("append", new RedArrayAppendMethod(this));
		setAttribute("extend", new RedArrayExtendMethod(this));
		setAttribute("insert", new RedArrayInsertMethod(this));
		setAttribute("remove", new RedArrayRemoveMethod(this));
		setAttribute("reverse", new RedArrayReverseMethod(this));
	}

	public int size()
	{
		return elements.size();
	}

	public RedObject get(int index)
	{
		return elements.get(index);
	}

	public void add(RedObject element)
	{
		elements.add(element);
	}

	public void set(int index, RedObject element)
	{
		elements.set(index, element);
	}

	public void sort()
	{
		Collections.sort(elements, new Comparator<RedObject>()
		{
			@Override
			public int compare(RedObject a, RedObject b)
			{
				RedObject result = a.invokeMethod("__cmp__", b);

				if (!(result instanceof RedIntObject))
					throw new RuntimeException("An integer is required.");

				int cmp = ((RedIntObject)result).getValue();

				if (cmp < -1 || cmp > 1)
					throw new RuntimeException("Invalid compare result " + cmp + ".");

				return cmp;
			}
		});
	}

	public void sort(RedObject callable)
	{
		final RedObject comparator = callable;
		Collections.sort(elements, new Comparator<RedObject>()
		{
			@Override
			public int compare(RedObject a, RedObject b)
			{
				RedObject result = comparator.invoke(a, b);

				if (!(result instanceof RedIntObject))
					throw new RuntimeException("An integer is required.");

				int cmp = ((RedIntObject)result).getValue();

				if (cmp < -1 || cmp > 1)
					throw new RuntimeException("Invalid compare result " + cmp + ".");

				return cmp;
			}
		});
	}

	public void remove(RedObject object)
	{
		elements.remove(object);
	}

	public void extend(RedArrayObject other)
	{
		elements.addAll(other.elements);
	}

	public void insert(int index, RedObject element)
	{
		elements.add(index, element);
	}

	public void reverse()
	{
		Collections.reverse(elements);
	}

	public int count(RedObject element)
	{
		int result = 0;

		for (RedObject item : elements)
			if (item.invokeMethod("__eq__", element).isTrue())
				result++;

		return result;
	}

	public int index(RedObject element)
	{
		return elements.indexOf(element);
	}

	public RedObject pop()
	{
		return elements.remove(elements.size() - 1);
	}

	public RedObject pop(int index)
	{
		return elements.remove(index);
	}

	public RedArrayObject slice(int length)
	{
		return slice(0, length, 1);
	}

	public RedArrayObject slice(int start, int length)
	{
		return slice(start, length, 1);
	}

	public RedArrayObject slice(int start, int length, int stride)
	{
		if (start >= elements.size())
			throw new RuntimeException("Array index out of bounds.");

		if (start < 0)
			start = (elements.size() + start) % elements.size();

		RedArrayObject array = new RedArrayObject();

		for (int i = start; i < start + length && i < elements.size(); i += stride)
			array.add(elements.get(i));

		return array;
	}

	@Override
	public String toString()
	{
		String result = "";

		for (int i = 0; i < elements.size(); i++)
			if (i == elements.size() - 1)
				result += elements.get(i).toString();
			else
				result += elements.get(i).toString() + ", ";

		return "[" + result + "]";
	}

	@Override
	public RedObject __getattr__(RedObject name)
	{
		if (name instanceof RedStringObject)
			if (name.toString().equals("length"))
				return RedIntObject.fromInteger(size());

		return super.__getattr__(name);
	}

	@Override
	public RedObject __setattr__(RedObject name, RedObject object)
	{
		if (name instanceof RedStringObject)
			if (name.toString().equals("length"))
				throw new RuntimeException("Attribute 'length' of array objects is readonly.");

		return super.__setattr__(name, object);
	}

	public RedObject __getitem__(RedObject item)
	{
		if (!(item instanceof RedIntObject))
			throw new RuntimeException("Array index must be integers.");

		int index = ((RedIntObject)item).getValue();

		if (index >= elements.size())
			throw new RuntimeException("Array index out of bounds.");

		if (index < 0)
			index = (elements.size() + index) % elements.size();

		return elements.get(index);
	}

	public RedObject __setitem__(RedObject item, RedObject value)
	{
		if (!(item instanceof RedIntObject))
			throw new RuntimeException("Array index must be integers.");

		int index = ((RedIntObject)item).getValue();

		if (index >= elements.size())
			throw new RuntimeException("Array index out of bounds.");

		if (index < 0)
			index = (elements.size() + index) % elements.size();

		elements.set(index, value);
		return RedNullObject.nullObject;
	}
}
