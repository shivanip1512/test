package com.cannontech.yc.bean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private int page = 1;
	private int pageSize = 25;

    public static final int SORT_ORDER_ASCENDING = 0;
    public static final int SORT_ORDER_DESCENDING = 1;

    private static final String NULL_OBJECT_STRING = "---";

    public static final int DEVICE_NAME_SEARCH_BY = 0;
    public static final int ADDRESS_SEARCH_BY = 1;
    public static final int METER_NUM_SEARCH_BY = 2;
    
    public static final int DEVICE_NAME_SORT_BY = 0;
    public static final int DEVICE_TYPE_SORT_BY = 1;
    public static final int ADDRESS_SORT_BY = 2;
    public static final int METER_NUM_SORT_BY = 3;
    public static final int ROUTE_SORT_BY = 4;

    public static final int NO_FILTER = 0;
    public static final int DEVICE_CLASS_FILTER = 1;
    public static final int COLLECTION_GROUP_FILTER = 2;

    private static final String DEVICE_NAME_STRING = "Device Name";
    private static final String DEVICE_TYPE_STRING = "Device Type";
    private static final String ADDRESS_STRING = "Address";
    private static final String METER_NUMBER_STRING = "Meter Number";
    private static final String COLL_GROUP_STRING = "Collection Group";
    private static final String ROUTE_STRING = "Route";

    public static final String[] sortByStrings = new String[] { 
            DEVICE_NAME_STRING, 
            DEVICE_TYPE_STRING, 
            ADDRESS_STRING,
            METER_NUMBER_STRING, 
            ROUTE_STRING };

    public static final String[] searchByStrings = new String[] { 
            DEVICE_NAME_STRING, 
            ADDRESS_STRING,
            METER_NUMBER_STRING};

    //Contains LiteYukonPaobjects
    private ArrayList deviceList = null;
    public int deviceClass = CtiUtilities.NONE_ZERO_ID;
    public String collGroup = "";
    private int sortBy = DEVICE_NAME_SORT_BY;
    private int sortOrder = SORT_ORDER_ASCENDING;
    private int filterBy = NO_FILTER;
    private int searchBy = DEVICE_NAME_SEARCH_BY;
    private String searchValue = "";
    
    //List of <int(deviceClass)> values
    public ArrayList validDeviceClasses = null;
    //List of <String, (collectionGroup)> values
    public ArrayList validCollGroups = null;

    
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

    public CommandDeviceBean()
    {
    }

    public static String getSortByString(int sortBy)
    {
        if (sortBy < 0 || sortBy > sortByStrings.length) return sortByStrings[DEVICE_NAME_SORT_BY];
        return sortByStrings[sortBy];
    }
    
    public static String getSearchByString(int searchBy)
    {
        if (searchBy < 0 || searchBy > searchByStrings.length) return searchByStrings[DEVICE_NAME_SORT_BY];
        return searchByStrings[searchBy];
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
                    if (getFilterBy() == DEVICE_CLASS_FILTER)
                    {
                        if (!(lPao.getPaoClass() == getDeviceClass())) isValid = false;
                    }
                    else if (getFilterBy() == COLLECTION_GROUP_FILTER)
                    {
                        LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(lPao.getYukonID());
                        if (!(ldmn != null && ldmn.getCollGroup().equalsIgnoreCase(getCollGroup()))) isValid = false;
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

    /**
     * Returns the sortOrder.
     * 
     * @return int
     */
    public int getSortOrder()
    {
        return sortOrder;
    }

    /**
     * Sets the sortOrder.
     * 
     * @param sortOrder
     *            The sortOrder to set
     */
    public void setSortOrder(int sortOrder)
    {
        this.sortOrder = sortOrder;
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
        this.sortBy = sortBy;
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

    public String getDeviceTableHTML()
    {
        organizeDeviceList();
        String html = new String();
		
	    int endIndex = (getPage() * getPageSize());
	    int startIndex = endIndex - getPageSize();
	    int maxPageNo = (int)Math.ceil(getDeviceList().size() * 1.0 / getPageSize());
	    if( endIndex > getDeviceList().size())
	    	endIndex = getDeviceList().size();

	    html += "<table width='95%' border='0' cellspacing='0' cellpadding='3'>" + LINE_SEPARATOR;
	    html += "  <tr>" + LINE_SEPARATOR; 
        html += "    <td>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>";
        html += "        <tr>";
        html += "          <td>" + (startIndex+1) + "-" + (endIndex)+ " of " + getDeviceList().size() +" | ";
        
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
            html += "          <td class=\'HeaderCell\' width=\'"+ getColumnWidth(columns[i]) +"%\'>" + columns[i] +"</td>" + LINE_SEPARATOR;

        html += "        </tr>" + LINE_SEPARATOR;

        for (int i = startIndex; i < endIndex; i++)
        {
            LiteYukonPAObject lPao = (LiteYukonPAObject) getDeviceList().get(i);
            html += "        <tr>" + LINE_SEPARATOR;
            for (int j = 0; j < columns.length; j++)
            {
                html += "          <td class=\'TableCell\' width=\'"+getColumnWidth(columns[j])+"%\'>";
                if ( j == 0)
                    html += "<a method='post' href='CommandDevice.jsp?deviceID=" + lPao.getYukonID() +"'>" + getAttributeValue(columns[j], lPao) + "</a>";
                else
                    html += getAttributeValue(columns[j], lPao);
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
        html += "          <td>" + (startIndex+1) + "-" + endIndex + " of " + getDeviceList().size() + " | ";
        
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
        switch (getSortBy())
        {
        case DEVICE_NAME_SORT_BY:
            Collections.sort(getDeviceList(), LiteComparators.liteStringComparator);
            break;
        case DEVICE_TYPE_SORT_BY:
            Collections.sort(getDeviceList(), DEVICE_TYPE_COMPARATOR);
            break;
        case METER_NUM_SORT_BY:
            Collections.sort(getDeviceList(), METER_NUMBER_COMPARATOR);
            break;
        case ADDRESS_SORT_BY:
            Collections.sort(getDeviceList(), ADDRESS_COMPARATOR);
            break;
        case ROUTE_SORT_BY:
            Collections.sort(getDeviceList(), ROUTE_COMPARATOR);
            break;
        default:
            break;
        }
        if (getSortOrder() == SORT_ORDER_DESCENDING) Collections.reverse(getDeviceList());
    }

    public ArrayList getValidDeviceClasses()
    {
        if (validDeviceClasses == null)
        {
            validDeviceClasses = new ArrayList();
            for (int i = 0; i < DeviceClasses.CORE_DEVICE_CLASSES.length; i++)
            {
                validDeviceClasses.add(new Integer(DeviceClasses.CORE_DEVICE_CLASSES[i]));
            }
        }
        return validDeviceClasses;
    }

    public ArrayList getValidCollGroups()
    {
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
     * @return Returns the collGroup.
     */
    public String getCollGroup()
    {
        return collGroup;
    }

    /**
     * @param collGroup
     *            The collGroup to set.
     */
    public void setCollGroup(String collGroup)
    {
        if(! this.collGroup.equalsIgnoreCase(collGroup))
            setChanged(true);
        this.collGroup = collGroup;
    }

    /**
     * @return Returns the deviceClass.
     */
    public int getDeviceClass()
    {
        return deviceClass;
    }

    /**
     * @param deviceClass
     *            The deviceClass to set.
     */
    public void setDeviceClass(int deviceClass)
    {
        if( this.deviceClass != deviceClass)
            setChanged(true);
        this.deviceClass = deviceClass;

    }
    
    private String[] getColumnStrings()
    {
        switch (getSortBy())
        {
        	case DEVICE_TYPE_SORT_BY:
        	    return new String[] { DEVICE_TYPE_STRING, DEVICE_NAME_STRING, ADDRESS_STRING, METER_NUMBER_STRING, COLL_GROUP_STRING, ROUTE_STRING };
        	case ADDRESS_SORT_BY:
	            return new String[] { ADDRESS_STRING, DEVICE_NAME_STRING, DEVICE_TYPE_STRING, METER_NUMBER_STRING, COLL_GROUP_STRING, ROUTE_STRING };
	        case METER_NUM_SORT_BY:
	            return new String[] { METER_NUMBER_STRING, DEVICE_NAME_STRING, DEVICE_TYPE_STRING, ADDRESS_STRING, COLL_GROUP_STRING, ROUTE_STRING };
	        case ROUTE_SORT_BY:
	            return new String[] { ROUTE_STRING, DEVICE_NAME_STRING, DEVICE_TYPE_STRING, ADDRESS_STRING, METER_NUMBER_STRING, COLL_GROUP_STRING};
	        case DEVICE_NAME_SORT_BY:
	        default:
	            return new String[] { DEVICE_NAME_STRING, DEVICE_TYPE_STRING, ADDRESS_STRING, METER_NUMBER_STRING, COLL_GROUP_STRING, ROUTE_STRING };
        }
    }
    
    private Object getAttributeValue(String valueString, LiteYukonPAObject lPao)
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

}

