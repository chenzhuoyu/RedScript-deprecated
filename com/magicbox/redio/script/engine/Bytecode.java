package com.magicbox.redio.script.engine;

import java.io.Serializable;

public class Bytecode implements Serializable
{
	private static final long serialVersionUID = 20141003L;
	
	public int opcode = 0;
	public int intAccum = 0;
	public String stringAccum = "";
	public Constant constAccum = new Constant();
	
	public Bytecode(int opcode)
	{
		this.opcode = opcode;
	}

	public static final int LOAD_CONST		= 0x00;		// LOAD_CONST	<const_id>		push CONST_TABLE[<const_id>]
	public static final int LOAD_OBJECT		= 0x01;		// LOAD_OBJECT	<name_id>		push NAME_TABLE[<name_id>]
	public static final int STOR_OBJECT		= 0x02;		// STOR_OBJECT	<name_id>		pop -> NAME_TABLE[<name_id>]

	public static final int GET_ATTR		= 0x03;		// GET_ATTR		<name_id>		<stack_top> = <stack_top>.<name>
	public static final int GET_ITEM		= 0x04;		// GET_ITEM						<stack_top> = <stack_top + 1>[<stack_top>]

	public static final int SET_ATTR		= 0x05;		// SET_ATTR		<name_id>		<stack_top + 1>.<name> = <stack_top>
	public static final int SET_ITEM		= 0x06;		// SET_ITEM						<stack_top + 2>[<stack_top + 1>] = <stack_top>

	public static final int INVOKE			= 0x07;		// INVOKE		<nargs>			<stack_top> = <stack_top>(<stack_top + 1> .. <stack_top + <nargs>>)
	public static final int RETURN			= 0x08;		// RETURN						return <stack_top>

	public static final int ADD				= 0x09;		// ADD							<stack_top> = <stack_top + 1> + <stack_top>
	public static final int SUB				= 0x0A;		// ...
	public static final int MUL				= 0x0B;
	public static final int DIV				= 0x0C;
	public static final int MOD				= 0x0D;
	public static final int POW				= 0x0E;
	public static final int BIT_OR			= 0x0F;
	public static final int BIT_AND			= 0x10;
	public static final int BIT_XOR			= 0x11;
	public static final int BIT_NOT			= 0x12;		// BIT_NOT						<stack_top> = ~<stack_top>
	public static final int LSHIFT			= 0x13;
	public static final int RSHIFT			= 0x14;

	public static final int AUG_ADD			= 0x15;		// AUG_ADD						<stack_top + 1> += <stack_top>
	public static final int AUG_SUB			= 0x16;		// ...
	public static final int AUG_MUL			= 0x17;
	public static final int AUG_DIV			= 0x18;
	public static final int AUG_MOD			= 0x19;
	public static final int AUG_POW			= 0x1A;
	public static final int AUG_BIT_OR		= 0x1B;
	public static final int AUG_BIT_AND		= 0x1C;
	public static final int AUG_BIT_XOR		= 0x1D;
	public static final int AUG_LSHIFT		= 0x1E;
	public static final int AUG_RSHIFT		= 0x1F;

	public static final int BOOL_OR			= 0x20;		// BOOL_OR						<stack_top> = <stack_top + 1> or <stack_top>
	public static final int BOOL_AND		= 0x21;		// ...
	public static final int BOOL_XOR		= 0x22;
	public static final int BOOL_NOT		= 0x23;		// BOOL_NOT						<stack_top> = not <stack_top>

	public static final int EQ				= 0x24;		// EQ							<stack_top> = <stack_top + 1> == <stack_top>
	public static final int LE				= 0x25;		// ...
	public static final int GE				= 0x26;
	public static final int NEQ				= 0x27;
	public static final int LEQ				= 0x28;
	public static final int GEQ				= 0x29;

	public static final int POS				= 0x2A;		// POS							<stack_top> = +<stack_top>
	public static final int NEG				= 0x2B;		// NEG							<stack_top> = -<stack_top>

	public static final int DUP				= 0x2C;		// DUP							Duplicate <stack_top>
	public static final int DUP2			= 0x2D;		// DUP2							Duplicate <stack_top> and <stack_top - 1>
	public static final int DROP			= 0x2E;		// DROP							Drop <stack_top>

	public static final int START_FUNC		= 0x2F;		// START_FUNC	<name_id>		Start making function named <name_id>
	public static final int MAKE_FUNC		= 0x30;		// MAKE_FUNC					Store bytecodes into new function

	public static final int BR				= 0x31;		// BR			<ip>			Branch to <ip>
	public static final int BRTRUE			= 0x32;		// BRFALSE		<ip>			Branch to <ip> if <stack_top> represents True
	public static final int BRFALSE			= 0x33;		// BRFALSE		<ip>			Branch to <ip> if <stack_top> represents False

	public static final int STOP			= 0xFF;		// STOP							Stop the VM
}
