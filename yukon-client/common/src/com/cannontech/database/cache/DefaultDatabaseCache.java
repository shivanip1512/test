package com.cannontech.database.cache;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author rneuharth
 * Sep 25, 2002 at 5:45:26 PM
 * 
 * A undefined generated comment
 */
public class DefaultDatabaseCache 
{      
    private IDatabaseCache databaseCache;
    
   public synchronized static final IDatabaseCache getInstance()
   {
       return (IDatabaseCache) YukonSpringHook.getBean("databaseCache");
   }


}
