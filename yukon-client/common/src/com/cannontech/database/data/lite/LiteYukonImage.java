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

   public LiteYukonImage( int imageID, String imgName_  )
   {
      this(imageID);
      setImageName( imgName_ );
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
      com.cannontech.database.SqlStatement s = 
         new com.cannontech.database.SqlStatement(
            "SELECT ImageCategory,ImageName,ImageValue from " +
               com.cannontech.database.db.state.YukonImage.TABLE_NAME +
               " where ImageID = " + getImageID(),
            com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
   
      try 
      {
         s.execute();
   
         if( s.getRowCount() <= 0 )
            throw new IllegalStateException("Unable to find DeviceMeterGroup with deviceID = " + getLiteID() );
   
   
         setImageCategory( s.getRow(0)[0].toString() );
         setImageName( s.getRow(0)[1].toString() );
         setImageValue( (byte[])s.getRow(0)[2] );
      }
      catch( Exception e )
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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

	public String toString()
	{
		return getImageName();
	}
	
}
