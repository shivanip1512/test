package com.cannontech.yc.bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.capcontrol.DeviceCBC;
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
public class CommandDeviceBean implements DBChangeListener
{
	private HashMap<Integer, YCLiteLoadGroup> loadGroupIDToLiteLoadGroupsMap = null;
    private HashMap<Integer, YCLiteCBC> cbcIDToLiteCBCMap = null;
	private boolean clear = false;	//flag to clear all selection settings
	private boolean changed = false;
	private int userID = -1;//	admin userid    
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private int page = 1;
	private int pageSize = 25;

	public static final int ORDER_DIR_ASCENDING = 0;
	public static final int ORDER_DIR_DESCENDING = 1;

	private static final String NULL_OBJECT_STRING = "---";
    
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
	private static final String LMGROUP_SERIAL_STRING = "Group Serial Number";
	private static final String LMGROUP_CAPACITY_STRING = "kW Capactiy";
	private static final String LMGROUP_ROUTE_STRING = "Group Route";
	private static final String LMGROUP_TYPE_STRING = "Group Type";
	
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
			LMGROUP_TYPE_STRING,
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
	private ArrayList<LiteYukonPAObject> deviceList = null;
	
	//stores the currently selected Filter By value
	public String filterValue = "";
	private int orderBy = DEVICE_NAME_ORDER_BY;
	private int orderDir = ORDER_DIR_ASCENDING;
	private int sortBy = DeviceClasses.CARRIER;
	private int filterBy = NO_FILTER;
	private String searchBy = DEVICE_NAME_STRING;
	private String searchValue = "";
    
	//List of <route> values
	public List<LiteYukonPAObject> validRoutes = null;
	//List of <int(commChannels)> values
	public List<LiteYukonPAObject> validCommChannels = null;
	//List of <String, (collectionGroup)> values
	public ArrayList<String> validCollGroups = null;
    //List of <int(CBC Types)> values
    public ArrayList<String> validCBCTypes = null;
	
