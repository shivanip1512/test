package com.cannontech.stars.web;

import java.util.ArrayList;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class InventoryBean {
	
	public static final int SORT_ORDER_ASCENDING = 0;
	public static final int SORT_ORDER_DESCENDING = 1;
	
	public static final int INV_LOCATION_WAREHOUSE = 0;
	public static final int INV_LOCATION_RESIDENCE = 1;
	
	public static final int HTML_STYLE_LIST_INVENTORY = 0;
	public static final int HTML_STYLE_SELECT_INVENTORY = 1;
	
	private static final int PAGE_SIZE = 20;
	private static final java.text.SimpleDateFormat dateFormat =
			new java.text.SimpleDateFormat("MM/dd/yyyy");
	
	private static final Comparator INV_ID_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteStarsLMHardware hw1 = (LiteStarsLMHardware) o1;
			LiteStarsLMHardware hw2 = (LiteStarsLMHardware) o2;
			return hw1.getInventoryID() - hw2.getInventoryID();
		}
	};
	
	private static final Comparator SERIAL_NO_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteStarsLMHardware hw1 = (LiteStarsLMHardware) o1;
			LiteStarsLMHardware hw2 = (LiteStarsLMHardware) o2;
			int rslt = hw1.getManufactureSerialNumber().compareTo( hw2.getManufactureSerialNumber() );
			if (rslt == 0)
				rslt = hw1.getInventoryID() - hw2.getInventoryID();
			return rslt;
		}
	};
	
	private static final Comparator INST_DATE_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteStarsLMHardware hw1 = (LiteStarsLMHardware) o1;
			LiteStarsLMHardware hw2 = (LiteStarsLMHardware) o2;
			int rslt = new java.util.Date(hw1.getInstallDate()).compareTo( new java.util.Date(hw2.getInstallDate()) );
			if (rslt == 0)
				rslt = hw1.getInventoryID() - hw2.getInventoryID();
			return rslt;
		}
	};
	
	private int sortBy = CtiUtilities.NONE_ID;
	private int sortOrder = SORT_ORDER_ASCENDING;
	private int filterBy = CtiUtilities.NONE_ID;
	private int deviceType = CtiUtilities.NONE_ID;
	private int serviceCompany = CtiUtilities.NONE_ID;
	private int location = INV_LOCATION_WAREHOUSE;
	private int addressingGroup = CtiUtilities.NONE_ID;
	private int page = 1;
	private int energyCompanyID = 0;
	private int htmlStyle = HTML_STYLE_LIST_INVENTORY;
	
	private LiteStarsEnergyCompany energyCompany = null;
	private ArrayList hardwareList = null;
	
	public InventoryBean() {
	}
	
	private LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null)
			energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		return energyCompany;
	}
	
	private ArrayList getHardwareList() {
		if (hardwareList != null) return hardwareList;
		
		ArrayList hardwares = getEnergyCompany().loadInventory();
		java.util.TreeSet sortedHws = null;
		
		if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO)
			sortedHws = new java.util.TreeSet( SERIAL_NO_CMPTOR );
		else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_INST_DATE)
			sortedHws = new java.util.TreeSet( INST_DATE_CMPTOR );
		else
			sortedHws = new java.util.TreeSet( INV_ID_CMPTOR );
		
		if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
				if (liteHw.getLmHardwareTypeID() == getDeviceType())
					sortedHws.add( liteHw );
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
				if (liteHw.getInstallationCompanyID() == getServiceCompany())
					sortedHws.add( liteHw );
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION) {
			if (getLocation() == INV_LOCATION_WAREHOUSE) {
				for (int i = 0; i < hardwares.size(); i++) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
					if (liteHw.getAccountID() == CtiUtilities.NONE_ID)
						sortedHws.add( liteHw );
				}
			}
			else {	// getLocation() == INV_LOCATION_RESIDENCE
				for (int i = 0; i < hardwares.size(); i++) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
					if (liteHw.getAccountID() != CtiUtilities.NONE_ID)
						sortedHws.add( liteHw );
				}
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CONFIG) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
				
				int groupID = CtiUtilities.NONE_ID;
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation)
						getEnergyCompany().getCustAccountInformation( liteHw.getInventoryID(), false );
				if (liteAcctInfo != null) {
					for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
						if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getAddressingGroupID() != CtiUtilities.NONE_ID) {
							groupID = liteApp.getAddressingGroupID();
							break;
						}
					}
				}
				else {
					com.cannontech.database.db.stars.hardware.LMHardwareConfiguration[] configs =
							com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getALLHardwareConfigs( new Integer(liteHw.getInventoryID()) );
					if (configs != null) {
						for (int j = 0; j < configs.length; j++) {
							if (configs[j].getAddressingGroupID().intValue() > 0) {
								groupID = configs[j].getAddressingGroupID().intValue();
								break;
							}
						}
					}
				}
				
				if (groupID == getAddressingGroup())
					sortedHws.add( liteHw );
			}
		}
		else {
			for (int i = 0; i < hardwares.size(); i++)
				sortedHws.add( hardwares.get(i) );
		}
		
		hardwareList = new ArrayList();
		java.util.Iterator it = sortedHws.iterator();
		while (it.hasNext()) {
			if (getSortOrder() == SORT_ORDER_ASCENDING)
				hardwareList.add( it.next() );
			else
				hardwareList.add( 0, it.next() );
		}
		
		return hardwareList;
	}
	
	public String getHTML(HttpServletRequest req) {
		ArrayList hwList = getHardwareList();
		if (hwList == null || hwList.size() == 0)
			return "<span class='Main'>No hardware found.</span>";
		
		if (page < 1) page = 1;
		int maxPageNo = (int) Math.ceil(hwList.size() * 1.0 / PAGE_SIZE);
		if (page > maxPageNo) page = maxPageNo;
		
		int minInvNo = (page - 1) * PAGE_SIZE + 1;
		int maxInvNo = Math.min(page * PAGE_SIZE, hwList.size());
        
        StringBuffer navBuf = new StringBuffer();
        navBuf.append(minInvNo);
        if (maxInvNo > minInvNo)
        	navBuf.append("-").append(maxInvNo);
        navBuf.append(" of ").append(hwList.size());
        navBuf.append(" | ");
        if (page == 1)
        	navBuf.append("<font color='#CCCCCC'>First</font>");
        else
        	navBuf.append("<a class='Link1' href='Inventory.jsp?page=1'>First</a>");
        navBuf.append(" | ");
        if (page == 1)
        	navBuf.append("<font color='#CCCCCC'>Previous</font>");
        else
        	navBuf.append("<a class='Link1' href='Inventory.jsp?page=").append(page-1).append("'>Previous</a>");
        navBuf.append(" | ");
        if (page == maxPageNo)
        	navBuf.append("<font color='#CCCCCC'>Next</font>");
        else
        	navBuf.append("<a class='Link1' href='Inventory.jsp?page=").append(page+1).append("'>Next</a>");
        navBuf.append(" | ");
        if (page == maxPageNo)
        	navBuf.append("<font color='#CCCCCC'>Last</font>");
        else
        	navBuf.append("<a class='Link1' href='Inventory.jsp?page=").append(maxPageNo).append("'>Last</a>");
		
		StringBuffer htmlBuf = new StringBuffer();
		htmlBuf.append("<table width='80%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>").append("\r\n");
		htmlBuf.append("  <tr>").append("\r\n");
        htmlBuf.append("    <td>").append(navBuf).append("</td>").append("\r\n");
        htmlBuf.append("  </tr>").append("\r\n");
        htmlBuf.append("  <tr>").append("\r\n");
        htmlBuf.append("    <td>").append("\r\n");
        htmlBuf.append("      <table width='100%' border='1' cellspacing='0' cellpadding='3'>").append("\r\n");
        htmlBuf.append("        <tr>").append("\r\n");
        if (getHtmlStyle() == HTML_STYLE_SELECT_INVENTORY) {
	        htmlBuf.append("          <td class='HeaderCell' width='5%'>&nbsp;</td>").append("\r\n");
        }
        htmlBuf.append("          <td class='HeaderCell' width='17%'>Serial #</td>").append("\r\n");
        htmlBuf.append("          <td class='HeaderCell' width='17%'>Device Type</td>").append("\r\n");
        htmlBuf.append("          <td class='HeaderCell' width='17%'>Install Date</td>").append("\r\n");
        htmlBuf.append("          <td class='HeaderCell' width='49%'>Location</td>").append("\r\n");
        htmlBuf.append("        </tr>").append("\r\n");
        
        for (int i = minInvNo; i <= maxInvNo; i++) {
        	LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hwList.get(i-1);
        	String deviceType = getEnergyCompany().getYukonListEntry(
        			YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, liteHw.getLmHardwareTypeID()
        			).getEntryText();
        	java.util.Date installDate = ServerUtils.translateDate( liteHw.getInstallDate() );
        	String instDate = (installDate != null)? ServletUtils.formatDate(installDate, dateFormat) : "----";
        	
            htmlBuf.append("        <tr>").append("\r\n");
	        if (getHtmlStyle() == HTML_STYLE_SELECT_INVENTORY) {
	        	htmlBuf.append("          <td class='TableCell' width='5%'>");
	        	htmlBuf.append("<input type='radio' name='InvID' value='").append(liteHw.getInventoryID()).append("'>");
	        	htmlBuf.append("</td>").append("\r\n");
	        }
            htmlBuf.append("          <td class='TableCell' width='17%'>");
            htmlBuf.append("<a href='InventoryDetail.jsp'>").append(liteHw.getManufactureSerialNumber()).append("</a>");
            htmlBuf.append("</td>").append("\r\n");
            htmlBuf.append("          <td class='TableCell' width='17%'>").append(deviceType).append("</td>").append("\r\n");
            htmlBuf.append("          <td class='TableCell' width='17%'>").append(instDate).append("</td>").append("\r\n");
            htmlBuf.append("          <td class='TableCell' width='49%'>");
            if (liteHw.getAccountID() == 0)
            	htmlBuf.append("Warehouse");
            else {
            	LiteStarsCustAccountInformation liteAcctInfo = getEnergyCompany().getBriefCustAccountInfo( liteHw.getAccountID(), true );
            	LiteAddress liteAddr = getEnergyCompany().getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
            	htmlBuf.append("Acct #").append(liteAcctInfo.getCustomerAccount().getAccountNumber())
            			.append(" (").append(ServerUtils.getOneLineAddress(liteAddr)).append(")");
            }
            htmlBuf.append("</td>").append("\r\n");
            htmlBuf.append("        </tr>").append("\r\n");
        }
        
        htmlBuf.append("      </table>").append("\r\n");
        htmlBuf.append("    </td>").append("\r\n");
        htmlBuf.append("  </tr>").append("\r\n");
        htmlBuf.append("  <tr>").append("\r\n");
        htmlBuf.append("    <td>").append(navBuf).append("</td>").append("\r\n");
        htmlBuf.append("  </tr>").append("\r\n");
        htmlBuf.append("</table>").append("\r\n");

		return htmlBuf.toString();
	}

	/**
	 * Returns the deviceType.
	 * @return int
	 */
	public int getDeviceType() {
		return deviceType;
	}

	/**
	 * Returns the filterBy.
	 * @return int
	 */
	public int getFilterBy() {
		return filterBy;
	}

	/**
	 * Returns the page.
	 * @return int
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Returns the serviceCompany.
	 * @return int
	 */
	public int getServiceCompany() {
		return serviceCompany;
	}

	/**
	 * Returns the sortBy.
	 * @return int
	 */
	public int getSortBy() {
		return sortBy;
	}

	/**
	 * Sets the deviceType.
	 * @param deviceType The deviceType to set
	 */
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * Sets the filterBy.
	 * @param filterBy The filterBy to set
	 */
	public void setFilterBy(int filterBy) {
		this.filterBy = filterBy;
		// Update the search result
		hardwareList = null;
	}

	/**
	 * Sets the page.
	 * @param page The page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Sets the serviceCompany.
	 * @param serviceCompany The serviceCompany to set
	 */
	public void setServiceCompany(int serviceCompany) {
		this.serviceCompany = serviceCompany;
	}

	/**
	 * Sets the sortBy.
	 * @param sortBy The sortBy to set
	 */
	public void setSortBy(int sortBy) {
		this.sortBy = sortBy;
	}

	/**
	 * Returns the location.
	 * @return int
	 */
	public int getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 * @param location The location to set
	 */
	public void setLocation(int location) {
		this.location = location;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(int energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	/**
	 * Returns the addressingGroup.
	 * @return int
	 */
	public int getAddressingGroup() {
		return addressingGroup;
	}

	/**
	 * Sets the addressingGroup.
	 * @param addressingGroup The addressingGroup to set
	 */
	public void setAddressingGroup(int addressingGroup) {
		this.addressingGroup = addressingGroup;
	}

	/**
	 * Returns the sortOrder.
	 * @return int
	 */
	public int getSortOrder() {
		return sortOrder;
	}

	/**
	 * Sets the sortOrder.
	 * @param sortOrder The sortOrder to set
	 */
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * Returns the htmlStyle.
	 * @return int
	 */
	public int getHtmlStyle() {
		return htmlStyle;
	}

	/**
	 * Sets the htmlStyle.
	 * @param htmlStyle The htmlStyle to set
	 */
	public void setHtmlStyle(int htmlStyle) {
		this.htmlStyle = htmlStyle;
	}

}
