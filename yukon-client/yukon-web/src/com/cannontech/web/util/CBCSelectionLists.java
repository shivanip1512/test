package com.cannontech.web.util;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import javax.faces.model.SelectItem;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.yukon.IDatabaseCache;

/**
 * A set of selection list used for many GUI's
 * 
 * @author ryan
 */
public class CBCSelectionLists {
	
	/* constants for indexing the tabbed pane
     * 
	 */
    public static final int CapBankControllerSetup = 7;
    public static final int General = 0;
    public static final int CapBankSetup = 6;
	public static final int CapControlSubBusSetup = 1;
	public static final int CapControlFeederCapBank = 5;
    public static final int CapControlStrategyEditor = 14;
    
    /* MyFaces 1.10 does not seem to show the correct time with h:outPutText, */
	/* so we only show date for now within our pages */
	private static final String dateOnly = "MM-dd-yyyy";
	private static final String dateTime = "MM-dd-yyyy HH:mm:ss";
	private static final String dateTimeNoSeconds = "MM-dd-yyyy HH:mm";

    private static final SelectItem[] pTypes = {
      new SelectItem(new Integer (PointTypes.ANALOG_POINT), "Analog"),
      new SelectItem(new Integer (PointTypes.STATUS_POINT), "Status"),
      new SelectItem(new Integer (PointTypes.DEMAND_ACCUMULATOR_POINT), "Accumulator"),
      new SelectItem(new Integer (PointTypes.CALCULATED_POINT), "Calculated"),        
    };
    
    private static final SelectItem[] pSubtypes = {
        
        new SelectItem(new Integer (PointTypes.CALCULATED_POINT), "Analog Output"),
        new SelectItem(new Integer (PointTypes.CALCULATED_STATUS_POINT), "Status Output"),
        };

	
    private static final SelectItem[] typeList701X = {
		new SelectItem(new Integer(PAOGroups.CBC_EXPRESSCOM), PAOGroups.getPAOTypeString(PAOGroups.CBC_EXPRESSCOM) ),
		new SelectItem(new Integer(PAOGroups.CAPBANKCONTROLLER), PAOGroups.getPAOTypeString(PAOGroups.CAPBANKCONTROLLER) ),
		new SelectItem(new Integer(PAOGroups.CBC_7010), PAOGroups.getPAOTypeString(PAOGroups.CBC_7010) ),
        new SelectItem(new Integer(PAOGroups.CBC_7011), PAOGroups.getPAOTypeString(PAOGroups.CBC_7011) ),
        new SelectItem(new Integer(PAOGroups.CBC_7012), PAOGroups.getPAOTypeString(PAOGroups.CBC_7012) )	
    };
    
    private static final SelectItem[] typeList702X = {
		new SelectItem(new Integer(PAOGroups.CBC_7020), PAOGroups.getPAOTypeString(PAOGroups.CBC_7020) ),
        new SelectItem(new Integer(PAOGroups.CBC_7022), PAOGroups.getPAOTypeString(PAOGroups.CBC_7022) ),
        new SelectItem(new Integer(PAOGroups.CBC_7023), PAOGroups.getPAOTypeString(PAOGroups.CBC_7023) ),
        new SelectItem(new Integer(PAOGroups.CBC_7024), PAOGroups.getPAOTypeString(PAOGroups.CBC_7024) )
    };
    
	private static final SelectItem[] wizardCBCTypes =  {
		//value, label
		new SelectItem(new Integer(PAOGroups.CBC_EXPRESSCOM), PAOGroups.getPAOTypeString(PAOGroups.CBC_EXPRESSCOM) ),
		new SelectItem(new Integer(PAOGroups.CAPBANKCONTROLLER), PAOGroups.getPAOTypeString(PAOGroups.CAPBANKCONTROLLER) ),
		new SelectItem(new Integer(PAOGroups.CBC_7010), PAOGroups.getPAOTypeString(PAOGroups.CBC_7010) ),
        new SelectItem(new Integer(PAOGroups.CBC_7011), PAOGroups.getPAOTypeString(PAOGroups.CBC_7011) ),
        new SelectItem(new Integer(PAOGroups.CBC_7012), PAOGroups.getPAOTypeString(PAOGroups.CBC_7012) ),        
		new SelectItem(new Integer(PAOGroups.CBC_7020), PAOGroups.getPAOTypeString(PAOGroups.CBC_7020) ),
        new SelectItem(new Integer(PAOGroups.CBC_7022), PAOGroups.getPAOTypeString(PAOGroups.CBC_7022) ),
        new SelectItem(new Integer(PAOGroups.CBC_7023), PAOGroups.getPAOTypeString(PAOGroups.CBC_7023) ),
        new SelectItem(new Integer(PAOGroups.CBC_7024), PAOGroups.getPAOTypeString(PAOGroups.CBC_7024) ),
        
		new SelectItem(new Integer(PAOGroups.CBC_FP_2800), PAOGroups.getPAOTypeString(PAOGroups.CBC_FP_2800) ),
        
	};

