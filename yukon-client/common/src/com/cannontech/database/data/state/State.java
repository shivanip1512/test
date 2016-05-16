package com.cannontech.database.data.state;

import com.cannontech.database.db.state.YukonImage;
/**
 * This type was created in VisualAge.
 * COMMENT TWO
 */

public class State extends com.cannontech.database.db.DBPersistent implements com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.state.State state = null;
   private YukonImage stateImage = null;
   

/**
 * StatusPoint constructor comment.
 */
public State() {
	super();
}


/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{	
   if( getState().getImageID().intValue() > YukonImage.NONE_IMAGE_ID
       && !YukonImage.imageIDExists(getState().getImageID(), getDbConnection()) )
   {
      getStateImage().add();
   }

	getState().add();
}

/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
	getState().delete();
   
/*   if( getState().getImageID().intValue()
       > com.cannontech.database.db.state.State.NONE_IMAGE_ID )
   {
      delete( IMAGE_TABLE_NAME, "ImageID", getState().getImageID() );
   }
*/
   
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.state.State
 */
public com.cannontech.database.db.state.State getState() 
{
	if( state == null )
		state = new com.cannontech.database.db.state.State();
		
	return state;
}


/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void retrieve() throws java.sql.SQLException 
{
	getState().retrieve();

   setImageID( getState().getImageID() );
   
   if( getState().getImageID().intValue() != YukonImage.NONE_IMAGE_ID )
   {      
      getStateImage().retrieve();
   }
	
}


public void setImageID( Integer imgID )
{
   getState().setImageID( imgID );
   getStateImage().setImageID( imgID );
}

/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getState().setDbConnection(conn);
   getStateImage().setDbConnection(conn);
}

/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.state.State
 */
public void setState(com.cannontech.database.db.state.State newValue) 
{
	state = newValue;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
{
	if( getState() != null )
		return getState().getText();
	else
		return null;
}

/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{	
   if( getState().getImageID().intValue() != YukonImage.NONE_IMAGE_ID
       && !YukonImage.imageIDExists(getState().getImageID(), getDbConnection()) )       
   {
      //delete( IMAGE_TABLE_NAME, "ImageID", getState().getImageID() );
      
      getStateImage().add();
   }
   
	getState().update();
}

	/**
	 * Returns the stateImage.
	 * @return StateImage
	 */
	public YukonImage getStateImage()
	{
      if( stateImage == null )
         stateImage = new YukonImage();

		return stateImage;
	}

	/**
	 * Sets the stateImage.
	 * @param stateImage The stateImage to set
	 */
	public void setStateImage(YukonImage stateImage)
	{
		this.stateImage = stateImage;
	}

}
