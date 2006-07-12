package com.cannontech.yukon.cbc;

import java.text.NumberFormat;
import java.util.Comparator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;

/**
 * @author ryan
 *
 * Generic utility classes for CapControl
 * 
 */
public final class CBCUtils
{
	public static final int TEMP_MOVE_REFRESH = 1000;
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
		//temp variables
		double sumOfVars = 0.0;
		double sumOfWatts = 0.0;
		
		if( subs == null )
			return CapControlConst.PF_INVALID_VALUE;

		int numberOfSubs = subs.length;
		
		for( int i = 0; i < numberOfSubs; i++ )
		{			
			sumOfVars += subs[i].getCurrentVarLoadPointValue().doubleValue();
			sumOfWatts += Math.abs(subs[i].getCurrentWattLoadPointValue().doubleValue() ); 			
			
		}		
		retVal  = sumOfWatts / ( Math.sqrt(Math.pow(sumOfVars, 2.0) + Math.pow(sumOfWatts, 2.0)) );
		if (sumOfVars < 0) {
			retVal = retVal * ( - 1);
		}
		
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
		//temp variables
		double sumOfVars = 0.0;
		double sumOfWatts = 0.0;
		
		if( subs == null )
			return CapControlConst.PF_INVALID_VALUE;

		int numberOfSubs = subs.length;
		
		for( int i = 0; i < numberOfSubs; i++ )
		{			
			sumOfVars += subs[i].getEstimatedVarLoadPointValue().doubleValue();
			sumOfWatts += Math.abs(subs[i].getCurrentWattLoadPointValue().doubleValue() ); 			
			
		}		
		retVal  = sumOfWatts / ( Math.sqrt(Math.pow(sumOfVars, 2.0) + Math.pow(sumOfWatts, 2.0)) );
		if (sumOfVars < 0) {
			retVal = retVal * ( - 1);
		}
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
	
    public static boolean isTwoWay(int type) {        
    	switch (type) {    
    		case PAOGroups.DNP_CBC_6510:
    		case PAOGroups.CBC_7020:
    		case PAOGroups.CBC_7022:
    		case PAOGroups.CBC_7023:
    		case PAOGroups.CBC_7024:
     			return true;    
    		default:
            	return false;
    	}   
    }
    
    public static boolean isTwoWay (LiteYukonPAObject obj) { 	
    	DBPersistent dbPers = LiteFactory.convertLiteToDBPersAndRetrieve(obj);
    	if (dbPers instanceof TwoWayDevice)
    		return true;
    	else	
    		return false;
    }

}