	private static final SelectItem[] wizardCBCPointTypes =  {
		//value, label
		new SelectItem(new Integer(PointTypes.ANALOG_POINT), PointTypes.getType(PointTypes.ANALOG_POINT) ),
		new SelectItem(new Integer(PointTypes.STATUS_POINT), PointTypes.getType(PointTypes.STATUS_POINT) )
	};

	private static final SelectItem[] daySelection =  {
		//value, label
		new SelectItem("0", "Sunday" ),
		new SelectItem("1", "Monday" ),
		new SelectItem("2", "Tuesday" ),
		new SelectItem("3", "Wednesday" ),
		new SelectItem("4", "Thursday" ),
		new SelectItem("5", "Friday" ),
		new SelectItem("6", "Saturday" )
	};

	private static final SelectItem[] scheduleCmds =  {
		//value, label
		new SelectItem("Verify ALL CapBanks", "Verify ALL CapBanks" ),
		new SelectItem("Verify Failed CapBanks", "Verify Failed CapBanks" ),
		new SelectItem("Verify Failed and Questionable CapBanks", "Verify Failed and Questionable CapBanks" ),
		new SelectItem("Verify Standalone CapBanks", "Verify Standalone CapBanks" ),
		new SelectItem("Verify Questionable CapBanks", "Verify Questionable CapBanks" ),
        new SelectItem("Confirm Sub", "Confirm Sub" )
	};


	private static final SelectItem[] cbcControlMethods =  {
		//value, label
		new SelectItem(CapControlStrategy.CNTRL_INDIVIDUAL_FEEDER,
				StringUtils.addCharBetweenWords( ' ', CapControlStrategy.CNTRL_INDIVIDUAL_FEEDER)),
		new SelectItem(CapControlStrategy.CNTRL_BUSOPTIMIZED_FEEDER,
				StringUtils.addCharBetweenWords( ' ', CapControlStrategy.CNTRL_BUSOPTIMIZED_FEEDER)),		
		new SelectItem(CapControlStrategy.CNTRL_MANUAL_ONLY,
				StringUtils.addCharBetweenWords( ' ', CapControlStrategy.CNTRL_MANUAL_ONLY)),		
		new SelectItem(CapControlStrategy.CNTRL_SUBSTATION_BUS,
				StringUtils.addCharBetweenWords( ' ', CapControlStrategy.CNTRL_SUBSTATION_BUS))
	};


	private static final SelectItem[] cbcControlAlgorithim =  {
		new SelectItem(CalcComponentTypes.LABEL_KVAR, CalcComponentTypes.LABEL_KVAR),
		new SelectItem(CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION, CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION),
		//removed because no points to attach yet
		//new SelectItem(CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION, CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION),
		new SelectItem(CalcComponentTypes.LABEL_MULTI_VOLT, CalcComponentTypes.LABEL_MULTI_VOLT),
		
		new SelectItem(CalcComponentTypes.LABEL_VOLTS, CalcComponentTypes.LABEL_VOLTS),
        
        new SelectItem(CalcComponentTypes.LABEL_MULTI_VOLT_VAR, CalcComponentTypes.LABEL_MULTI_VOLT_VAR),

    };

	private static final SelectItem[] capBankOpStates =  {
		new SelectItem(CapBank.FIXED_OPSTATE, CapBank.FIXED_OPSTATE),
		new SelectItem(CapBank.STANDALONE_OPSTATE, CapBank.STANDALONE_OPSTATE),
		new SelectItem(CapBank.SWITCHED_OPSTATE, CapBank.SWITCHED_OPSTATE),
		new SelectItem(CapBank.UNINSTALLED_OPSTATE, CapBank.UNINSTALLED_OPSTATE)
	};

