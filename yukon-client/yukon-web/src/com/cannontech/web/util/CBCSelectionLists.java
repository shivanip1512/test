package com.cannontech.web.util;

import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import com.cannontech.common.util.StringUtils;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;

/**
 * A set of selection list used for the GUI
 * 
 * @author ryan
 */
public class CBCSelectionLists {

	private static final SelectItem[] wizardCBCTypes =  {
		//value, label
		new SelectItem(new Integer(PAOGroups.CBC_EXPRESSCOM), PAOGroups.getPAOTypeString(PAOGroups.CBC_EXPRESSCOM) ),
		new SelectItem(new Integer(PAOGroups.CAPBANKCONTROLLER), PAOGroups.getPAOTypeString(PAOGroups.CAPBANKCONTROLLER) ),
		new SelectItem(new Integer(PAOGroups.CBC_7010), PAOGroups.getPAOTypeString(PAOGroups.CBC_7010) ),
		new SelectItem(new Integer(PAOGroups.CBC_7020), PAOGroups.getPAOTypeString(PAOGroups.CBC_7020) ),
		new SelectItem(new Integer(PAOGroups.CBC_FP_2800), PAOGroups.getPAOTypeString(PAOGroups.CBC_FP_2800) ),
	};

	private static final SelectItem[] wizardCBCPointTypes =  {
		//value, label
		new SelectItem(new Integer(PointTypes.ANALOG_POINT), PointTypes.getType(PointTypes.ANALOG_POINT) ),
		new SelectItem(new Integer(PointTypes.STATUS_POINT), PointTypes.getType(PointTypes.STATUS_POINT) )
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
		new SelectItem(CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION, CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION),
		new SelectItem(CalcComponentTypes.LABEL_VOLTS, CalcComponentTypes.LABEL_VOLTS)
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
		new SelectItem(new Integer(300), "5 minutes"),
		new SelectItem(new Integer(600), "10 minutes"),
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

		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
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
	 * Returns all possible Routes
	 */
	public SelectItem[] getRoutes() {

		SelectItem[] selItems = new SelectItem[0];

		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
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
	 * @return
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
	public SelectItem[] getCbcControlMethods() {
		return cbcControlMethods;
	}

	/**
	 * @return
	 */
	public SelectItem[] getCbcControlAlgorithim() {
		return cbcControlAlgorithim;
	}

}