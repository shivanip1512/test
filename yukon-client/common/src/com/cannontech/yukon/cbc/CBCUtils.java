package com.cannontech.yukon.cbc;

import java.text.NumberFormat;
import java.util.Comparator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;

/**
 * @author ryan
 *
 * Generic utility classes for CapControl
 * 
 */
public final class CBCUtils
{
	//responsible for how to render data for CBC displays
	public static final CBCDisplay CBC_DISPLAY = new CBCDisplay();


	public static final Comparator SUB_AREA_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			try
			{
				String thisArea = ((SubBus)o1).getCcArea();
				String anotherArea = ((SubBus)o2).getCcArea();
				
				if( !thisArea.equalsIgnoreCase(anotherArea) )
					return( thisArea.compareToIgnoreCase(anotherArea) );
				
				//if the Area Names	are equal, we need to sort by SubName
				String thisName = ((SubBus)o1).getCcName();
				String anotherName = ((SubBus)o2).getCcName();
				
				return( thisName.compareToIgnoreCase(anotherName) );				
			}
			catch( Exception e )
			{
				CTILogger.error( "Something went wrong with sorting, ignoring sorting rules", e );
				return 0; 
			}
			
		}
	};

	public static final Comparator CCNAME_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			try
			{
				String strA = ((StreamableCapObject)o1).getCcName();
				String strB = ((StreamableCapObject)o2).getCcName();
				
				return strA.compareToIgnoreCase(strB);				
			}
			catch( Exception e )
			{
				CTILogger.error( "Something went wrong with sorting, ignoring sorting rules", e );
				return 0; 
			}
			
		}
	};

	public static final boolean isPowerFactorControlled( String controlUnits )
	{
	   return( CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION.equalsIgnoreCase(controlUnits)
				|| CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION.equalsIgnoreCase(controlUnits) );
	}

	/**
	 * Calculates the summation of VARS for an array of CapBankDevices
	 * that are in a open state.
	 */
	public static final int calcAvailableVARS( CapBankDevice[] capBanks )
	{
		int retVal = 0;
		if( capBanks == null )
			return retVal;

		for( int i = 0; i < capBanks.length; i++ )
		{
			CapBankDevice capBank = capBanks[i];
			if( CapBankDevice.isInAnyOpenState(capBank) )
				retVal += capBanks[i].getBankSize().intValue();
		}

		return retVal;		
	}

	/**
	 * Calculates the summation of VARS for an array of CapBankDevices
	 * that are in a closed state.
	 */
	public static final int calcTotalVARS( CapBankDevice[] capBanks )
	{
		int retVal = 0;
		if( capBanks == null )
			return retVal;

		for( int i = 0; i < capBanks.length; i++ )
		{
			CapBankDevice capBank = capBanks[i];
			if( CapBankDevice.isInAnyCloseState(capBank) )
				retVal += capBanks[i].getBankSize().intValue();
		}

		return retVal;		
	}

	/**
	 * Calculates the average PowerFactor for an array of SubBuses
	 * that have valid PowerFactor values.
	 * 
	 */
	public static final double calcAvgPF( SubBus[] subs )
	{
		double retVal = 0.0;
		if( subs == null )
			return CapControlConst.PF_INVALID_VALUE;

		int pfSubs = subs.length;
		for( int i = 0; i < subs.length; i++ )
		{
			double val = subs[i].getPowerFactorValue().doubleValue();
			if( val <= CapControlConst.PF_INVALID_VALUE )
				--pfSubs;				
			else
				retVal += val;
		}

		//CBCDisplay.STR_NA
		if( pfSubs > 0 )
			retVal = (retVal / pfSubs);
		else
			retVal = CapControlConst.PF_INVALID_VALUE;

		return retVal;
	}

	/**
	 * Calculates the estimaged average PowerFactor for an array of SubBuses
	 * that have valid PowerFactor values.
	 * 
	 */
	public static final double calcAvgEstPF( SubBus[] subs )
	{
		double retVal = 0.0;
		if( subs == null )
			return CapControlConst.PF_INVALID_VALUE;

		int pfSubs = subs.length;
		for( int i = 0; i < subs.length; i++ )
		{
			double val = subs[i].getEstimatedPFValue().doubleValue();
			if( val <= CapControlConst.PF_INVALID_VALUE )
				--pfSubs;				
			else
				retVal += val;
		}

		//CBCDisplay.STR_NA
		if( pfSubs > 0 )
			retVal = (retVal / pfSubs);
		else
			retVal = CapControlConst.PF_INVALID_VALUE;

		return retVal;
	}

	public static String format( int val )
	{
		return NumberFormat.getInstance().format( val );
	}

	public static String format( double val )
	{
		return NumberFormat.getInstance().format( val );
	}

}
