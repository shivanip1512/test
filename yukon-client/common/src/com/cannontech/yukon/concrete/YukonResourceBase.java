package com.cannontech.yukon.concrete;

import com.cannontech.yukon.IDBPersistent;
import com.cannontech.yukon.IDatabaseCache;
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
   protected static IDatabaseCache dbCache = null;
   protected static IDBPersistent dbPersistent = null;
   protected static ISQLStatement sqlStatement = null;
   protected static IMACSConnection macsConnection = null;



}
