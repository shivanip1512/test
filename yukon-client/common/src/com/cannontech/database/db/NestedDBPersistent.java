package com.cannontech.database.db;

import java.sql.SQLException;

import com.cannontech.database.Transaction;

/**
 * @author rneuharth
 *
 * This class represents DB objects that are inside other DB objects. 
 * These nested DB objects should not be used alone, but instead operations
 * are provided by a owner DBPersistent class.
 * For example:
 *   Owner may be inside its update() method, but the NestedDBPersistent
 *   objects need to actually delete() themselves.
 * 
 */
public abstract class NestedDBPersistent extends DBPersistent
{
	//used to specify what the DB operation for this instance is 
	private transient int opCode = Transaction.UPDATE;


	/**
	 * @return
	 */
	public int getOpCode()
	{
		return opCode;
	}

	/**
	 * @param i
	 */
	public void setOpCode(int i)
	{
		opCode = i;
	}

	/**
	 * Executes the set operation
	 */
	public void executeNestedOp() throws SQLException
	{
		switch( getOpCode() )
		{
			case Transaction.INSERT:
				add();
				break;

			case Transaction.DELETE:
				delete();
				break;

			case Transaction.ADD_PARTIAL:
				addPartial();
				break;

			case Transaction.DELETE_PARTIAL:
				deletePartial();
				break;

			case Transaction.RETRIEVE:
				retrieve();
				break;

			case Transaction.UPDATE:
				update();
				break;
				
			default:
				throw new SQLException(
					"Unrecognized NestedDBPersistent opCode set (opCode=" + getOpCode() + ")" );
		}

	}


}
