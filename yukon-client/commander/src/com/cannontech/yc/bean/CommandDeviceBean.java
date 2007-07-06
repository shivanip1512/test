package com.cannontech.yc.bean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.lm.LMGroup;
import com.cannontech.database.db.device.lm.LMGroupExpressCom;
import com.cannontech.database.db.device.lm.LMGroupVersacom;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author yao
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class CommandDeviceBean implements DBChangeLiteListener
{
	private HashMap loadGroupIDToLiteLoadGroupsMap = null;
    private HashMap cbcIDToLiteCBCMap = null;
	private boolean clear = false;	//flag to clear all selection settings
	private boolean changed = false;
	private int userID = -1;//	admin userid    
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private int page = 1;
	private int pageSize = 25;

	public static final int ORDER_DIR_ASCENDING = 0;
	public static final int ORDER_DIR_DESCENDING = 1;

	private static final String NULL_OBJECT_STRING = "---";

	//device search by values
	public static final int DEVICE_NAME_SEARCH_BY = 0;
	public static final int ADDRESS_SEARCH_BY = 1;
	public static final int METER_NUM_SEARCH_BY = 2;
    
	//device order by values
	public static final int DEVICE_NAME_ORDER_BY = 0;
	public static final int DEVICE_TYPE_ORDER_BY = 1;
	public static final int ADDRESS_ORDER_BY = 2;
	public static final int METER_NUM_ORDER_BY = 3;
	public static final int ROUTE_ORDER_BY = 4;
	public static final int LMGROUP_ROUTE_ORDER_BY = 5;
	public static final int LMGROUP_SERIAL_ORDER_BY = 6;
	public static final int LMGROUP_CAPACITY_ORDER_BY = 7;
    public static final int CBC_SERIAL_ORDER_BY = 8;
 
	// Possible Filters 
	public static final int NO_FILTER = 0;
	public static final int ROUTE_FILTER = 1;
	//public static final int COLLECTION_GROUP_FILTER = 2;
	public static final int COMM_CHANNEL_FILTER = 3;
    public static final int CBC_TYPE_FILTER = 4;

	private static final String DEVICE_NAME_STRING = "Device Name";
	private static final String DEVICE_TYPE_STRING = "Device Type";
	private static final String ADDRESS_STRING = "Address";
	private static final String METER_NUMBER_STRING = "Meter Number";
	private static final String ROUTE_STRING = "Route";
	private static final String COMM_CHANNEL_STRING = "Comm Channel";

	private static final String LOAD_GROUP_STRING = "Load Group";
	private static final String LMGROUP_SERIAL_STRING = "Serial Number";
	private static final String LMGROUP_CAPACITY_STRING = "kW Capactiy";
	private static final String LMGROUP_ROUTE_STRING = "Group Route";
	
    private static final String CBC_SERIAL_STRING = "Serial Number";
    
	private static final String[] orderByStrings_Core = new String[] { 
			DEVICE_NAME_STRING, 
			DEVICE_TYPE_STRING, 
			ADDRESS_STRING,
			METER_NUMBER_STRING, 
			ROUTE_STRING };

	private static final String[] orderByStrings_Meter = new String[] { 
			DEVICE_NAME_STRING, 
			DEVICE_TYPE_STRING, 
			METER_NUMBER_STRING, 
			COMM_CHANNEL_STRING};

	private static final String[] orderByStrings_Transmitter = new String[] { 
			DEVICE_NAME_STRING, 
			DEVICE_TYPE_STRING, 
			COMM_CHANNEL_STRING};
    
	public static final String[] orderByStrings_LoadGroup = new String[] { 
			LOAD_GROUP_STRING,
			LMGROUP_ROUTE_STRING,
			LMGROUP_SERIAL_STRING,
			LMGROUP_CAPACITY_STRING};
	
	public static final String[] orderByStrings_CapControl = new String[] { 
			DEVICE_NAME_STRING, 
			DEVICE_TYPE_STRING,
            CBC_SERIAL_STRING};
 
	private static final String[] searchByStrings_Core = new String[] { 
			DEVICE_NAME_STRING, 
			ADDRESS_STRING,
			METER_NUMBER_STRING};
    
	private static final String[] searchByStrings_Meter = new String[] { 
			DEVICE_NAME_STRING, 
			METER_NUMBER_STRING};

	private static final String[] searchByStrings_SimpleDevice = new String[] { 
			DEVICE_NAME_STRING};

	public static final String[] searchByStrings_LoadGroup = new String[] { 
			LOAD_GROUP_STRING};

	//Create a local (DeviceClasses.x) value to represent all the core/custom classes.
	public static final int EXPRESSCOM_SORT_BY = -2;
	public static final int VERSACOM_SORT_BY = -3;
	public static final int DCU_SA205_SERIAL_SORT_BY = -4;
	public static final int DCU_SA305_SERIAL_SORT_BY = -5;	

	//Contains LiteYukonPaobjects
	private ArrayList deviceList = null;
	
	//stores the currently selected Filter By value
	public String filterValue = "";
	private int orderBy = DEVICE_NAME_ORDER_BY;
	private int orderDir = ORDER_DIR_ASCENDING;
	private int sortBy = DeviceClasses.CARRIER;
	private int filterBy = NO_FILTER;
	private int searchBy = DEVICE_NAME_SEARCH_BY;
	private String searchValue = "";
    
	//List of <route> values
	public List<LiteYukonPAObject> validRoutes = null;
	//List of <int(commChannels)> values
	public List<LiteYukonPAObject> validCommChannels = null;
	//List of <String, (collectionGroup)> values
	public ArrayList validCollGroups = null;
    //List of <int(CBC Types)> values
    public ArrayList validCBCTypes = null;
	
	/**
	 * Sort deviceTypeCommands by their displayOrder
	 */
	public static java.util.Comparator DEVICE_TYPE_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;

			if (o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				thisVal = PAOGroups.getPAOTypeString(((LiteYukonPAObject) o1).getType());
				anotherVal = PAOGroups.getPAOTypeString(((LiteYukonPAObject) o2).getType());

				if (thisVal.equalsIgnoreCase(anotherVal))
				{
					//if the types are equal, we need to sort by deviceName
					thisVal = ((LiteYukonPAObject) o1).getPaoName();
					anotherVal = ((LiteYukonPAObject) o2).getPaoName();
				}
			}

			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};

	/**
	 * Sort deviceTypeCommands by their displayOrder
	 */
	public static java.util.Comparator ADDRESS_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;

			if (o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				thisVal = String.valueOf(((LiteYukonPAObject) o1).getAddress());
				anotherVal = String.valueOf(((LiteYukonPAObject) o2).getAddress());
			}

			if (thisVal.equalsIgnoreCase(anotherVal))
			{
				//if the types are equal, we need to sort by deviceName
				thisVal = ((LiteYukonPAObject) o1).getPaoName();
				anotherVal = ((LiteYukonPAObject) o2).getPaoName();
			}

			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};

	/**
	 * Sort deviceTypeCommands by their displayOrder
	 */
    public java.util.Comparator CBC_SERIAL_COMPARATOR = new java.util.Comparator()
    {
        public int compare(Object o1, Object o2)
        {
            String thisVal = null, anotherVal = null;

            if (o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
            {
                YCLiteCBC lcbc1 = (YCLiteCBC)getCBCIDToLiteCBCMap().get(new Integer( ((LiteYukonPAObject) o1).getYukonID()));
                YCLiteCBC lcbc2 = (YCLiteCBC)getCBCIDToLiteCBCMap().get(new Integer( ((LiteYukonPAObject) o2).getYukonID()));
                if (lcbc1 != null && lcbc2 != null)
                {
                    thisVal = lcbc1.getSerial();
                    anotherVal = lcbc2.getSerial();
            
                    if (thisVal.equalsIgnoreCase(anotherVal))
                    {
                        thisVal = DaoFactory.getPaoDao().getYukonPAOName(lcbc1.getCbcID());
                        anotherVal = DaoFactory.getPaoDao().getYukonPAOName(lcbc2.getCbcID());
                    }
                }
            }
            return (thisVal.compareToIgnoreCase(anotherVal));
        }

        public boolean equals(Object obj)
        {
            return false;
        }
    };
	/**
	 * Sort deviceTypeCommands by their displayOrder
	 */
	public static java.util.Comparator ROUTE_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;

			if (o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				if (PAOGroups.INVALID == ((LiteYukonPAObject) o1).getRouteID())
					thisVal = NULL_OBJECT_STRING;
				else
					thisVal = DaoFactory.getPaoDao().getYukonPAOName(((LiteYukonPAObject) o1).getRouteID());

				if (PAOGroups.INVALID == ((LiteYukonPAObject) o2).getRouteID())
					anotherVal = NULL_OBJECT_STRING;
				else
					anotherVal = DaoFactory.getPaoDao().getYukonPAOName(((LiteYukonPAObject) o2).getRouteID());
			}

			if (thisVal.equalsIgnoreCase(anotherVal))
			{
				//if the types are equal, we need to sort by deviceName
				thisVal = ((LiteYukonPAObject) o1).getPaoName();
				anotherVal = ((LiteYukonPAObject) o2).getPaoName();
			}

			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};

	/**
	 * Sort deviceTypeCommands by their displayOrder
	 */
	public static java.util.Comparator METER_NUMBER_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;

			if (o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				LiteDeviceMeterNumber ldmn1 = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(((LiteYukonPAObject) o1).getYukonID());
				LiteDeviceMeterNumber ldmn2 = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(((LiteYukonPAObject) o2).getYukonID());

				thisVal = (ldmn1 != null ? ldmn1.getMeterNumber() : NULL_OBJECT_STRING);
				anotherVal = (ldmn2 != null ? ldmn2.getMeterNumber() : NULL_OBJECT_STRING);

				if (thisVal.equalsIgnoreCase(anotherVal))
				{
					//if the types are equal, we need to sort by deviceName
					thisVal = ((LiteYukonPAObject) o1).getPaoName();
					anotherVal = ((LiteYukonPAObject) o2).getPaoName();
				}
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};
    
	public java.util.Comparator LOAD_GROUP_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;

			if (o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				thisVal = DaoFactory.getPaoDao().getYukonPAOName(((LiteYukonPAObject) o1).getYukonID());
				anotherVal = DaoFactory.getPaoDao().getYukonPAOName(((LiteYukonPAObject) o2).getYukonID());
			}
			if (thisVal.equalsIgnoreCase(anotherVal))
			{
				YCLiteLoadGroup llg1 = (YCLiteLoadGroup)getLoadGroupIDToLiteLoadGroupsMap().get(new Integer( ((LiteYukonPAObject) o1).getYukonID()));
				YCLiteLoadGroup llg2 = (YCLiteLoadGroup)getLoadGroupIDToLiteLoadGroupsMap().get(new Integer( ((LiteYukonPAObject) o2).getYukonID()));
				if (llg1 != null && llg2 != null)
				{
					//if the types are equal, we need to sort by serialNumber
					thisVal = llg1.getSerial();
					anotherVal = llg2.getSerial();
				}
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public java.util.Comparator LMGROUP_ROUTE_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;

			if (o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				YCLiteLoadGroup llg1 = (YCLiteLoadGroup)getLoadGroupIDToLiteLoadGroupsMap().get(new Integer( ((LiteYukonPAObject) o1).getYukonID()));
				YCLiteLoadGroup llg2 = (YCLiteLoadGroup)getLoadGroupIDToLiteLoadGroupsMap().get(new Integer( ((LiteYukonPAObject) o2).getYukonID()));
				if (llg1 != null && llg2 != null)
				{
					thisVal = DaoFactory.getPaoDao().getYukonPAOName(llg1.getRouteID());
					anotherVal = DaoFactory.getPaoDao().getYukonPAOName(llg2.getRouteID());
			
					if (thisVal.equalsIgnoreCase(anotherVal))
					{
						thisVal = DaoFactory.getPaoDao().getYukonPAOName(llg1.getGroupID());
						anotherVal = DaoFactory.getPaoDao().getYukonPAOName(llg2.getGroupID());
					}
				}
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public java.util.Comparator LMGROUP_SERIAL_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;

			if (o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				YCLiteLoadGroup llg1 = (YCLiteLoadGroup)getLoadGroupIDToLiteLoadGroupsMap().get(new Integer( ((LiteYukonPAObject) o1).getYukonID()));
				YCLiteLoadGroup llg2 = (YCLiteLoadGroup)getLoadGroupIDToLiteLoadGroupsMap().get(new Integer( ((LiteYukonPAObject) o2).getYukonID()));
				if (llg1 != null && llg2 != null)
				{
					thisVal = llg1.getSerial();
					anotherVal = llg2.getSerial();
			
					if (thisVal.equalsIgnoreCase(anotherVal))
					{
						thisVal = DaoFactory.getPaoDao().getYukonPAOName(llg1.getGroupID());
						anotherVal = DaoFactory.getPaoDao().getYukonPAOName(llg2.getGroupID());
					}
				}
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};	
	public java.util.Comparator LMGROUP_CAPACITY_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			double thisVal = 0;
			double anotherVal = 0;
		    
			if (o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				YCLiteLoadGroup llg1 = (YCLiteLoadGroup)getLoadGroupIDToLiteLoadGroupsMap().get(new Integer( ((LiteYukonPAObject) o1).getYukonID()));
				YCLiteLoadGroup llg2 = (YCLiteLoadGroup)getLoadGroupIDToLiteLoadGroupsMap().get(new Integer( ((LiteYukonPAObject) o2).getYukonID()));
				if (llg1 != null && llg2 != null)
				{
					thisVal = llg1.getKwCapacity();
					anotherVal = llg2.getKwCapacity();
					
					if (thisVal == anotherVal)
					{
						String thisValStr = DaoFactory.getPaoDao().getYukonPAOName(llg1.getGroupID());
						String anotherValStr = DaoFactory.getPaoDao().getYukonPAOName(llg2.getGroupID());
						return (thisValStr.compareToIgnoreCase(anotherValStr));
					}
				}
			}
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	
	public CommandDeviceBean() {
		AsyncDynamicDataSource dataSource =  (AsyncDynamicDataSource) YukonSpringHook.getBean("asyncDynamicDataSource");
        dataSource.addDBChangeLiteListener(this);
	}

	public ArrayList getDeviceList()
	{
    	
		if (deviceList == null || isChanged())
		{

			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized (cache)
			{
				List allPaos = cache.getAllDevices();

				deviceList = new ArrayList(allPaos.size());
				for (int i = 0; i < allPaos.size(); i++)
				{
					boolean isValid = true;
					LiteYukonPAObject lPao = (LiteYukonPAObject) allPaos.get(i);
					if( getSearchValue().length() > 0)
					{
						if( getSearchBy() == DEVICE_NAME_SEARCH_BY)
							isValid = lPao.getPaoName().toLowerCase().startsWith(getSearchValue().toLowerCase());
						else if( getSearchBy() == ADDRESS_SEARCH_BY)
							isValid = String.valueOf(lPao.getAddress()).toLowerCase().startsWith(getSearchValue().toLowerCase());
						else if( getSearchBy() == METER_NUM_SEARCH_BY)
						{
							LiteDeviceMeterNumber ldmn = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(lPao.getYukonID());
							if (ldmn != null) 
								isValid = ldmn.getMeterNumber().toLowerCase().startsWith(getSearchValue().toLowerCase());
						}
					}
                    
					if( getSortBy() == DeviceClasses.CARRIER)
					{
						if( !isCarrierSortBy(lPao))
							isValid = false;
					}
					else if( getSortBy() == DeviceClasses.IED)
					{
						if( !isIEDSortBy(lPao))
							isValid = false;
					}
					else if( getSortBy() == DeviceClasses.RTU)
					{
						if (!isRTUSortBy(lPao))
							isValid = false;
					}
					else if( getSortBy() == DeviceClasses.TRANSMITTER)
					{
						if( !isTransmitterSortBy(lPao))
							isValid = false;
					}
					else if( getSortBy() == DeviceClasses.GROUP)
					{
						if( getLoadGroupIDToLiteLoadGroupsMap().get(new Integer(lPao.getYukonID())) == null) 
							isValid = false;                            
					}
                    else if( getSortBy() == PAOGroups.CAT_CAPCONTROL)
                    {
                        if( getCBCIDToLiteCBCMap().get(new Integer(lPao.getYukonID())) == null) 
                            isValid = false;                            
                    }                    
                    /*else if( getSortBy() == DeviceTypes.CAPBANKCONTROLLER)
                    {
                        if( !isCBCVersacomSortBy(lPao))
                            isValid = false;
                    }
                    else if( getSortBy() == DeviceTypes.CBC_EXPRESSCOM)
                    {
                        if( !isCBCExpresscomSortBy(lPao))
                            isValid = false;
                    }
                    else if( getSortBy() == DeviceTypes.CBC_FP_2800)
                    {
                        if( !isCBC_FP_2800(lPao))
                            isValid = false;
                    }
                    else if( getSortBy() == DeviceTypes.CBC_7010)
                    {
                        if( !isCBC_7010(lPao))
                            isValid = false;
                    }
                    else if( getSortBy() == DeviceTypes.CBC_7011)
                    {
                        if( !isCBC_7011(lPao))
                            isValid = false;
                    }
                    else if( getSortBy() == DeviceTypes.CBC_7012)
                    {
                        if( !isCBC_7012(lPao))
                            isValid = false;
                    }
                    else if( getSortBy() == DeviceTypes.CBC_7020)
                    {
                        if( !isCBC_7020(lPao))
                            isValid = false;
                    }
                    else if( getSortBy() == DeviceTypes.CBC_7022)
                    {
                        if( !isCBC_7022(lPao))
                            isValid = false;
                    }
                    else if( getSortBy() == DeviceTypes.CBC_7023)
                    {
                        if( !isCBC_7023(lPao))
                            isValid = false;
                    }
                    else if( getSortBy() == DeviceTypes.CBC_7024)
                    {
                        if( !isCBC_7024(lPao))
                            isValid = false;
                    }*/
					else
					{
						if( ! (getSortBy() == lPao.getPaoClass()) ) isValid = false;
					}
                    
					if( isValid ) //Filter the sortBy objects even more.
					{
						if (getFilterBy() == ROUTE_FILTER)
						{
							if (!(String.valueOf(lPao.getRouteID()).equals(getFilterValue()))) isValid = false;
						}
						else if( getFilterBy() == COMM_CHANNEL_FILTER)
						{
							if (!(String.valueOf(lPao.getPortID()).equals(getFilterValue()))) isValid = false;
						}
                        else if( getFilterBy() == CBC_TYPE_FILTER)
                        {
                            if (!(String.valueOf(PAOGroups.getPAOTypeString(lPao.getType())).equals(getFilterValue()))) isValid = false;
						}
					}
                    
					if (isValid) deviceList.add(lPao);
				}
				//organizeDeviceList();
				setChanged(false);	//reset the change flag
				//reset the searchValue, do this after resetting changed flag...this way flag will update if needed
//				  setSearchValue("");
			}
		}
		return deviceList;
	}
	
	/**
	 * Returns the orderDir.
	 * 
	 * @return int
	 */
	public int getOrderDir()
	{
		return orderDir;
	}

	/**
	 * Sets the orderDir.
	 * 
	 * @param orderDir
	 *            The orderDir to set
	 */
	public void setOrderDir(int orderDir)
	{
		this.orderDir = orderDir;
	}

	/**
	 * Returns the orderBy.
	 * 
	 * @return int
	 */
	public int getOrderBy()
	{
		return orderBy;
	}

	/**
	 * Sets the orderBy.
	 * 
	 * @param orderBy
	 *            The orderBy to set
	 */
	public void setOrderBy(int orderBy)
	{
		this.orderBy = orderBy;
	}

	/**
	 * Returns the filterBy.
	 * 
	 * @return int
	 */
	public int getFilterBy()
	{
		return filterBy;
	}

	/**
	 * Sets the filterBy.
	 * 
	 * @param filterBy
	 *            The filterBy to set
	 */
	public void setFilterBy(int filterBy)
	{
		if( this.filterBy != filterBy)
			setChanged(true);
		this.filterBy = filterBy;
		//TODO I guess this is one way to clear them out?! maybe a better way
		// would be to listen for dbchanges
		validCollGroups = null;
	}

	/**
	 * Returns the sortBy.
	 * 
	 * @return int
	 */
	public int getSortBy()
	{
		return sortBy;
	}

	/**
	 * Sets the sortBy.
	 * 
	 * @param sortBy
	 *            The sortBy to set
	 */
	public void setSortBy(int sortBy)
	{
		if( this.sortBy != sortBy)
		{
			setChanged(true);
			setOrderBy(0);	//clear out the previously selected orderby
			setFilterBy(NO_FILTER);	//clear out previous filters
		}
			
		this.sortBy = sortBy;
		//TODO I guess this is one way to clear them out?! maybe a better way
		// would be to listen for dbchanges
//		validCollGroups = null;
	}

	public String getDeviceTableHTML()
	{
		int listSize;
		organizeDeviceList();
		listSize = getDeviceList().size();
        
		String html = new String();
		
		int endIndex = (getPage() * getPageSize());
		int startIndex = endIndex - getPageSize();
		int maxPageNo = (int)Math.ceil(listSize * 1.0 / getPageSize());
		if( endIndex > listSize)
			endIndex = listSize;

		html += "<table width='95%' border='0' cellspacing='0' cellpadding='3'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR; 
		html += "    <td>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>";
		html += "        <tr>";
		html += "          <td colspan=2 align='right'>Entries per Page:" + LINE_SEPARATOR;
		html += "            <input type='text' id='PageSize' style='border:1px solid #0066CC; font:11px' size='1' value='" + getPageSize() +"'>" + LINE_SEPARATOR;
		html += "            <input type='button' style='font:11px; margin-bottom:-1px' value='Go' onclick='location.href=\"SelectDevice.jsp?pageSize=\" + document.getElementById(\"PageSize\").value;'>" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>";
		html += "        <tr>";
		html += "          <td>" + (startIndex+1) + "-" + (endIndex)+ " of " + listSize +" | ";
        
		if (getPage() == 1)
			html += "<font color='#CCCCCC'>First</font>";
		else
			html += "<a class='Link1' href='SelectDevice.jsp?page_=1'>First</a>";
		html += " | ";
		if (getPage() == 1)
			html += "<font color='#CCCCCC'>Previous</font>";
		else
			html += "<a class='Link1' href='SelectDevice.jsp?page_=" + (getPage()-1)+ "'>Previous</a>";
		html += " | ";
		if (getPage() == maxPageNo || maxPageNo == 0)
			html += "<font color='#CCCCCC'>Next</font>";
		else
			html += "<a class='Link1' href='SelectDevice.jsp?page_=" + (getPage()+1) + "'>Next</a>";
		html += " | ";
		if (getPage() == maxPageNo || maxPageNo == 0)
			html += "<font color='#CCCCCC'>Last</font>";
		else
			html += "<a class='Link1' href='SelectDevice.jsp?page_=" + maxPageNo + "'>Last</a>" + LINE_SEPARATOR;
        
		html += "          </td>" + LINE_SEPARATOR;
		html += "          <td align='right'>Page(" + getPage() + "-" + maxPageNo + "):" + LINE_SEPARATOR;
		html += "            <input type='text' id='GoPage' style='border:1px solid #0066CC; font:11px' size='1' value='" + getPage() +"'>" + LINE_SEPARATOR;
		html += "            <input type='button' style='font:11px; margin-bottom:-1px' value='Go' onclick='location.href=\"SelectDevice.jsp?page_=\" + document.getElementById(\"GoPage\").value;'>" + LINE_SEPARATOR;
        
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;

		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td>" + LINE_SEPARATOR;
       
		html += "      <table width=\'100%\' border=\'1\' cellspacing=\'0\' cellpadding=\'3\'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		String [] columns = getColumnStrings();
		for (int i = 0; i < columns.length; i++)
		{
			html += "          <td class=\'HeaderCell\' width=\'"+ getColumnWidth(columns[i]) +"%\'>" + columns[i] +"</td>" + LINE_SEPARATOR;
		}

		html += "        </tr>" + LINE_SEPARATOR;

		for (int i = startIndex; i < endIndex; i++)
		{
			LiteYukonPAObject lPao = (LiteYukonPAObject) getDeviceList().get(i);
			html += "        <tr>" + LINE_SEPARATOR;
			for (int j = 0; j < columns.length; j++)
			{
				html += "          <td class=\'TableCell\' width=\'"+getColumnWidth(columns[j])+"%\'>";
				if ( j == 0)
					html += "<a method='post' href='CommandDevice.jsp?deviceID=" + lPao.getYukonID() +"'>" + getDeviceAttributeValue(columns[j], lPao) + "</a>";
				else
					html += getDeviceAttributeValue(columns[j], lPao);
				html += "          </td>" + LINE_SEPARATOR;
			}
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
        
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td>" + (startIndex+1) + "-" + endIndex + " of " + listSize + " | ";
        
		if (getPage() == 1)
			html += " <font color='#CCCCCC'>First</font>";
		else
			html += "<a class='Link1' href='SelectDevice.jsp?page_=1'>First</a>|";
		html += " | ";
		if (getPage() == 1)
			html += "<font color='#CCCCCC'>Previous</font>";
		else
			html += "<a class='Link1' href='SelectDevice.jsp?page_=" + (getPage()-1)+ "'>Previous</a>|";
		html += " | ";
		if (getPage() == maxPageNo || maxPageNo == 0)
			html += "<font color='#CCCCCC'>Next</font>";
		else
			html += "<a class='Link1' href='SelectDevice.jsp?page_=" + (getPage()+1) + "'>Next</a>|";
		html += " | ";
		if (getPage() == maxPageNo || maxPageNo == 0)
			html += "<font color='#CCCCCC'>Last</font>";
		else
			html += "<a class='Link1' href='SelectDevice.jsp?page_=" + maxPageNo + "'>Last</a>" + LINE_SEPARATOR;
        
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}

	/**
	 *  
	 */
	private void organizeDeviceList()
	{
		switch (getOrderBy())
		{
			case DEVICE_NAME_ORDER_BY:
				Collections.sort(getDeviceList(), LiteComparators.liteStringComparator);
				break;
			case DEVICE_TYPE_ORDER_BY:
				Collections.sort(getDeviceList(), DEVICE_TYPE_COMPARATOR);
				break;
			case METER_NUM_ORDER_BY:
				Collections.sort(getDeviceList(), METER_NUMBER_COMPARATOR);
				break;
			case ADDRESS_ORDER_BY:
				Collections.sort(getDeviceList(), ADDRESS_COMPARATOR);
				break;
			case ROUTE_ORDER_BY:
				Collections.sort(getDeviceList(), ROUTE_COMPARATOR);
				break;
			case LMGROUP_ROUTE_ORDER_BY:
				Collections.sort(getDeviceList(), LMGROUP_ROUTE_COMPARATOR);
				break;
			case LMGROUP_CAPACITY_ORDER_BY:
				Collections.sort(getDeviceList(), LMGROUP_CAPACITY_COMPARATOR);
				break;
			case LMGROUP_SERIAL_ORDER_BY:
				Collections.sort(getDeviceList(), LMGROUP_SERIAL_COMPARATOR);
				break;
            case CBC_SERIAL_ORDER_BY:
                Collections.sort(getDeviceList(), CBC_SERIAL_COMPARATOR);
                break;                
			default:
				break;
		}
		if (getOrderDir() == ORDER_DIR_DESCENDING) Collections.reverse(getDeviceList());       
	}

	public List<LiteYukonPAObject> getValidRoutes()
	{
		if (validRoutes == null)
		{
			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			List routes = cache.getAllRoutes();
			validRoutes= new ArrayList<LiteYukonPAObject>();
			for (int i = 0; i < routes.size(); i++)
			{
				LiteYukonPAObject lPao = (LiteYukonPAObject)routes.get(i);
				validRoutes.add(lPao);
			}
		}
		return validRoutes;
	}

	public List<LiteYukonPAObject> getValidCommChannels()
	{
		if (validCommChannels == null)
		{
			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			List ports = cache.getAllPorts();
			validCommChannels = new ArrayList<LiteYukonPAObject>();
			for (int i = 0; i < ports.size(); i++)
			{
				LiteYukonPAObject lPao = (LiteYukonPAObject)ports.get(i);
				validCommChannels.add(lPao);
			}
		}
		return validCommChannels;
	}

    public ArrayList getValidCBCTypes()
    {
        if (validCBCTypes == null)
        {
            try
            {
                String[] valids = retrieveCBCTypes();
                validCBCTypes = new ArrayList(valids.length);
                for (int i = 0; i < valids.length; i++)
                    validCBCTypes.add(valids[i]);
            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return validCBCTypes;
    }
    
    public final static String[] retrieveCBCTypes() throws java.sql.SQLException
    {
        String[] retVal = null; 
        
        SqlStatement stmt = new SqlStatement(
                "SELECT DISTINCT TYPE FROM " + YukonPAObject.TABLE_NAME +
                " WHERE CATEGORY = '"+PAOGroups.STRING_CAT_DEVICE+"' "+
                " AND PAOCLASS = '"+PAOGroups.STRING_CAT_CAPCONTROL+"' "+
                " AND TYPE != '"+ DeviceTypes.STRING_CAP_BANK[0] + "'" , 
             CtiUtilities.getDatabaseAlias() );
                                                     
        try
        {                                           
            stmt.execute();
    
            retVal = new String[stmt.getRowCount()];
            for( int i = 0; i < stmt.getRowCount(); i++ )
                retVal[i] = new String( ((String)stmt.getRow(i)[0]) );  
        }
        catch( Exception e )
        {
            CTILogger.error( e.getMessage(), e );
        }   
    
        return retVal;
    }
    
	/**
	 * @return
	 */
	public String getFilterValue()
	{
		return filterValue;
	}

	/**
	 * @param string
	 */
	public void setFilterValue(String filterValue_)
	{
		if (this.filterValue != filterValue_)
			setChanged(true);
		this.filterValue = filterValue_;
	}

	public String[] getColumnStrings()
	{
		String [] returnStrs;
		returnStrs = new String[getOrderByStrings().length];
		returnStrs[0] = getOrderByStrings()[getOrderBy()];
		int index = 1;
		for (int i = 0; i < getOrderByStrings().length; i++)
		{
			if( i != getOrderBy())
				returnStrs[index++] = getOrderByStrings()[i];
		}
		return returnStrs;
	}

	public String[] getOrderByStrings()
	{
		switch(getSortBy())
		{
			case DeviceClasses.GROUP:
				return orderByStrings_LoadGroup;
			case DeviceClasses.TRANSMITTER:
				return orderByStrings_Transmitter;
			case DeviceClasses.RTU:
			case DeviceClasses.IED:
				return orderByStrings_Meter;
			case PAOGroups.CAT_CAPCONTROL:
				return orderByStrings_CapControl; 
			default:
				return orderByStrings_Core;

		}
	}
	public String[] getSearchByStrings()
	{
		switch(getSortBy())
		{
			case DeviceClasses.RTU:
			case DeviceClasses.IED:
				return searchByStrings_Meter;
			case PAOGroups.CAT_CAPCONTROL:
			case DeviceClasses.TRANSMITTER:
			case DeviceClasses.GROUP:
				return searchByStrings_SimpleDevice;
			default:
				return searchByStrings_Core;
		}
	}
    
	private Object getDeviceAttributeValue(String valueString, LiteYukonPAObject lPao)
	{
		if( valueString.equalsIgnoreCase(DEVICE_NAME_STRING) ||valueString.equalsIgnoreCase(LOAD_GROUP_STRING)) 
			return lPao.getPaoName();
		else if( valueString.equalsIgnoreCase(DEVICE_TYPE_STRING))
			return PAOGroups.getPAOTypeString(lPao.getType());
		else if( valueString.equalsIgnoreCase(ADDRESS_STRING))
		{
			if (! (lPao.getAddress() == PAOGroups.INVALID))
				return String.valueOf(lPao.getAddress());
		}
		else if( valueString.equalsIgnoreCase(METER_NUMBER_STRING))
		{
			LiteDeviceMeterNumber ldmn = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(lPao.getYukonID());
			if (ldmn != null)
				return ldmn.getMeterNumber().trim();
		}
		else if( valueString.equalsIgnoreCase(ROUTE_STRING))
		{
			if (lPao.getRouteID() != PAOGroups.INVALID)
				return DaoFactory.getPaoDao().getYukonPAOName(lPao.getRouteID());
		}
		else if( valueString.equalsIgnoreCase(COMM_CHANNEL_STRING))
		{
			if (lPao.getPortID() != PAOGroups.INVALID)
				return DaoFactory.getPaoDao().getYukonPAOName(lPao.getPortID());
		}
        else if(valueString.equalsIgnoreCase(CBC_SERIAL_STRING))
        {
            YCLiteCBC lcbc = (YCLiteCBC)getCBCIDToLiteCBCMap().get(new Integer(lPao.getYukonID()));
            if (lcbc != null)
            {
                if( valueString.equalsIgnoreCase(CBC_SERIAL_STRING))
                {
                    return lcbc.getSerial();
                }
            }
            
        }        
		else
		{
			YCLiteLoadGroup llg = (YCLiteLoadGroup)getLoadGroupIDToLiteLoadGroupsMap().get(new Integer(lPao.getYukonID()));
			if (llg != null)
			{
				if( valueString.equalsIgnoreCase(LMGROUP_SERIAL_STRING))
				{
					return llg.getSerial();
				}
				else if( valueString.equalsIgnoreCase(LMGROUP_CAPACITY_STRING))
				{
					return String.valueOf(llg.getKwCapacity());
				}
				else if( valueString.equalsIgnoreCase(LMGROUP_ROUTE_STRING))
				{
					return DaoFactory.getPaoDao().getYukonPAOName(llg.getRouteID());
				}
			}
            
		}
		return NULL_OBJECT_STRING;
	}
    
	private int getColumnWidth(String columnString)
	{
		if (columnString.equalsIgnoreCase(DEVICE_NAME_STRING))
			return 25;
        
		return 15;        
	}
	public int getSearchBy()
	{
		return searchBy;
	}
	public void setSearchBy(int searchBy)
	{
		if( this.searchBy != searchBy)
			setChanged(true);
		this.searchBy = searchBy;
	}
	public String getSearchValue()
	{
		return searchValue;
	}
	public void setSearchValue(String searchValue)
	{
		if( !this.searchValue.equalsIgnoreCase(searchValue.trim()))
			setChanged(true);
        
		this.searchValue = searchValue.trim();
	}
	public boolean isChanged()
	{
		return changed;
	}
	public void setChanged(boolean changed)
	{
		if( changed)//reset the page
			setPage(1);
		this.changed = changed;
	}
	public boolean isClear()
	{
		return clear;
	}
	public void setClear(boolean clear)
	{
		if( clear )	//clear all previous settings, show all
		{
			setSearchValue("");
			setSearchBy(DEVICE_NAME_SEARCH_BY);
			setFilterBy(NO_FILTER);
		}
		this.clear = clear;
	}
	public int getPage()
	{
		return page;
	}
	public void setPage(int page)
	{
		this.page = page;
	}
	/**
	 * @return
	 */
	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * @param i
	 */
	public void setPageSize(int i)
	{
		pageSize = i;
	}

	/**
	 * @return
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @param i
	 */
	public void setUserID(int i) {
		userID = i;
	}
	
	public static boolean isCarrierSortBy(LiteYukonPAObject lPao)
	{
		if( DeviceTypesFuncs.isMCT(lPao.getType()) && lPao.getCategory() == PAOGroups.CAT_DEVICE)
			return true;
		return false;
	}
	
	public static boolean isIEDSortBy(LiteYukonPAObject lPao)
	{
		if( DeviceTypesFuncs.isMeter(lPao.getType())  || lPao.getType() == PAOGroups.DAVISWEATHER
				  && lPao.getCategory() == PAOGroups.CAT_DEVICE )
			return true;
		return false;
	}
	public static boolean isRTUSortBy(LiteYukonPAObject lPao)
	{
		if ( (DeviceTypesFuncs.isRTU(lPao.getType()) || lPao.getType() == PAOGroups.DAVISWEATHER)
			&& lPao.getCategory() == PAOGroups.CAT_DEVICE
			&& !DeviceTypesFuncs.isIon(lPao.getType()) )
			return true;
		return false;
	}
	public static boolean isTransmitterSortBy(LiteYukonPAObject lPao)
	{
		if ( DeviceTypesFuncs.isTransmitter(lPao.getType()) && DeviceClasses.isMeterClass(lPao.getPaoClass())
			 && lPao.getCategory() == PAOGroups.CAT_DEVICE )
			return true;
		return false;
	}
	public static boolean isDeviceSortByGroup(LiteYukonPAObject lPao)
	{
		return (isCarrierSortBy(lPao) || isIEDSortBy(lPao) || isRTUSortBy(lPao) || isTransmitterSortBy(lPao));
	}
	public static boolean isCapControlSortByGroup(LiteYukonPAObject lPao)
	{
		return (PAOGroups.CLASS_CAPCONTROL == lPao.getPaoClass());
	}
	public static boolean isLoadManagementSortByGroup(LiteYukonPAObject lPao)
	{
		return (DeviceClasses.LOADMANAGEMENT == lPao.getPaoClass() || DeviceClasses.GROUP == lPao.getPaoClass());
	}
	
	public HashMap getLoadGroupIDToLiteLoadGroupsMap()
	{
		if (loadGroupIDToLiteLoadGroupsMap == null)
		{
			//Vector of Integer values(loadGroup or loadgroup(from within a macroGroup) ids)
			Vector groupIDs = null; 

			StringBuffer sql = new StringBuffer	("SELECT DISTINCT PAO.PAOBJECTID FROM " + YukonPAObject.TABLE_NAME  + " PAO " +
				" WHERE PAO.PAOBJECTID IN ( " +
					" SELECT DISTINCT GM.CHILDID FROM " + GenericMacro.TABLE_NAME + " GM ");
					sql.append(" WHERE GM.MACROTYPE = '" + MacroTypes.GROUP + "') ");
				sql.append(" OR PAO.PAOBJECTID IN ( " +
					" SELECT DISTINCT LMG.DEVICEID FROM " + LMGroup.TABLE_NAME + " LMG )");
                
			java.sql.Connection conn = null;
			java.sql.PreparedStatement stmt = null;
			java.sql.ResultSet rset = null;
			Set<Integer> permittedPaos = null;
            LiteYukonUser user = new LiteYukonUser(userID);
            if( userID != -1 ) {
                PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
                permittedPaos = pService.getPaoIdsForUserPermission(user, Permission.LM_VISIBLE);
            }
            
			try {
				conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
				if( conn == null )
				{
					CTILogger.info("Error getting database connection.");
					return null;
				}
				else
				{
					stmt = conn.prepareStatement(sql.toString());
					rset = stmt.executeQuery();
					Integer paoID = 0;
                    groupIDs = new Vector();
					while( rset.next()) {
                        paoID = rset.getInt(1);
                        //no permissions for this user, they can see them all
					    if(permittedPaos == null || permittedPaos.isEmpty())
					        groupIDs.add(new Integer( paoID ) );
                        /*there are permissions for this user, only allow them to see this pao if
                        it is permitted*/
                        else if(permittedPaos.contains(paoID)) 
                            groupIDs.add(new Integer( paoID ) );
                    }	
					if( stmt != null )	//close the statement after every use.
						stmt.close();

					loadGroupIDToLiteLoadGroupsMap = new HashMap(groupIDs.size());
					//Load all serial numbers for versacom and expresscom					
					sql = new StringBuffer(" SELECT DISTINCT LMGV.DEVICEID, ROUTEID, KWCAPACITY, SERIALADDRESS " +
							" FROM " + LMGroupVersacom.TABLE_NAME + " LMGV, " + LMGroup.TABLE_NAME + " LMG " +
							" WHERE LMGV.DEVICEID = LMG.DEVICEID");
					stmt = conn.prepareStatement(sql.toString());
					rset = stmt.executeQuery();
					while (rset.next())
					{
						Integer deviceID = new Integer(rset.getInt(1));
						if (groupIDs.contains(deviceID))
						{
							int routeID = rset.getInt(2);
							double capacity = rset.getDouble(3);
							String serial = rset.getString(4);
							YCLiteLoadGroup llg = new YCLiteLoadGroup(deviceID.intValue(), capacity, routeID, serial);
							loadGroupIDToLiteLoadGroupsMap.put(deviceID, llg);
						}
					}

					if( stmt != null )	//close the statement after every use.
						stmt.close();

					sql = new StringBuffer(" SELECT DISTINCT LMGROUPID, ROUTEID, KWCAPACITY, SERIALNUMBER " +
							" FROM " + LMGroupExpressCom.TABLE_NAME + " LMGE, " + LMGroup.TABLE_NAME + " LMG " +
							" WHERE LMGE.LMGROUPID = LMG.DEVICEID");
					stmt = conn.prepareStatement(sql.toString());
					rset = stmt.executeQuery();
					while (rset.next())
					{
						Integer deviceID = new Integer(rset.getInt(1));
						if (groupIDs.contains(deviceID))
						{
							int routeID = rset.getInt(2);
							double capacity = rset.getDouble(3);
							String serial = rset.getString(4);
							YCLiteLoadGroup llg = new YCLiteLoadGroup(deviceID.intValue(), capacity, routeID, serial);
							loadGroupIDToLiteLoadGroupsMap.put(deviceID, llg);
						}
					}
				}
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if( stmt != null )
						stmt.close();
					if( conn != null )
						conn.close();
				}
				catch( java.sql.SQLException e )
				{
					e.printStackTrace();
				}
			}
		}
		return loadGroupIDToLiteLoadGroupsMap;
	}
    
    public HashMap getCBCIDToLiteCBCMap()
    {
        if (cbcIDToLiteCBCMap == null)
        {
            cbcIDToLiteCBCMap = new HashMap();
            //Vector of Integer values(CBC ids)
            Vector cbcIDs = null; 

            java.sql.Connection conn = null;
            java.sql.PreparedStatement stmt = null;
            java.sql.ResultSet rset = null;
    
            try
            {
                conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
    
                if( conn == null )
                {
                    CTILogger.info("Error getting database connection.");
                    return null;
                }
                else
                {
                    //Load all cbcs                   
                    StringBuffer sql = new StringBuffer(" SELECT DISTINCT DEVICEID, SERIALNUMBER, ROUTEID " +
                            " FROM " + DeviceCBC.TABLE_NAME);
                    stmt = conn.prepareStatement(sql.toString());
                    rset = stmt.executeQuery();
                    while (rset.next())
                    {
                        int cbcID = rset.getInt(1);
                        String serial = rset.getString(2);                        
                        int routeID = rset.getInt(3);
                        YCLiteCBC lcbc = new YCLiteCBC(cbcID, routeID, serial);
                        cbcIDToLiteCBCMap.put(new Integer(cbcID), lcbc);
                    }
                }
            }
            catch( java.sql.SQLException e )
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (rset != null) rset.close();
                    if( stmt != null ) stmt.close();
                    if( conn != null ) conn.close();
                }
                catch( java.sql.SQLException e )
                {
                    e.printStackTrace();
                }
            }
        }
        return cbcIDToLiteCBCMap;
    }

	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase lBase) {
		if( msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB )
			setChanged(true);
	}    
}