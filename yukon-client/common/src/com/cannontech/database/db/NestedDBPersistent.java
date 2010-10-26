package com.cannontech.database.db;

import java.sql.SQLException;

import com.cannontech.database.TransactionType;

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
	private transient TransactionType opCode = TransactionType.UPDATE;


	public TransactionType getOpCode() {
        return opCode;
    }
	
	public void setOpCode(TransactionType opCode) {
        this.opCode = opCode;
    }
	
	/**
	 * Executes the set operation
	 */
	public void executeNestedOp() throws SQLException {
		switch( getOpCode() )
		{
			case INSERT:
				add();
				break;

			case DELETE:
				delete();
				break;

			case ADD_PARTIAL:
				addPartial();
				break;

			case DELETE_PARTIAL:
				deletePartial();
				break;

			case RETRIEVE:
				retrieve();
				break;

			case UPDATE:
				update();
				break;
				
			default:
				throw new SQLException(
					"Unrecognized NestedDBPersistent opCode set (opCode=" + getOpCode() + ")" );
		}

	}
}