package com.cannontech.database.cache.functions;

import com.cannontech.database.data.lite.LiteYukonImage;
/**
 * @author rneuharth
 * Aug 28, 2002 at 8:08:44 AM
 * 
 * A undefined generated comment
 */
public final class YukonImageFuncs
{

	/**
	 * Constructor for YukonImageFuncs.
	 */
	private YukonImageFuncs()
	{
		super();
	}



   /**
    * Insert the method's description here.
    * Creation date: (3/26/2001 9:47:28 AM)
    * @return com.cannontech.database.data.lite.LiteState
    * @param stateGroupID int
    * @param rawState int
    */
   public static String[] getAllCategoris() 
   {
      java.util.ArrayList imgList = new java.util.ArrayList(10);

      com.cannontech.database.cache.DefaultDatabaseCache cache =
            com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

      synchronized( cache )
      {
         java.util.List images = cache.getAllYukonImages();
         java.util.Collections.sort( images, com.cannontech.database.data.lite.LiteComparators.liteYukonImageCategoryComparator );
         String currCat = null;
         
         for( int i = 0; i < images.size(); i++ )
         {
            com.cannontech.database.data.lite.LiteYukonImage image = 
                  (com.cannontech.database.data.lite.LiteYukonImage)images.get(i);

            if( currCat == null )
               currCat = image.getImageCategory();
            
            if( !currCat.equalsIgnoreCase(image.getImageCategory()) )
            {
               imgList.add( image.getImageCategory() );
               currCat = image.getImageCategory();
            }

         }

      }
      
      String[] strs = new String[ imgList.size() ];
      return (String[])imgList.toArray( strs );
   }

}
