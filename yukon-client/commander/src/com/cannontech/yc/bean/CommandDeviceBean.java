package com.cannontech.yc.bean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.DeviceFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.device.lm.LMGroup;
import com.cannontech.database.db.device.lm.LMGroupExpressCom;
import com.cannontech.database.db.device.lm.LMGroupVersacom;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.db.user.UserPaoOwner;

/**
 * @author yao
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class CommandDeviceBean
{
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
    
    //Switch search by values
    public static final int SERIAL_NUMBER_SEARCH_BY = 0; 
    
    //device order by values
    public static final int DEVICE_NAME_ORDER_BY = 0;
    public static final int DEVICE_TYPE_ORDER_BY = 1;
    public static final int ADDRESS_ORDER_BY = 2;
    public static final int METER_NUM_ORDER_BY = 3;
    public static final int ROUTE_ORDER_BY = 4;
    
    //switch order by values
	public static final int SERIAL_NUMBER_ORDER_BY = 0;
    public static final int LOAD_GROUP_ORDER_BY = 1;

    public static final int NO_FILTER = 0;
    // Possible Filters 
    public static final int ROUTE_FILTER = 1;
    public static final int COLLECTION_GROUP_FILTER = 2;
	public static final int LOAD_GROUP_FILTER = 3;

	private static final String DEVICE_NAME_STRING = "Device Name";
    private static final String DEVICE_TYPE_STRING = "Device Type";
    private static final String ADDRESS_STRING = "Address";
    private static final String METER_NUMBER_STRING = "Meter Number";
    private static final String COLL_GROUP_STRING = "Collection Group";
    private static final String ROUTE_STRING = "Route";

	private static final String SERIAL_NUMBER_STRING = "Serial Number";
	private static final String LOAD_GROUP_STRING = "Load Group";
	
    private static final String[] orderByStrings_Core = new String[] { 
            DEVICE_NAME_STRING, 
            DEVICE_TYPE_STRING, 
            ADDRESS_STRING,
            METER_NUMBER_STRING, 
            COLL_GROUP_STRING,
            ROUTE_STRING };

	private static final String[] orderByStrings_SimpleDevice = new String[] { 
			DEVICE_NAME_STRING, 
			DEVICE_TYPE_STRING};

	
	public static final String[] orderByStrings_LoadGroup = new String[] { 
			SERIAL_NUMBER_STRING, 
			LOAD_GROUP_STRING};
 
    private static final String[] searchByStrings_Core = new String[] { 
            DEVICE_NAME_STRING, 
            ADDRESS_STRING,
            METER_NUMBER_STRING};

	private static final String[] searchByStrings_SimpleDevice = new String[] { 
			DEVICE_NAME_STRING};

	public static final String[] searchByStrings_LoadGroup = new String[] { 
			SERIAL_NUMBER_STRING};

	//Create a local (DeviceClasses.x) value to represent all the core/custom classes.
	public static final int DEVICE_SORT_BY = -1;
	public static final int EXPRESSCOM_SORT_BY = -2;
	public static final int VERSACOM_SORT_BY = -3;
	public static final int DCU_SA205_SERIAL_SORT_BY = -4;
	public static final int DCU_SA305_SERIAL_SORT_BY = -5;	
    //Contains LiteYukonPaobjects
    private ArrayList deviceList = null;
	
	//Contains LiteLoadGroups (a "local" yc class)
	private ArrayList switchList = null;
	
    //stores the currently selected Filter By value
	public String filterValue = "";
    private int orderBy = DEVICE_NAME_ORDER_BY;
    private int orderDir = ORDER_DIR_ASCENDING;
    private int sortBy = DEVICE_SORT_BY;
    private int filterBy = NO_FILTER;
    private int searchBy = DEVICE_NAME_SEARCH_BY;
    private String searchValue = "";
    
    //List of <int(routes)> values
    public ArrayList validRoutes = null;
    //List of <String, (collectionGroup)> values
    public ArrayList validCollGroups = null;
	//Map of <Integer (userID) to Vector of (inner class object)LiteLoadGroup values
	public HashMap validUserToLoadGroupsMap = null;
	
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
                    thisVal = PAOFuncs.getYukonPAOName(((LiteYukonPAObject) o1).getRouteID());

                if (PAOGroups.INVALID == ((LiteYukonPAObject) o2).getRouteID())
                    anotherVal = NULL_OBJECT_STRING;
                else
                    anotherVal = PAOFuncs.getYukonPAOName(((LiteYukonPAObject) o2).getRouteID());
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
                LiteDeviceMeterNumber ldmn1 = DeviceFuncs.getLiteDeviceMeterNumber(((LiteYukonPAObject) o1).getYukonID());
                LiteDeviceMeterNumber ldmn2 = DeviceFuncs.getLiteDeviceMeterNumber(((LiteYukonPAObject) o2).getYukonID());

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
    
	/**
	 * Sort deviceTypeCommands by their displayOrder
	 */
	public static java.util.Comparator SERIAL_NUMBER_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;

			if (o1 instanceof LiteLoadGroup && o2 instanceof LiteLoadGroup)
			{
				thisVal = ((LiteLoadGroup) o1).getSerial();
				anotherVal = ((LiteLoadGroup) o2).getSerial();
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
	public static java.util.Comparator LOAD_GROUP_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;

			if (o1 instanceof LiteLoadGroup && o2 instanceof LiteLoadGroup)
			{
				thisVal = PAOFuncs.getYukonPAOName(((LiteLoadGroup) o1).getGroupID());
				anotherVal = PAOFuncs.getYukonPAOName(((LiteLoadGroup) o2).getGroupID());
			}
			if (thisVal.equalsIgnoreCase(anotherVal))
			{
				//if the types are equal, we need to sort by serialNumber
				thisVal = ((LiteLoadGroup) o1).getSerial();
				anotherVal = ((LiteLoadGroup) o2).getSerial();
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};

    public CommandDeviceBean()
    {
    }

    public ArrayList getDeviceList()
    {
    	
        if (deviceList == null || isChanged())
        {

            DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
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
                            LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(lPao.getYukonID());
                            if (ldmn != null) 
                                isValid = ldmn.getMeterNumber().toLowerCase().startsWith(getSearchValue().toLowerCase());
                        }
                    }
                    
                    if( getSortBy() == DEVICE_SORT_BY )
                    {
						if(! ((lPao.getPaoClass() == DeviceClasses.CARRIER ||
										lPao.getPaoClass() == DeviceClasses.IED ||
										lPao.getPaoClass() == DeviceClasses.METER)   
								&& lPao.getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE 
										&& lPao.getType()!= com.cannontech.database.data.pao.PAOGroups.MCTBROADCAST) )
							isValid = false; 										
                    }
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
						else if (getFilterBy() == COLLECTION_GROUP_FILTER)
						{
							LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(lPao.getYukonID());
							if (!(ldmn != null && ldmn.getCollGroup().equalsIgnoreCase(getFilterValue()))) isValid = false;
						}
					}
                    
                    if (isValid) deviceList.add(lPao);
                }
                //organizeDeviceList();
                setChanged(false);	//reset the change flag
                //reset the searchValue, do this after resetting changed flag...this way flag will update if needed
