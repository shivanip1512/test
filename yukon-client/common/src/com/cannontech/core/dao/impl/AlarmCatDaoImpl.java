package com.cannontech.core.dao.impl;

import java.util.List;

import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author rneuharth
 * Oct 16, 2002 at 2:12:21 PM
 * 
 * A undefined generated comment
 */
public final class AlarmCatDaoImpl implements AlarmCatDao
{
    private IDatabaseCache databaseCache;
    
	/**
	 * Constructor for AlarmCategoriesFuncs.
	 */
	private AlarmCatDaoImpl()
	{
		super();
	}


   /* Insert the method's description here.
    * Creation date: (3/26/2001 9:41:59 AM)
    * @return com.cannontech.database.data.lite.LitePoint
    * @param pointID int
    */
   /* (non-Javadoc)
 * @see com.cannontech.core.dao.AlarmCatDao#getAlarmCategoryName(int)
 */
public String getAlarmCategoryName( int alarmCatID )
   {   		
   		LiteAlarmCategory lac = getAlarmCategory(alarmCatID);
   		return (lac == null ? null : lac.getCategoryName());	
   }
   
   /* (non-Javadoc)
 * @see com.cannontech.core.dao.AlarmCatDao#getAlarmCategory(int)
 */
public LiteAlarmCategory getAlarmCategory(int id)
   {
      synchronized( databaseCache )
      {
         List categories = databaseCache.getAllAlarmCategories();
         
         for( int j = 0; j < categories.size(); j++ )
         {
            if( id == ((com.cannontech.database.data.lite.LiteAlarmCategory)categories.get(j)).getAlarmStateID() )
               return (com.cannontech.database.data.lite.LiteAlarmCategory)categories.get(j);
         }
   
         return null;
      }	
   }
   
   
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmCatDao#getAlarmCategoryId(java.lang.String)
     */
	public int getAlarmCategoryId( String categoryName ) {
		synchronized( databaseCache ) {

		   List categories = databaseCache.getAllAlarmCategories();
         
		   for( int j = 0; j < categories.size(); j++ )
		   {
			  LiteAlarmCategory alCat = (LiteAlarmCategory)categories.get(j);
			  if( alCat.getCategoryName().equals(categoryName) )
				 return alCat.getAlarmStateID();
		   }
   
		   return PointAlarming.NONE_NOTIFICATIONID;
		}	

	}
    
    // method to return a List of LiteAlarmCategories
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.AlarmCatDao#getAlarmCategories()
     */
    public List getAlarmCategories() 
    {
        List alarmList = databaseCache.getAllAlarmCategories();
        return alarmList;
    }


    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

}