	private static final SelectItem[] capBankSizes =  {
		new SelectItem(new Integer(50), "50 kVar"),
		new SelectItem(new Integer(100), "100 kVar"),
		new SelectItem(new Integer(150), "120 kVar"),
		new SelectItem(new Integer(275), "275 kVar"),
		new SelectItem(new Integer(300), "300 kVar"),
		new SelectItem(new Integer(450), "450 kVar"),
		new SelectItem(new Integer(550), "550 kVar"),
		new SelectItem(new Integer(600), "600 kVar"),
		new SelectItem(new Integer(825), "825 kVar"),
		new SelectItem(new Integer(900), "900 kVar"),
		new SelectItem(new Integer(1100), "1100 kVar"),
		new SelectItem(new Integer(1200), "1200 kVar")
	};


	//generic time list in seconds for a many fields
	public static final SelectItem[] TIME_INTERVAL = {
		//value, label
		new SelectItem(new Integer(1), "1 seconds"),
		new SelectItem(new Integer(2), "2 seconds"),
		new SelectItem(new Integer(5), "5 seconds"),
		new SelectItem(new Integer(10), "10 seconds"),
		new SelectItem(new Integer(15), "15 seconds"),
		new SelectItem(new Integer(30), "30 seconds"),
		new SelectItem(new Integer(60), "1 minute"),
		new SelectItem(new Integer(120), "2 minutes"),
		new SelectItem(new Integer(180), "3 minutes"),
        new SelectItem(new Integer(240), "4 minutes"),
		new SelectItem(new Integer(300), "5 minutes"),
        new SelectItem(new Integer(420), "7 minutes"),
		new SelectItem(new Integer(600), "10 minutes"),
        new SelectItem(new Integer(720), "12 minutes"),
		new SelectItem(new Integer(900), "15 minutes"),
		new SelectItem(new Integer(1200), "20 minutes"),
		new SelectItem(new Integer(1500), "25 minutes"),		
		new SelectItem(new Integer(1800), "30 minutes"),
		new SelectItem(new Integer(3600), "1 hour"),
		new SelectItem(new Integer(7200), "2 hours"),
		new SelectItem(new Integer(21600), "6 hours"),
		new SelectItem(new Integer(43200), "12 hours"),
		new SelectItem(new Integer(86400), "1 day")
	};

	private static final SelectItem[] ptArchiveType =  {
		new SelectItem(PointTypes.ARCHIVE_NONE, PointTypes.ARCHIVE_NONE),
		new SelectItem(PointTypes.ARCHIVE_ON_CHANGE, PointTypes.ARCHIVE_ON_CHANGE),
		new SelectItem(PointTypes.ARCHIVE_ON_TIMER, PointTypes.ARCHIVE_ON_TIMER),
		new SelectItem(PointTypes.ARCHIVE_ON_UPDATE, PointTypes.ARCHIVE_ON_UPDATE),
		new SelectItem(PointTypes.ARCHIVE_ON_TIMER_OR_UPDATE, "On Timer Or Update")
	};

	private static final SelectItem[] ptUpdateType =  {
		new SelectItem(PointTypes.UPDATE_ALL_CHANGE, PointTypes.UPDATE_ALL_CHANGE),
		new SelectItem(PointTypes.UPDATE_FIRST_CHANGE, PointTypes.UPDATE_FIRST_CHANGE),
		new SelectItem(PointTypes.UPDATE_HISTORICAL, PointTypes.UPDATE_HISTORICAL),
		new SelectItem(PointTypes.UPDATE_TIMER, PointTypes.UPDATE_TIMER),
		new SelectItem(PointTypes.UPDATE_TIMER_CHANGE, PointTypes.UPDATE_TIMER_CHANGE)
	};

	private static final SelectItem[] ptAlarmNotification =  {
		new SelectItem(PointAlarming.NONE_VALUE_STRING, PointAlarming.NONE_VALUE_STRING),
		new SelectItem(PointAlarming.EXCLUDE_NOTIFY_VALUE_STRING, PointAlarming.EXCLUDE_NOTIFY_VALUE_STRING),
		new SelectItem(PointAlarming.AUTO_ACK_VALUE_STRING, PointAlarming.AUTO_ACK_VALUE_STRING),
		new SelectItem(PointAlarming.BOTH_OPTIONS_VALUE_STRING, PointAlarming.BOTH_OPTIONS_VALUE_STRING)
	};