//                setSearchValue("");
            }
        }
        return deviceList;
    }
	public ArrayList getSwitchList()
	{
		if (switchList == null || isChanged())
		{
			Vector lgVector = getValidLoadGroups();
			if( lgVector != null)
			{
				switchList = new ArrayList(lgVector.size());
				for (int i = 0; i < lgVector.size(); i++)
				{
					boolean isValid = true;
					LiteLoadGroup llg = (LiteLoadGroup)lgVector.get(i);
					if( getSearchValue().length() > 0)
					{
						if( getSearchBy() == SERIAL_NUMBER_SEARCH_BY)
							isValid = llg.getSerial().toLowerCase().startsWith(getSearchValue().toLowerCase());
					}
					if (getFilterBy() == LOAD_GROUP_FILTER)
					{
						if (!(String.valueOf(llg.getGroupID()).equals(getFilterValue())) ) isValid = false;
					}
					if (isValid) switchList.add(llg);
				}
			}
			setChanged(false);	//reset the change flag				
		}
		return switchList;
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
		}
			
		this.sortBy = sortBy;
		//TODO I guess this is one way to clear them out?! maybe a better way
		// would be to listen for dbchanges
//		validCollGroups = null;
	}

    public String getDeviceTableHTML()
    {
    	int listSize;
    	if( getSortBy() == DeviceClasses.GROUP)
   		{
   			organizeSwitchList();
   			listSize = getSwitchList().size();
    	}
    	else
    	{
    		organizeDeviceList();
    		listSize = getDeviceList().size();
    	}
        
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
        html += "          <td>" + (startIndex+1) + "-" + (endIndex)+ " of " + listSize +" | ";
        
        if (getPage() == 1)
            html += "<font color='#CCCCCC'>First</font>";
        else
            html += "<a class='Link1' href='SelectDevice.jsp?page=1'>First</a>";
        html += " | ";
        if (getPage() == 1)
            html += "<font color='#CCCCCC'>Previous</font>";
        else
            html += "<a class='Link1' href='SelectDevice.jsp?page=" + (getPage()-1)+ "'>Previous</a>";
        html += " | ";
        if (getPage() == maxPageNo || maxPageNo == 0)
        	html += "<font color='#CCCCCC'>Next</font>";
        else
            html += "<a class='Link1' href='SelectDevice.jsp?page=" + (getPage()+1) + "'>Next</a>";
        html += " | ";
        if (getPage() == maxPageNo || maxPageNo == 0)
        	html += "<font color='#CCCCCC'>Last</font>";
        else
            html += "<a class='Link1' href='SelectDevice.jsp?page=" + maxPageNo + "'>Last</a>" + LINE_SEPARATOR;
        
        html += "          </td>" + LINE_SEPARATOR;
        html += "          <td align='right'>Page(" + getPage() + "-" + maxPageNo + "):" + LINE_SEPARATOR;
        html += "            <input type='text' id='GoPage' style='border:1px solid #666699; font:11px' size='1' value='" + getPage() +"'>" + LINE_SEPARATOR;
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

		if( getSortBy() == DeviceClasses.GROUP )
		{
			for (int i = startIndex; i < endIndex; i++)
	        {
	            LiteLoadGroup llg = (LiteLoadGroup) getSwitchList().get(i);
	            html += "        <tr>" + LINE_SEPARATOR;
	            for (int j = 0; j < columns.length; j++)
	            {
	                html += "          <td class=\'TableCell\' width=\'"+getColumnWidth(columns[j])+"%\'>";
	                if ( j == 0)
	                    html += "<a method='post' href='CommandDevice.jsp?deviceID=" + llg.getGroupID() +"'>" + getSwitchAttributeValue(columns[j], llg) + "</a>";
	                else
	                    html += getSwitchAttributeValue(columns[j], llg);
	                html += "          </td>" + LINE_SEPARATOR;
	            }
	            html += "        </tr>" + LINE_SEPARATOR;
	        }
		}
		else
		{
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
            html += "<a class='Link1' href='SelectDevice.jsp?page=1'>First</a>|";
        html += " | ";
        if (getPage() == 1)
            html += "<font color='#CCCCCC'>Previous</font>";
        else
            html += "<a class='Link1' href='SelectDevice.jsp?page=" + (getPage()-1)+ "'>Previous</a>|";
        html += " | ";
        if (getPage() == maxPageNo || maxPageNo == 0)
        	html += "<font color='#CCCCCC'>Next</font>";
        else
            html += "<a class='Link1' href='SelectDevice.jsp?page=" + (getPage()+1) + "'>Next</a>|";
        html += " | ";
        if (getPage() == maxPageNo || maxPageNo == 0)
        	html += "<font color='#CCCCCC'>Last</font>";
        else
            html += "<a class='Link1' href='SelectDevice.jsp?page=" + maxPageNo + "'>Last</a>" + LINE_SEPARATOR;
        
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
        default:
            break;
        }
		if (getOrderDir() == ORDER_DIR_DESCENDING) Collections.reverse(getDeviceList());       
    }
	private void organizeSwitchList()
	{
		switch (getOrderBy())
		{
			case SERIAL_NUMBER_ORDER_BY:
				Collections.sort(getSwitchList(), SERIAL_NUMBER_COMPARATOR);
				break;
			case LOAD_GROUP_ORDER_BY:
				Collections.sort(getSwitchList(), LOAD_GROUP_COMPARATOR);
				break;
			default:
				break;
		}
		if (getOrderDir() == ORDER_DIR_DESCENDING) Collections.reverse(getSwitchList());
	}

