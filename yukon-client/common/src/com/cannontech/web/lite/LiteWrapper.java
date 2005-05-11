package com.cannontech.web.lite;

import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.UnitMeasureFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;

/**
 * @author ryan
 *
 * Wraps LitePAOs and LitePoints into a more usable format.
 * 
 */
public class LiteWrapper
{
	private LiteBase liteBase = null;


	public LiteWrapper( LiteBase lBase )
	{
		super();
		
		if( !(lBase instanceof LiteYukonPAObject) && !(lBase instanceof LitePoint) )
			throw new IllegalArgumentException("LiteWrapper does not currently accept the given LiteBase object");

		_setLiteBase( lBase );
	}


	public String getItemType()
	{
		//default the type to invalid
		String retVal = PAOGroups.STRING_INVALID;
		
		if( _getLiteBase() instanceof LiteYukonPAObject )
		{
			retVal = 
				PAOGroups.getPAOTypeString(
					((LiteYukonPAObject)_getLiteBase()).getType() ) +
					" device";			
		}
		else if( _getLiteBase() instanceof LitePoint )
		{
			retVal =
				PointTypes.getType(
					((LitePoint)_getLiteBase()).getPointType()) + " point";
		}

		return retVal.toLowerCase();
	}
	
	public String getParent()
	{
		if( _getLiteBase() instanceof LitePoint )
		{
			return 
				PAOFuncs.getLiteYukonPAO(
				((LitePoint)_getLiteBase()).getPaobjectID()).getPaoName();			
		}
		else
			return "---";
	}
	
	public String toString()
	{
		if( _getLiteBase() != null )
			return _getLiteBase().toString();
		else
			return "";
	}

	public String getDescription()
	{
		//default the type to invalid
		String retVal = PAOGroups.STRING_INVALID;
		
		if( _getLiteBase() instanceof LiteYukonPAObject )
		{
			retVal = ((LiteYukonPAObject)_getLiteBase()).getPaoDescription();
		}
		else if( _getLiteBase() instanceof LitePoint )
		{
			retVal =
				UnitMeasureFuncs.getLiteUnitMeasure(
					((LitePoint)_getLiteBase()).getUofmID() ).toString();
		}

		return retVal.toLowerCase();
	}

	public int getItemID()
	{
		if( _getLiteBase() != null )
			return _getLiteBase().getLiteID();
		else
			return 0;
	}

	/**
	 * @return
	 */
	private LiteBase _getLiteBase()
	{
		return liteBase;
	}

	/**
	 * @param base
	 */
	private void _setLiteBase(LiteBase base)
	{
		liteBase = base;
	}

}