	/**
	 * Sort deviceTypeCommands by their displayOrder
	 */
	public static java.util.Comparator<LiteYukonPAObject> DEVICE_TYPE_COMPARATOR = new java.util.Comparator<LiteYukonPAObject>()
	{
		public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2)
		{
			String thisVal = null, anotherVal = null;

			thisVal = o1.getPaoType().getDbString();
			anotherVal = o2.getPaoType().getDbString();

			if (thisVal.equalsIgnoreCase(anotherVal))
			{
				//if the types are equal, we need to sort by deviceName
				thisVal = o1.getPaoName();
				anotherVal = o2.getPaoName();
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
	public static java.util.Comparator<LiteYukonPAObject> ADDRESS_COMPARATOR = new java.util.Comparator<LiteYukonPAObject>()
	{
		public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2)
		{
			String thisVal = null, anotherVal = null;

			thisVal = String.valueOf(((LiteYukonPAObject) o1).getAddress());
			anotherVal = String.valueOf(((LiteYukonPAObject) o2).getAddress());

			if (thisVal.equalsIgnoreCase(anotherVal))
			{
				//if the types are equal, we need to sort by deviceName
				thisVal = o1.getPaoName();
				anotherVal = o2.getPaoName();
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
    public java.util.Comparator<LiteYukonPAObject> CBC_SERIAL_COMPARATOR = new java.util.Comparator<LiteYukonPAObject>()
    {
        public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2)
        {
            String thisVal = null, anotherVal = null;

            YCLiteCBC lcbc1 = getCBCIDToLiteCBCMap().get(o1.getYukonID());
            YCLiteCBC lcbc2 = getCBCIDToLiteCBCMap().get(o2.getYukonID());
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
	public static java.util.Comparator<LiteYukonPAObject> ROUTE_COMPARATOR = new java.util.Comparator<LiteYukonPAObject>()
	{
		public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2)
		{
			String thisVal = null, anotherVal = null;

			if (PAOGroups.INVALID == o1.getRouteID())
				thisVal = NULL_OBJECT_STRING;
			else
				thisVal = DaoFactory.getPaoDao().getYukonPAOName(o1.getRouteID());

			if (PAOGroups.INVALID == o2.getRouteID())
				anotherVal = NULL_OBJECT_STRING;
			else
				anotherVal = DaoFactory.getPaoDao().getYukonPAOName(o2.getRouteID());

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
	public static java.util.Comparator<LiteYukonPAObject> METER_NUMBER_COMPARATOR = new java.util.Comparator<LiteYukonPAObject>()
	{
		public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2)
		{
			String thisVal = null, anotherVal = null;

			LiteDeviceMeterNumber ldmn1 = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(o1.getYukonID());
			LiteDeviceMeterNumber ldmn2 = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(o2.getYukonID());

			thisVal = (ldmn1 != null ? ldmn1.getMeterNumber() : NULL_OBJECT_STRING);
			anotherVal = (ldmn2 != null ? ldmn2.getMeterNumber() : NULL_OBJECT_STRING);

			if (thisVal.equalsIgnoreCase(anotherVal))
			{
				//if the types are equal, we need to sort by deviceName
				thisVal = o1.getPaoName();
				anotherVal = o2.getPaoName();
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};
    
	public java.util.Comparator<LiteYukonPAObject> LOAD_GROUP_COMPARATOR = new java.util.Comparator<LiteYukonPAObject>()
	{
		public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2)
		{
			String thisVal = null, anotherVal = null;

			thisVal = DaoFactory.getPaoDao().getYukonPAOName(o1.getYukonID());
			anotherVal = DaoFactory.getPaoDao().getYukonPAOName(o2.getYukonID());

			if (thisVal.equalsIgnoreCase(anotherVal))
			{
				YCLiteLoadGroup llg1 = getLoadGroupIDToLiteLoadGroupsMap().get(o1.getYukonID());
				YCLiteLoadGroup llg2 = getLoadGroupIDToLiteLoadGroupsMap().get(o2.getYukonID());
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

	public java.util.Comparator<LiteYukonPAObject> LMGROUP_ROUTE_COMPARATOR = new java.util.Comparator<LiteYukonPAObject>()
	{
		public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2)
		{
			String thisVal = null, anotherVal = null;

			YCLiteLoadGroup llg1 = getLoadGroupIDToLiteLoadGroupsMap().get(o1.getYukonID());
			YCLiteLoadGroup llg2 = getLoadGroupIDToLiteLoadGroupsMap().get(o2.getYukonID());
			if (llg1 != null && llg2 != null)
			{
				thisVal = DaoFactory.getPaoDao().getYukonPAOName(llg1.getRouteID());
				anotherVal = DaoFactory.getPaoDao().getYukonPAOName(llg2.getRouteID());
		
				if (thisVal.equalsIgnoreCase(anotherVal))
				{
					thisVal = DaoFactory.getPaoDao().getYukonPAOName(llg1.getPaoIdentifier().getPaoId());
					anotherVal = DaoFactory.getPaoDao().getYukonPAOName(llg2.getPaoIdentifier().getPaoId());
				}
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public java.util.Comparator<LiteYukonPAObject> LMGROUP_SERIAL_COMPARATOR = new java.util.Comparator<LiteYukonPAObject>()
	{
		public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2)
		{
			String thisVal = null, anotherVal = null;

			YCLiteLoadGroup llg1 = getLoadGroupIDToLiteLoadGroupsMap().get(o1.getYukonID());
			YCLiteLoadGroup llg2 = getLoadGroupIDToLiteLoadGroupsMap().get(o2.getYukonID());
			if (llg1 != null && llg2 != null)
			{
				thisVal = llg1.getSerial();
				anotherVal = llg2.getSerial();
		
				if (thisVal.equalsIgnoreCase(anotherVal))
				{
					thisVal = DaoFactory.getPaoDao().getYukonPAOName(llg1.getPaoIdentifier().getPaoId());
					anotherVal = DaoFactory.getPaoDao().getYukonPAOName(llg2.getPaoIdentifier().getPaoId());
				}
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};	
	public java.util.Comparator<LiteYukonPAObject> LMGROUP_CAPACITY_COMPARATOR = new java.util.Comparator<LiteYukonPAObject>()
	{
		public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2)
		{
			double thisVal = 0;
			double anotherVal = 0;
		    
			YCLiteLoadGroup llg1 = getLoadGroupIDToLiteLoadGroupsMap().get(o1.getYukonID());
			YCLiteLoadGroup llg2 = getLoadGroupIDToLiteLoadGroupsMap().get(o2.getYukonID());
			if (llg1 != null && llg2 != null)
			{
				thisVal = llg1.getKwCapacity();
				anotherVal = llg2.getKwCapacity();
				
				if (thisVal == anotherVal)
				{
					String thisValStr = DaoFactory.getPaoDao().getYukonPAOName(llg1.getPaoIdentifier().getPaoId());
					String anotherValStr = DaoFactory.getPaoDao().getYukonPAOName(llg2.getPaoIdentifier().getPaoId());
					return (thisValStr.compareToIgnoreCase(anotherValStr));
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
        dataSource.addDBChangeListener(this);
	}

	public ArrayList<LiteYukonPAObject> getDeviceList()
	{
    	
		if (deviceList == null || isChanged())
		{
            if (isChanged()) {    //clear out all maps, we're not sure what changed at this point.
                loadGroupIDToLiteLoadGroupsMap = null;
                cbcIDToLiteCBCMap = null;
            }
            
			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized (cache)
			{
				List<LiteYukonPAObject> allPaos = cache.getAllDevices();

				deviceList = new ArrayList<LiteYukonPAObject>(allPaos.size());
				for (int i = 0; i < allPaos.size(); i++)
				{
					boolean isValid = true;
					LiteYukonPAObject lPao = (LiteYukonPAObject) allPaos.get(i);
					if( getSearchValue().length() > 0)
					{
						if( getSearchBy().equalsIgnoreCase(DEVICE_NAME_STRING))
							isValid = lPao.getPaoName().toLowerCase().startsWith(getSearchValue().toLowerCase());
						else if( getSearchBy().equalsIgnoreCase(ADDRESS_STRING))
							isValid = String.valueOf(lPao.getAddress()).toLowerCase().startsWith(getSearchValue().toLowerCase());
						else if( getSearchBy().equalsIgnoreCase(METER_NUMBER_STRING))
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
						if( ! (getSortBy() == lPao.getPaoType().getPaoClass().getPaoClassId()) ) isValid = false;
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
                            if (!(String.valueOf(lPao.getPaoType().getDbString()).equals(getFilterValue()))) isValid = false;
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

		html += "<table class='full_width' border='0' cellspacing='0' cellpadding='3'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR; 
		html += "    <td>" + LINE_SEPARATOR;
		html += "      <table border='0' cellspacing='0' cellpadding='3' class='TableCell full_width'>";
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
       
		html += "      <table class=\"resultsTable full_width\">" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		String [] columns = getColumnStrings();
		for (int i = 0; i < columns.length; i++)
		{
			html += "          <th width=\'"+ getColumnWidth(columns[i]) +"%\'>" + columns[i] +"</th>" + LINE_SEPARATOR;
		}

		html += "        </tr>" + LINE_SEPARATOR;

		for (int i = startIndex; i < endIndex; i++)
		{
			LiteYukonPAObject lPao = (LiteYukonPAObject) getDeviceList().get(i);
			html += "        <tr>" + LINE_SEPARATOR;
			for (int j = 0; j < columns.length; j++)
			{
				html += "          <td width=\'"+getColumnWidth(columns[j])+"%\'>";
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
		html += "      <table border='0' cellspacing='0' cellpadding='3' class='TableCell full_width'>" + LINE_SEPARATOR;
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
			List<LiteYukonPAObject> routes = cache.getAllRoutes();
			validRoutes= new ArrayList<LiteYukonPAObject>();
			for (int i = 0; i < routes.size(); i++)
			{
				LiteYukonPAObject lPao = routes.get(i);
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
			List<LiteYukonPAObject> ports = cache.getAllPorts();
			validCommChannels = new ArrayList<LiteYukonPAObject>();
			for (int i = 0; i < ports.size(); i++)
			{
				LiteYukonPAObject lPao = ports.get(i);
				validCommChannels.add(lPao);
			}
		}
		return validCommChannels;
	}

    public ArrayList<String> getValidCBCTypes()
    {
        if (validCBCTypes == null)
        {
            try
            {
                String[] valids = retrieveCBCTypes();
                validCBCTypes = new ArrayList<String>(valids.length);
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
			case DeviceClasses.IED:
				return searchByStrings_Meter;
			case PAOGroups.CAT_CAPCONTROL:
			case DeviceClasses.RTU:
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
			return lPao.getPaoType().getDbString();
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
					return (llg.getSerial() == null ? NULL_OBJECT_STRING : llg.getSerial());
				}
				else if( valueString.equalsIgnoreCase(LMGROUP_CAPACITY_STRING))
				{
					return String.valueOf(llg.getKwCapacity());
				}
				else if( valueString.equalsIgnoreCase(LMGROUP_ROUTE_STRING))
				{
					return (llg.getRouteID() >= 0 ? DaoFactory.getPaoDao().getYukonPAOName(llg.getRouteID()) : NULL_OBJECT_STRING);
				}
                else if( valueString.equalsIgnoreCase(LMGROUP_TYPE_STRING))
                {
                    return llg.getPaoIdentifier().getPaoType().getDbString();
                }
			}
            
		}
		return NULL_OBJECT_STRING;
	}
    
	private int getColumnWidth(String columnString)
	{
		if (columnString.equalsIgnoreCase(DEVICE_NAME_STRING) || 
		        columnString.equalsIgnoreCase(LOAD_GROUP_STRING))
			return 25;
        
		return 15;        
	}
	
    public String getSearchBy()
    {
        return searchBy;
    }
    public void setSearchBy(String searchBy)
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
			setSearchBy(DEVICE_NAME_STRING);
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
		if( DeviceTypesFuncs.isMCT(lPao.getPaoType().getDeviceTypeId()) && lPao.getPaoType().getPaoCategory() == PaoCategory.DEVICE)
			return true;
		return false;
	}
	
	public static boolean isIEDSortBy(LiteYukonPAObject lPao)
	{
		if( DeviceTypesFuncs.isMeter(lPao.getPaoType().getDeviceTypeId())  || lPao.getPaoType() == PaoType.DAVISWEATHER
				  && lPao.getPaoType().getPaoCategory() == PaoCategory.DEVICE )
			return true;
		return false;
	}
	public static boolean isRTUSortBy(LiteYukonPAObject lPao)
	{
		if ( (DeviceTypesFuncs.isRTU(lPao.getPaoType().getDeviceTypeId()) || lPao.getPaoType() == PaoType.DAVISWEATHER)
			&& lPao.getPaoType().getPaoCategory() == PaoCategory.DEVICE
			&& !DeviceTypesFuncs.isIon(lPao.getPaoType().getDeviceTypeId()) )
			return true;
		return false;
	}
	public static boolean isTransmitterSortBy(LiteYukonPAObject lPao)
	{
		if ( DeviceTypesFuncs.isTransmitter(lPao.getPaoType().getDeviceTypeId()) 
				&& DeviceClasses.isMeterClass(lPao.getPaoType().getPaoClass().getPaoClassId())
				&& lPao.getPaoType().getPaoCategory() == PaoCategory.DEVICE )
			return true;
		return false;
	}
	public static boolean isDeviceSortByGroup(LiteYukonPAObject lPao)
	{
		return (isCarrierSortBy(lPao) || isIEDSortBy(lPao) || isRTUSortBy(lPao) || isTransmitterSortBy(lPao));
	}
	public static boolean isCapControlSortByGroup(LiteYukonPAObject lPao)
	{
		return (PaoClass.CAPCONTROL == lPao.getPaoType().getPaoClass());
	}
	public static boolean isLoadManagementSortByGroup(LiteYukonPAObject lPao)
	{
		return (PaoClass.LOADMANAGEMENT == lPao.getPaoType().getPaoClass() || 
				PaoClass.GROUP == lPao.getPaoType().getPaoClass());
	}
	
	public HashMap<Integer, YCLiteLoadGroup> getLoadGroupIDToLiteLoadGroupsMap()
	{
		if (loadGroupIDToLiteLoadGroupsMap == null)
		{
		    final List<YCLiteLoadGroup> liteLoadGroups = new ArrayList<YCLiteLoadGroup>();
			SqlStatementBuilder sql = new SqlStatementBuilder();
			sql.append(" SELECT LMG.DeviceId, Type, kWCapacity, RouteId, Serial ");
			sql.append(" FROM LMGroup lmg JOIN ");
			sql.append(" (SELECT DeviceId, RouteId, SerialAddress AS Serial FROM LMGroupVersacom "); 
			sql.append(" UNION SELECT LMGroupId as DeviceId, RouteId, SerialNumber FROM LMGroupExpressCom "); 
			sql.append(" UNION SELECT DeviceId, RouteId, NULL FROM LMGroupEmetcon ");
			sql.append(" UNION SELECT OwnerId as DeviceId, -1, NULL FROM  GenericMacro ) LMGroups ");
			sql.append(" ON lmg.DeviceId = LMGroups.DeviceId ");
			sql.append(" JOIN YukonPaobject pao on lmg.DeviceId = pao.PaobjectId");

			JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
	        template.query(sql.getSql(), sql.getArguments(), new RowCallbackHandler() {
	            @Override
	            public void processRow(ResultSet rs) throws SQLException {
	                int groupID = rs.getInt(1);
	                String type = rs.getString(2);
	                double kwCapacity = rs.getDouble(3);
	                int routeID = rs.getInt(4);
	                String serial = rs.getString(5);

	                PaoType paoType = PaoType.getForDbString(type);
	                YCLiteLoadGroup ycLiteLoadGroup = new YCLiteLoadGroup(groupID, kwCapacity, routeID, serial, paoType);
	                liteLoadGroups.add(ycLiteLoadGroup);
	            }
	        });

	        final PaoAuthorizationService paoAuthorizationService = 
	            YukonSpringHook.getBean("paoAuthorizationService", PaoAuthorizationService.class);
	        final LiteYukonUser unloadedLiteYukonUser = new LiteYukonUser(getUserID());
	        loadGroupIDToLiteLoadGroupsMap = new HashMap<Integer, YCLiteLoadGroup>();
	        
            List<YCLiteLoadGroup> filtered = 
                paoAuthorizationService.filterAuthorized(unloadedLiteYukonUser, 
                                                         liteLoadGroups, 
                                                         Permission.LM_VISIBLE);
            for (YukonPao yukonPao : filtered) {
                YCLiteLoadGroup ycLiteLoadGroup = (YCLiteLoadGroup)yukonPao;
                loadGroupIDToLiteLoadGroupsMap.put(ycLiteLoadGroup.getPaoIdentifier().getPaoId(), 
                                                   ycLiteLoadGroup);
            }
		}
		return loadGroupIDToLiteLoadGroupsMap;
	}
    
    public HashMap<Integer, YCLiteCBC> getCBCIDToLiteCBCMap()
    {
        if (cbcIDToLiteCBCMap == null)
        {
            cbcIDToLiteCBCMap = new HashMap<Integer, YCLiteCBC>();

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
                        cbcIDToLiteCBCMap.put(cbcID, lcbc);
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

	@Override
	public void dbChangeReceived(DBChangeMsg msg) {
	    if( msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB )
	        setChanged(true);
	}
}