/**
 * @return Vector of (innerclass) LiteLoadGroup
 */
	public Vector getValidLoadGroups()
	{
		Vector loadGroups = (Vector)getValidUserToLoadGroupsMap().get(new Integer(getUserID()));
		if (loadGroups == null)
		{
			//Vector of Integer values(loadGroup or loadgroup(from within a macroGroup) ids)
			Vector groupIDs = null; 
			
			StringBuffer sql = new StringBuffer	("SELECT DISTINCT PAO.PAOBJECTID FROM " + YukonPAObject.TABLE_NAME  + " PAO " +
				" WHERE PAO.PAOBJECTID IN ( " +
					" SELECT DISTINCT GM.CHILDID FROM " + GenericMacro.TABLE_NAME + " GM ");
					if( userID != -1 ) 
						sql.append(", " + UserPaoOwner.TABLE_NAME + " UPO ");
					
					sql.append(" WHERE GM.MACROTYPE = '" + MacroTypes.GROUP + "' ");
				
					if( userID != -1){
						sql.append(" AND UPO.USERID = " + userID +
						" AND GM.OWNERID = UPO.PAOID) ");
					}
				
				sql.append(" OR PAO.PAOBJECTID IN ( " +
					" SELECT DISTINCT LMG.DEVICEID FROM " + LMGroup.TABLE_NAME + " LMG");
					if( userID != -1 )
					{
						sql.append(", " + UserPaoOwner.TABLE_NAME + " UPO " +
						" WHERE LMG.DEVICEID = UPO.PAOID " +
						" AND UPO.USERID = " + userID + ")");
					}

			java.sql.Connection conn = null;
			java.sql.PreparedStatement stmt = null;
			java.sql.ResultSet rset = null;
	
			try
			{
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
					groupIDs = new Vector();
					while( rset.next())
						groupIDs.add(new Integer( rset.getInt(1) ) );
						
					loadGroups = new Vector();
					//Load all serial numbers for versacom and expresscom					
					sql = new StringBuffer(" SELECT DISTINCT DEVICEID, SERIALADDRESS FROM " + LMGroupVersacom.TABLE_NAME + " LMGV");
					stmt = conn.prepareStatement(sql.toString());
					rset = stmt.executeQuery();
					while (rset.next())
					{
						Integer deviceID = new Integer(rset.getInt(1));
						if (groupIDs.contains(deviceID))
						{
							LiteLoadGroup llg = new LiteLoadGroup(deviceID.intValue(), rset.getString(2));
							loadGroups.add(llg);
						}
					}

					sql = new StringBuffer(" SELECT DISTINCT LMGROUPID, SERIALNUMBER FROM " + LMGroupExpressCom.TABLE_NAME + " LMGE");
					stmt = conn.prepareStatement(sql.toString());
					rset = stmt.executeQuery();
					while (rset.next())
					{
						Integer deviceID = new Integer(rset.getInt(1));
						if (groupIDs.contains(deviceID))
						{
							LiteLoadGroup llg = new LiteLoadGroup(deviceID.intValue(), rset.getString(2));
							loadGroups.add(llg);
						}
					}
					getValidUserToLoadGroupsMap().put(new Integer(userID), loadGroups);
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
		return loadGroups;
	}


    public ArrayList getValidRoutes()
    {
        if (validRoutes == null)
        {
        	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
        	List routes = cache.getAllRoutes();
            validRoutes= new ArrayList();
            for (int i = 0; i < routes.size(); i++)
            {
            	LiteYukonPAObject lPao = (LiteYukonPAObject)routes.get(i);
                validRoutes.add(new Integer(lPao.getYukonID()));
            }
			validRoutes.add(new Integer(DeviceClasses.GROUP));
        }
        return validRoutes;
    }

    public ArrayList getValidCollGroups()
    {
		int id = ((Integer)getValidRoutes().get(0)).intValue();
        if (validCollGroups == null)
        {
            try
            {
                String[] valids = DeviceMeterGroup.getDeviceCollectionGroups();
                validCollGroups = new ArrayList(valids.length);
                for (int i = 0; i < valids.length; i++)
                    validCollGroups.add(valids[i]);
            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return validCollGroups;
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
			case DeviceClasses.RTU:
				return orderByStrings_SimpleDevice;
			default:
				return orderByStrings_Core;

		}
	}
	public String[] getSearchByStrings()
	{
		switch(getSortBy())
		{
			case DeviceClasses.GROUP:
				return searchByStrings_LoadGroup;
			case DeviceClasses.TRANSMITTER:
			case DeviceClasses.RTU:
				return searchByStrings_SimpleDevice;
			default:
				return searchByStrings_Core;

		}
	}
    
    private Object getDeviceAttributeValue(String valueString, LiteYukonPAObject lPao)
    {
        if( valueString.equalsIgnoreCase(DEVICE_NAME_STRING)) 
            return lPao.getPaoName();
        else if( valueString.equalsIgnoreCase(DEVICE_TYPE_STRING))
            return PAOGroups.getPAOTypeString(lPao.getType());
        else if( valueString.equalsIgnoreCase(ADDRESS_STRING))
        {
            if (lPao.getAddress() == PAOGroups.INVALID)
                return NULL_OBJECT_STRING;
            else
                return String.valueOf(lPao.getAddress());
        }
        else if( valueString.equalsIgnoreCase(METER_NUMBER_STRING))
        {
            LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(lPao.getYukonID());
            if (ldmn != null)
                return ldmn.getMeterNumber().trim();
        }
        else if( valueString.equalsIgnoreCase(COLL_GROUP_STRING))
        {
            LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(lPao.getYukonID());
            if (ldmn != null)
                return ldmn.getCollGroup().trim();
        }
        else if( valueString.equalsIgnoreCase(ROUTE_STRING))
        {
            if (lPao.getRouteID() != PAOGroups.INVALID)
                return PAOFuncs.getYukonPAOName(lPao.getRouteID());
        }

        return NULL_OBJECT_STRING;
    }
    
	private Object getSwitchAttributeValue(String valueString, LiteLoadGroup llg)
	{
		if( valueString.equalsIgnoreCase(LOAD_GROUP_STRING)) 
			return PAOFuncs.getYukonPAOName(llg.getGroupID());
		else if( valueString.equalsIgnoreCase(SERIAL_NUMBER_STRING))
			return llg.getSerial();
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
        if( !this.searchValue.equalsIgnoreCase(searchValue))
            setChanged(true);
        
        this.searchValue = searchValue;
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
            if( getSortBy() == DeviceClasses.GROUP)
            	setSearchBy(SERIAL_NUMBER_SEARCH_BY);
            else
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
	public HashMap getValidUserToLoadGroupsMap() {
		if (validUserToLoadGroupsMap == null)
			validUserToLoadGroupsMap = new HashMap();
		return validUserToLoadGroupsMap;
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
}

