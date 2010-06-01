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
import com.cannontech.database.data.pao.PAOGroups;
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
	
	public static final String INVENTORY_SQL_ERROR_FUNCTION = "SQL error occurred, you may not use this function if database has non-numeric serial numbers";
    public static final String INVENTORY_SQL_ERROR_FILTER = "SQL error occurred, you may not use Serial number filters if database has non-numeric serial numbers";
    
    private final SimpleCollectionFactory simpleCollectionFactory = 
        YukonSpringHook.getBean("simpleCollectionFactory", SimpleCollectionFactory.class);
    
	private int sortBy = YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO;
	private int sortOrder = SORT_ORDER_ASCENDING;
	
	private int searchBy = CtiUtilities.NONE_ZERO_ID;
	private String searchValue = null;
	
	private int energyCompanyID = 0;
	private int htmlStyle = HTML_STYLE_LIST_INVENTORY;
	private List<LiteInventoryBase> inventorySet = null;
	
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
            return Arrays.asList(getEnergyCompany().getEnergyCompanyID());
        
        List<LiteStarsEnergyCompany> memberList = getMembersFromFilterList();
        List<LiteStarsEnergyCompany> ecList = (!memberList.isEmpty()) ?
                memberList : ECUtils.getAllDescendants( getEnergyCompany());

        List<Integer> idList = new ArrayList<Integer>(ecList.size());
        for (final LiteStarsEnergyCompany energyCompany : ecList) {
            idList.add(energyCompany.getEnergyCompanyID());
        }
        
        return idList;
    }
    
    private DirectionAwareOrderBy getDirectionAwareOrderBy() {
        boolean isAscending = getSortOrder() == SORT_ORDER_ASCENDING;
        OrderBy orderBy = InventoryOrderBy.valueOf(getSortBy());
        return new DirectionAwareOrderBy(orderBy, isAscending);
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

	public int getSortBy() {
		return sortBy;
	}

    public List<FilterWrapper> getFilterByList() 
    {
        return filterByList;
    }
    
	public int getSearchBy() {
        return searchBy;
    }
	
	public String getSearchValue() {
        return searchValue;
    }

	/**
	 * Returns the sortOrder.
	 * @return int
	 */
	public int getSortOrder() {
		return sortOrder;
	}

	/**
	 * Returns the htmlStyle.
	 * @return int
	 */
	public int getHtmlStyle() {
		return htmlStyle;
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
	
	public void setFilterByList(final List<FilterWrapper> filterByList) {
        if (filterByList == null) throw new IllegalArgumentException("filterByList cannot be null.");
        this.filterByList = filterByList;
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
}
