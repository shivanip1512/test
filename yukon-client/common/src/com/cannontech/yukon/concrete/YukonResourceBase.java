package com.cannontech.yukon.concrete;

import com.cannontech.yukon.IDBPersistent;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.ITimedDatabaseCache;
import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.ISQLStatement;
import com.cannontech.yukon.IYukon;

/**
 * @author rneuharth
 *
 * This class is here to add default functionality to all that extend it.
 * May be needed in the future.  This class may implement new interfaces too.
 */
public abstract class YukonResourceBase implements IYukon
{	
   protected IDatabaseCache dbCache = null;
   protected ITimedDatabaseCache timedDBCache = null;
   protected IDBPersistent dbPersistent = null;
   protected ISQLStatement sqlStatement = null;
   protected IMACSConnection macsConnection = null;



}
