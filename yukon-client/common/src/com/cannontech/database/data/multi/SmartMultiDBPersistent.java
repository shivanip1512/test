package com.cannontech.database.data.multi;

/**
 * This type was created in VisualAge.
 */
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
 * Insert the method's description here.
 * Creation date: (10/25/2001 4:18:09 PM)
 * @return boolean
 */
public synchronized boolean addDBPersistent( DBPersistent value )
{
	return getDBPersistentVector().add(value);
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2002 4:30:34 PM)
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
 * Insert the method's description here.
 * Creation date: (10/25/2001 4:18:09 PM)
 * @return DBPersistent
 */
public DBPersistent getDBPersistent( int index )
{
	return (DBPersistent)getDBPersistentVector().get(index);
}
/**
 * Insert the method's description here.
 * Creation date: (12/27/2001 1:07:21 PM)
 * @return com.cannontech.database.db.DBPersistent
 */
public com.cannontech.database.db.DBPersistent getOwnerDBPersistent() {
	return ownerDBPersistent;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2001 4:18:09 PM)
 * @return boolean
 */
public synchronized void insertDBPersistentAt( DBPersistent value, int loc )
{
	getDBPersistentVector().insertElementAt( value, loc );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	for(int i = 0; i < getDBPersistentVector().size(); i++)
		((DBPersistent)getDBPersistentVector().elementAt(i)).retrieve();
	
}
/**
 * Insert the method's description here.
 * Creation date: (12/27/2001 1:07:21 PM)
 * @param newOwnerDBPersistent com.cannontech.database.db.DBPersistent
 
   This method set the current ownerDBPersistent to the one given.
   It then places that owner in the zeroth position of our Vector.
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
 * Insert the method's description here.
 * Creation date: (10/25/2001 4:18:09 PM)
 * @return int 
 */

public int size() 
{
	return getDBPersistentVector().size();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {

	if( getOwnerDBPersistent() != null )
		return getOwnerDBPersistent().toString();
	else
		return super.toString();
}
}
