package com.cannontech.database.data.multi;

import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public abstract class CommonMulti extends com.cannontech.database.db.DBPersistent 
{
	private java.util.Vector<DBPersistent> dbPersistentVector = null;

/**
 * CommonMulti constructor comment.
 */
protected CommonMulti() {
	super();
}

/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	for(int i = 0; i < getDBPersistentVector().size(); i++)
	{			
		getDBPersistentVector().elementAt(i).add();
	}
}

/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{

    for(int i = 0; i < getDBPersistentVector().size(); i++){           
        getDBPersistentVector().elementAt(i).delete();
    }

}

public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType)
{
	java.util.ArrayList<DBChangeMsg> list = new java.util.ArrayList<DBChangeMsg>(10);

	for( int i = 0; i < getDBPersistentVector().size(); i++ )
	{
		if( getDBPersistentVector().get(i) instanceof CTIDbChange )
		{
			//add the basic change method
			DBChangeMsg[] msgs = 
				((CTIDbChange)getDBPersistentVector().get(i)).getDBChangeMsgs(dbChangeType);

			for( int j = 0; j < msgs.length; j++ )
				list.add( msgs[j] );
		}

	}
	

	if( list.size() == 0 )
		return new DBChangeMsg[0];
	else
	{ 
		DBChangeMsg[] dbChange = new DBChangeMsg[list.size()];
		return list.toArray( dbChange );
	}

}

protected java.util.Vector<DBPersistent> getDBPersistentVector() 
{
	if( dbPersistentVector == null )
		dbPersistentVector = new java.util.Vector<DBPersistent>();
		
	return dbPersistentVector;
}

/**
 * Finds the first object of the given class in the Vector.
 * This method checks the first element and searches the entire
 * object hierarchy of that element for the Class, 
 * then moves to the next element and repeats.
 */
@SuppressWarnings("unchecked")
public static final DBPersistent getFirstObjectOfType( Class type, CommonMulti multi )
{
	if( multi != null )
	{
		for( int i = 0; i < multi.getDBPersistentVector().size(); i++ )
		{
         Class currClass = multi.getDBPersistentVector().get(i).getClass();
         
         while( currClass != null )
         {
   			if( currClass.equals(type) )
   				return multi.getDBPersistentVector().get(i);
            else
               currClass = currClass.getSuperclass();
         }
		}

	}

	return null;
}

/**
 * @param newCreateNewPAOIDs boolean
 * @deprecated has no effect
 */
@Deprecated()
public void setCreateNewPAOIDs(boolean newCreateNewPAOIDs) {
	// no-op
}

/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	for(int i = 0;i < getDBPersistentVector().size();i++)
	{
		getDBPersistentVector().elementAt(i).setDbConnection(conn);
	}
}

/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.Device
 */
protected void setDBPersistentVector(java.util.Vector<DBPersistent> newValue) {
	this.dbPersistentVector = newValue;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {

	if( getDBPersistentVector().size() > 0 )
		return getDBPersistentVector().elementAt(0).toString();
	else
		return null;
}

/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
    for (DBPersistent dbPersistent : getDBPersistentVector()) {
        dbPersistent.update();
    }
}
}
