package com.cannontech.stars.dr.selectionList.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.DisplayableSelectionList;
import com.cannontech.common.constants.SelectionListCategory;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteApplianceCategory;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.dr.selectionList.dao.SelectionListDao;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
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
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DBPersistentDao dbPersistentDao;

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
        YukonEnergyCompany energyCompany = ecDao.getEnergyCompany(ecId);
        return isListInherited(energyCompany, list);
    }

    @Override
    @Transactional
    public void restoreToDefault(YukonSelectionList list) {
        YukonEnergyCompany defaultEc = ecDao.getEnergyCompany(EnergyCompanyDao.DEFAULT_ENERGY_COMPANY_ID);
        YukonSelectionList defaultList = getSelectionList(defaultEc, list.getListName());
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
        YukonEnergyCompany energyCompany = ecDao.getEnergyCompany(list.getEnergyCompanyId());
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
            YukonEnergyCompany energyCompany = ecDao.getEnergyCompany(ecId);
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

    private boolean showAdditionalProtocols(YukonEnergyCompany ec) {
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

    private boolean isListInherited(YukonEnergyCompany energyCompany, YukonSelectionList list) {
        boolean hasParent = ecDao.getEnergyCompany(energyCompany.getEnergyCompanyId()).getParent() != null;
        YukonSelectionList ecList = getSelectionList(energyCompany, list.getListName(), false, false);
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
                YukonSelectionList list = getSelectionList(energyCompany, listType.getListName());
                if (list != null) {
                    lists.add(list);
                }
            }
        }

        return lists;
    }

    @Override
    public YukonSelectionList getSelectionList(YukonEnergyCompany yukonEnergyCompany, String listName) {
        return getSelectionList(yukonEnergyCompany, listName, true, true);
    }
    
    @Override
    public YukonSelectionList getSelectionList(YukonEnergyCompany yukonEnergyCompany, String listName, 
                                               boolean useInherited, boolean useDefault) {
        EnergyCompany energyCompany = ecDao.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        YukonSelectionList yukonSelectionList =
            listDao.findSelectionListByEnergyCompanyIdAndListName(energyCompany.getEnergyCompanyId(), listName);
        if (yukonSelectionList != null) {
            return yukonSelectionList;
        }

        // If parent company exists, then search the parent company for the list
        if (useInherited && energyCompany.getParent() != null) {
            return getSelectionList(energyCompany.getParent(), listName, useInherited, useDefault);
        }

        if (useDefault && !ecDao.isDefaultEnergyCompany(energyCompany)) {
            YukonEnergyCompany defaultEc = ecDao.getEnergyCompany(EnergyCompanyDao.DEFAULT_ENERGY_COMPANY_ID);
            YukonSelectionList dftList = getSelectionList(defaultEc, listName, false, false);
            if (dftList != null) {
                // If the list is user updatable, returns a copy of the default list; otherwise
                // returns the default list itself
                if (dftList.isUserUpdateAvailable()) {
                    return addSelectionList(energyCompany, listName, dftList, true);
                }

                return dftList;
            }
        }

        return null;
    }

    @Override
    public YukonListEntry getListEntry(YukonEnergyCompany energyCompany, int yukonDefId) {
        YukonDefinition yukonDefinition = YukonDefinition.getById(yukonDefId);
        if (yukonDefinition == null) {
            return null;
        }

        YukonSelectionList list = getSelectionList(energyCompany, yukonDefinition.getRelevantList().getListName());
        for (int i = 0; i < list.getYukonListEntries().size(); i++) {
            YukonListEntry entry = list.getYukonListEntries().get(i);
            if (entry.getYukonDefID() == yukonDefId) {
                return entry;
            }
        }

        return new YukonListEntry();
    }

    private YukonSelectionList addSelectionList(YukonEnergyCompany energyCompany, String listName, 
                                                YukonSelectionList dftList, boolean populateDefault) {
        try {
            com.cannontech.database.data.constants.YukonSelectionList list =
                    new com.cannontech.database.data.constants.YukonSelectionList();
            com.cannontech.database.db.constants.YukonSelectionList listDB = list.getYukonSelectionList();
            listDB.setOrdering(dftList.getOrdering().getDbString());
            listDB.setSelectionLabel( dftList.getSelectionLabel() );
            listDB.setWhereIsList( dftList.getWhereIsList() );
            listDB.setListName( listName );
            listDB.setUserUpdateAvailable("" + CtiUtilities.getBooleanCharacter(dftList.isUserUpdateAvailable()));
            listDB.setEnergyCompanyId(energyCompany.getEnergyCompanyId());

            dbPersistentDao.performDBChange(list, TransactionType.INSERT);
            listDB = list.getYukonSelectionList();
            
            YukonSelectionList cList = new YukonSelectionList();
            StarsLiteFactory.setConstantYukonSelectionList(cList, listDB);
            
            if (populateDefault) {
                for (int i = 0; i < dftList.getYukonListEntries().size(); i++) {
                    YukonListEntry dftEntry = dftList.getYukonListEntries().get(i);
                    if (dftEntry.getEntryOrder() < 0) {
                        continue;
                    }
                    
                    com.cannontech.database.db.constants.YukonListEntry entry =
                            new com.cannontech.database.db.constants.YukonListEntry();
                    entry.setListID( listDB.getListID() );
                    entry.setEntryOrder( new Integer(dftEntry.getEntryOrder()) );
                    entry.setEntryText( dftEntry.getEntryText() );
                    entry.setYukonDefID( new Integer(dftEntry.getYukonDefID()) );
                    dbPersistentDao.performDBChange(entry, TransactionType.INSERT);
                    
                    YukonListEntry cEntry = new YukonListEntry();
                    StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
                    cList.getYukonListEntries().add( cEntry );
                }
            }
            
            return cList;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return null;
    }
}
