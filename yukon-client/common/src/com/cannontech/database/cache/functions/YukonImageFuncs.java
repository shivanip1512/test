package com.cannontech.database.cache.functions;

import java.util.Iterator;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
/**
 * @author rneuharth
 * @author alauinger
 * Aug 28, 2002 at 8:08:44 AM
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
    * Returns all available YukonImage categories from the cache.
    * Creation date: (3/26/2001 9:47:28 AM)
    * @return com.cannontech.database.data.lite.LiteState
    * @param stateGroupID int
    * @param rawState int
    */
   public static String[] getAllCategoris() 
   {
      java.util.ArrayList imgList = new java.util.ArrayList(10);

      DefaultDatabaseCache cache =
            DefaultDatabaseCache.getInstance();

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
   
	/**
	 * Returns the LiteYukonImage in the cache with the given id
	 * @param id
	 * @return LiteYukonImage
	 */
   	public static LiteYukonImage getLiteYukonImage(int id) {
         
   		 DefaultDatabaseCache cache =
            DefaultDatabaseCache.getInstance();
         synchronized( cache )
         {
            Iterator iter = cache.getAllYukonImages().iterator();
            while( iter.hasNext() ) 
            {
               LiteYukonImage img = (LiteYukonImage) iter.next();
               if( img.getImageID() == id ) 
               {
                  return img;
               }
            }
         }        
         
         return null;
      }
      
   /**
	 * Returns the first LiteYukonImage in the cache with the given name
	 * @param name
	 * @return LiteYukonImage
	 */
   public static LiteYukonImage getLiteYukonImage(String name) {
   		 DefaultDatabaseCache cache =
            DefaultDatabaseCache.getInstance();
         synchronized( cache )
         {
            Iterator iter = cache.getAllYukonImages().iterator();
            while( iter.hasNext() ) 
            {
               LiteYukonImage img = (LiteYukonImage) iter.next();
               if( img.getImageName().equalsIgnoreCase(name) ) 
               {
                  return img;
               }
            }
         }        
         
         return null;
   }

   /** 
    * Returns the StateGroup that uses the YukonImageID,
    * If no StateGroup uses the YukonImageID a null is returned
    */
   public static String yukonImageUsage( int yukImgID_ ) 
   {
      java.util.ArrayList imgList = new java.util.ArrayList(10);

      DefaultDatabaseCache cache =
            DefaultDatabaseCache.getInstance();

      synchronized( cache )
      {
         Iterator stateGroupsItr = cache.getAllStateGroupMap().values().iterator();         
         
         while( stateGroupsItr.hasNext() )
         {
            LiteStateGroup sGroup = (LiteStateGroup)stateGroupsItr.next();
            
            for( int j = 0; j < sGroup.getStatesList().size(); j++ )
            {
               LiteState state = (LiteState)sGroup.getStatesList().get(j);
               
               if( state.getImageID() == yukImgID_ )
               {
                  return sGroup.getStateGroupName();
               }
            }
            
         }

      }
      
      //this image is not used, just return null
      return null;
   }

}
