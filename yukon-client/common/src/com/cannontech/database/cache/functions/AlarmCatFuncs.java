package com.cannontech.database.cache.functions;

/**
 * @author rneuharth
 * Oct 16, 2002 at 2:12:21 PM
 * 
 * A undefined generated comment
 */
public final class AlarmCatFuncs
{

	/**
	 * Constructor for AlarmCategoriesFuncs.
	 */
	private AlarmCatFuncs()
	{
		super();
	}


   /* Insert the method's description here.
    * Creation date: (3/26/2001 9:41:59 AM)
    * @return com.cannontech.database.data.lite.LitePoint
    * @param pointID int
    */
   public static String getAlarmCategoryName( int alarmCatID )
   {
      com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
      
      synchronized( cache )
      {
         java.util.List categories = cache.getAllAlarmCategories();
         
         for( int j = 0; j < categories.size(); j++ )
         {
            if( alarmCatID == ((com.cannontech.database.data.lite.LiteAlarmCategory)categories.get(j)).getAlarmStateID() )
               return ((com.cannontech.database.data.lite.LiteAlarmCategory)categories.get(j)).getCategoryName();
         }
   
         return null;
      }
   
   }

}