	private static final SelectItem[] ptControlTypes =  {
		//value, label
		new SelectItem( PointTypes.getType(PointTypes.CONTROLTYPE_NONE), PointTypes.getType(PointTypes.CONTROLTYPE_NONE) ),
		new SelectItem( PointTypes.getType(PointTypes.CONTROLTYPE_LATCH), PointTypes.getType(PointTypes.CONTROLTYPE_LATCH) ),
		new SelectItem( PointTypes.getType(PointTypes.CONTROLTYPE_NORMAL), PointTypes.getType(PointTypes.CONTROLTYPE_NORMAL) ),
		new SelectItem( PointTypes.getType(PointTypes.CONTROLTYPE_PSEUDO), PointTypes.getType(PointTypes.CONTROLTYPE_PSEUDO) ),
		new SelectItem( PointTypes.getType(PointTypes.CONTROLTYPE_SBO_LATCH), PointTypes.getType(PointTypes.CONTROLTYPE_SBO_LATCH) ),
		new SelectItem( PointTypes.getType(PointTypes.CONTROLTYPE_SBO_PULSE), PointTypes.getType(PointTypes.CONTROLTYPE_SBO_PULSE) )
	};
    



	/**
	 * 
	 */
	public CBCSelectionLists() {
		super();
	}

	/**
	 * Returns all possible Comm Channels
	 */
	public SelectItem[] getCommChannels() {

		SelectItem[] selItems = new SelectItem[0];

		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) {
			List ports = cache.getAllPorts();
			Collections.sort( ports, LiteComparators.liteStringComparator );

			selItems = new SelectItem[ports.size()];
			for( int i = 0; i < ports.size(); i++ ) {
				LiteYukonPAObject litePort = (LiteYukonPAObject)ports.get(i);
				selItems[i] = new SelectItem(new Integer(litePort.getYukonID()), litePort.getPaoName() );
			}

		}
		
