package com.cannontech.database.data.multi;

import com.cannontech.database.db.DBPersistent;

public class SmartMultiDBPersistent extends CommonMulti implements com.cannontech.database.db.CTIDbChange
{
	//this is the object that is considered to be the owner of all the Objects
	private DBPersistent ownerDBPersistent = null;

/**
 * DeviceBase constructor comment.
 */
public SmartMultiDBPersistent() {
	super();
}

public SmartMultiDBPersistent(MultiDBPersistent aMulti) {
	super();
	super.setDBPersistentVector(aMulti.getDBPersistentVector());
}

/**
 * Adds a DB DBPersistent object to our Multi
 */
public synchronized boolean addDBPersistent( DBPersistent value )
{
	return getDBPersistentVector().add( value );
}

/**
 * A convenience method to add and set the Owner DBPersistent
 */
public synchronized boolean addOwnerDBPersistent( DBPersistent value )
{
	boolean val = getDBPersistentVector().add( value );
	setOwnerDBPersistent( value );

	return val;
}

/**
 * @return boolean
 * @param obj java.lang.Object
 */
private int findReference(Object obj) 
{
	if( obj != null )
		for( int i = 0; i < getDBPersistentVector().size(); i++ )
			if( getDBPersistentVector().get(i).hashCode() == obj.hashCode() )
				return i;


	return -1;
}

/**
 * @return DBPersistent
 */
public DBPersistent getDBPersistent( int index )
{
	return getDBPersistentVector().get(index);
}

/**
 * @return com.cannontech.database.db.DBPersistent
 */
public com.cannontech.database.db.DBPersistent getOwnerDBPersistent() {
	return ownerDBPersistent;
}

public synchronized void insertDBPersistentAt( DBPersistent value, int loc )
{
	getDBPersistentVector().insertElementAt( value, loc );
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
    for(DBPersistent dbPersistent : getDBPersistentVector()) {
        dbPersistent.retrieve();
    }
}

/**
 * This method sets the current ownerDBPersistent to the one given.
 * It then places that owner in the zeroth position of our Vector.
 * @param newOwnerDBPersistent com.cannontech.database.db.DBPersistent
 */
public synchronized void setOwnerDBPersistent(com.cannontech.database.db.DBPersistent newOwnerDBPersistent) 
{
	int loc = findReference( newOwnerDBPersistent );

	if( loc >= 0 )
	{	
		ownerDBPersistent = newOwnerDBPersistent;

		//ensure our owner is at the 0th position of our Vector!!
		if( loc > 0 )
		{
			getDBPersistentVector().remove( loc );
			getDBPersistentVector().insertElementAt( ownerDBPersistent, 0 );
		}
	}
	else
		throw new IllegalArgumentException("New SmartMutliDBPersistent owner reference was not found in the DBPersistent Vector()");
}

/**
 * @return int 
 */

public int size() 
{
	return getDBPersistentVector().size();
}

/**
 * @return java.lang.String
 */
public String toString() {

	if( getOwnerDBPersistent() != null )
		return getOwnerDBPersistent().toString();
	else
		return super.toString();
}
}
