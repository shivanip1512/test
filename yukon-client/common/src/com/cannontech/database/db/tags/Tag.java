/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.tags;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Tag extends com.cannontech.database.db.DBPersistent 
{	
	private Integer tagID = null;
	private String tagName = null;
   	private Integer tagLevel = null;
   	private String inhibit = null;
   	private Integer colorID = null;
   	private Integer imageID = null;
   

   public static final String SETTER_COLUMNS[] = 
   { 
	  "TagName", "TagLevel", "Inhibit", "ColorID", "ImageID"
   };

   public static final String CONSTRAINT_COLUMNS[] = { "TagID" };

   private static final String TABLE_NAME = "Tag";
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


public static synchronized Integer getNextTagID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{		
			stmt = conn.createStatement();
			 rset = stmt.executeQuery( "SELECT Max(TagID)+1 FROM " + TABLE_NAME );	
				
			 //get the first returned result
			 rset.next();
			return new Integer( rset.getInt(1) );
		}
		catch (java.sql.SQLException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if ( stmt != null) stmt.close();
			}
			catch (java.sql.SQLException e2) 
			{
				e2.printStackTrace();
			}
		}
		
		//strange, should not get here
		return new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
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

public String getInhibit() {
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
			setTagName( (String) results[1] );
			setTagLevel( (Integer) results[2] );
			setInhibit( (String) results[3] );
			setColorID( (Integer) results[4] );
			setImageID( (Integer) results[5] );
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

public void setInhibit(String newInhibit) {
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
}
