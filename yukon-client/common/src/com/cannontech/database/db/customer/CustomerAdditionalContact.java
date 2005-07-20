/*
 * Created on Jul 20, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.database.db.customer;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.DBPersistent;
import java.sql.SQLException;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CustomerAdditionalContact extends DBPersistent
{
	private Integer customerID = null;
	private Integer contactID = null;
	private Integer ordering = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"ContactID, Ordering"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "CustomerID"};

	public static final String TABLE_NAME = "CustomerAdditionalContact";

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException
	{
		Object addValues[] = { getCustomerID(), getContactID(), getOrdering() };

		add( TABLE_NAME, addValues );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException
	{
		Integer values[] = { getCustomerID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException
	{
		Object constraintValues[] = { getCustomerID() };
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

		if( results.length == SETTER_COLUMNS.length )
		{
			setContactID( (Integer) results[0] );
			setOrdering( (Integer) results[1] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException
	{
		Object setValues[] = { getContactID(), getOrdering() };

		Object constraintValues[] = { getCustomerID() };

		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @return
	 */
	public Integer getContactID()
	{
		return contactID;
	}

	/**
	 * @return
	 */
	public Integer getCustomerID()
	{
		return customerID;
	}

	public Integer getOrdering()
	{
		return ordering;
	}

	/**
	 * @param integer
	 */
	public void setContactID(Integer cont)
	{
		contactID = cont;
	}

	/**
	 * @param integer
	 */
	public void setCustomerID(Integer cust)
	{
		customerID = cust;
	}
	
	public void setOrdering(Integer order)
	{
		ordering = order;
	}

}