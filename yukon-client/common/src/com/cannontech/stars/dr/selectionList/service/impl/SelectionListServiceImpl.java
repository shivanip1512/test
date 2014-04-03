package com.cannontech.stars.dr.selectionList.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.DisplayableSelectionList;
import com.cannontech.common.constants.SelectionListCategory;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteApplianceCategory;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.selectionList.dao.SelectionListDao;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.util.ECUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

public class SelectionListServiceImpl implements SelectionListService {
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SelectionListDao selectionListDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonListDao listDao;

    @Override
    public SortedSetMultimap<SelectionListCategory, DisplayableSelectionList> getUserEditableLists(int ecId,
            LiteYukonUser user) {
        LiteStarsEnergyCompany ec = starsDatabaseCache.getEnergyCompany(ecId);

        Comparator<SelectionListCategory> selListComparator = Ordering.natural();
        Comparator<DisplayableSelectionList> dslComparator = new Comparator<DisplayableSelectionList>() {
            @Override
            public int compare(DisplayableSelectionList dsl1, DisplayableSelectionList dsl2) {
                return dsl1.getType().compareTo(dsl2.getType());
            }
        };
        SortedSetMultimap<SelectionListCategory, DisplayableSelectionList> retVal =
            TreeMultimap.create(selListComparator, dslComparator);
        Set<YukonSelectionList> userLists = getSelectionListsInUse(ec, user);
        for (YukonSelectionList list : userLists) {
            if (list.isUserUpdateAvailable()) {
                retVal.put(list.getType().getCategory(),
                           new DisplayableSelectionList(list, isListInherited(ec, list)));
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
        boolean showAdditionalProtocols = showAdditionalProtocols(energyCompany);
        for (YukonListEntry entry : defaultList.getYukonListEntries()) {
            if (list.getType() == YukonSelectionListEnum.DEVICE_TYPE
                    && !showAdditionalProtocols
                    && HardwareType.valueOf(entry.getYukonDefID()).isSA()) {
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
            if (!showAdditionalProtocols(energyCompany)) {
                List<YukonDefinition> filteredRetVal = Lists.newArrayList();
                for (YukonDefinition definition : retVal) {
                    if (!HardwareType.valueOf(definition.getDefinitionId()).isSA()) {
                        filteredRetVal.add(definition);
                    }
                }
                retVal = filteredRetVal;
            }
        }
        return retVal;
    }

    private boolean showAdditionalProtocols(LiteStarsEnergyCompany ec) {
        String optionalProductDevStr = ecSettingDao.getString(EnergyCompanySettingType.OPTIONAL_PRODUCT_DEV,
            ec.getEnergyCompanyId());
        boolean isEnabled = ecSettingDao.isEnabled(EnergyCompanySettingType.OPTIONAL_PRODUCT_DEV,
            ec.getEnergyCompanyId());
        if (!isEnabled || StringUtils.isEmpty(optionalProductDevStr)) {
            return false;
        }
        try {
            int optionalProductDev = Integer.parseInt(optionalProductDevStr, 16);
            return (optionalProductDev & ECUtils.RIGHT_SHOW_ADDTL_PROTOCOLS) != 0;
        } catch (NumberFormatException nfe) {
            // If this string is null, we just don't enable any special options.
        }
        return false;
    }

    private boolean isListInherited(LiteStarsEnergyCompany energyCompany, YukonSelectionList list) {
        boolean hasParent = energyCompany.getParent() != null;
        YukonSelectionList ecList =
            energyCompany.getYukonSelectionList(list.getListName(), false, false);
        return hasParent && ecList == null;
    }

    private Set<YukonSelectionList> getSelectionListsInUse(LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {
        Set<YukonSelectionList> lists = Sets.newTreeSet(new Comparator<YukonSelectionList>() {
            @Override
            public int compare(YukonSelectionList list1, YukonSelectionList list2) {
                return list1.getListName().compareToIgnoreCase(list2.getListName());
            }});

        Iterable<LiteApplianceCategory> applianceCategories = energyCompany.getAllApplianceCategories();
        Set<Integer> usedApplianceListEntryTypes = Sets.newHashSet();
        for (LiteApplianceCategory appCat : applianceCategories) {
            usedApplianceListEntryTypes.add(listDao.getYukonListEntry(appCat.getCategoryID()).getYukonDefID());
        }

        for (SelectionListCategory category : SelectionListCategory.values()) {
            YukonRole role = category.getRole();
            Set<YukonRoleProperty> roleProperties = category.getRoleProperties();
            if (role == null && roleProperties.size() == 0) {
                // This category doesn't have any user editable lists.
                continue;
            }
            if (role != null && !rolePropertyDao.checkRole(role, user)) {
                continue;
            }
            if (roleProperties.size() > 0
                    && !rolePropertyDao.checkAnyProperties(user, roleProperties)) {
                continue;
            }
            Integer listEntryType = category.getListEntryType();
            if (listEntryType != null) {
                if (!usedApplianceListEntryTypes.contains(listEntryType)) {
                    continue;
                }
            }
            // We've passed all the negative tests...we can display lists in this category.
            for (YukonSelectionListEnum listType : YukonSelectionListEnum.getByCategory(category)) {
                YukonSelectionList list = energyCompany.getYukonSelectionList(listType.getListName());
                if (list != null) {
                    lists.add(list);
                }
            }
        }

        return lists;
    }
}
