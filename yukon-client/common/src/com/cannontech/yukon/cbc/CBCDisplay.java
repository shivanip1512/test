package com.cannontech.yukon.cbc;

import java.awt.Color;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.util.ColorUtil;

/**
 * @author rneuharth
 *
 */
public class CBCDisplay
{
    public static final String STR_NA = "  NA";
    public static final String DASH_LINE = "  ----"; 
	public static final String STR_UNKNOWN = "Unknown";   
    public short dateTimeFormat = ModifiedDate.FRMT_DEFAULT;
    
    
	//Column numbers for the CabBank display
	public static final int CB_NAME_COLUMN = 0;
	public static final int CB_BANK_ADDRESS_COLUMN = 1;
	public static final int CB_BANK_SIZE_COLUMN = 2;
	public static final int CB_STATUS_COLUMN = 3;
	public static final int CB_TIME_STAMP_COLUMN = 4;
	public static final int CB_OP_COUNT_COLUMN = 5;
	public static final int CB_PARENT_COLUMN = 6;
	    
	//Column numbers for the Feeder display	
	public static final int FDR_NAME_COLUMN = 0;
	public static final int FDR_CURRENT_STATE_COLUMN = 1;
	public static final int FDR_TARGET_COLUMN = 2;
	public static final int FDR_VAR_LOAD_COLUMN = 3;
	public static final int FDR_WATTS_COLUMN = 4;
	public static final int FDR_POWER_FACTOR_COLUMN = 5;
	public static final int FDR_TIME_STAMP_COLUMN = 6;
	public static final int FDR_DAILY_OPERATIONS_COLUMN = 7;
	
	//Column numbers for the SubBus display	
	public static final int SUB_AREA_NAME_COLUMN  = 0;
	public static final int SUB_NAME_COLUMN = 1;
	public static final int SUB_CURRENT_STATE_COLUMN = 2;
	public static final int SUB_TARGET_COLUMN = 3;
	public static final int SUB_VAR_LOAD_COLUMN = 4;
	public static final int SUB_WATTS_COLUMN = 5;
	public static final int SUB_POWER_FACTOR_COLUMN = 6;
	public static final int SUB_TIME_STAMP_COLUMN = 7;
	public static final int SUB_DAILY_OPERATIONS_COLUMN = 8;
	

	//The color schemes - based on the schedule status
	private static final Color[] _DEFAULT_COLORS =
	{
		//Enabled subbus (Green like color)
		new Color(60, 130, 66),
		//Disabled subbus
		Color.RED,
		//Pending subbus (Yellow like color)
		new Color(240, 145, 0)
	};


    public CBCDisplay()
    {
    	this( ModifiedDate.FRMT_DEFAULT );
    }

    public CBCDisplay( short formatInt )
    {
    	super();
    	dateTimeFormat = formatInt;
    }
    
	/**
	 * The text of capbanks states. This can change since is based on a state group
	 * 
	 */
	public static LiteState[] getCBCStateNames()
	{
		return StateFuncs.getLiteStates( StateGroupUtils.STATEGROUPID_CAPBANK );		
	}
	
