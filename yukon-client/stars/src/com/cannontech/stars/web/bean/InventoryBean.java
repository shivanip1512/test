package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
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
	public static final int HTML_STYLE_INVENTORY_SET = 2;
	
	private static final int DEFAULT_PAGE_SIZE = 20;
	private static final java.text.SimpleDateFormat dateFormat =
			new java.text.SimpleDateFormat("MM/dd/yyyy");
	
	private static final Comparator INV_ID_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteInventoryBase inv1 = (LiteInventoryBase) o1;
			LiteInventoryBase inv2 = (LiteInventoryBase) o2;
			return inv1.getInventoryID() - inv2.getInventoryID();
		}
	};
	
	/**
	 * Comparator of serial # and device names. Serial # is always "less than"
	 * device name. To compare two serial #s, try to convert them into decimal
	 * values first, compare the decimal values if conversion is successful.
	 */
	private static final Comparator SERIAL_NO_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteInventoryBase inv1 = (LiteInventoryBase) o1;
			LiteInventoryBase inv2 = (LiteInventoryBase) o2;
			
			if ((inv1 instanceof LiteStarsLMHardware) && (inv2 instanceof LiteStarsLMHardware)) {
				LiteStarsLMHardware hw1 = (LiteStarsLMHardware) inv1;
				LiteStarsLMHardware hw2 = (LiteStarsLMHardware) inv2;
				
				Long sn1 = null;
				try {
					sn1 = Long.valueOf( hw1.getManufactureSerialNumber() );
				}
				catch (NumberFormatException e) {}
				
				Long sn2 = null;
				try {
					sn2 = Long.valueOf( hw2.getManufactureSerialNumber() );
				}
				catch (NumberFormatException e) {}
				
				if (sn1 != null && sn2 != null)
					return sn1.compareTo( sn2 );
				else if (sn1 != null && sn2 == null)
					return -1;
				else if (sn1 == null && sn2 != null)
					return 1;
				else
					return hw1.getManufactureSerialNumber().compareTo( hw2.getManufactureSerialNumber() );
			}
			else if ((inv1 instanceof LiteStarsLMHardware) && !(inv2 instanceof LiteStarsLMHardware))
				return -1;
			else if (!(inv1 instanceof LiteStarsLMHardware) && (inv2 instanceof LiteStarsLMHardware))
				return 1;
			else
				return PAOFuncs.getYukonPAOName(inv1.getDeviceID()).compareTo( PAOFuncs.getYukonPAOName(inv2.getDeviceID()) );
		}
	};
	
	private static final Comparator INST_DATE_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteInventoryBase inv1 = (LiteInventoryBase) o1;
			LiteInventoryBase inv2 = (LiteInventoryBase) o2;
			
			return new java.util.Date(inv1.getInstallDate()).compareTo( new java.util.Date(inv2.getInstallDate()) );
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
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int energyCompanyID = 0;
	private int htmlStyle = HTML_STYLE_LIST_INVENTORY;
	private String referer = null;
	private ArrayList inventorySet = null;
	
	private LiteStarsEnergyCompany energyCompany = null;
	private ArrayList inventoryList = null;
	
	public InventoryBean() {
	}
	
	private LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null)
			energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		return energyCompany;
	}
	
	private ArrayList getHardwareList() {
		if (inventoryList != null) return inventoryList;
		
		ArrayList hardwares = null;
		if (getHtmlStyle() == HTML_STYLE_INVENTORY_SET)
			hardwares = inventorySet;
		else
			hardwares = getEnergyCompany().loadAllInventory();
		
		java.util.TreeSet sortedInvs = null;
		if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO)
			sortedInvs = new java.util.TreeSet( SERIAL_NO_CMPTOR );
		else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_INST_DATE)
			sortedInvs = new java.util.TreeSet( INST_DATE_CMPTOR );
		else
			sortedInvs = new java.util.TreeSet( INV_ID_CMPTOR );
		
		if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) hardwares.get(i);
				
				if (liteInv instanceof LiteStarsLMHardware &&
					((LiteStarsLMHardware)liteInv).getLmHardwareTypeID() == getDeviceType()
					||
					ECUtils.isMCT( liteInv.getCategoryID() ) &&
					getEnergyCompany().getYukonListEntry(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, getDeviceType()).getYukonDefID()
							== YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_METER)
				{
					sortedInvs.add( liteInv );
				}
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) hardwares.get(i);
				
				if (liteInv.getInstallationCompanyID() == getServiceCompany())
					sortedInvs.add( liteInv );
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION) {
			if (getLocation() == INV_LOCATION_WAREHOUSE) {
				for (int i = 0; i < hardwares.size(); i++) {
					LiteInventoryBase liteInv = (LiteInventoryBase) hardwares.get(i);
					
					if (liteInv.getAccountID() == CtiUtilities.NONE_ID)
						sortedInvs.add( liteInv );
				}
			}
			else {	// getLocation() == INV_LOCATION_RESIDENCE
				for (int i = 0; i < hardwares.size(); i++) {
					LiteInventoryBase liteInv = (LiteInventoryBase) hardwares.get(i);
					
					if (liteInv.getAccountID() != CtiUtilities.NONE_ID)
						sortedInvs.add( liteInv );
				}
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CONFIG) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) hardwares.get(i);
				
				int groupID = CtiUtilities.NONE_ID;
				if (liteInv instanceof LiteStarsLMHardware) {
					LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation)
							getEnergyCompany().getCustAccountInformation( liteInv.getInventoryID(), false );
					
					if (liteAcctInfo != null) {
						for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
							LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
							if (liteApp.getInventoryID() == liteInv.getInventoryID() && liteApp.getAddressingGroupID() != CtiUtilities.NONE_ID) {
								groupID = liteApp.getAddressingGroupID();
								break;
							}
						}
					}
					else {
						com.cannontech.database.db.stars.hardware.LMHardwareConfiguration[] configs =
								com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getALLHardwareConfigs( new Integer(liteInv.getInventoryID()) );
						if (configs != null) {
							for (int j = 0; j < configs.length; j++) {
								if (configs[j].getAddressingGroupID().intValue() > 0) {
									groupID = configs[j].getAddressingGroupID().intValue();
									break;
								}
							}
						}
					}
				}
				
				if (groupID == getAddressingGroup())
					sortedInvs.add( liteInv );
			}
		}
		else {
			sortedInvs.addAll( hardwares );
		}
		
		inventoryList = new ArrayList();
		
		java.util.Iterator it = sortedInvs.iterator();
		while (it.hasNext()) {
			if (getSortOrder() == SORT_ORDER_ASCENDING)
				inventoryList.add( it.next() );
			else
				inventoryList.add( 0, it.next() );
		}
		
		return inventoryList;
	}
	
	public void resetInventoryList() {
		inventoryList = null;
	}
	
	public String getHTML(HttpServletRequest req) {
		StringBuffer htmlBuf = new StringBuffer();
		
		ArrayList hwList = getHardwareList();
		if (hwList == null || hwList.size() == 0) {
			htmlBuf.append("<p class='ErrorMsg'>No hardware found.</p>").append("\r\n");
			if (getHtmlStyle() != HTML_STYLE_LIST_INVENTORY) {
				htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='0'>").append("\r\n");
				htmlBuf.append("  <tr>").append("\r\n");
				htmlBuf.append("    <td align='center'><input type='button' name='Back' value='Back' onclick='history.back()'></td>").append("\r\n");
				htmlBuf.append("  </tr>").append("\r\n");
				htmlBuf.append("</table>").append("\r\n");
			}
			return htmlBuf.toString();
		}
		
		String uri = req.getRequestURI();
		String pageName = uri.substring( uri.lastIndexOf('/') + 1 );
		
		if (page < 1) page = 1;
		int maxPageNo = (int) Math.ceil(hwList.size() * 1.0 / pageSize);
		if (page > maxPageNo) page = maxPageNo;
		
		int minInvNo = (page - 1) * pageSize + 1;
		int maxInvNo = Math.min(page * pageSize, hwList.size());
        
        StringBuffer navBuf = new StringBuffer();
        navBuf.append(minInvNo);
        if (maxInvNo > minInvNo)
        	navBuf.append("-").append(maxInvNo);
        navBuf.append(" of ").append(hwList.size());
        navBuf.append(" | ");
        if (page == 1)
        	navBuf.append("<font color='#CCCCCC'>First</font>");
        else
        	navBuf.append("<a class='Link1' href='").append(pageName).append("?page=1'>First</a>");
        navBuf.append(" | ");
        if (page == 1)
        	navBuf.append("<font color='#CCCCCC'>Previous</font>");
        else
        	navBuf.append("<a class='Link1' href='").append(pageName).append("?page=").append(page-1).append("'>Previous</a>");
        navBuf.append(" | ");
        if (page == maxPageNo)
        	navBuf.append("<font color='#CCCCCC'>Next</font>");
        else
        	navBuf.append("<a class='Link1' href='").append(pageName).append("?page=").append(page+1).append("'>Next</a>");
        navBuf.append(" | ");
        if (page == maxPageNo)
        	navBuf.append("<font color='#CCCCCC'>Last</font>");
        else
        	navBuf.append("<a class='Link1' href='").append(pageName).append("?page=").append(maxPageNo).append("'>Last</a>");
		
		if (getHtmlStyle() == HTML_STYLE_SELECT_INVENTORY) {
			htmlBuf.append("<form name='InventoryBeanForm' method='post' action='").append(req.getContextPath()).append("/servlet/InventoryManager'>").append("\r\n");
			htmlBuf.append("<input type='hidden' name='action' value='SelectInventory'>").append("\r\n");
		}
		
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
        htmlBuf.append("          <td class='HeaderCell' width='17%'>Serial # / DeviceName</td>").append("\r\n");
        htmlBuf.append("          <td class='HeaderCell' width='17%'>Device Type</td>").append("\r\n");
        htmlBuf.append("          <td class='HeaderCell' width='17%'>Install Date</td>").append("\r\n");
        htmlBuf.append("          <td class='HeaderCell' width='49%'>Location</td>").append("\r\n");
        htmlBuf.append("        </tr>").append("\r\n");
        
        for (int i = minInvNo; i <= maxInvNo; i++) {
        	LiteInventoryBase liteInv = (LiteInventoryBase) hwList.get(i-1);
        	
        	String deviceType = null;
        	String deviceName = null;
        	if (liteInv instanceof LiteStarsLMHardware) {
				deviceType = YukonListFuncs.getYukonListEntry( ((LiteStarsLMHardware)liteInv).getLmHardwareTypeID() ).getEntryText();
				deviceName = ((LiteStarsLMHardware)liteInv).getManufactureSerialNumber();
        	}
        	else if (ECUtils.isMCT( liteInv.getCategoryID() )) {
        		deviceType = getEnergyCompany().getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_METER ).getEntryText();
        		deviceName = PAOFuncs.getYukonPAOName( liteInv.getDeviceID() );
        	}
        	if (deviceName.equals("")) deviceName = "&nbsp;";
        	
        	java.util.Date installDate = ServerUtils.translateDate( liteInv.getInstallDate() );
        	dateFormat.setTimeZone( getEnergyCompany().getDefaultTimeZone() );
        	String instDate = (installDate != null)? dateFormat.format(installDate) : "----";
        	
            htmlBuf.append("        <tr>").append("\r\n");
	        if (getHtmlStyle() == HTML_STYLE_SELECT_INVENTORY) {
	        	htmlBuf.append("          <td class='TableCell' width='5%'>");
	        	htmlBuf.append("<input type='radio' name='InvID' value='").append(liteInv.getInventoryID()).append("'>");
	        	htmlBuf.append("</td>").append("\r\n");
	        }
            htmlBuf.append("          <td class='TableCell' width='17%'>");
			htmlBuf.append("<a href='../Hardware/InventoryDetail.jsp?InvId=").append(liteInv.getInventoryID());
			if (getHtmlStyle() == HTML_STYLE_SELECT_INVENTORY)
				htmlBuf.append("&src=SelectInv");
			else if (getHtmlStyle() == HTML_STYLE_LIST_INVENTORY)
				htmlBuf.append("&src=Inventory");
			else if (getHtmlStyle() == HTML_STYLE_INVENTORY_SET)
				htmlBuf.append("&src=ResultSet");
			htmlBuf.append("'>").append(deviceName).append("</a>");
            htmlBuf.append("</td>").append("\r\n");
            htmlBuf.append("          <td class='TableCell' width='17%'>").append(deviceType).append("</td>").append("\r\n");
            htmlBuf.append("          <td class='TableCell' width='17%'>").append(instDate).append("</td>").append("\r\n");
            htmlBuf.append("          <td class='TableCell' width='49%'>");
            if (liteInv.getAccountID() == 0)
            	htmlBuf.append("Warehouse");
            else {
            	LiteStarsCustAccountInformation liteAcctInfo = getEnergyCompany().getBriefCustAccountInfo( liteInv.getAccountID(), true );
            	LiteContact liteCont = getEnergyCompany().getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
            	LiteAddress liteAddr = getEnergyCompany().getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
            	
            	htmlBuf.append("<a href='' class='Link1' onclick='selectAccount(").append(liteAcctInfo.getAccountID()).append("); return false;'>");
            	htmlBuf.append("Acct # ").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append("</a>");
            	htmlBuf.append(" ").append(ServerUtils.getFormattedName(liteCont));
            	htmlBuf.append(", ").append(ServerUtils.getOneLineAddress(liteAddr));
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
        
        if (getHtmlStyle() == HTML_STYLE_SELECT_INVENTORY) {
        	htmlBuf.append("<br>").append("\r\n");
			htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append("\r\n");
			htmlBuf.append("  <tr>").append("\r\n");
			htmlBuf.append("    <td align='right'>").append("\r\n");
			htmlBuf.append("      <input type='submit' name='Submit' value='Select' onclick='return validate(this.form)'>").append("\r\n");
			htmlBuf.append("    </td>").append("\r\n");
			htmlBuf.append("    <td>").append("\r\n");
			if (referer != null)
				htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='location.href=\"").append(referer).append("\"'>").append("\r\n");
			else
				htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='history.back()'>").append("\r\n");
			htmlBuf.append("    </td>").append("\r\n");
			htmlBuf.append("  </tr>").append("\r\n");
			htmlBuf.append("</table>").append("\r\n");
			htmlBuf.append("</form>").append("\r\n");
        }
        
        if (getHtmlStyle() == HTML_STYLE_INVENTORY_SET) {
			htmlBuf.append("<br>").append("\r\n");
			htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append("\r\n");
			htmlBuf.append("  <tr>").append("\r\n");
			htmlBuf.append("    <td align='center'>").append("\r\n");
			if (referer != null)
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='location.href=\"").append(referer).append("\"'>").append("\r\n");
			else
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='history.back()'>").append("\r\n");
			htmlBuf.append("    </td>").append("\r\n");
			htmlBuf.append("  </tr>").append("\r\n");
			htmlBuf.append("</table>").append("\r\n");
        }
                
        htmlBuf.append("<form name='cusForm' method='post' action='").append(req.getContextPath()).append("/servlet/SOAPClient'>").append("\r\n");
        htmlBuf.append("  <input type='hidden' name='action' value='GetCustAccount'>").append("\r\n");
        htmlBuf.append("  <input type='hidden' name='AccountID' value=''>").append("\r\n");
        htmlBuf.append("  <input type='hidden' name='REDIRECT' value='").append(req.getContextPath()).append("/operator/Consumer/Update.jsp'>").append("\r\n");
        htmlBuf.append("  <input type='hidden' name='REFERRER' value='").append(req.getRequestURI()).append("'>").append("\r\n");
        htmlBuf.append("</form>").append("\r\n");
        
        htmlBuf.append("<script language='JavaScript'>").append("\r\n");
		htmlBuf.append("function validate(form) {").append("\r\n");
		htmlBuf.append("  var radioBtns = document.getElementsByName('InvID');").append("\r\n");
		htmlBuf.append("  if (radioBtns != null) {").append("\r\n");
		htmlBuf.append("    for (i = 0; i < radioBtns.length; i++)").append("\r\n");
		htmlBuf.append("      if (radioBtns[i].checked) return true;").append("\r\n");
		htmlBuf.append("  }").append("\r\n");
		htmlBuf.append("  return false;").append("\r\n");
		htmlBuf.append("}").append("\r\n");
        htmlBuf.append("function selectAccount(accountID) {").append("\r\n");
        htmlBuf.append("  var form = document.cusForm;").append("\r\n");
        htmlBuf.append("  form.AccountID.value = accountID;").append("\r\n");
        htmlBuf.append("  form.submit();").append("\r\n");
        htmlBuf.append("}").append("\r\n");
        htmlBuf.append("</script>").append("\r\n");
        
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
		inventoryList = null;
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

	/**
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param i
	 */
	public void setPageSize(int i) {
		pageSize = i;
	}

	/**
	 * @param list
	 */
	public void setInventorySet(ArrayList list) {
		inventorySet = list;
		inventoryList = null;
	}

	/**
	 * @param string
	 */
	public void setReferer(String string) {
		referer = string;
	}

}