		return selItems;
	}

	/**
	 * Returns the valid start of time for Yukon
	 * @return
	 */
	public long getStartOfTime() {

		return CtiUtilities.get1990GregCalendar().getTime().getTime();
	}

	/**
	 * Returns all possible Routes
	 */
	public SelectItem[] getRoutes() {

		SelectItem[] selItems = new SelectItem[0];

		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) {
			List routes = cache.getAllRoutes();
			Collections.sort( routes, LiteComparators.liteStringComparator );

			selItems = new SelectItem[routes.size()];
			for( int i = 0; i < routes.size(); i++ ) {
				LiteYukonPAObject liteRoute = (LiteYukonPAObject)routes.get(i);
				selItems[i] = new SelectItem(new Integer(liteRoute.getYukonID()), liteRoute.getPaoName() );
			}
		}
		
		return selItems;
	}


    
    
    /**
	 * Returns possible selection choices for GUI
	 */
	public SelectItem[] getCBCTypes() {
		return wizardCBCTypes;
	}

	/**
	 * Returns possible selection choices for GUI
	 */
	public SelectItem[] getCBCPointTypes() {
		return wizardCBCPointTypes;
	}

	/**
	 * @return SelectItem[]
	 */
	public SelectItem[] getCapBankOpStates() {
		return capBankOpStates;
	}

	/**
	 * @return
	 */
	public SelectItem[] getCapBankSizes() {
		return capBankSizes;
	}

	/**
	 * @return
	 */
	public SelectItem[] getDaySelections() {
		return daySelection;
	}

	/**
	 * @return
	 */
	public SelectItem[] getCbcControlMethods() {
		return cbcControlMethods;
	}

	/**
	 * @return
	 */
	public SelectItem[] getCbcControlAlgorithim() {
		return cbcControlAlgorithim;
	}

	/**
	 * @return
	 */
	public SelectItem[] getScheduleCmds() {
		return scheduleCmds;
	}
	
	public String getDateTime() {
		 return dateTime;
	}

	public String getDateTimeNoSeconds() {
		 return dateTimeNoSeconds;
	}

	public String getDateOnly() {
		 return dateOnly;
	}

	/**
	 * Returns a sublist of Time Interval SelectItem[]
	 */
	public static SelectItem[] getTimeSubList( int startSecs, int endSecs ) {

		if( startSecs >= endSecs )
			return CBCSelectionLists.TIME_INTERVAL;

		int startIndx = 0, endIndx = CBCSelectionLists.TIME_INTERVAL.length;

		for( int i = 0; i < CBCSelectionLists.TIME_INTERVAL.length; i++ ) {
			
			int secsVal = ((Integer)CBCSelectionLists.TIME_INTERVAL[i].getValue()).intValue();

			if( secsVal >= startSecs && startIndx <= 0 )
				startIndx = i;

			if( secsVal >= endSecs ) {
				endIndx = i;
				break;
			}
		}
		
		SelectItem[] items = new SelectItem[endIndx - startIndx];
		System.arraycopy( CBCSelectionLists.TIME_INTERVAL, startIndx, items, 0, items.length );
		return items;
	}

	/**
	 * Returns a sublist of Time Interval SelectItem[].
	 * Starts at the given startSecs value and returns the entire upper list
	 */
	public static SelectItem[] getTimeSubList( int startSecs ) {
		return getTimeSubList( startSecs, Integer.MAX_VALUE );
	}

	/**
	 * @return
	 */
	public SelectItem[] getPtArchiveType() {
		return ptArchiveType;
	}

	/**
	 * @return
	 */
	public SelectItem[] getPtUpdateType() {
		return ptUpdateType;
	}

	/**
	 * @return
	 */
	public SelectItem[] getPtAlarmNotification() {
		return ptAlarmNotification;
	}

	/**
	 * @return
	 */
	public SelectItem[] getPtControlTypes() {
		return ptControlTypes;
	}

    public SelectItem[] getPointTypes() {
        return pTypes;
    }

    public SelectItem[] getPointSubtypes() {
        return pSubtypes;
    }

	public  SelectItem[] getTypeList701X() {
		return typeList701X;
	}

	public  SelectItem[] getTypeList702X() {
		return typeList702X;
	}
	
	public SelectItem[] getTimeInterval() {

		return TIME_INTERVAL;
	}
    
    public SelectItem[] getIntegrationPeriods () {
        return getIntegrationPeriods;
    }
    
    private static final SelectItem[] getIntegrationPeriods =  {
        new SelectItem(new Integer (60), "1 minute"),
        new SelectItem(new Integer (120), "2 minutes"),
        new SelectItem(new Integer (180), "3 minutes"),
        new SelectItem(new Integer (240), "4 minutes"),
        new SelectItem(new Integer (300), "5 minutes"),
        new SelectItem(new Integer (360), "6 minutes"),
        new SelectItem(new Integer (420), "7 minutes"),
        new SelectItem(new Integer (480), "8 minutes"),
        new SelectItem(new Integer (540), "9 minutes"),
        new SelectItem(new Integer (600), "10 minutes"),
        new SelectItem(new Integer (660), "11 minutes"),
        new SelectItem(new Integer (720), "12 minutes"),
        new SelectItem(new Integer (780), "13 minutes"),
        new SelectItem(new Integer (840), "14 minutes"),
        new SelectItem(new Integer (900), "15 minutes")
        
    };
 
    public Integer getSubstationType () {
        return PAOGroups.CAP_CONTROL_SUBSTATION;
    }
    public Integer getSubstationBusType () {
        return PAOGroups.CAP_CONTROL_SUBBUS;
    }
    
    public Integer getFeederType () {
        return PAOGroups.CAP_CONTROL_FEEDER;
    }
    
    public Integer getCapType () {
        return DeviceTypes.CAPBANK;
    }
    
    public Integer getStrategyEditorType () {
        return DBEditorTypes.EDITOR_STRATEGY;
    }
    
    public SelectItem[] getCapBankConfigs (){
        return new SelectItem[]{
                new SelectItem ("none"),
                new SelectItem ("Wye"),
                new SelectItem("Delta"),
                new SelectItem("Serial")
        };
    }
    
    public SelectItem[] getCapBankCommMedium() {
        return new SelectItem[] {
                new SelectItem ("none"),
                new SelectItem ("Paging"),
                new SelectItem("DLC"),
                new SelectItem("VHF"),
                new SelectItem("1XRTT"),
                new SelectItem("GPRS"),
                new SelectItem("SSRadio")
        };
    }
    
    public SelectItem[] getCapBankAntennaType () {
       return new SelectItem[] {
               new SelectItem ("none"),
               new SelectItem ("Yagi"),
               new SelectItem("Omni")
       }; 
    }
    
    public SelectItem[] getPotentialTransformer () {
        return new SelectItem[] {
                new SelectItem ("none"),
                new SelectItem ("Dedicated"),
                new SelectItem("Shared")
        }; 
    }
    
    public SelectItem[] getAddCapBankSizes() {
        return new SelectItem[] {
                new SelectItem (new Integer (300), "300"),
                new SelectItem (new Integer (600), "600"),
                new SelectItem (new Integer (900), "900"),
                new SelectItem (new Integer (1200), "1200"),
                new SelectItem (new Integer (1800), "1800"),
                new SelectItem (new Integer (2400), "2400"),
        }; 
    }
    
    public TimeZone getTimeZone () {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone;
    }
}