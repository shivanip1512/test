package com.cannontech.core.dao.impl;

import java.util.Iterator;

import com.cannontech.core.dao.YukonImageDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.yukon.IDatabaseCache;
/**
 * @author rneuharth
 * @author alauinger
 * Aug 28, 2002 at 8:08:44 AM
 */
public final class YukonImageDaoImpl implements YukonImageDao
{
    private IDatabaseCache databaseCache;
	/**
	 * Constructor for DaoFactory.getYukonImageDao().
	 */
	public YukonImageDaoImpl()
	{
		super();
	}

   /* (non-Javadoc)
 * @see com.cannontech.core.dao.YukonImageDao#getAllCategoris()
 */
   public String[] getAllCategoris() 
   {
      java.util.ArrayList imgList = new java.util.ArrayList(10);

      synchronized( databaseCache )
      {
         java.util.List images = databaseCache.getAllYukonImages();
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
   
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonImageDao#getLiteYukonImage(int)
     */
   	public LiteYukonImage getLiteYukonImage(int id) {
         
         synchronized( databaseCache )
         {
            Iterator iter = databaseCache.getAllYukonImages().iterator();
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
      
   /* (non-Javadoc)
 * @see com.cannontech.core.dao.YukonImageDao#getLiteYukonImage(java.lang.String)
 */
   public LiteYukonImage getLiteYukonImage(String name) {
         synchronized( databaseCache )
         {
            Iterator iter = databaseCache.getAllYukonImages().iterator();
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

   /* (non-Javadoc)
 * @see com.cannontech.core.dao.YukonImageDao#yukonImageUsage(int)
 */
   public String yukonImageUsage( int yukImgID_ ) 
   {
      synchronized( databaseCache )
      {
         Iterator stateGroupsItr = databaseCache.getAllStateGroupMap().values().iterator();         
         
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

   public void setDatabaseCache(IDatabaseCache databaseCache) {
    this.databaseCache = databaseCache;
   }
}
