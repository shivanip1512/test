package com.cannontech.stars.dr.selectionList.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.DisplayableSelectionList;
import com.cannontech.common.constants.SelectionListCategory;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.selectionList.dao.SelectionListDao;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

public class SelectionListServiceImpl implements SelectionListService {
    private SelectionListDao selectionListDao;
    private StarsDatabaseCache starsDatabaseCache;
    private RolePropertyDao rolePropertyDao;

    @Override
    public SortedSetMultimap<SelectionListCategory, DisplayableSelectionList> getUserEditableLists(int ecId, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);

        Comparator<SelectionListCategory> selListComparator = Ordering.natural();
        Comparator<DisplayableSelectionList> dslComparator = new Comparator<DisplayableSelectionList>() {
            @Override
            public int compare(DisplayableSelectionList dsl1, DisplayableSelectionList dsl2) {
                return dsl1.getType().compareTo(dsl2.getType());
            }
        };
        SortedSetMultimap<SelectionListCategory, DisplayableSelectionList> retVal =
            TreeMultimap.create(selListComparator, dslComparator);
        Set<YukonSelectionList> userLists = getSelectionListsInUse(energyCompany, user);
        for (YukonSelectionList list : userLists) {
            if (list.isUserUpdateAvailable()) {
                retVal.put(list.getType().getCategory(),
                           new DisplayableSelectionList(list, isListInherited(energyCompany, list)));
            }
        }
        return retVal;
    }

    @Override
    public boolean isListInherited(int ecId, YukonSelectionList list) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        return isListInherited(energyCompany, list);
    }

    @Override
    @Transactional
    public void restoreToDefault(YukonSelectionList list) {
        YukonSelectionList defaultList =
            starsDatabaseCache.getDefaultEnergyCompany().getYukonSelectionList(list.getListName());
        YukonSelectionList newList = new YukonSelectionList();
        newList.setListId(list.getListId());
        newList.setOrdering(defaultList.getOrdering());
        newList.setSelectionLabel(defaultList.getSelectionLabel());
        newList.setWhereIsList(defaultList.getWhereIsList());
        newList.setType(list.getType());
        newList.setUserUpdateAvailable(list.isUserUpdateAvailable());
        newList.setEnergyCompanyId(list.getEnergyCompanyId());
        Function<YukonListEntry, Integer> getEntryId = new Function<YukonListEntry, Integer>() {
            @Override
            public Integer apply(YukonListEntry from) {
                return from.getEntryID();
            }
        };
        List<Integer> entriesToDelete = Lists.transform(list.getYukonListEntries(), getEntryId);
        List<YukonListEntry> newEntries = Lists.newArrayList();
        LiteStarsEnergyCompany energyCompany =
            starsDatabaseCache.getEnergyCompany(list.getEnergyCompanyId());
        boolean showAdditionalProtocols =
            ECUtils.hasRight(energyCompany, ECUtils.RIGHT_SHOW_ADDTL_PROTOCOLS);
        for (YukonListEntry entry : defaultList.getYukonListEntries()) {
            if (list.getType() == YukonSelectionListEnum.DEVICE_TYPE
                    && !showAdditionalProtocols
                    && InventoryUtils.isAdditionalProtocol(entry.getYukonDefID())) {
                continue;
            }
            YukonListEntry newEntry = new YukonListEntry();
            newEntry.setListID(list.getListId());
            newEntry.setEntryOrder(entry.getEntryOrder());
            newEntry.setEntryText(entry.getEntryText());
            newEntry.setYukonDefID(entry.getYukonDefID());
            newEntries.add(newEntry);
        }
        newList.setYukonListEntries(newEntries);
        selectionListDao.saveList(newList, entriesToDelete);
    }

    @Override
    public List<YukonDefinition> getValidDefinitions(int ecId, YukonSelectionListEnum listType) {
        List<YukonDefinition> retVal =
            Lists.newArrayList(YukonDefinition.valuesForList(listType));
        if (listType == YukonSelectionListEnum.DEVICE_TYPE) {
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
            if (!ECUtils.hasRight(energyCompany, ECUtils.RIGHT_SHOW_ADDTL_PROTOCOLS)) {
                List<YukonDefinition> filteredRetVal = Lists.newArrayList();
                for (YukonDefinition definition : retVal) {
                    if (!InventoryUtils.isAdditionalProtocol(definition.getDefinitionId())) {
                        filteredRetVal.add(definition);
                    }
                }
                retVal = filteredRetVal;
            }
        }
        return retVal;
    }

    private boolean isListInherited(LiteStarsEnergyCompany energyCompany, YukonSelectionList list) {
        boolean hasParent = energyCompany.getParent() != null;
        YukonSelectionList ecList =
            energyCompany.getYukonSelectionList(list.getListName(), false, false);
        return hasParent && ecList == null;
    }

    private Set<YukonSelectionList> getSelectionListsInUse(LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {
        Set<YukonSelectionList> lists = Sets.newTreeSet(new Comparator<YukonSelectionList>(){
            @Override
            public int compare(YukonSelectionList list1, YukonSelectionList list2) {
                return list1.getListName().compareToIgnoreCase(list2.getListName());
            }});

        lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE));

        if (rolePropertyDao.checkRole(YukonRole.ODDS_FOR_CONTROL, user))
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL));

        if (rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING, user))
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE));

        if (rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES, user)
                || rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE, user)) {
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION));

            Iterable<LiteApplianceCategory> categories = energyCompany.getAllApplianceCategories();
            List<Integer> catDefIDs = new ArrayList<Integer>();

            for (LiteApplianceCategory liteAppCat : categories) {
                int catDefID = DaoFactory.getYukonListDao().getYukonListEntry(liteAppCat.getCategoryID()).getYukonDefID();
                if (catDefIDs.contains(new Integer(catDefID))) continue;
                catDefIDs.add(new Integer(catDefID));

                if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE));
                } else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION));
                } else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE));
                } else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG));
                } else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE));
                    lists.add(energyCompany .getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE));
                } else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE));
                } else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE));
                } else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION));
                    lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE));
                }
            }
        }

        if (rolePropertyDao.checkRole(YukonRole.INVENTORY, user)
                || rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, user)
                || rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, user)) {
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_RATE_SCHEDULE));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE));
        }

        if (rolePropertyDao.checkRole(YukonRole.WORK_ORDER, user)
                || rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WORK_ORDERS, user)) {
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS));
        }

        if (rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE, user)) {
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM));
            lists.add(energyCompany .getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE));
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE));
        }

        if (rolePropertyDao.checkRole(YukonRole.INVENTORY, user)) {
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY));
            if (rolePropertyDao.checkProperty(YukonRoleProperty.INVENTORY_SHOW_ALL, user)) {
                lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY));
                lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY));
            }
        }

        if (rolePropertyDao.checkRole(YukonRole.WORK_ORDER, user)) {
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY));
            if (rolePropertyDao.checkProperty(YukonRoleProperty.WORK_ORDER_SHOW_ALL, user)) {
                lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY));
                lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY));
            }
        }

        if (rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY, user)) {
            lists.add(energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY));
        }

        return lists;
    }

    @Autowired
    public void setSelectionListDao(SelectionListDao selectionListDao) {
        this.selectionListDao = selectionListDao;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}