    /**
     * getValueAt method for CapBanks.
     * 
     */
    public synchronized Object getCapBankValueAt( CapBankDevice capBank, int col) 
    {
        if( capBank == null )
            return "";

        switch( col )
        {
            case CB_NAME_COLUMN :
            {
                return capBank.getCcName() + " (" + capBank.getControlOrder() + ")";
            }

            case CB_BANK_ADDRESS_COLUMN :
            {
                return capBank.getCcArea();
            }
    
            case CB_STATUS_COLUMN:
            {
                if( capBank.getControlStatus().intValue() < 0 ||
                        capBank.getControlStatus().intValue() >= getCBCStateNames().length )
                {
                    CTILogger.info("*** A CapBank state was found that has no corresponding status.");
                    return STR_UNKNOWN + " (" + capBank.getControlStatus().intValue() +")" ;
                }
                else
                {
                    if( capBank.getCcDisableFlag().booleanValue() == true )
                        return "DISABLED : " + 
                        	(capBank.getOperationalState().equalsIgnoreCase(CapBank.FIXED_OPSTATE) 
                                ? CapBank.FIXED_OPSTATE 
                                : getCBCStateNames()[capBank.getControlStatus().intValue()].getStateText());
                    else
                        return (capBank.getOperationalState().equalsIgnoreCase(CapBank.FIXED_OPSTATE) 
                                    ? CapBank.FIXED_OPSTATE + " : " 
                                    : "") + getCBCStateNames()[capBank.getControlStatus().intValue()].getStateText();
                }
            }
                    
            case CB_OP_COUNT_COLUMN :
            {
                return capBank.getCurrentDailyOperations();
            }
                
            case CB_BANK_SIZE_COLUMN :
            {
                return capBank.getBankSize();
            }

            case CB_TIME_STAMP_COLUMN:
            {
                if( capBank.getLastStatusChangeTime().getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    return "  ----";
                else
                    return new ModifiedDate(
                            capBank.getLastStatusChangeTime().getTime(), dateTimeFormat );    
            }

			case CB_PARENT_COLUMN:
			{
				LiteYukonPAObject paoParent = PAOFuncs.getLiteYukonPAO( capBank.getParentID() );				
				if( paoParent != null )
					return paoParent;
				else
					return DASH_LINE;
			}

            default:
                return null;
        }               
    }

    /**
     * getValueAt method for SubBuses
     * 
     */
    public synchronized Object getSubBusValueAt(SubBus subBus, int col) 
    {
        if( subBus == null )
            return "";

        switch( col )
        {
            case SUB_NAME_COLUMN:
            {
                return subBus.getCcName();
            }

            case SUB_AREA_NAME_COLUMN:
            {
                return subBus.getCcArea();
            }

            case SUB_CURRENT_STATE_COLUMN:
            {
                String state = null;
                
                if( subBus.getCcDisableFlag().booleanValue() )
                {
                    state = "DISABLED";
                }
                else if( subBus.getRecentlyControlledFlag().booleanValue() )
                {
                    state = _getSubBusPendingState( subBus );
                    
                    if( state == null )
                    {
                        state = "PENDING"; //we only know its pending for sure
                    }
                    
                }
                else
                    state = "ENABLED";


                //show waived with a W at the end of the state
                if( subBus.getWaiveControlFlag().booleanValue() )
                    state += "-W";

                return state;

            }

            case SUB_TARGET_COLUMN:
            {
                // decide which set Point we are to use
                if( CBCUtils.isPowerFactorControlled(subBus.getControlUnits()) )
                {
                    return getPowerFactorText(subBus.getPeakSetPoint().doubleValue(), false);
                }
                else if( subBus.getLowerBandWidth().doubleValue() == 0
                             && subBus.getUpperBandWidth().doubleValue() == 0 )
                {
                    return STR_NA;
                }
                else if( subBus.getPeakTimeFlag().booleanValue() )
                {
                    return
                        CommonUtils.formatDecimalPlaces(subBus.getPeakSetPoint().doubleValue() - subBus.getLowerBandWidth().doubleValue(), 0) +
                        " to " + 
                        CommonUtils.formatDecimalPlaces(subBus.getUpperBandWidth().doubleValue() + subBus.getPeakSetPoint().doubleValue(), 0) + 
                        " Pk";
                }
                else
                {
                    return
                        CommonUtils.formatDecimalPlaces(subBus.getOffPeakSetPoint().doubleValue() - subBus.getLowerBandWidth().doubleValue(), 0) +
                        " to " + 
                        CommonUtils.formatDecimalPlaces(subBus.getUpperBandWidth().doubleValue() + subBus.getOffPeakSetPoint().doubleValue(), 0) + 
                        " OffPk";
                }
            }
                
            case SUB_DAILY_OPERATIONS_COLUMN:
            {
                return new String(subBus.getCurrentDailyOperations() + " / " + 
                        (subBus.getMaxDailyOperation().intValue() <= 0 
                            ? STR_NA 
                            : subBus.getMaxDailyOperation().toString()) );
            }

            case SUB_VAR_LOAD_COLUMN:
            {
                String retVal = DASH_LINE; //default just in case

                if( subBus.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
                   retVal = DASH_LINE;
                else 
                {                        
                    if( subBus.getDecimalPlaces().intValue() == 0 )
                            retVal =  CommonUtils.formatDecimalPlaces( 
                          subBus.getCurrentVarLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() );             
                    else
                            retVal = CommonUtils.formatDecimalPlaces( 
                          subBus.getCurrentVarLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() );
                }
                
                retVal += " / ";

                if( subBus.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
                    retVal += DASH_LINE;
                else 
                {               
                    if( subBus.getDecimalPlaces().intValue() == 0 )
                            retVal += CommonUtils.formatDecimalPlaces( 
                          subBus.getEstimatedVarLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() );           
                    else
                            retVal += CommonUtils.formatDecimalPlaces( 
                                subBus.getEstimatedVarLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() );
                }
                
                return retVal;
            }
          
            case SUB_POWER_FACTOR_COLUMN:
            {
                return getPowerFactorText( subBus.getPowerFactorValue().doubleValue(), true )
                    + " / " +
                    getPowerFactorText( subBus.getEstimatedPFValue().doubleValue(), true );
            }

            case SUB_WATTS_COLUMN:
            {
                if( subBus.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
                    return DASH_LINE;
                 else {
                    if( subBus.getDecimalPlaces().intValue() == 0 )
                            return new Integer( CommonUtils.formatDecimalPlaces( 
                           subBus.getCurrentWattLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() ) );             
                    else
                         return new Double( CommonUtils.formatDecimalPlaces( 
                              subBus.getCurrentWattLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() ) );
                 }
            }
            
            case SUB_TIME_STAMP_COLUMN:
            {
                if( subBus.getLastCurrentVarPointUpdateTime().getTime() <= 
                    com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    return DASH_LINE;
                else
                    return new ModifiedDate( subBus.getLastCurrentVarPointUpdateTime().getTime(), dateTimeFormat );
            }

            
            default:
                return null;
        }

        
    }
    
    
    
    /**
     * Discovers if the given SubBus is in any Pending state
     *
     */
    private String _getSubBusPendingState( SubBus subBus ) 
    {
        for( int i = 0; i < subBus.getCcFeeders().size(); i++ )
        {
            com.cannontech.yukon.cbc.Feeder feeder =
                (com.cannontech.yukon.cbc.Feeder)subBus.getCcFeeders().get(i);

            int size = feeder.getCcCapBanks().size();
            for( int j = 0; j < size; j++ )
            {
                CapBankDevice capBank = ((CapBankDevice)feeder.getCcCapBanks().elementAt(j));
                
                if( capBank.getControlStatus().intValue() == CapControlConst.BANK_CLOSE_PENDING )
                    return getCBCStateNames()[CapControlConst.BANK_CLOSE_PENDING].getStateText();
                    
                if( capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING )
                    return getCBCStateNames()[CapControlConst.BANK_OPEN_PENDING].getStateText();
            }

        }

        // we are not pending
        return null;
    }

    /**
     * Discovers if the given Feeder is in any Pending state
     *
     */
    private String _getFeederPendingState( Feeder feeder )
    {
        int size = feeder.getCcCapBanks().size();
        for( int j = 0; j < size; j++ )
        {
            CapBankDevice capBank = ((CapBankDevice)feeder.getCcCapBanks().elementAt(j));
            
            if( capBank.getControlStatus().intValue() == CapControlConst.BANK_CLOSE_PENDING )
                return getCBCStateNames()[CapControlConst.BANK_CLOSE_PENDING].getStateText();
                
            if( capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING )
                return getCBCStateNames()[CapControlConst.BANK_OPEN_PENDING].getStateText();
        }

        // we are not pending
        return null;
    }

    /**
     * getValueAt method for Feeders
     * 
     */
    public synchronized Object getFeederValueAt( Feeder feeder, int col ) 
    {
        if( feeder == null )
            return "";

        switch( col )
        {
            case FDR_NAME_COLUMN:
            {
                return feeder.getCcName();
            }

            case FDR_CURRENT_STATE_COLUMN:
            {
                String state = null;
                
                if( feeder.getCcDisableFlag().booleanValue() )
                {
                   state = "DISABLED";
                }
                else if( feeder.getRecentlyControlledFlag().booleanValue() )
                {
                    state = _getFeederPendingState( feeder );
                    
                    if( state == null )
                    {
                        state = "PENDING";  //we dont know what Pending state its in
                    }
                    
                }
                else
                    state = "ENABLED";
                    
                //show waived with a W at the end of the state
                if( feeder.getWaiveControlFlag().booleanValue() )
                    state += "-W";

                return state;
            }

            case FDR_TARGET_COLUMN:
            {
                // decide which set Point we are to use
                if( CBCUtils.isPowerFactorControlled(feeder.getSubControlUnits()) )
                {
                    return getPowerFactorText(feeder.getPeakSetPoint().doubleValue(), false) + " Pk";
                }
                else if( feeder.getLowerBandWidth().doubleValue() == 0
							&& feeder.getUpperBandWidth().doubleValue() == 0 )
                {
                    return STR_NA;
                }

                if( feeder.getSubPeakTimeFlag().booleanValue() )
                    return
						CommonUtils.formatDecimalPlaces(feeder.getPeakSetPoint().doubleValue() - feeder.getLowerBandWidth().doubleValue(), 0) +
						" to " + 
						CommonUtils.formatDecimalPlaces(feeder.getUpperBandWidth().doubleValue() + feeder.getPeakSetPoint().doubleValue(), 0) + 
						" Pk";
                else
                    return
                        CommonUtils.formatDecimalPlaces(feeder.getOffPeakSetPoint().doubleValue() - feeder.getLowerBandWidth().doubleValue(), 0) +
                        " to " + 
                        CommonUtils.formatDecimalPlaces(feeder.getUpperBandWidth().doubleValue() + feeder.getOffPeakSetPoint().doubleValue(), 0) + 
                        " OffPk";
            }

            case FDR_POWER_FACTOR_COLUMN:
            {
                return getPowerFactorText( feeder.getPowerFactorValue().doubleValue(), true )
                        + " / " +
                        getPowerFactorText( feeder.getEstimatedPFValue().doubleValue(), true );
            }
         
            case FDR_DAILY_OPERATIONS_COLUMN:
            {
                return feeder.getCurrentDailyOperations();                
            }
                
            case FDR_VAR_LOAD_COLUMN:
            {
                String retVal = DASH_LINE; //default just in case
                
                if( feeder.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
					retVal = DASH_LINE;
                else
					retVal = CommonUtils.formatDecimalPlaces( 
						        feeder.getCurrentVarLoadPointValue().doubleValue(), feeder.getSubDecimalPlaces() );

                retVal += " / ";
                if( feeder.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
					retVal += DASH_LINE;
                else
					retVal += CommonUtils.formatDecimalPlaces( 
								feeder.getEstimatedVarLoadPointValue().doubleValue(), feeder.getSubDecimalPlaces() );

                return retVal;
            }

            case FDR_WATTS_COLUMN:
            {
                if( feeder.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
                    return DASH_LINE;
                  else {
                    if( feeder.getSubDecimalPlaces() == 0 )
                        return new Integer( CommonUtils.formatDecimalPlaces( 
                                feeder.getCurrentWattLoadPointValue().doubleValue(), feeder.getSubDecimalPlaces() ) );           
                    else
                        return new Double( CommonUtils.formatDecimalPlaces( 
                                feeder.getCurrentWattLoadPointValue().doubleValue(), feeder.getSubDecimalPlaces() ) );
                  }
            }

            case FDR_TIME_STAMP_COLUMN:
            {
                if( feeder.getLastCurrentVarPointUpdateTime().getTime() <= 
                        com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    return DASH_LINE;
                else
                    return new ModifiedDate( 
                            feeder.getLastCurrentVarPointUpdateTime().getTime(), dateTimeFormat );
            }

            default:
                return null;
        }
    }


    /**
     * Gets the powerfactor as a percent
     * @param value
     * @param compute
     * @return
     */
    public static String getPowerFactorText( double value, boolean compute )
    {   
       int decPlaces = 1;
       try
       {
          decPlaces = 
             Integer.parseInt(
                    ClientSession.getInstance().getRolePropertyValue(
					CBCSettingsRole.PFACTOR_DECIMAL_PLACES, 
                   "1") );
       }
       catch( Exception e)
       {}
        
       if( value <= CapControlConst.PF_INVALID_VALUE )
          return STR_NA;
       else
          return CommonUtils.formatDecimalPlaces(
                value * (compute ? 100 : 1), decPlaces ) + "%"; //get percent   
    }

	public static synchronized String getHTMLFgColor( SubBus subBus ) 
	{
		Color rc = getSubFgColor( subBus, Color.BLACK );				
		return ColorUtil.getHex(
			new int[] {rc.getRed(), rc.getGreen(), rc.getBlue()} );
	}

	public static synchronized String getHTMLFgColor( Feeder feeder ) 
	{
		Color rc = getFeederFgColor( feeder, Color.BLACK );				
		return ColorUtil.getHex(
			new int[] {rc.getRed(), rc.getGreen(), rc.getBlue()} );
	}

	public static synchronized String getHTMLFgColor( CapBankDevice capBank ) 
	{
		Color rc = getCapBankFGColor( capBank, Color.BLACK );				
		return ColorUtil.getHex(
			new int[] {rc.getRed(), rc.getGreen(), rc.getBlue()} );
	}

	public static synchronized Color getSubFgColor( SubBus subBus, Color defColor ) 
	{
		Color retColor = defColor;
				
		if( subBus != null )
		{
			if( subBus.getCcDisableFlag().booleanValue() )
			{
				retColor = _DEFAULT_COLORS[1]; //disabled color
			}
			else if( subBus.getRecentlyControlledFlag().booleanValue() )
			{
				retColor = _DEFAULT_COLORS[2]; //pending color
			}
			else
			{
				retColor = _DEFAULT_COLORS[0];
			}
		}
		
		return retColor;
	}

	public static synchronized Color getCapBankFGColor( CapBankDevice capBank, Color defColor ) 
	{
		Color retColor = defColor;
		int status = capBank.getControlStatus().intValue();

		synchronized( CBCDisplay.getCBCStateNames() )
		{
			if( status >= 0 && status < CBCDisplay.getCBCStateNames().length )
				retColor = Colors.getColor( CBCDisplay.getCBCStateNames()[status].getFgColor() );
		}

		return retColor;
	}



	public static synchronized Color getFeederFgColor( Feeder feeder, Color defColor ) 
	{
		Color retColor = defColor;
				
		if( feeder != null )
		{
			if( feeder.getCcDisableFlag().booleanValue() )
			{
				retColor = _DEFAULT_COLORS[1]; //disabled color
			}
			else if( feeder.getRecentlyControlledFlag().booleanValue() )
			{
				retColor = _DEFAULT_COLORS[2]; //pending color
			}
			else
			{
				retColor = _DEFAULT_COLORS[0];
			}
		}
		
		return retColor;
	}


}