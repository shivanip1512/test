package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.core.dao.LMDao;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class LMDaoImpl implements LMDao
{
    private IDatabaseCache databaseCache;

    /**
	 * LMFuncs constructor comment.
	 */
	public LMDaoImpl() {
		super();
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.LMDao#getAllLMScenarios()
     */
	public LiteYukonPAObject[] getAllLMScenarios()
	{
		//Get an instance of the cache.
		ArrayList scenarioList = new ArrayList(32);
		synchronized(databaseCache)
		{
			List lmScenarios = databaseCache.getAllLMScenarios();
			Collections.sort( lmScenarios, LiteComparators.liteStringComparator );
		
			for (int i = 0; i < lmScenarios.size(); i++)
			{
				LiteYukonPAObject litePao = (LiteYukonPAObject)lmScenarios.get(i);
				scenarioList.add(litePao);	
			}
		}
		
		LiteYukonPAObject retVal[] = new LiteYukonPAObject[scenarioList.size()];
		scenarioList.toArray( retVal );
		return retVal;
	}


	/* (non-Javadoc)
     * @see com.cannontech.core.dao.LMDao#getLMScenarioProgs(int)
     */
	public LiteLMProgScenario[] getLMScenarioProgs( int scenarioID )
	{
		//Get an instance of the cache.
		ArrayList progList = new ArrayList(32);
		synchronized(databaseCache)
		{
			List lmProgs = databaseCache.getAllLMScenarioProgs();
			//Collections.sort( lmProgs, LiteComparators.liteStringComparator );
		
			for( int i = 0; i < lmProgs.size(); i++ )
			{
				LiteLMProgScenario liteProg = (LiteLMProgScenario)lmProgs.get(i);
				if( scenarioID == liteProg.getScenarioID() )
					progList.add(liteProg);	
			}
		}
		
		LiteLMProgScenario retVal[] = new LiteLMProgScenario[progList.size()];
		progList.toArray( retVal );
		return retVal;
	}
    
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
}
