package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.util.ServletUtil;

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
	
	public static final int HTML_STYLE_LIST_INVENTORY = 1;
	public static final int HTML_STYLE_SELECT_INVENTORY = 2;
	public static final int HTML_STYLE_SELECT_LM_HARDWARE = 4;
	public static final int HTML_STYLE_INVENTORY_SET = 8;
	
	private static final int DEFAULT_PAGE_SIZE = 20;
	
	private static final java.text.SimpleDateFormat dateFormat =
			new java.text.SimpleDateFormat("MM/dd/yyyy");
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static final Comparator INV_ID_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteInventoryBase inv1 = (LiteInventoryBase)
					((o1 instanceof Pair)? ((Pair)o1).getFirst() :  o1);
			LiteInventoryBase inv2 = (LiteInventoryBase)
					((o2 instanceof Pair)? ((Pair)o2).getFirst() :  o2);
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
			LiteInventoryBase inv1 = (LiteInventoryBase)
					((o1 instanceof Pair)? ((Pair)o1).getFirst() :  o1);
			LiteInventoryBase inv2 = (LiteInventoryBase)
					((o2 instanceof Pair)? ((Pair)o2).getFirst() :  o2);
			int result = 0;
			
			if ((inv1 instanceof LiteStarsLMHardware) && (inv2 instanceof LiteStarsLMHardware)) {
				LiteStarsLMHardware hw1 = (LiteStarsLMHardware) inv1;
				LiteStarsLMHardware hw2 = (LiteStarsLMHardware) inv2;
				
				Long sn1 = null;
				try {
					sn1 = Long.valueOf( hw1.getManufacturerSerialNumber() );
				}
				catch (NumberFormatException e) {}
				
				Long sn2 = null;
				try {
					sn2 = Long.valueOf( hw2.getManufacturerSerialNumber() );
				}
				catch (NumberFormatException e) {}
				
				if (sn1 != null && sn2 != null) {
					result = sn1.compareTo( sn2 );
					if (result == 0) result = hw1.getManufacturerSerialNumber().compareTo( hw2.getManufacturerSerialNumber() );
				}
				else if (sn1 != null && sn2 == null)
					return -1;
				else if (sn1 == null && sn2 != null)
					return 1;
				else
					result = hw1.getManufacturerSerialNumber().compareTo( hw2.getManufacturerSerialNumber() );
			}
			else if ((inv1 instanceof LiteStarsLMHardware) && !(inv2 instanceof LiteStarsLMHardware))
				return -1;
			else if (!(inv1 instanceof LiteStarsLMHardware) && (inv2 instanceof LiteStarsLMHardware))
				return 1;
			else {
				String devName1 = null;
				if (inv1.getDeviceID() > 0)
					devName1 = PAOFuncs.getYukonPAOName( inv1.getDeviceID() );
				else if (inv1.getDeviceLabel() != null && inv1.getDeviceLabel().length() > 0)
					devName1 = inv1.getDeviceLabel();
				
				String devName2 = null;
				if (inv2.getDeviceID() > 0)
					devName2 = PAOFuncs.getYukonPAOName( inv2.getDeviceID() );
				else if (inv2.getDeviceLabel() != null && inv2.getDeviceLabel().length() > 0)
					devName2 = inv2.getDeviceLabel();
				
				if (devName1 != null && devName2 != null)
					result = devName1.compareTo( devName2 );
				else if (devName1 != null && devName2 == null)
					return -1;
				else if (devName1 == null && devName2 != null)
					return 1;
				else
					return -1;
			}
			
			if (result == 0) result = inv1.getInventoryID() - inv2.getInventoryID();
			return result;
		}
	};
	
	private static final Comparator INST_DATE_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteInventoryBase inv1 = (LiteInventoryBase)
					((o1 instanceof Pair)? ((Pair)o1).getFirst() :  o1);
			LiteInventoryBase inv2 = (LiteInventoryBase)
					((o2 instanceof Pair)? ((Pair)o2).getFirst() :  o2);
			
			int result = new java.util.Date(inv1.getInstallDate()).compareTo( new java.util.Date(inv2.getInstallDate()) );
			if (result == 0)
				result = inv1.getInventoryID() - inv2.getInventoryID();
			
			return result;
		}
	};
	
	private int sortBy = CtiUtilities.NONE_ID;
	private int sortOrder = SORT_ORDER_ASCENDING;
	private int filterBy = CtiUtilities.NONE_ID;
	private int deviceType = CtiUtilities.NONE_ID;
	private int serviceCompany = CtiUtilities.NONE_ID;
	private int location = INV_LOCATION_WAREHOUSE;
	private int addressingGroup = CtiUtilities.NONE_ID;
	private int deviceStatus = CtiUtilities.NONE_ID;
	private int page = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int energyCompanyID = 0;
	private int htmlStyle = HTML_STYLE_LIST_INVENTORY;
	private String referer = null;
	private ArrayList inventorySet = null;
	private int member = -1;
	private int searchBy = CtiUtilities.NONE_ID;
	private String searchValue = null;
	
	private LiteStarsEnergyCompany energyCompany = null;
	private ArrayList inventoryList = null;
	
	public InventoryBean() {
	}
	
	private LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null || energyCompany.getLiteID() != energyCompanyID)
			energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		return energyCompany;
	}
	
	private ArrayList getHardwareList(boolean showEnergyCompany) {
		if (inventoryList != null) return inventoryList;
		
		ArrayList hardwares = null;
		if ((getHtmlStyle() & HTML_STYLE_INVENTORY_SET) != 0 && inventorySet != null) {
			hardwares = inventorySet;
		}
		else if (showEnergyCompany) {
			if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_ENERGY_COMPANY) {
				LiteStarsEnergyCompany member = SOAPServer.getEnergyCompany( getMember() );
				ArrayList inventory = member.loadAllInventory();
				
				hardwares = new ArrayList();
				for (int i = 0; i < inventory.size(); i++)
					hardwares.add( new Pair(inventory.get(i), member) );
			}
			else {
				ArrayList members = ECUtils.getAllDescendants( getEnergyCompany() );
				hardwares = new ArrayList();
				
				for (int i = 0; i < members.size(); i++) {
					LiteStarsEnergyCompany member = (LiteStarsEnergyCompany) members.get(i);
					ArrayList inventory = member.loadAllInventory();
					for (int j = 0; j < inventory.size(); j++)
						hardwares.add( new Pair(inventory.get(j), member) );
				}
			}
		}
		else {
			hardwares = getEnergyCompany().loadAllInventory();
		}
		
		if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
		{
			if ((getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0) {
				Iterator it = hardwares.iterator();
				while (it.hasNext()) {
					Object invObj = it.next();
					if (invObj instanceof Pair)
						invObj = ((Pair)invObj).getFirst();
					if (!(invObj instanceof LiteStarsLMHardware))
						it.remove();
				}
			}
			
			if (getSearchBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO) {
				inventorySet = new ArrayList();
				for (int i = 0; i < hardwares.size(); i++) {
					Object invObj = hardwares.get(i);
					if (invObj instanceof Pair)
						invObj = ((Pair)invObj).getFirst();
					if (invObj instanceof LiteStarsLMHardware
						&& ((LiteStarsLMHardware)invObj).getManufacturerSerialNumber().startsWith( getSearchValue() ))
						inventorySet.add( hardwares.get(i) );
				}
				
				hardwares = inventorySet;
				setSortBy( YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO );
			}
			else if (getSearchBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_INST_DATE) {
				Date instDate = ServletUtil.parseDateStringLiberally( getSearchValue(), getEnergyCompany().getDefaultTimeZone() );
				
				inventorySet = new ArrayList();
				if (instDate != null) {
					for (int i = 0; i < hardwares.size(); i++) {
						Object invObj = hardwares.get(i);
						if (invObj instanceof Pair)
							invObj = ((Pair)invObj).getFirst();
						if (((LiteInventoryBase)invObj).getInstallDate() > instDate.getTime())
							inventorySet.add( invObj );
					}
				}
				
				hardwares = inventorySet;
				setSortBy( YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_INST_DATE );
			}
		}
		
		java.util.TreeSet sortedInvs = null;
		if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO)
			sortedInvs = new java.util.TreeSet( SERIAL_NO_CMPTOR );
		else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_INST_DATE)
			sortedInvs = new java.util.TreeSet( INST_DATE_CMPTOR );
		else
			sortedInvs = new java.util.TreeSet( INV_ID_CMPTOR );
		
		if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE) {
			int devTypeMCT = getEnergyCompany().getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT ).getEntryID();
			
			for (int i = 0; i < hardwares.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase)
						(showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
				
				if (liteInv instanceof LiteStarsLMHardware &&
					YukonListFuncs.areSameInYukon( ((LiteStarsLMHardware)liteInv).getLmHardwareTypeID(), getDeviceType() )
					|| getDeviceType() == devTypeMCT && ECUtils.isMCT(liteInv.getCategoryID()))
				{
					sortedInvs.add( hardwares.get(i) );
				}
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase)
						(showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
				
				if (liteInv.getInstallationCompanyID() == getServiceCompany())
					sortedInvs.add( hardwares.get(i) );
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase)
						(showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
				
				if (getLocation() == INV_LOCATION_WAREHOUSE && liteInv.getAccountID() == CtiUtilities.NONE_ID
					|| getLocation() == INV_LOCATION_RESIDENCE && liteInv.getAccountID() != CtiUtilities.NONE_ID)
				{
					sortedInvs.add( hardwares.get(i) );
				}
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CONFIG) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase)
						(showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
				
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
					sortedInvs.add( hardwares.get(i) );
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS) {
			for (int i = 0; i < hardwares.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase)
						(showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
				
				if (liteInv.getDeviceStatus() == getDeviceStatus())
					sortedInvs.add( hardwares.get(i) );
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
		StarsYukonUser user = (StarsYukonUser) req.getSession().getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		boolean showEnergyCompany = false;
		if ((getHtmlStyle() & HTML_STYLE_INVENTORY_SET) != 0) {
			if (inventorySet != null && inventorySet.size() > 0 && inventorySet.get(0) instanceof Pair)
				showEnergyCompany = true;
		}
		else if ((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0 || (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0) {
			if (AuthFuncs.checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
				&& (getEnergyCompany().getChildren().size() > 0))
				showEnergyCompany = true;
		}
		
		ArrayList hwList = getHardwareList( showEnergyCompany );
		StringBuffer htmlBuf = new StringBuffer();
		
		if (hwList == null || hwList.size() == 0) {
			htmlBuf.append("<p class='ErrorMsg'>No hardware found.</p>").append(LINE_SEPARATOR);
			if ((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) == 0) {
				htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='0'>").append(LINE_SEPARATOR);
				htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
				htmlBuf.append("    <td align='center'>");
				if (referer != null)
					htmlBuf.append("<input type='button' name='Back' value='Back' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
				else
					htmlBuf.append("<input type='button' name='Back' value='Back' onclick='history.back()'>").append(LINE_SEPARATOR);
				htmlBuf.append("    </td>").append(LINE_SEPARATOR);
				htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
				htmlBuf.append("</table>").append(LINE_SEPARATOR);
			}
			return htmlBuf.toString();
		}
		
		String uri = req.getRequestURI();
		String pageName = uri.substring( uri.lastIndexOf('/') + 1 );
		
		String srcStr = "";
		if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
		{
			srcStr = "&src=SelectInv";
		}
		else if ((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0) {
			srcStr = "&src=Inventory";
		}
		else if (getHtmlStyle() == HTML_STYLE_INVENTORY_SET) {
			srcStr = "&src=ResultSet";
		}
		
		if (page < 1) page = 1;
		int maxPageNo = (int) Math.ceil(hwList.size() * 1.0 / pageSize);
		if (page > maxPageNo) page = maxPageNo;
		
		int maxPageDigit = (int)(Math.log(maxPageNo) / Math.log(10)) + 1;
		
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
		
		htmlBuf.append("<table width='80%' border='0' cellspacing='0' cellpadding='0'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td>").append(navBuf).append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td align='right'>Page(1-").append(maxPageNo).append("): ")
				.append("<input type='text' id='GoPage' style='border:1px solid #666699; font:11px' size='").append(maxPageDigit).append("' value='").append(page).append("'> ")
				.append("<input type='button' style='font:11px; margin-bottom:-1px' value='Go' onclick='location.href=\"").append(pageName).append("?page=\" + document.getElementById(\"GoPage\").value;'>")
				.append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("      </table>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='1' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
		if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
		{
			htmlBuf.append("          <td class='HeaderCell' width='1%'>&nbsp;</td>").append(LINE_SEPARATOR);
		}
		htmlBuf.append("          <td class='HeaderCell' width='17%'>Serial # / Device Name</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell' width='17%'>Device Type</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell' width='15%'>Install Date</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell'>Location</td>").append(LINE_SEPARATOR);
		if (showEnergyCompany)
			htmlBuf.append("          <td class='HeaderCell' width='17%'>Energy Company</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
        
		for (int i = minInvNo; i <= maxInvNo; i++) {
			LiteInventoryBase liteInv = null;
			LiteStarsEnergyCompany member = null;
			boolean isManagable = false;
			
			if (showEnergyCompany) {
				liteInv = (LiteInventoryBase) ((Pair)hwList.get(i-1)).getFirst();
				member = (LiteStarsEnergyCompany) ((Pair)hwList.get(i-1)).getSecond();
				isManagable = AuthFuncs.checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
						&& ECUtils.isDescendantOf( member, energyCompany );
			}
			else {
				liteInv = (LiteInventoryBase) hwList.get(i-1);
				member = getEnergyCompany();
			}
        	
			String deviceType = "(none)";
			String deviceName = "(none)";
			if (liteInv instanceof LiteStarsLMHardware) {
				deviceType = YukonListFuncs.getYukonListEntry( ((LiteStarsLMHardware)liteInv).getLmHardwareTypeID() ).getEntryText();
				deviceName = ((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber();
			}
			else if (liteInv.getDeviceID() > 0) {
				LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( liteInv.getDeviceID() );
				deviceType = PAOGroups.getPAOTypeString( litePao.getType() );
				deviceName = litePao.getPaoName();
			}
			else if (ECUtils.isMCT( liteInv.getCategoryID() )) {
				deviceType = getEnergyCompany().getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT).getEntryText();
				if (liteInv.getDeviceLabel() != null && liteInv.getDeviceLabel().length() > 0)
					deviceName = liteInv.getDeviceLabel();
			}
        	
			Date installDate = ServerUtils.translateDate( liteInv.getInstallDate() );
			dateFormat.setTimeZone( getEnergyCompany().getDefaultTimeZone() );
			String instDate = (installDate != null)? dateFormat.format(installDate) : "----";
			
			htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
            
			if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
				|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
			{
				htmlBuf.append("          <td class='TableCell' width='1%'>");
				htmlBuf.append("<input type='radio' name='InvID' onclick='selectInventory(").append(liteInv.getInventoryID()).append(",").append(member.getLiteID()).append(")'>");
				htmlBuf.append("</td>").append(LINE_SEPARATOR);
			}
	        
			htmlBuf.append("          <td class='TableCell' width='17%'>");
			if (!showEnergyCompany || member.equals(energyCompany))
				htmlBuf.append("<a href='").append(req.getContextPath()).append("/operator/Hardware/InventoryDetail.jsp?InvId=").append(liteInv.getInventoryID()).append(srcStr).append("'>").append(deviceName).append("</a>");
			else if (isManagable)
				htmlBuf.append("<a href='' onclick='selectMemberInventory(").append(liteInv.getInventoryID()).append(",").append(member.getLiteID()).append("); return false;'>").append(deviceName).append("</a>");
			else
				htmlBuf.append(deviceName);
			htmlBuf.append("</td>").append(LINE_SEPARATOR);
            
			htmlBuf.append("          <td class='TableCell' width='17%'>").append(deviceType).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='15%'>").append(instDate).append("</td>").append(LINE_SEPARATOR);
            
			htmlBuf.append("          <td class='TableCell'>");
			if (liteInv.getAccountID() == 0) {
				htmlBuf.append("Warehouse");
			}
			else {
				LiteStarsCustAccountInformation liteAcctInfo = member.getBriefCustAccountInfo( liteInv.getAccountID(), true );
				LiteContact liteCont = member.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
				LiteAddress liteAddr = member.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
            	
				String name = ECUtils.formatName( liteCont );
				StreetAddress starsAddr = new StreetAddress();
				StarsLiteFactory.setStarsCustomerAddress( starsAddr, liteAddr );
				String address = ServletUtils.getOneLineAddress( starsAddr );
            	
            	if (!showEnergyCompany || member.equals(energyCompany))
					htmlBuf.append("<a href='' class='Link1' onclick='selectAccount(").append(liteAcctInfo.getAccountID()).append("); return false;'>")
							.append("Acct # ").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append("</a>");
				else if (isManagable)
					htmlBuf.append("<a href='' class='Link1' onclick='selectMemberAccount(").append(liteAcctInfo.getAccountID()).append(",").append(member.getLiteID()).append("); return false;'>")
							.append("Acct # ").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append("</a>");
				else
					htmlBuf.append("Acct # ").append(liteAcctInfo.getCustomerAccount().getAccountNumber());
				if (name.length() > 0)
					htmlBuf.append(" ").append(name);
				if (address.length() > 0)
					htmlBuf.append(", ").append(address);
			}
			htmlBuf.append("</td>").append(LINE_SEPARATOR);
            
			if (showEnergyCompany)
				htmlBuf.append("          <td class='TableCell' width='17%'>").append(member.getName()).append("</td>").append(LINE_SEPARATOR);
            
			htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		}
        
		htmlBuf.append("      </table>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td>").append(navBuf).append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("      </table>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("</table>").append(LINE_SEPARATOR);
        
		if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
		{
			htmlBuf.append("<br>").append(LINE_SEPARATOR);
			htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
			htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
			htmlBuf.append("    <td align='right'>").append(LINE_SEPARATOR);
			htmlBuf.append("      <input type='button' name='Select' value='Select' onclick='validate()'>").append(LINE_SEPARATOR);
			htmlBuf.append("    </td>").append(LINE_SEPARATOR);
			htmlBuf.append("    <td>").append(LINE_SEPARATOR);
			if (referer != null)
				htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
			else
				htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='history.back()'>").append(LINE_SEPARATOR);
			htmlBuf.append("    </td>").append(LINE_SEPARATOR);
			htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
			htmlBuf.append("</table>").append(LINE_SEPARATOR);
		}
        
		if (getHtmlStyle() == HTML_STYLE_INVENTORY_SET) {
			htmlBuf.append("<br>").append(LINE_SEPARATOR);
			htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
			htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
			htmlBuf.append("    <td align='center'>").append(LINE_SEPARATOR);
			if (referer != null)
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
			else
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='history.back()'>").append(LINE_SEPARATOR);
			htmlBuf.append("    </td>").append(LINE_SEPARATOR);
			htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
			htmlBuf.append("</table>").append(LINE_SEPARATOR);
		}
        
		htmlBuf.append("<form name='InventoryBeanForm' method='post' action='").append(req.getContextPath()).append("/servlet/InventoryManager'>").append(LINE_SEPARATOR);
		if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0)
			htmlBuf.append("  <input type='hidden' name='action' value='SelectInventory'>").append(LINE_SEPARATOR);
		else if ((getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
			htmlBuf.append("  <input type='hidden' name='action' value='SelectLMHardware'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='InvID' value=''>").append(LINE_SEPARATOR);
		if (showEnergyCompany)
			htmlBuf.append("  <input type='hidden' name='MemberID' value=''>").append(LINE_SEPARATOR);
		htmlBuf.append("</form>").append(LINE_SEPARATOR);
        
		htmlBuf.append("<form name='cusForm' method='post' action='").append(req.getContextPath()).append("/servlet/SOAPClient'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='action' value='GetCustAccount'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='AccountID' value=''>").append(LINE_SEPARATOR);
		if (showEnergyCompany)
			htmlBuf.append("  <input type='hidden' name='SwitchContext' value=''>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='REDIRECT' value='").append(req.getContextPath()).append("/operator/Consumer/Update.jsp'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='REFERRER' value='").append(req.getRequestURI()).append("'>").append(LINE_SEPARATOR);
		htmlBuf.append("</form>").append(LINE_SEPARATOR);
        
		if (showEnergyCompany) {
			htmlBuf.append("<form name='invForm' method='post' action='").append(req.getContextPath()).append("/servlet/StarsAdmin'>").append(LINE_SEPARATOR);
			htmlBuf.append("  <input type='hidden' name='action' value='SwitchContext'>").append(LINE_SEPARATOR);
			htmlBuf.append("  <input type='hidden' name='MemberID' value=''>").append(LINE_SEPARATOR);
			htmlBuf.append("  <input type='hidden' name='REDIRECT' value=''>").append(LINE_SEPARATOR);
			htmlBuf.append("</form>").append(LINE_SEPARATOR);
		}
        
		htmlBuf.append("<script language='JavaScript'>").append(LINE_SEPARATOR);
		htmlBuf.append("function goToPage() {").append(LINE_SEPARATOR);
		htmlBuf.append("  location.href='").append(pageName).append("?page=' + document.getElementById('Page').value;").append(LINE_SEPARATOR);
		htmlBuf.append("}").append(LINE_SEPARATOR);
		
		htmlBuf.append("function selectInventory(invID, memberID) {").append(LINE_SEPARATOR);
		htmlBuf.append("  var form = document.InventoryBeanForm;").append(LINE_SEPARATOR);
		htmlBuf.append("  form.InvID.value = invID;").append(LINE_SEPARATOR);
		if (showEnergyCompany)
			htmlBuf.append("  form.MemberID.value = memberID;").append(LINE_SEPARATOR);
		htmlBuf.append("}").append(LINE_SEPARATOR);
		
		htmlBuf.append("function validate() {").append(LINE_SEPARATOR);
		htmlBuf.append("  var radioBtns = document.getElementsByName('InvID');").append(LINE_SEPARATOR);
		htmlBuf.append("  if (radioBtns != null) {").append(LINE_SEPARATOR);
		htmlBuf.append("    for (i = 0; i < radioBtns.length; i++)").append(LINE_SEPARATOR);
		htmlBuf.append("      if (radioBtns[i].checked) document.InventoryBeanForm.submit();").append(LINE_SEPARATOR);
		htmlBuf.append("  }").append(LINE_SEPARATOR);
		htmlBuf.append("}").append(LINE_SEPARATOR);
		
		htmlBuf.append("function selectAccount(accountID) {").append(LINE_SEPARATOR);
		htmlBuf.append("  var form = document.cusForm;").append(LINE_SEPARATOR);
		htmlBuf.append("  form.AccountID.value = accountID;").append(LINE_SEPARATOR);
		htmlBuf.append("  form.submit();").append(LINE_SEPARATOR);
		htmlBuf.append("}").append(LINE_SEPARATOR);
        
		if (showEnergyCompany) {
			htmlBuf.append("function selectMemberAccount(accountID, memberID) {").append(LINE_SEPARATOR);
			htmlBuf.append("  var form = document.cusForm;").append(LINE_SEPARATOR);
			htmlBuf.append("  form.AccountID.value = accountID;").append(LINE_SEPARATOR);
			htmlBuf.append("  form.SwitchContext.value = memberID;").append(LINE_SEPARATOR);
			htmlBuf.append("  form.submit();").append(LINE_SEPARATOR);
			htmlBuf.append("}").append(LINE_SEPARATOR);
			
			htmlBuf.append("function selectMemberInventory(invID, memberID) {").append(LINE_SEPARATOR);
			htmlBuf.append("  var form = document.invForm;").append(LINE_SEPARATOR);
			htmlBuf.append("  form.MemberID.value = memberID;").append(LINE_SEPARATOR);
			htmlBuf.append("  form.REDIRECT.value = '").append(req.getContextPath()).append("/operator/Hardware/InventoryDetail.jsp?InvId='");
			htmlBuf.append(" + invID + '").append(srcStr).append("';").append(LINE_SEPARATOR);
			htmlBuf.append("  form.submit();").append(LINE_SEPARATOR);
			htmlBuf.append("}").append(LINE_SEPARATOR);
		}
		
		htmlBuf.append("</script>").append(LINE_SEPARATOR);
        
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

	/**
	 * @return
	 */
	public int getDeviceStatus() {
		return deviceStatus;
	}

	/**
	 * @param i
	 */
	public void setDeviceStatus(int i) {
		deviceStatus = i;
	}

	/**
	 * @return
	 */
	public int getMember() {
		return member;
	}

	/**
	 * @param i
	 */
	public void setMember(int i) {
		member = i;
	}

	/**
	 * @return
	 */
	public int getSearchBy() {
		return searchBy;
	}

	/**
	 * @return
	 */
	public String getSearchValue() {
		return searchValue;
	}

	/**
	 * @param i
	 */
	public void setSearchBy(int i) {
		if (searchBy != CtiUtilities.NONE_ID || i != CtiUtilities.NONE_ID) {
			searchBy = i;
			inventoryList = null;
			inventorySet = null;
		}
		
		if (searchBy != CtiUtilities.NONE_ID) {
			filterBy = CtiUtilities.NONE_ID;
			htmlStyle |= HTML_STYLE_INVENTORY_SET;
		}
	}

	/**
	 * @param string
	 */
	public void setSearchValue(String string) {
		searchValue = string;
	}

}
