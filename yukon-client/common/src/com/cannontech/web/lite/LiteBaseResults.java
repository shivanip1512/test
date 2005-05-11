package com.cannontech.web.lite;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Executes a linear search by name on LitePAOs and
 * LitePoints based on a given criteria. The given criteria is
 * a java regular expression.
 *  
 * @author ryan
 */
public class LiteBaseResults
{
	//contains LiteWrapper objects
	private LiteWrapper[] foundItems = new LiteWrapper[0];
	private String criteria = null;
	
	/**
	 * 
	 */
	public LiteBaseResults()
	{
		super();
	}
	
	public void searchLiteObjects( String srchCriteria )
	{
		Vector tempVect = new Vector(32);
		foundItems = new LiteWrapper[0];
		setCriteria( srchCriteria );

		if( getCriteria() != null && getCriteria().length() > 0 )
		{
			Pattern p = null;

			try {
				//always make this search have wildcards on the ends of the criteria
				p = Pattern.compile( ".*"+getCriteria()+".*", Pattern.CASE_INSENSITIVE );
			}
			catch( PatternSyntaxException pse ) {
				CTILogger.error( "Error with regular expression pattern", pse );
				return;
			}

			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();	
			synchronized(cache)
			{
				List allPaos = cache.getAllYukonPAObjects();				
				for( int i = 0; i < allPaos.size(); i++ )
				{
					LiteYukonPAObject pao = (LiteYukonPAObject)allPaos.get(i);
					if( pao.getPaoClass() == PAOGroups.CLASS_CAPCONTROL )
					{
						Matcher m = p.matcher( pao.getPaoName() );
						try {
							if( m.matches() )
								tempVect.add( new LiteWrapper(pao) );
						} catch( PatternSyntaxException pse ) {
							CTILogger.error( "Error with matching regular expression pattern", pse );
						}
							
						LitePoint[] devPoints =
							PAOFuncs.getLitePointsForPAObject( pao.getLiteID() );
						for( int j = 0; j < devPoints.length; j++ )
						{
							Matcher ptM = p.matcher( devPoints[j].getPointName() );
							try {
								if( ptM.matches() )
									tempVect.add( new LiteWrapper(devPoints[j]) );
							} catch( PatternSyntaxException pse ) {
								CTILogger.error( "Error with matching regular expression pattern", pse );
							}
						}
						
					}
				}
				
			}
			
			//sort base on name			
			Collections.sort( tempVect, LiteComparators.liteNameComparator );
			
			foundItems = new LiteWrapper[tempVect.size()];
			foundItems = (LiteWrapper[])tempVect.toArray( foundItems );
		}

	}

	/**
	 * @return
	 */
	public LiteWrapper[] getFoundItems()
	{
		return foundItems;
	}


	/**
	 * @return
	 */
	public String getCriteria()
	{
		return criteria;
	}

	/**
	 * @param string
	 */
	public void setCriteria(String string)
	{
		criteria = string;
	}

}
