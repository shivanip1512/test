package com.cannontech.database.cache.functions;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.LiteContact;

/**
 * @author rneuharth
 * Oct 16, 2002 at 2:12:21 PM
 * 
 * A undefined generated comment
 */
public final class YukonUserFuncs
{

	/**
	 * Constructor for YukonUserFuncs.
	 */
	private YukonUserFuncs()
	{
		super();
	}

   
   public static LiteYukonUser getLiteYukonUser( int userID_ )
   {
      com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
      
      synchronized( cache )
      {
         java.util.List users = cache.getAllYukonUsers();
         
         for( int j = 0; j < users.size(); j++ )
         {
            if( userID_ == ((LiteYukonUser)users.get(j)).getUserID() )
               return (LiteYukonUser)users.get(j);
         }
   
         return null;
      }	
   }

	public static LiteYukonUser getLiteYukonUser( String userName_ )
	{
		if( userName_ == null )
			return null;


		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
      
		synchronized( cache )
		{
			java.util.List users = cache.getAllYukonUsers();
         
			for( int j = 0; j < users.size(); j++ )
			{
				if( userName_.equalsIgnoreCase( ((LiteYukonUser)users.get(j)).getUsername() ) )
					return (LiteYukonUser)users.get(j);
			}
   
			return null;
		}	
	}
	
   public static LiteContact getLiteContact ( int userID_ )
   {
      com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
      
      synchronized( cache )
      {
         java.util.List contacts = cache.getAllContacts();
         
         for( int j = 0; j < contacts.size(); j++ )
         {
            if( userID_ == ((LiteContact)contacts.get(j)).getLoginID() )
               return (LiteContact)contacts.get(j);
         }
   
         return null;
      }	
   }

}
