package com.cannontech.database.db.state;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author rneuharth
 * Aug 1, 2002 at 11:42:04 AM
 * 
 * A undefined generated comment
 */
public class YukonImage extends DBPersistent
{
   private Integer imageID = null;
   private String imageCategory = com.cannontech.common.util.CtiUtilities.STRING_NONE;   
   private String imageName = com.cannontech.common.util.CtiUtilities.STRING_NONE;   
   private byte[] imageValue = null;

   public static final int NONE_IMAGE_ID = 0;

   public static final String SETTER_COLUMNS[] = 
   { 
      "ImageCategory", "ImageName", "ImageValue"
   };

   public static final String CONSTRAINT_COLUMNS[] = { "ImageID" };
   
   public static final String TABLE_NAME = "YukonImage";

	/**
	 * Constructor for StateImage.
	 */
	public YukonImage()
	{
		super();
	}


   public static int getNextImageID( java.sql.Connection conn )
   {
      java.sql.PreparedStatement pstmt = null;
      java.sql.ResultSet rSet = null;
   
      String sql = "select MAX(ImageID) from " + TABLE_NAME;
   
      try
      {     
   
         if( conn == null )
         {
            throw new IllegalStateException("Database connection can not be (null).");
         }
         else
         {
            pstmt = conn.prepareStatement( sql.toString() );
            rSet = pstmt.executeQuery();
          
            while( rSet.next() )
               return rSet.getInt(1) + 1;
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
            if( pstmt != null ) pstmt.close();
            if( conn != null ) conn.close();
         } 
         catch( java.sql.SQLException e2 )
         {
            com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
         }  
      }
      
      return -1;
   }
   
   /**
    * This method was created by Cannon Technologies Inc.
    * @return boolean
    * @param imageID java.lang.Integer
    */
   public static boolean imageIDExists(Integer imageID, java.sql.Connection conn )
   {
      java.sql.PreparedStatement pstmt = null;
      java.sql.ResultSet rSet = null;
   
      String sql = "select count(*) from " + TABLE_NAME +
                   " where ImageID= " + imageID;
   
      try
      {     
   
         if( conn == null )
         {
            throw new IllegalStateException("Database connection can not be (null).");
         }
         else
         {
            pstmt = conn.prepareStatement( sql.toString() );
            rSet = pstmt.executeQuery();
          
            while( rSet.next() )
               return rSet.getInt(1) > 0;
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
            if( pstmt != null ) pstmt.close();
         } 
         catch( java.sql.SQLException e2 )
         {
            com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
         }  
      }
   
   
      return false;
   }



	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException
	{
      Object setValues[] = 
      { 
         getImageID(), getImageCategory(), getImageName(), getImageValue()
      };
   
      add( TABLE_NAME, setValues );
   
   }

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException
	{
      Object constraintValues[] = { getImageID() };
      
      delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );      
   }

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException
	{
      Object constraintValues[] = { getImageID() };
   
      Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
   
      if( results.length == SETTER_COLUMNS.length )
      {
         setImageCategory( (String) results[0] );
         setImageName( (String) results[1] );
         setImageValue( (byte[]) results[2] );
      }
      else
         throw new Error( getClass() + "::retrieve - Incorrect number of results" );

   }

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException
	{   
      Object constraintValues[] = { getImageID() };
      
      Object setValues[] = 
      { 
         getImageCategory(), getImageName(), getImageValue()
      };

      update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );   
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

	/**
	 * Returns the imageID.
	 * @return Integer
	 */
	public Integer getImageID()
	{
		return imageID;
	}

	/**
	 * Sets the imageID.
	 * @param imageID The imageID to set
	 */
	public void setImageID(Integer imageID)
	{
		this.imageID = imageID;
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
