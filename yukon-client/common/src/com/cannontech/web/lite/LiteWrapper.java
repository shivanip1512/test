package com.cannontech.web.lite;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author ryan
 *
 * Wraps LitePAOs and LitePoints into a more usable format.
 * 
 */
public class LiteWrapper
{
	public static final String NO_DATA = "---";	
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
					((LiteYukonPAObject)_getLiteBase()).getPaoType().getDbString() +
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


	/**
	 * Returns the type for the current LiteBase object.  This is not the
	 * liteType, but rather the specific type of the object.
	 *
	 */
	public int getRawType()
	{
		//default the type to invalid
		int retVal = PAOGroups.INVALID;
		
		if( _getLiteBase() instanceof LiteYukonPAObject )
		{
			retVal = ((LiteYukonPAObject)_getLiteBase()).getPaoType().getDeviceTypeId();
		}
		else if( _getLiteBase() instanceof LitePoint )
		{
			retVal = ((LitePoint)_getLiteBase()).getPointType();
		}

		return retVal;
	}


	/**
	 * Returns the lite type for the current LiteBase object.
	 *
	 */
	public int getLiteType()
	{
		return _getLiteBase().getLiteType();
	}

	/**
	 * Returns the string representation of our Parent object if this
	 * is a Point
	 */
	public String getParent()
	{
		int id = CtiUtilities.NONE_ZERO_ID;
		if( (id = getParentID()) != CtiUtilities.NONE_ZERO_ID ) {
			return 
				YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(id).getPaoName();
		}
		else
			return NO_DATA;
	}

	/**
	 * Returns the PAObjectID if we are a point, or we return an invalid value
	 */
	public int getParentID()
	{
		if( _getLiteBase() instanceof LitePoint ) {
			return ((LitePoint)_getLiteBase()).getPaobjectID();			
		}
		else
			return CtiUtilities.NONE_ZERO_ID;
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
		String retVal = NO_DATA;
		
		if( _getLiteBase() instanceof LiteYukonPAObject )
		{
			retVal = ((LiteYukonPAObject)_getLiteBase()).getPaoDescription();
		}
		else if( _getLiteBase() instanceof LitePoint )
		{
			if( ((LitePoint)_getLiteBase()).getUofmID() > UnitOfMeasure.INVALID.getId() )
			{
				retVal =
					YukonSpringHook.getBean(UnitMeasureDao.class).getLiteUnitMeasure(
						((LitePoint)_getLiteBase()).getUofmID() ).toString();
			}

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
