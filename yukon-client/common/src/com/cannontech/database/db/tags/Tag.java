/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.tags;

import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Tag extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{	
	private Integer tagID = null;
	private String tagName = null;
   	private Integer tagLevel = null;
	private Character inhibit = null;
   	private Integer colorID = null;
   	private Integer imageID = null;
   

   public static final String SETTER_COLUMNS[] = 
   { 
	  "TagName", "TagLevel", "Inhibit", "ColorID", "ImageID"
   };

   public static final String CONSTRAINT_COLUMNS[] = { "TagID" };

   private static final String TABLE_NAME = "Tags";
/**
 * StateGroup constructor comment.
 */
public Tag() 
{
	super();
}
/**
 * StateGroup constructor comment.
 */
public Tag(Integer id, String name) {
	super();
	setTagID(id);
	setTagName(name);
}


/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if(getTagID() == null)
		setTagID(getNextTagID());
	
	Object addValues[] = { getTagID(), getTagName(), getTagLevel(), getInhibit(), getColorID(), getImageID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Object constraintValues[] = { getTagID() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}

public final static Integer getNextTagID() throws java.sql.SQLException 
{	
	return getNextTagID(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}

/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static Integer getNextTagID(String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT Max(TagID)+1 FROM " + TABLE_NAME ,
													databaseAlias );

	try
	{
		stmt.execute();
		return ((Integer)stmt.getRow(0)[0] );
	}
	catch( Exception e )
	{
		return new Integer(-5);
	}
}


public Integer getTagID() {
	return tagID;
}

public String getTagName() {
	return tagName;
}

public Integer getTagLevel() {
	return tagLevel;
}

public Character getInhibit() {
	return inhibit;
}

public Integer getColorID() {
	return colorID;
}

public Integer getImageID() {
	return imageID;
}

public void retrieve() 
{
	Integer constraintValues[] = { getTagID() };	
	
	try
	{
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setTagName( (String) results[0] );
			setTagLevel( (Integer) results[1] );
			setInhibit( new Character( ((String)results[2]).charAt(0) ) );
			setColorID( (Integer) results[3] );
			setImageID( (Integer) results[4] );
		}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

public void setTagLevel(java.lang.Integer newTagLevel) {
	tagLevel = newTagLevel;
}

public void setInhibit(Character newInhibit) {
	inhibit = newInhibit;
}

public void setTagName(java.lang.String newTagName) {
	tagName = newTagName;
}

public void setTagID(java.lang.Integer newTagID) {
	tagID = newTagID;
}

public void setColorID(java.lang.Integer newColorID) {
	colorID = newColorID;
}

public void setImageID(java.lang.Integer newImageID) {
	imageID = newImageID;
}

public void update() 
{
	Object setValues[] =
	{ 
		getTagID(),
		getTagName(), getTagLevel(),
		getInhibit(), getColorID(),
		getImageID()
	};
	
	Object constraintValues[] = { getTagID() };
	
	try
	{
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}
	/* (non-Javadoc)
	 * @see com.cannontech.database.db.CTIDbChange#getDBChangeMsgs(int)
	 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
			getTagID().intValue(),
			com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TAG_DB,
			com.cannontech.message.dispatch.message.DBChangeMsg.CAT_TAG,
			com.cannontech.message.dispatch.message.DBChangeMsg.CAT_TAG,
			typeOfChange)
	};


	return msgs;
}

}
