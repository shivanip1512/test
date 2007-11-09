package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.AbstractFilter;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.InventoryFilter;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.web.navigation.CtiNavObject;

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
    public static final int HTML_STYLE_FILTERED_INVENTORY_SUMMARY = 9;
	
	private static final int DEFAULT_PAGE_SIZE = 250;
    
    private final int MAX_ALLOW_DISPLAY = 500;
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static final Comparator<Object> INV_ID_CMPTOR = new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
			LiteInventoryBase inv1 = (LiteInventoryBase)
					((o1 instanceof Pair)? ((Pair)o1).getFirst() :  o1);
			LiteInventoryBase inv2 = (LiteInventoryBase)
					((o2 instanceof Pair)? ((Pair)o2).getFirst() :  o2);
			return inv1.getInventoryID() - inv2.getInventoryID();
		}
	};
    
    private HttpServletRequest internalRequest;
    private String numberOfRecords = "0";
    private boolean viewResults = false;
    private boolean shipmentCheck = false;
    private boolean overHardwareDisplayLimit = false;
	private boolean differentOrigin = true;
    private boolean checkInvenForAccount = false;
    
	/**
	 * Comparator of serial # and device names. Serial # is always "less than"
	 * device name. To compare two serial #s, try to convert them into decimal
	 * values first, compare the decimal values if conversion is successful.
	 */
	private static final Comparator<Object> SERIAL_NO_CMPTOR = new Comparator<Object>() {
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
				{
                    try
                    {
                        devName1 = DaoFactory.getPaoDao().getYukonPAOName( inv1.getDeviceID() );
                    }
                    catch(NotFoundException e) {}
                }
				else if (inv1.getDeviceLabel() != null && inv1.getDeviceLabel().length() > 0)
					devName1 = inv1.getDeviceLabel();
				
				String devName2 = null;
				if (inv2.getDeviceID() > 0)
					devName2 = DaoFactory.getPaoDao().getYukonPAOName( inv2.getDeviceID() );
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
	
	private static final Comparator<Object> INST_DATE_CMPTOR = new Comparator<Object>() {
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
	
	private int sortBy = YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO;
	private int sortOrder = SORT_ORDER_ASCENDING;
	private int page = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int energyCompanyID = 0;
	private int htmlStyle = HTML_STYLE_LIST_INVENTORY;
	private String referer = null;
	private List<Object> inventorySet = null;
	private int searchBy = CtiUtilities.NONE_ZERO_ID;
	private String searchValue = null;
	private String action = null;
	
	private LiteStarsEnergyCompany energyCompany = null;
	private List<Object> inventoryList = null;
    private List<FilterWrapper> filterByList = null;
	
	public InventoryBean() {
	}
	
	private LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null || energyCompany.getLiteID() != energyCompanyID)
			energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
		return energyCompany;
	}
	
    public List<Object> getInventoryList()
    {
        return inventoryList;
    }
    
    @SuppressWarnings("unchecked")
    private List<LiteStarsEnergyCompany> getMembersFromFilterList() {
        final List<LiteStarsEnergyCompany> companyList = new ArrayList<LiteStarsEnergyCompany>();
        List<FilterWrapper> filterList = getFilterByList();
        for (final FilterWrapper filter : filterList) {
            int filterType = Integer.parseInt(filter.getFilterTypeID());
            if(filterType == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_MEMBER) {
                Integer id = Integer.parseInt(filter.getFilterID());
                companyList.add(StarsDatabaseCache.getInstance().getEnergyCompany(id));
            }
        }
        return companyList;
    }
    
	@SuppressWarnings("unchecked")
    private List<Object> getHardwareList(boolean showEnergyCompany) throws WebClientException {
		if (inventoryList != null && !shipmentCheck) return inventoryList;
		
		List<Object> hardwares = new ArrayList<Object>();
		if ((getHtmlStyle() & HTML_STYLE_INVENTORY_SET) != 0 && inventorySet != null) {
			hardwares = inventorySet;
		}
		else if (showEnergyCompany) {
		    List<LiteStarsEnergyCompany> memberList = getMembersFromFilterList();
		    if (memberList.size() > 0) {
		        for (final LiteStarsEnergyCompany company : memberList) {
		            List<LiteInventoryBase> invList = company.loadAllInventory(true);
		            for (LiteInventoryBase inv : invList) {
		                hardwares.add(new Pair(inv, company));
		            }
		        }
			} else if (getSearchBy() == 0) {
                List<LiteStarsEnergyCompany> members = ECUtils.getAllDescendants( getEnergyCompany() );
				hardwares = new ArrayList<Object>();
				
				for (int i = 0; i < members.size(); i++) {
					LiteStarsEnergyCompany member = members.get(i);
                    List<LiteInventoryBase> inventory = member.loadAllInventory( true );
					for (int j = 0; j < inventory.size(); j++)
						hardwares.add( new Pair<Object,LiteStarsEnergyCompany>(inventory.get(j), member) );
				}
			}
			else {
				hardwares = InventoryManagerUtil.searchInventory( getEnergyCompany(), getSearchBy(), getSearchValue(), true );
			}
		}
		else {
			if (getSearchBy() == 0)
				hardwares = (List) getEnergyCompany().loadAllInventory( true );
			else
				hardwares = InventoryManagerUtil.searchInventory( getEnergyCompany(), getSearchBy(), getSearchValue(), false );
		}
		
		if ((getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0) {
			Iterator<Object> it = hardwares.iterator();
			while (it.hasNext()) {
				Object invObj = it.next();
				if (invObj instanceof Pair)
					invObj = ((Pair)invObj).getFirst();
				if (!(invObj instanceof LiteStarsLMHardware))
					it.remove();
			}
		}
		
        /*
         * Now that we have potentially n filters instead of one, we need to iterate through.
         */
        if(getHtmlStyle() != HTML_STYLE_INVENTORY_SET) {
            final AbstractFilter<LiteInventoryBase> inventoryFilter = new InventoryFilter();
            inventoryFilter.setEnergyCompany(energyCompany);
            final List<Object> filteredHardwareList = inventoryFilter.filter(hardwares, getFilterByList());
            hardwares = filteredHardwareList;    
        }
            
        Set<Object> sortedInvs = null;
        if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO)
            sortedInvs = new java.util.TreeSet<Object>( SERIAL_NO_CMPTOR );
        else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_INST_DATE)
            sortedInvs = new java.util.TreeSet<Object>( INST_DATE_CMPTOR );
        else
            sortedInvs = new java.util.TreeSet<Object>( INV_ID_CMPTOR );

        sortedInvs.addAll(hardwares);
        
		inventoryList = new ArrayList<Object>();
		Iterator<Object> it = sortedInvs.iterator();
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
	
	public String getHTML(HttpServletRequest req) 
    {
		StarsYukonUser user = (StarsYukonUser) req.getSession().getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        String previousPage = ((CtiNavObject) req.getSession().getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();        
        setDifferentOrigin(previousPage.lastIndexOf("Inventory.jsp") < 0);
        
		boolean showEnergyCompany = false;
        if((getHtmlStyle() == HTML_STYLE_FILTERED_INVENTORY_SUMMARY) && DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
                && (getEnergyCompany().getChildren().size() > 0))
                showEnergyCompany = true;
        else if ((getHtmlStyle() & HTML_STYLE_INVENTORY_SET) != 0) 
        {
			if (inventorySet != null && inventorySet.size() > 0 && inventorySet.get(0) instanceof Pair)
				showEnergyCompany = true;
		}
		else if ((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0 || 
                (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0 ||
                (getHtmlStyle() & HTML_STYLE_FILTERED_INVENTORY_SUMMARY) != 0 ||
                (getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0) 
        {
			if (DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
				&& (getEnergyCompany().getChildren().size() > 0))
				showEnergyCompany = true;
		}
		
		List<Object> hwList = null;
		String errorMsg = null;
        int numberOfHardware = 0;
		try {
			hwList = getHardwareList( showEnergyCompany );
			if (hwList == null || hwList.size() == 0)
            {
				errorMsg = "No hardware found.";
            }
        }
		catch (WebClientException e) {
			errorMsg = e.getMessage();
		}
		
        StringBuffer htmlBuf = new StringBuffer();
        
        numberOfHardware = hwList.size();
        if(getHtmlStyle() == HTML_STYLE_FILTERED_INVENTORY_SUMMARY)
        {
            return htmlBuf.append(numberOfHardware).toString();
        }
        		
		if (errorMsg != null) {
			htmlBuf.append("<p class='ErrorMsg'>").append(errorMsg).append("</p>").append(LINE_SEPARATOR);
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
				.append("<input type='text' id='GoPage' style='border:1px solid #0066CC; font:11px' size='").append(maxPageDigit).append("' value='").append(page).append("'> ")
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
			|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0
            || (getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0)
		{
			htmlBuf.append("          <td class='HeaderCell' width='1%'>&nbsp;</td>").append(LINE_SEPARATOR);
		}
		htmlBuf.append("          <td class='HeaderCell' width='17%'>Serial # / Device Name</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell' width='17%'>Device Type</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell' width='15%'>Device Status</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell'>Location</td>").append(LINE_SEPARATOR);
		if (showEnergyCompany)
			htmlBuf.append("          <td class='HeaderCell' width='17%'>Member</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
        
        htmlBuf.append("<form name='chooseForm' method='post' action='").append(req.getContextPath()).append("/servlet/InventoryManager'>").append(LINE_SEPARATOR);
		htmlBuf.append("<input type='hidden' name='action' value='ManipulateInventoryResults'>").append(LINE_SEPARATOR);
        for (int i = minInvNo; i <= maxInvNo; i++) {
			LiteInventoryBase liteInv = null;
			LiteStarsEnergyCompany member = null;
			boolean isManagable = false;
			
			if (showEnergyCompany) {
				liteInv = (LiteInventoryBase) ((Pair)hwList.get(i-1)).getFirst();
				member = (LiteStarsEnergyCompany) ((Pair)hwList.get(i-1)).getSecond();
				isManagable = DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
						&& ECUtils.isDescendantOf( member, getEnergyCompany() );
			}
			else {
				liteInv = (LiteInventoryBase) hwList.get(i-1);
				member = getEnergyCompany();
			}
        	
			String deviceType = "(none)";
			String deviceName = "(none)";
			if (liteInv instanceof LiteStarsLMHardware) {
				deviceType = DaoFactory.getYukonListDao().getYukonListEntry( ((LiteStarsLMHardware)liteInv).getLmHardwareTypeID() ).getEntryText();
				deviceName = ((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber();
			}
			else if (liteInv.getDeviceID() > 0) {
				LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO( liteInv.getDeviceID() );
				deviceType = PAOGroups.getPAOTypeString( litePao.getType() );
				deviceName = litePao.getPaoName();
			}
			else if (InventoryUtils.isMCT( liteInv.getCategoryID() )) {
				deviceType = getEnergyCompany().getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT).getEntryText();
				if (liteInv.getDeviceLabel() != null && liteInv.getDeviceLabel().length() > 0)
					deviceName = liteInv.getDeviceLabel();
			}
        	
			String currentDeviceState = DaoFactory.getYukonListDao().getYukonListEntry(liteInv.getCurrentStateID()).getEntryText();
            
            /*Date installDate = StarsUtils.translateDate( liteInv.getInstallDate() );
			dateFormat.setTimeZone( getEnergyCompany().getDefaultTimeZone() );
			String instDate = (installDate != null)? dateFormat.format(installDate) : "----";
			*/
			htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
            
			if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
				|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
			{
				htmlBuf.append("          <td class='TableCell' width='1%'>");
				htmlBuf.append("<input type='radio' name='InvID' onclick='selectInventory(").append(liteInv.getInventoryID()).append(",").append(member.getLiteID()).append(")'>");
				htmlBuf.append("</td>").append(LINE_SEPARATOR);
			}
            else if ((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0)
            {
                htmlBuf.append("          <td class='TableCell' width='1%'>");
                htmlBuf.append("<input type='checkbox' name='checkMultiInven' value='").append(liteInv.getInventoryID()).append("'>");
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
			htmlBuf.append("          <td class='TableCell' width='15%'>").append(currentDeviceState).append("</td>").append(LINE_SEPARATOR);
            
			htmlBuf.append("          <td class='TableCell'>");
			if (liteInv.getAccountID() == 0) 
            {
				String warehouseName = Warehouse.getWarehouseNameFromInventoryID(liteInv.getInventoryID());
                if(warehouseName.length() > 0)
                    htmlBuf.append(warehouseName);
                else
                    htmlBuf.append("General Inventory");
			}
			else {
				LiteStarsCustAccountInformation liteAcctInfo = member.getBriefCustAccountInfo( liteInv.getAccountID(), true );
				LiteContact liteCont = DaoFactory.getContactDao().getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
				LiteAddress liteAddr = member.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
            	
				String name = StarsUtils.formatName( liteCont );
				StreetAddress starsAddr = new StreetAddress();
				StarsLiteFactory.setStarsCustomerAddress( starsAddr, liteAddr );
				String address = ServletUtils.getOneLineAddress( starsAddr );
            	
            	if (!showEnergyCompany || member.equals(energyCompany) || isManagable)
					htmlBuf.append("<a href='' class='Link1' onclick='selectAccount(").append(liteAcctInfo.getAccountID()).append(",").append(member.getLiteID()).append("); return false;'>")
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
        htmlBuf.append("</form>").append(LINE_SEPARATOR);        
        
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
        else if((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0)
        {
            htmlBuf.append("<br>").append(LINE_SEPARATOR);
            htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
            htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
            htmlBuf.append("    <td align='left'>").append(LINE_SEPARATOR);
            htmlBuf.append("      <input type='button' name='CheckAll' value='Check All On Page' onclick='checkAll()'>").append(LINE_SEPARATOR);
            htmlBuf.append("    </td>").append(LINE_SEPARATOR);
            htmlBuf.append("    <td align='left'>").append(LINE_SEPARATOR);
            htmlBuf.append("      <input type='button' name='UncheckAll' value='Uncheck All On Page' onclick='uncheckAll()'>").append(LINE_SEPARATOR);
            htmlBuf.append("    </td>").append(LINE_SEPARATOR);
            htmlBuf.append("    <td align='left'>").append(LINE_SEPARATOR);
            htmlBuf.append("      <input type='button' name='ChooseSelected' value='Manipulate Selected' onclick='manipSelected()'>").append(LINE_SEPARATOR);
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
        htmlBuf.append("  <input type='hidden' name='InvID' value=''>").append(LINE_SEPARATOR);
		if (getAction() != null)
			htmlBuf.append("  <input type='hidden' name='action' value='" + getAction() + "'>").append(LINE_SEPARATOR);
		if (showEnergyCompany)
			htmlBuf.append("  <input type='hidden' name='MemberID' value=''>").append(LINE_SEPARATOR);
		htmlBuf.append("</form>").append(LINE_SEPARATOR);
        
		htmlBuf.append("<form name='cusForm' method='post' action='").append(req.getContextPath()).append("/servlet/SOAPClient'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='action' value='GetCustAccount'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='AccountID' value=''>").append(LINE_SEPARATOR);
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
        htmlBuf.append("  form.action.value = 'InsertInventory';").append(LINE_SEPARATOR);
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
		
		htmlBuf.append("function selectAccount(accountID, memberID) {").append(LINE_SEPARATOR);
		htmlBuf.append("  var form = document.cusForm;").append(LINE_SEPARATOR);
		htmlBuf.append("  form.AccountID.value = accountID;").append(LINE_SEPARATOR);
		htmlBuf.append("  form.SwitchContext.value = memberID;").append(LINE_SEPARATOR);
		htmlBuf.append("  form.submit();").append(LINE_SEPARATOR);
		htmlBuf.append("}").append(LINE_SEPARATOR);
        
        //htmlBuf.append("function changeSelected() {").append(LINE_SEPARATOR);
        //htmlBuf.append("  form.action.value = 'ManipulateInventoryResults';").append(LINE_SEPARATOR);
        //htmlBuf.append("  form.submit();").append(LINE_SEPARATOR);
        //htmlBuf.append("}").append(LINE_SEPARATOR);

        if((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0)
        {
            htmlBuf.append("function checkAll() {").append(LINE_SEPARATOR);
            htmlBuf.append("var checkBoxArray = new Array();").append(LINE_SEPARATOR);
            htmlBuf.append("checkBoxArray = document.chooseForm.checkMultiInven;").append(LINE_SEPARATOR);
            htmlBuf.append("if ( checkBoxArray.length == undefined) {").append(LINE_SEPARATOR);
            htmlBuf.append("  document.chooseForm.checkMultiInven.checked = true ;").append(LINE_SEPARATOR);
            htmlBuf.append("} else {").append(LINE_SEPARATOR);
            htmlBuf.append("  for (i = 0; i < checkBoxArray.length; i++)").append(LINE_SEPARATOR);
            htmlBuf.append("    checkBoxArray[i].checked = true ;").append(LINE_SEPARATOR);
            htmlBuf.append("  }  }").append(LINE_SEPARATOR);
            
            htmlBuf.append("function uncheckAll() {").append(LINE_SEPARATOR);
            htmlBuf.append("var checkBoxArray = new Array();").append(LINE_SEPARATOR);
            htmlBuf.append("checkBoxArray = document.chooseForm.checkMultiInven;").append(LINE_SEPARATOR);
            htmlBuf.append("if ( checkBoxArray.length == undefined) {").append(LINE_SEPARATOR);
            htmlBuf.append("  document.chooseForm.checkMultiInven.checked = false ;").append(LINE_SEPARATOR);
            htmlBuf.append("} else {").append(LINE_SEPARATOR);
            htmlBuf.append("  for (i = 0; i < checkBoxArray.length; i++)").append(LINE_SEPARATOR);
            htmlBuf.append("    checkBoxArray[i].checked = false ;").append(LINE_SEPARATOR);
            htmlBuf.append("  }  }").append(LINE_SEPARATOR);
            
            htmlBuf.append("function manipSelected() {").append(LINE_SEPARATOR);
            htmlBuf.append("    document.chooseForm.action.value = \"ManipulateSelectedResults\";").append(LINE_SEPARATOR);
            htmlBuf.append("    document.chooseForm.submit();").append(LINE_SEPARATOR);
            htmlBuf.append("}").append(LINE_SEPARATOR);
     
        }
        
		if (showEnergyCompany) {
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
	 * Returns the page.
	 * @return int
	 */
	public int getPage() {
		return page;
	}

	public int getSortBy() {
		return sortBy;
	}

    public List<FilterWrapper> getFilterByList() 
    {
        return filterByList;
    }
    
    public void setFilterByList(final List<FilterWrapper> newFilters)
    {
        if(newFilters == null)
            return;
        
        List<FilterWrapper> oldFilters = filterByList;
        filterByList = new ArrayList<FilterWrapper>();
        
        /**
         * Because of the size of Xcel, we need to handle members as the first part of
         * the filter process, regardless of where it is in the list of filters.
         * This improves our best case significantly, and to some degree, also improves
         * the worst case scenario.
         * 
         * Also added to this method: goes through and checks to see whether the stored
         * inventoryList should be rebuilt, ie. did the filters actually change.
         */
        if(oldFilters == null || oldFilters.size() != newFilters.size())
            inventoryList = null;
        
        for(int j = 0; j < newFilters.size(); j++)
        {
            Integer filterType = Integer.valueOf(newFilters.get(j).getFilterTypeID());
            String specificFilterString = newFilters.get(j).getFilterID();
            Integer specificFilterID = InventoryUtils.returnIntegerIfPossible(specificFilterString);

            if(inventoryList != null)
            {
                boolean found = false;
                for(int x = 0; x < oldFilters.size(); x++)
                {
                    if(filterType == new Integer(((FilterWrapper)oldFilters.get(x)).getFilterTypeID())
                            && specificFilterID.intValue() == new Integer(((FilterWrapper)oldFilters.get(x)).getFilterID()).intValue())
                    {
                        found = true;
                    }    
                }
                
                if(!found)
                    inventoryList = null;
            }
        }
        
        /*In order to add OR logic to the filters instead of AND logic we need to first
         * sort the filters by type
         */
        Collections.sort(newFilters, FilterWrapper.filterWrapperComparator);
        filterByList = newFilters;
	}

	/**
	 * Sets the page.
	 * @param page The page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Sets the sortBy.
	 * @param sortBy The sortBy to set
	 */
	public void setSortBy(int sortBy) {
		this.sortBy = sortBy;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(int energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
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
	public void setInventorySet(final List<Object> list) {
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
		if (searchBy != CtiUtilities.NONE_ZERO_ID || i != CtiUtilities.NONE_ZERO_ID) {
			searchBy = i;
			inventoryList = null;
			inventorySet = null;
		}
		
		if (searchBy != CtiUtilities.NONE_ZERO_ID) {
			//filterBy = CtiUtilities.NONE_ZERO_ID;
			htmlStyle |= HTML_STYLE_INVENTORY_SET;
		}
	}

	/**
	 * @param string
	 */
	public void setSearchValue(String string) {
		searchValue = string;
	}

	/**
	 * @return
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param string
	 */
	public void setAction(String string) {
		action = string;
	}
    
    @SuppressWarnings("unchecked")
    public String getFilterInventoryHTML()
    {
        setHtmlStyle(HTML_STYLE_FILTERED_INVENTORY_SUMMARY);
        
        setFilterByList((List) internalRequest.getSession().getAttribute(ServletUtils.FILTER_INVEN_LIST));
        String hardwareNum = getHTML(internalRequest);
        setHtmlStyle(HTML_STYLE_LIST_INVENTORY);
        numberOfRecords = hardwareNum;
        return hardwareNum;
    }
    
    public String getNumberOfRecords()
    {
        return numberOfRecords;
    }
    
    public void setInternalRequest(HttpServletRequest req)
    {
        internalRequest = req;
    }

    public boolean getViewResults()
    {
        if(isDifferentOrigin() && isOverHardwareDisplayLimit())
            return false;
        return viewResults;
    }
    
    public void setViewResults(boolean truth)
    {
        viewResults = truth;
    }

    public void setNumberOfRecords(String numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public void setInventoryList(final List<Object> inventoryList) {
        this.inventoryList = inventoryList;
    }
    
    public List<Object> getLimitedHardwareList()
    {
        try
        {
            return getHardwareList(true);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean isShipmentCheck() 
    {
        return shipmentCheck;
    }

    public void setShipmentCheck(boolean shipmentCheck) 
    {
        this.shipmentCheck = shipmentCheck;
    }

    public boolean isOverHardwareDisplayLimit() 
    {
        overHardwareDisplayLimit = Integer.parseInt(numberOfRecords) > MAX_ALLOW_DISPLAY;
        return overHardwareDisplayLimit;
    }
    
    public boolean isDifferentOrigin() 
    {
        return differentOrigin;
    }

    public void setDifferentOrigin(boolean truth) 
    {
        this.differentOrigin = truth;
    }

    public boolean isCheckInvenForAccount() {
        return checkInvenForAccount;
    }

    public void setCheckInvenForAccount(boolean checkInvenForAccount) {
        this.checkInvenForAccount = checkInvenForAccount;
    }
}
