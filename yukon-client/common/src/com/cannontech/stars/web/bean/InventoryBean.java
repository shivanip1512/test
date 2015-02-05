package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.filter.DirectionAwareOrderBy;
import com.cannontech.stars.util.filter.OrderBy;
import com.cannontech.stars.util.filter.filterBy.inventory.InventoryOrderBy;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.collection.SimpleCollection;
import com.cannontech.stars.web.collection.SimpleCollectionFactory;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

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

    public static final String INVENTORY_SQL_ERROR_FUNCTION = "SQL error occurred, you may not use this function if database has non-numeric serial numbers";
    public static final String INVENTORY_SQL_ERROR_FILTER = "SQL error occurred, you may not use Serial number filters if database has non-numeric serial numbers";

    private int numberOfRecords = 0;
    private boolean viewResults = false;
    private boolean overHardwareDisplayLimit = false;
    private boolean checkInvenForAccount = false;

    private final SimpleCollectionFactory simpleCollectionFactory = YukonSpringHook.getBean("simpleCollectionFactory", SimpleCollectionFactory.class);

    private int sortBy = YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO;
    private int sortOrder = SORT_ORDER_ASCENDING;

    private int searchBy = CtiUtilities.NONE_ZERO_ID;
    private String searchValue = null;

    private int page = 1;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int energyCompanyID = 0;
    private int htmlStyle = HTML_STYLE_LIST_INVENTORY;
    private List<LiteInventoryBase> inventorySet = null;
    private String action = null;

    private LiteStarsEnergyCompany energyCompany = null;
    private List<FilterWrapper> filterByList = null;

    public final static ImmutableMap<Integer, Boolean> unsupportedDeviceTypes;
    static {
        Builder<Integer, Boolean> builder = ImmutableMap.builder();
        builder.put(HardwareType.NON_YUKON_METER.getDefinitionId(), true);
        builder.put(HardwareType.DIGI_GATEWAY.getDefinitionId(), true);
        builder.put(HardwareType.UTILITY_PRO_ZIGBEE.getDefinitionId(), true);
        builder.put(HardwareType.LCR_6200_ZIGBEE.getDefinitionId(), true);
        builder.put(HardwareType.LCR_6600_ZIGBEE.getDefinitionId(), true);
        unsupportedDeviceTypes = builder.build();
    }

    public Map<Integer, Boolean> getUnsupportedDeviceTypes() {
        return unsupportedDeviceTypes;
    }

    public InventoryBean() {
    }

    private LiteStarsEnergyCompany getEnergyCompany() {
        if (energyCompany == null || energyCompany.getLiteID() != energyCompanyID)
            energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyID);
        return energyCompany;
    }

    private List<LiteStarsEnergyCompany> getMembersFromFilterList() {
        final List<LiteStarsEnergyCompany> companyList = new ArrayList<LiteStarsEnergyCompany>();
        List<FilterWrapper> filterList = getFilterByList();
        for (final FilterWrapper filter : filterList) {
            int filterType = Integer.parseInt(filter.getFilterTypeID());
            if (filterType == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_MEMBER) {
                Integer id = Integer.parseInt(filter.getFilterID());
                companyList.add(StarsDatabaseCache.getInstance().getEnergyCompany(id));
            }
        }
        return companyList;
    }

    private List<Integer> getEnergyCompanyIdList(boolean showEnergyCompany) {
        if (!showEnergyCompany) {
            return Arrays.asList(getEnergyCompany().getEnergyCompanyId());
        }

        List<LiteStarsEnergyCompany> memberList = getMembersFromFilterList();
        List<LiteStarsEnergyCompany> ecList = (!memberList.isEmpty()) ? memberList
                : ECUtils.getAllDescendants(getEnergyCompany());

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

    private SimpleCollection<LiteInventoryBase> getSimpleCollection(
            boolean showEnergyCompany) throws WebClientException {

        boolean isSet = ((getHtmlStyle() & HTML_STYLE_INVENTORY_SET) != 0 && inventorySet != null);
        if (isSet) {
            return simpleCollectionFactory.createInventorySeachCollection(inventorySet);
        }

        boolean isSearch = ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0) && getSearchBy() != CtiUtilities.NONE_ZERO_ID;
        if (isSearch) {
            setInventorySet(InventoryManagerUtil.searchInventory(getEnergyCompany(), getSearchBy(),
                                                                 getSearchValue(), showEnergyCompany));
            return simpleCollectionFactory.createInventorySeachCollection(inventorySet);
        }

        return simpleCollectionFactory.createInventoryFilterCollection(getEnergyCompanyIdList(showEnergyCompany),
                                                                       getFilterByList(), getDirectionAwareOrderBy());
    }

    private boolean isShowEnergyCompany(HttpServletRequest request) {
        StarsYukonUser user = (StarsYukonUser) request.getSession(false).getAttribute(ServletUtils.ATT_STARS_YUKON_USER);

        boolean showEnergyCompany = false;
        boolean manageMembers = YukonSpringHook.getBean(RolePropertyDao.class).checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS,
                                                              user.getYukonUser());
        boolean hasChildren = getEnergyCompany().hasChildEnergyCompanies();

        int style = getHtmlStyle();
        if ((style == HTML_STYLE_FILTERED_INVENTORY_SUMMARY || (style & HTML_STYLE_LIST_INVENTORY) != 0 || 
                (style & HTML_STYLE_SELECT_LM_HARDWARE) != 0 || (style & HTML_STYLE_FILTERED_INVENTORY_SUMMARY) != 0 || 
                (style & HTML_STYLE_SELECT_INVENTORY) != 0) && manageMembers && hasChildren) {

            showEnergyCompany = true;
        } else if ((style & HTML_STYLE_INVENTORY_SET) != 0) {
            showEnergyCompany = manageMembers;
        }

        return showEnergyCompany;
    }

    public int getPage() {
        return page;
    }

    public int getSortBy() {
        return sortBy;
    }

    public List<FilterWrapper> getFilterByList() {
        return filterByList;
    }

    public void setFilterByList(final List<FilterWrapper> filterByList) {
        if (filterByList == null)
            throw new IllegalArgumentException("filterByList cannot be null.");
        this.filterByList = filterByList;
    }

    public void setPage(int page) {
        this.page = page;
    }

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

    public void setEnergyCompanyID(int energyCompanyID) {
        this.energyCompanyID = energyCompanyID;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getHtmlStyle() {
        return htmlStyle;
    }

    public void setHtmlStyle(int htmlStyle) {
        this.htmlStyle = htmlStyle;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int i) {
        pageSize = i;
    }

    public void setInventorySet(final List<?> list) {
        if (list == null) {
            inventorySet = null;
            return;
        }

        List<LiteInventoryBase> cleanList = new ArrayList<LiteInventoryBase>(Pair.removePair(list, LiteInventoryBase.class));
        inventorySet = cleanList;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String string) {
        action = string;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public String getNumberOfRecordsSelected() {
        return Integer.toString(inventorySet == null ? 0 : inventorySet.size());
    }

    public boolean getViewResults() {
        return viewResults;
    }

    public void setViewResults(boolean truth) {
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
        } catch (PersistenceException e) {
            throw new PersistenceException(INVENTORY_SQL_ERROR_FUNCTION, e);
        }
        return list;
    }

    public List<LiteInventoryBase> getSelectedInventoryList() {
        return inventorySet;
    }

    public boolean isOverHardwareDisplayLimit() {
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