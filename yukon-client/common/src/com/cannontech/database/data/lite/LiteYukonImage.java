package com.cannontech.database.data.lite;

/**
 * @author rneuharth
 * Aug 1, 2002 at 3:04:49 PM
 * 
 * A undefined generated comment
 */
public class LiteYukonImage extends LiteBase
{
   public static final LiteYukonImage NONE_IMAGE = new LiteYukonImage(
               com.cannontech.database.db.state.YukonImage.NONE_IMAGE_ID,
               com.cannontech.common.util.CtiUtilities.STRING_NONE,
               com.cannontech.common.util.CtiUtilities.STRING_NONE,
               null);
   
   private String imageCategory = null;
   private String imageName = null;
   private byte[] imageValue = null;


   public LiteYukonImage( int imageID )
   {
      super();
      setImageID(imageID);
      setLiteType(LiteTypes.STATE_IMAGE);
   }

   /**
    * LiteDevice
    */
   public LiteYukonImage( int imageID, String imageCategory_, String imageName_, byte[] imageValue_ )
   {
      this( imageID );
      setImageName( imageName_ );
      setImageCategory( imageCategory_ );
      setImageValue( imageValue_ );
      setLiteType(LiteTypes.STATE_IMAGE);
   }



   /**
    * retrieve method comment.
    */
   public void retrieve(String databaseAlias)
   {
      //get all the customer contacts that are assigned to a customer
      String sqlString = "SELECT ImageCategory,ImageName,ImageValue from " +
                  com.cannontech.database.db.state.YukonImage.TABLE_NAME +
                  " where ImageID = " + getImageID() ;
   
      java.sql.Connection conn = null;
      java.sql.Statement stmt = null;
      java.sql.ResultSet rset = null;
      try
      {
         conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sqlString);
   
         while (rset.next())
         {
            setImageCategory( rset.getString(1).trim() );
            setImageName( rset.getString(2).trim() );
            setImageValue( (byte[])rset.getObject(3) );
         }

      }
      catch( java.sql.SQLException e )
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      finally
      {
         try
         {
            if( stmt != null )
               stmt.close();
            if( conn != null )
               conn.close();
         }
         catch( java.sql.SQLException e )
         {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
         }
      }
   
   }   
   
	/**
	 * Returns the imageName.
	 * @return String
	 */
	public String getImageName()
	{
		return imageName;
	}

	/**
	 * Sets the imageName.
	 * @param imageName The imageName to set
	 */
	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

   public void setImageID(int imageID_) 
   {
      setLiteID( imageID_ );
   }
   
   public int getImageID()
   {
      return getLiteID();
   }
   
	/**
	 * Returns the imageCategory.
	 * @return String
	 */
	public String getImageCategory()
	{
		return imageCategory;
	}

	/**
	 * Returns the imageValue.
	 * @return byte[]
	 */
	public byte[] getImageValue()
	{
		return imageValue;
	}

	/**
	 * Sets the imageCategory.
	 * @param imageCategory The imageCategory to set
	 */
	public void setImageCategory(String imageCategory)
	{
		this.imageCategory = imageCategory;
	}

	/**
	 * Sets the imageValue.
	 * @param imageValue The imageValue to set
	 */
	public void setImageValue(byte[] imageValue)
	{
		this.imageValue = imageValue;
	}

}
