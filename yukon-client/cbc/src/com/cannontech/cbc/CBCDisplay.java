package com.cannontech.cbc;

import com.cannontech.cbc.gui.CapBankTableModel;
import com.cannontech.cbc.gui.FeederTableModel;
import com.cannontech.cbc.gui.SubBusTableModel;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlConst;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

/**
 * @author rneuharth
 *
 */
public class CBCDisplay
{
    public static final String STR_NA = "  NA";
    public static final String DASH_LINE = "  ----";


    /**
     * getValueAt method for CapBanks.
     * 
     */
    public Object getCapBankValueAt( CapBankDevice capBank, int col) 
    {
        if( capBank == null )
            return "";

        switch( col )
        {
            case CapBankTableModel.CB_NAME_COLUMN :
            {
                return capBank.getCcName() + " (" + capBank.getControlOrder() + ")";
            }

            case CapBankTableModel.BANK_ADDRESS_COLUMN :
            {
                return capBank.getCcArea();
            }
    
            case CapBankTableModel.STATUS_COLUMN:
            {
                if( capBank.getControlStatus().intValue() < 0 ||
                        capBank.getControlStatus().intValue() >= CapBankTableModel.getStateNames().length )
                {
                    com.cannontech.clientutils.CTILogger.info("*** A CapBank state was found that has no corresponding status.");
                    return CapBankTableModel.UNKNOWN_STATE + " (" + capBank.getControlStatus().intValue() +")" ;
                }
                else
                {
                    if( capBank.getCcDisableFlag().booleanValue() == true )
                        return "DISABLED : " + (capBank.getOperationalState().equalsIgnoreCase(com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE) 
                                                        ? com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE 
                                                        : CapBankTableModel.getStateNames()[capBank.getControlStatus().intValue()]);
                    else
                        return (capBank.getOperationalState().equalsIgnoreCase(com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE) 
                                    ? com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE + " : " 
                                    : "") + CapBankTableModel.getStateNames()[capBank.getControlStatus().intValue()];
                }
            }
                    
            case CapBankTableModel.OP_COUNT_COLUMN :
            {
                return capBank.getCurrentDailyOperations();
            }
                
            case CapBankTableModel.BANK_SIZE_COLUMN :
            {
                return capBank.getBankSize();
            }

            case CapBankTableModel.TIME_STAMP_COLUMN:
            {
                if( capBank.getLastStatusChangeTime().getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    return "  ----";
                else
                    return new ModifiedDate(
                            capBank.getLastStatusChangeTime().getTime(), ModifiedDate.FRMT_NOSECS );    
            }

            default:
                return null;
        }               
    }

    /**
     * getValueAt method for SubBuses
     * 
     */
    public Object getSubBusValueAt(SubBus subBus, int col) 
    {
        if( subBus == null )
            return "";

        switch( col )
        {
            case SubBusTableModel.SUB_NAME_COLUMN:
            {
                return subBus.getCcName();
            }

            case SubBusTableModel.AREA_NAME_COLUMN:
            {
                return subBus.getCcArea();
            }

            case SubBusTableModel.CURRENT_STATE_COLUMN:
            {
                String state = null;
                
                if( subBus.getCcDisableFlag().booleanValue() )
                {
                    state = "DISABLED";
                }
                else if( subBus.getRecentlyControlledFlag().booleanValue() )
                {
                    state = getSubBusPendingState( subBus );
                    
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

            case SubBusTableModel.TARGET_COLUMN:
            {
                // decide which set Point we are to use
                if( subBus.isPowerFactorControlled() )
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
                
            case SubBusTableModel.DAILY_OPERATIONS_COLUMN:
            {
                return new String(subBus.getCurrentDailyOperations() + " / " + 
                        (subBus.getMaxDailyOperation().intValue() <= 0 
                            ? STR_NA 
                            : subBus.getMaxDailyOperation().toString()) );
            }

            case SubBusTableModel.VAR_LOAD_COLUMN:
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
          
            case SubBusTableModel.POWER_FACTOR_COLUMN:
            {
                return getPowerFactorText( subBus.getPowerFactorValue().doubleValue(), true )
                    + " / " +
                    getPowerFactorText( subBus.getEstimatedPFValue().doubleValue(), true );
            }

            case SubBusTableModel.WATTS_COLUMN:
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
            
            case SubBusTableModel.TIME_STAMP_COLUMN:
            {
                if( subBus.getLastCurrentVarPointUpdateTime().getTime() <= 
                    com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    return DASH_LINE;
                else
                    return new ModifiedDate( subBus.getLastCurrentVarPointUpdateTime().getTime(), ModifiedDate.FRMT_NOSECS );
            }

            
            default:
                return null;
        }

        
    }
    
    
    
    /**
     * Discovers if the given SubBus is in any Pending state
     *
     */
    private String getSubBusPendingState( SubBus subBus ) 
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
                    return CapBankTableModel.getStateNames()[CapControlConst.BANK_CLOSE_PENDING];
                    
                if( capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING )
                    return CapBankTableModel.getStateNames()[CapControlConst.BANK_OPEN_PENDING];
            }

        }

        // we are not pending
        return null;
    }

    /**
     * Discovers if the given Feeder is in any Pending state
     *
     */
    private String getFeederPendingState( Feeder feeder )
    {
        int size = feeder.getCcCapBanks().size();
        for( int j = 0; j < size; j++ )
        {
            CapBankDevice capBank = ((CapBankDevice)feeder.getCcCapBanks().elementAt(j));
            
            if( capBank.getControlStatus().intValue() == CapControlConst.BANK_CLOSE_PENDING )
                return CapBankTableModel.getStateNames()[CapControlConst.BANK_CLOSE_PENDING];
                
            if( capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING )
                return CapBankTableModel.getStateNames()[CapControlConst.BANK_OPEN_PENDING];
        }

        // we are not pending
        return null;
    }

    /**
     * getValueAt method for Feeders
     * 
     */
    public Object getFeederValueAt( Feeder feeder, int col, SubBus parentSub ) 
    {
        if( feeder == null || parentSub == null )
            return "";

        switch( col )
        {
//            case FeederTableModel.AREA_NAME_COLUMN:
//            {
//                return feeder.getCcArea();
//            }

            case FeederTableModel.NAME_COLUMN:
            {
                return feeder.getCcName();
            }

            case FeederTableModel.CURRENT_STATE_COLUMN:
            {
                String state = null;
                
                if( feeder.getCcDisableFlag().booleanValue() )
                {
                   state = "DISABLED";
                }
                else if( feeder.getRecentlyControlledFlag().booleanValue() )
                {
                    state = getFeederPendingState( feeder );
                    
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

            case FeederTableModel.TARGET_COLUMN:
            {
                // decide which set Point we are to use
                if( parentSub.isPowerFactorControlled() )
                {
                    return getPowerFactorText(feeder.getPeakSetPoint().doubleValue(), false) + " Pk";
                }
                else if( feeder.getLowerBandWidth().doubleValue() == 0
                             && feeder.getUpperBandWidth().doubleValue() == 0 )
                {
                    return STR_NA;
                }
                if( parentSub.getPeakTimeFlag().booleanValue() )
                {
                    return
                     CommonUtils.formatDecimalPlaces(feeder.getPeakSetPoint().doubleValue() - feeder.getLowerBandWidth().doubleValue(), 0) +
                     " to " + 
                     CommonUtils.formatDecimalPlaces(feeder.getUpperBandWidth().doubleValue() + feeder.getPeakSetPoint().doubleValue(), 0) + 
                     " Pk";
                }
                else
                {
                    return
                        CommonUtils.formatDecimalPlaces(feeder.getOffPeakSetPoint().doubleValue() - feeder.getLowerBandWidth().doubleValue(), 0) +
                        " to " + 
                        CommonUtils.formatDecimalPlaces(feeder.getUpperBandWidth().doubleValue() + feeder.getOffPeakSetPoint().doubleValue(), 0) + 
                        " OffPk";
                }
            }

            case FeederTableModel.POWER_FACTOR_COLUMN:
            {
                return getPowerFactorText( feeder.getPowerFactorValue().doubleValue(), true )
                        + " / " +
                        getPowerFactorText( feeder.getEstimatedPFValue().doubleValue(), true );
            }
         
            case FeederTableModel.DAILY_OPERATIONS_COLUMN:
            {
                return feeder.getCurrentDailyOperations();                
            }
                
            case FeederTableModel.VAR_LOAD_COLUMN:
            {
                String retVal = DASH_LINE; //default just in case
                
                if( feeder.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
                   retVal = DASH_LINE;
                else
                        retVal = com.cannontech.clientutils.CommonUtils.formatDecimalPlaces( 
                                feeder.getCurrentVarLoadPointValue().doubleValue(), parentSub.getDecimalPlaces().intValue() );

                    retVal += " / ";

                if( feeder.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
                        retVal += DASH_LINE;
                else
                        retVal += com.cannontech.clientutils.CommonUtils.formatDecimalPlaces( 
                                feeder.getEstimatedVarLoadPointValue().doubleValue(), parentSub.getDecimalPlaces().intValue() );
             
                return retVal;
            }

            case FeederTableModel.WATTS_COLUMN:
            {
                if( feeder.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
                    return DASH_LINE;
                  else {
                    if( parentSub.getDecimalPlaces().intValue() == 0 )
                        return new Integer( CommonUtils.formatDecimalPlaces( 
                                feeder.getCurrentWattLoadPointValue().doubleValue(), parentSub.getDecimalPlaces().intValue() ) );           
                    else
                        return new Double( CommonUtils.formatDecimalPlaces( 
                                feeder.getCurrentWattLoadPointValue().doubleValue(), parentSub.getDecimalPlaces().intValue() ) );
                  }
            }

            case FeederTableModel.TIME_STAMP_COLUMN:
            {
                if( feeder.getLastCurrentVarPointUpdateTime().getTime() <= 
                        com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    return DASH_LINE;
                else
                    return new ModifiedDate( 
                            feeder.getLastCurrentVarPointUpdateTime().getTime(), ModifiedDate.FRMT_NOSECS );
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
    private String getPowerFactorText( double value, boolean compute )
    {   
       int decPlaces = 1;
       try
       {
          decPlaces = 
             Integer.parseInt(
                    ClientSession.getInstance().getRolePropertyValue(
                   TDCRole.PFACTOR_DECIMAL_PLACES, 
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
}