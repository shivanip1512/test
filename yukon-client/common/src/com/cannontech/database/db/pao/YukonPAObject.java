package com.cannontech.database.db.pao;
/**
 * This type was created in VisualAge.
 */

public class YukonPAObject extends com.cannontech.database.db.DBPersistent
{
	private Integer paObjectID = null;
	private String category = null;
	private String paoClass = null;
	private String paoName = null;
	private String type = null;
	private String description = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private Character disableFlag = new Character('N');
	private String paoStatistics = "-----";


	public static final char STAT_HOURLY  = 'H';
	public static final char STAT_DAILY   = 'D';
	public static final char STAT_MONTHLY = 'M';
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"Category", "PAOClass", "PAOName",
		"Type", "Description", "DisableFlag", "PAOStatistics"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "PAObjectID" };

	public static final String TABLE_NAME = "YukonPAObject";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public YukonPAObject() {
	super();
}


/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getPaObjectID(), getCategory(),
					getPaoClass(), 
					(getPaoName().length() <= 60 ? getPaoName() : getPaoName().substring(0,60) ),
					getType(), 
					getDescription(), getDisableFlag(), getPaoStatistics() };

	add( TABLE_NAME, addValues );
}


/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Object values[] = { getPaObjectID() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @return java.lang.String
 */
public java.lang.String getCategory() {
	return category;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @return java.lang.Character
 */
public java.lang.Character getDisableFlag() {
	return disableFlag;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static Integer getNextYukonPAObjectID()
{

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List paObjects = cache.getAllYukonPAObjects();
		java.util.Collections.sort(paObjects);

		int counter = 1;
		int currentID;
		 														
		for(int i=0;i<paObjects.size();i++)
		{
			currentID = ((com.cannontech.database.data.lite.LiteYukonPAObject)paObjects.get(i)).getYukonID();

			if( currentID > counter )
				break;
			else
				counter = currentID + 1;
		}		
		
		return new Integer( counter );
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static int[] getNextYukonPAObjectIDs( int idCount )
{
	//THIS IS A SUPER HACK WAY TO DO THINGS, CHANGE IN THE FUTURE  (It is quick and dirty!!)
	// *************** BEGIN SUPER HACK *************************/
	System.out.println("----- getNextYukonPAObjectIDs(yukonPAObjectsCnt) called with " + idCount + " ids!");

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	int[] returnIDs = new int[idCount];
	
	synchronized(cache)
	{
		java.util.List paObjects = cache.getAllYukonPAObjects();
		int[] ids = new int[paObjects.size()];
		int counter = 1;

		for( int i = 0; i < paObjects.size(); i++ )
			ids[i] = ((com.cannontech.database.data.lite.LiteYukonPAObject)paObjects.get(i)).getYukonID();		

		java.util.Arrays.sort(ids);

		for( int i = 0; i < idCount; i++ )
		{
			int loc = -1;
			while( (loc = java.util.Arrays.binarySearch(ids, counter)) >= 0 )
				counter++;

			//unfound value
			returnIDs[i] = counter;
			counter++;		
		}
		
		return returnIDs;//new Integer( counter );
	}
	
	// *************** END SUPER HACK *************************/
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPaObjectID() {
	return paObjectID;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @return java.lang.String
 */
public java.lang.String getPaoClass() {
	return paoClass;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @return java.lang.String
 */
public java.lang.String getPaoName() {
	return paoName;
}


/**
 * Insert the method's description here.
 * Creation date: (6/19/2002 2:59:53 PM)
 * @return java.lang.String
 */
public java.lang.String getPaoStatistics() {
	return paoStatistics;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:00:14 AM)
 * @return java.lang.Object
 */
public Object getPrimaryKey()
{
	return getPaObjectID();
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}


/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getPaObjectID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setCategory( (String) results[0] );
		setPaoClass( (String) results[1] );
		setPaoName( (String) results[2] );
		setType( (String) results[3] );
		setDescription( (String)results[4] );
		setDisableFlag( new Character( ((String)results[5]).charAt(0) ) );
		setPaoStatistics( (String)results[6] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @param newCategory java.lang.String
 */
public void setCategory(java.lang.String newCategory) {
	category = newCategory;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @param newDisableFlag java.lang.Character
 */
public void setDisableFlag(java.lang.Character newDisableFlag) {
	disableFlag = newDisableFlag;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @param newPaObjectID java.lang.Integer
 */
public void setPaObjectID(java.lang.Integer newPaObjectID) {
	paObjectID = newPaObjectID;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @param newPaoClass java.lang.String
 */
public void setPaoClass(java.lang.String newPaoClass) {
	paoClass = newPaoClass;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @param newPaoName java.lang.String
 */
public void setPaoName(java.lang.String newPaoName) {
	paoName = newPaoName;
}


/**
 * Insert the method's description here.
 * Creation date: (6/19/2002 2:59:53 PM)
 * @param newPaoStatistics java.lang.String
 */
public void setPaoStatistics(java.lang.String newPaoStatistics) {
	paoStatistics = newPaoStatistics;
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:50:57 AM)
 * @param newType java.lang.String
 */
public void setType(java.lang.String newType) {
	type = newType;
}


/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getCategory(),
					getPaoClass(), 
					(getPaoName().length() <= 60 ? getPaoName() : getPaoName().substring(0,60) ),
					getType(), 
					getDescription(), getDisableFlag(), getPaoStatistics() };
					
	Object constraintValues[] = { getPaObjectID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}