package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.filter.DirectionAwareOrderBy;
import com.cannontech.stars.util.filter.OrderBy;
import com.cannontech.stars.util.filter.filterby.inventory.InventoryOrderBy;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.collection.SimpleCollection;
import com.cannontech.stars.web.collection.SimpleCollectionFactory;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StreetAddress;

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
	public static final String INVENTORY_SQL_ERROR_FUNCTION = "SQL error occurred, you may not use this function if database has non-numeric serial numbers";
    public static final String INVENTORY_SQL_ERROR_FILTER = "SQL error occurred, you may not use Serial number filters if database has non-numeric serial numbers";
    
    private HttpServletRequest internalRequest;
    private int numberOfRecords = 0;
    private boolean viewResults = false;
    private boolean overHardwareDisplayLimit = false;
    private boolean checkInvenForAccount = false;
    
    private final StarsCustAccountInformationDao starsCustAccountInformationDao = 
        YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
    
    private final SimpleCollectionFactory simpleCollectionFactory = 
        YukonSpringHook.getBean("simpleCollectionFactory", SimpleCollectionFactory.class);
    
	private int sortBy = YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO;
	private int sortOrder = SORT_ORDER_ASCENDING;
	
	private int searchBy = CtiUtilities.NONE_ZERO_ID;
	private String searchValue = null;
	
	private int page = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int energyCompanyID = 0;
	private int htmlStyle = HTML_STYLE_LIST_INVENTORY;
	private String referer = null;
	private List<LiteInventoryBase> inventorySet = null;
	private String action = null;
	
	private LiteStarsEnergyCompany energyCompany = null;
    private List<FilterWrapper> filterByList = null;
	
	public InventoryBean() {
	}
	
	private LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null || energyCompany.getLiteID() != energyCompanyID)
			energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
		return energyCompany;
	}
    
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
    
    private List<Integer> getEnergyCompanyIdList(boolean showEnergyCompany) {
        if (!showEnergyCompany) 
            return Arrays.asList(getEnergyCompany().getEnergyCompanyId());
        
        List<LiteStarsEnergyCompany> memberList = getMembersFromFilterList();
        List<LiteStarsEnergyCompany> ecList = (!memberList.isEmpty()) ?
                memberList : ECUtils.getAllDescendants( getEnergyCompany());

        List<Integer> idList = new ArrayList<Integer>(ecList.size());
        for (final LiteStarsEnergyCompany energyCompany : ecList) {
            idList.add(energyCompany.getEnergyCompanyId());
        }
        
        return idList;
    }
    
    private DirectionAwareOrderBy getDirectionAwareOrderBy() {
        boolean isAscending = getSortOrder() == SORT_ORDER_ASCENDING;
        OrderBy orderBy = InventoryOrderBy.valueOf(getSortBy());
        return new DirectionAwareOrderBy(orderBy, isAscending);
    }
	
	public void resetInventoryList() {
		inventorySet = null;
	}
	
	private SimpleCollection<LiteInventoryBase> getSimpleCollection(boolean showEnergyCompany) 
	    throws WebClientException {
	    
	    boolean isSet = ((getHtmlStyle() & HTML_STYLE_INVENTORY_SET) != 0 && inventorySet != null);
	    if (isSet) {
	        return simpleCollectionFactory.createInventorySeachCollection(inventorySet);
	    }
	    
	    boolean isSearch = ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0) && getSearchBy() != CtiUtilities.NONE_ZERO_ID;
	    if (isSearch) {
	        setInventorySet(InventoryManagerUtil.searchInventory(getEnergyCompany(), getSearchBy(), getSearchValue(), showEnergyCompany));
	        return simpleCollectionFactory.createInventorySeachCollection(inventorySet);	        
	    }
	    
	    return simpleCollectionFactory.createInventoryFilterCollection(getEnergyCompanyIdList(showEnergyCompany),
                                                                        getFilterByList(),
                                                                        getDirectionAwareOrderBy());
	}
	
	private boolean isShowEnergyCompany(HttpServletRequest request) {
	    StarsYukonUser user = (StarsYukonUser) request.getSession(false).getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
	    
	    boolean showEnergyCompany = false;
        boolean manageMembers = DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS);
        boolean hasChildren = getEnergyCompany().hasChildEnergyCompanies();
        
        int style = getHtmlStyle();
        if ((style == HTML_STYLE_FILTERED_INVENTORY_SUMMARY
                || (style & HTML_STYLE_LIST_INVENTORY) != 0
                || (style & HTML_STYLE_SELECT_LM_HARDWARE) != 0
                || (style & HTML_STYLE_FILTERED_INVENTORY_SUMMARY) != 0 
                || (style & HTML_STYLE_SELECT_INVENTORY) != 0)
                && manageMembers && hasChildren) {

            showEnergyCompany = true;
        } else if ((style & HTML_STYLE_INVENTORY_SET) != 0) {
                showEnergyCompany = manageMembers;
        }
        
        return showEnergyCompany;
	}
	
	public String getHTML(HttpServletRequest req) throws WebClientException {
		StarsYukonUser user = (StarsYukonUser) req.getSession().getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        
		boolean showEnergyCompany = isShowEnergyCompany(req);
		boolean manageMembers = DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS);

		int style = getHtmlStyle();
		
		SimpleCollection<LiteInventoryBase> simpleCollection = getSimpleCollection(showEnergyCompany);
		
		String errorMsg = null;
        int numberOfHardware = 0;
        try {
            numberOfHardware = simpleCollection.getCount();
        // In case of SQL non-numeric serial number errors
        } catch (PersistenceException e){
            errorMsg = INVENTORY_SQL_ERROR_FILTER;
        }
        
        numberOfRecords = numberOfHardware;
        if (numberOfHardware == 0 && errorMsg == null) {
            errorMsg = "No matching hardware records found";
        }

        StringBuilder htmlBuf = new StringBuilder();
        
        if ((style & HTML_STYLE_SELECT_INVENTORY) != 0 || 
            (style & HTML_STYLE_SELECT_LM_HARDWARE) != 0) {

            htmlBuf.append("<table width='80%' border='0' cellspacing='0' cellpadding='1'>");
            htmlBuf.append("    <tr>");
            htmlBuf.append("        <td class='MainText' align='center'>Check the radio button "); 
            htmlBuf.append("            of the hardware you want to select, then click Select.</td>");
            htmlBuf.append("    </tr>");
            htmlBuf.append("</table>");

        }
        
        if(style == HTML_STYLE_FILTERED_INVENTORY_SUMMARY) {
            String hwRecordsMsg = errorMsg;
            if (errorMsg == null) {
                hwRecordsMsg = Integer.toString(numberOfHardware) + " hardware records found";
            }
            return hwRecordsMsg;
        }

        		
		if (errorMsg != null) {
			htmlBuf.append("<p class='ErrorMsg'>").append(errorMsg).append("</p>").append(LINE_SEPARATOR);
			if ((style & HTML_STYLE_LIST_INVENTORY) == 0) {
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
		if ((style & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (style & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
		{
			srcStr = "&src=SelectInv";
		}
		else if ((style & HTML_STYLE_LIST_INVENTORY) != 0) {
			srcStr = "&src=Inventory";
		}
		else if (style == HTML_STYLE_INVENTORY_SET) {
			srcStr = "&src=ResultSet";
		}
		
		if (page < 1) page = 1;
		int maxPageNo = (int) Math.ceil(numberOfHardware * 1.0 / pageSize);
		if (page > maxPageNo) page = maxPageNo;
		
		int maxPageDigit = (int)(Math.log(maxPageNo) / Math.log(10)) + 1;
		
		// this minInvNo, maxInvNo displays as page navigation		
		int minInvNo = (page - 1) * pageSize + 1;
		int maxInvNo = Math.min(page * pageSize, numberOfHardware);
        
		StringBuilder navBuf = new StringBuilder();
		navBuf.append(minInvNo);
		if (maxInvNo > minInvNo)
			navBuf.append("-").append(maxInvNo);
		navBuf.append(" of ").append(numberOfHardware);
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
		if ((style & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (style & HTML_STYLE_SELECT_LM_HARDWARE) != 0
            || (style & HTML_STYLE_LIST_INVENTORY) != 0)
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
        
		Map<Integer, LiteStarsEnergyCompany> ecMap = 
			StarsDatabaseCache.getInstance().getAllEnergyCompanyMap();
		
		// these need to be zero-based indexes to correctly work with both types of Filters, i.e.,
		// SearchBasedSimpleCollection and FilterBasedSimpleCollection
		int fromIndex = minInvNo - 1;
		int toIndex = maxInvNo;
		
		List<LiteInventoryBase> hardwareList = simpleCollection.getList(fromIndex, toIndex);
		
		for (final LiteInventoryBase liteInv : hardwareList) {
			LiteStarsEnergyCompany member = null;
			boolean isManagable = false;
			
			if (showEnergyCompany) {
				member = ecMap.get(liteInv.getEnergyCompanyId());
				isManagable = manageMembers && ECUtils.isDescendantOf( member, getEnergyCompany() );
			}
			else {
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
				deviceType = litePao.getPaoType().getDbString();
				deviceName = litePao.getPaoName();
			}
			else if (InventoryUtils.isMCT( liteInv.getCategoryID() )) {
				deviceType = getEnergyCompany().getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT).getEntryText();
				if (liteInv.getDeviceLabel() != null && liteInv.getDeviceLabel().length() > 0)
					deviceName = liteInv.getDeviceLabel();
			}
        	
			String currentDeviceState = DaoFactory.getYukonListDao().getYukonListEntry(liteInv.getCurrentStateID()).getEntryText();
            
			htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
            
			if ((style & HTML_STYLE_SELECT_INVENTORY) != 0
				|| (style & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
			{
				htmlBuf.append("          <td class='TableCell' width='1%'>");
				htmlBuf.append("<input type='radio' name='InvID' onclick='selectInventory(").append(liteInv.getInventoryID()).append(",").append(member.getLiteID()).append(")'>");
				htmlBuf.append("</td>").append(LINE_SEPARATOR);
			}
            else if ((style & HTML_STYLE_LIST_INVENTORY) != 0)
            {
                htmlBuf.append("          <td class='TableCell' width='1%'>");
                htmlBuf.append("<input type='checkbox' name='checkMultiInven' value='").append(liteInv.getInventoryID()).append("'>");
                htmlBuf.append("</td>").append(LINE_SEPARATOR);
            }
	        
			htmlBuf.append("          <td class='TableCell' width='17%'>");
			if (liteInv.getAccountID() == 0) {
			    htmlBuf.append("<a href='").append(req.getContextPath()).append("/operator/Hardware/InventoryDetail.jsp?InvId=").append(liteInv.getInventoryID()).append(srcStr).append("'>").append(deviceName).append("</a>");
			} else if (!showEnergyCompany || member.equals(energyCompany) || isManagable) {
			    htmlBuf.append("<a href='").append(req.getContextPath()).append("/spring/stars/operator/hardware/hardwareEdit?accountId=").append(liteInv.getAccountID()).append("&inventoryId=").append(liteInv.getInventoryID()).append("'>").append(deviceName).append("</a>");
			} else {
				htmlBuf.append(deviceName);
			}
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
				LiteStarsCustAccountInformation liteAcctInfo = 
				    starsCustAccountInformationDao.getById(liteInv.getAccountID(), member.getEnergyCompanyId());
				LiteContact liteCont = DaoFactory.getContactDao().getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
				LiteAddress liteAddr = member.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
            	
				String name = StarsUtils.formatName( liteCont );
				StreetAddress starsAddr = new StreetAddress();
				StarsLiteFactory.setStarsCustomerAddress( starsAddr, liteAddr );
				String address = ServletUtils.getOneLineAddress( starsAddr );
            	
            	if (!showEnergyCompany || member.equals(energyCompany) || isManagable) {
            	        htmlBuf.append("<a href='/spring/stars/operator/account/view?accountId=").append(liteAcctInfo.getAccountID()).append("' class='Link1'>")
                        .append("Acct # ").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append("</a>");
            	}
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
        
        
        if ((style & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (style & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
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
        else if((style & HTML_STYLE_LIST_INVENTORY) != 0)
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
        
		if (style == HTML_STYLE_INVENTORY_SET) {
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

        if((style & HTML_STYLE_LIST_INVENTORY) != 0)
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
    
    public void setFilterByList(final List<FilterWrapper> filterByList) {
        if (filterByList == null) throw new IllegalArgumentException("filterByList cannot be null.");
        this.filterByList = filterByList;
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

	public void setSearchBy(int searchBy) {
        this.searchBy = searchBy;
    }
	
	public int getSearchBy() {
        return searchBy;
    }
	
	public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
	
	public String getSearchValue() {
        return searchValue;
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

	@SuppressWarnings("unchecked")
	public void setInventorySet(final List list) {
	    if (list == null) {
	        inventorySet = null;
	        return;
	    }
	    
	    List<LiteInventoryBase> cleanList = 
	        new ArrayList<LiteInventoryBase>(Pair.removePair(list, LiteInventoryBase.class));
		inventorySet = cleanList;
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
    public String getFilterInventoryHTML() throws WebClientException {
        setHtmlStyle(HTML_STYLE_FILTERED_INVENTORY_SUMMARY);
        
        setFilterByList((List) internalRequest.getSession().getAttribute(ServletUtils.FILTER_INVEN_LIST));
        String hwRecordsMessage = getHTML(internalRequest);
        setHtmlStyle(HTML_STYLE_LIST_INVENTORY);
        return hwRecordsMessage;
    }
    
    public int getNumberOfRecords()
    {
        return numberOfRecords;
    }
    
    public String getNumberOfRecordsSelected() {
        return Integer.toString(inventorySet == null ? 0 : inventorySet.size());
    }
    
    public void setInternalRequest(HttpServletRequest req)
    {
        internalRequest = req;
    }

    public boolean getViewResults()
    {
        return viewResults;
    }
    
    public void setViewResults(boolean truth)
    {
        viewResults = truth;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public void setInventoryList(final List<LiteInventoryBase> inventoryList) {
        setInventorySet(inventoryList);
    }
    
    public List<LiteInventoryBase> getInventoryList(HttpServletRequest request) 
        throws WebClientException, PersistenceException {
        
        boolean showEnergyCompany = isShowEnergyCompany(request);
        SimpleCollection<LiteInventoryBase> simpleCollection = getSimpleCollection(showEnergyCompany);
        
        List<LiteInventoryBase> list;
        try {
            list = simpleCollection.getList();
        // In case of SQL non-numeric serial number errors
        } catch (PersistenceException e){
            throw new PersistenceException(INVENTORY_SQL_ERROR_FUNCTION, e);
        }
        return list;
    }
    
    public List<LiteInventoryBase> getSelectedInventoryList() {
    	return inventorySet;
    }

    public boolean isOverHardwareDisplayLimit() 
    {
        overHardwareDisplayLimit = numberOfRecords > MAX_ALLOW_DISPLAY;
        return overHardwareDisplayLimit;
    }

    public boolean isCheckInvenForAccount() {
        return checkInvenForAccount;
    }

    public void setCheckInvenForAccount(boolean checkInvenForAccount) {
        this.checkInvenForAccount = checkInvenForAccount;
    }
}
