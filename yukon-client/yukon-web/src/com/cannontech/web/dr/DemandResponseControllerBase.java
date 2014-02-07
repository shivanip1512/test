package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.assetavailability.ApplianceWithRuntime;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.NaturalOrderComparator;
import com.cannontech.web.common.chart.service.AssetAvailabilityChartService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public abstract class DemandResponseControllerBase {
    
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private AssetAvailabilityChartService assetAvailabilityChartService;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ObjectFormattingService objectFormatingService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    
    protected static final TypeReference<Set<AssetAvailabilityCombinedStatus>> assetAvailStatusType
        = new TypeReference<Set<AssetAvailabilityCombinedStatus>>() {};

    protected static Map<AssetAvailabilityCombinedStatus, String> colorMap;
    static {
        colorMap = new HashMap<>();
        colorMap.put(AssetAvailabilityCombinedStatus.ACTIVE, "green");
        colorMap.put(AssetAvailabilityCombinedStatus.INACTIVE, "orange");
        colorMap.put(AssetAvailabilityCombinedStatus.UNAVAILABLE, "red");
        colorMap.put(AssetAvailabilityCombinedStatus.OPTED_OUT, "grey");
    }

    protected static final String ITEMS_PER_PAGE = "10";

    protected static enum AssetDetailsColumn {SERIAL_NUM, TYPE, LAST_COMM, LAST_RUN, APPLIANCES, AVAILABILITY};

    protected static NaturalOrderComparator naturalOrder = new NaturalOrderComparator();


    protected ModelMap getAssetAvailabilityInfo(DisplayablePao dispPao, 
                                         ModelMap model, 
                                         YukonUserContext userContext) {
        SimpleAssetAvailabilitySummary aaSummary = 
                assetAvailabilityService.getAssetAvailabilityFromDrGroup(dispPao.getPaoIdentifier());
        model.addAttribute("assetAvailabilitySummary", aaSummary);
        model.addAttribute("assetTotal", aaSummary.getAll().size());
        model.addAttribute("pieJSONData", assetAvailabilityChartService.getJsonPieData(aaSummary, userContext));
        model.addAttribute("colorMap", colorMap);
        return model;
    }

    protected List<AssetAvailabilityDetails> getResultsList(DisplayablePao dispPao, 
                                                            YukonUserContext userContext, 
                                                            String[] filters) {
        
        Set<AssetAvailabilityCombinedStatus> filterSet = getFilterSet(filters);

        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), dispPao, Permission.LM_VISIBLE);
        
        Map<Integer, SimpleAssetAvailability> resultMap = 
                assetAvailabilityService.getAssetAvailability(dispPao.getPaoIdentifier());
        List<AssetAvailabilityDetails> resultList = new ArrayList<>(resultMap.size());

        // Create set of applianceCategoryId's (& eliminates duplicates)
        Set<Integer> applianceCatIds = Sets.newHashSet();
        for (SimpleAssetAvailability entry : resultMap.values()) {
            ImmutableSet<ApplianceWithRuntime> appRuntime = entry.getApplianceRuntimes();
            for (ApplianceWithRuntime dispAppRun : appRuntime) {
                applianceCatIds.add(dispAppRun.getApplianceCategoryId());
            }
        }
        Map<Integer, ApplianceCategory> appCatMap = applianceCategoryDao.getByApplianceCategoryIds(applianceCatIds);
        Map<Integer, HardwareSummary> hwSumMap = inventoryDao.findHardwareSummariesById(resultMap.keySet());
        
        for (Map.Entry<Integer, SimpleAssetAvailability> entry : resultMap.entrySet()) {
            
            AssetAvailabilityDetails aaDetails = new AssetAvailabilityDetails();
            HardwareSummary hwSum = hwSumMap.get(entry.getKey());

            // Set the Serial Number and Type columns
            aaDetails.setSerialNumber(hwSum.getSerialNumber());
            aaDetails.setType(hwSum.getHardwareType());
            
            SimpleAssetAvailability simpleAA = entry.getValue();
            // set the Last Communication column
            aaDetails.setLastComm(simpleAA.getLastCommunicationTime());

            // parse through the appRuntime set to get list of appliances & last runtime.
            ImmutableSet<ApplianceWithRuntime> appRuntime = simpleAA.getApplianceRuntimes();
            String applianceStr = "";
            Instant lastRun = null;
            for (ApplianceWithRuntime awr: appRuntime) {
                // Only get the latest last runtime.
                Instant lnzrt = awr.getLastNonZeroRuntime();
                if (lnzrt != null) {
                    if (lastRun == null || lastRun.isBefore(lnzrt)) {
                        lastRun = lnzrt;  
                    }
                }
                ApplianceCategory appCat = appCatMap.get(awr.getApplianceCategoryId());
                // The Appliances will be a comma-separated list of the appliances for this device. 
                // No Internationalization since this comes from YukonSelectionList.
                applianceStr = (applianceStr == "") ? appCat.getDisplayName() 
                                                    : applianceStr + ", " + appCat.getDisplayName();
            }
            aaDetails.setAppliances(applianceStr);
            aaDetails.setLastRun(lastRun);

            aaDetails.setAvailability(simpleAA.getCombinedStatus());
            if (filterSet.contains(aaDetails.getAvailability())) {
                resultList.add(aaDetails);
            } else {
                aaDetails = null;
            }
        }
        return resultList;
    }


    private Set<AssetAvailabilityCombinedStatus> getFilterSet(String[] filterArray) {
        // Create a HashSet for the filtered AssetAvailabilityCombinedStatus values.
        // Either null or an empty JSONArray will display all data values.
        List<String> filters = Collections.emptyList();
        if (filterArray != null) {
            filters = Arrays.asList(filterArray);
        }

        Set<AssetAvailabilityCombinedStatus> filterSet = Sets.newHashSet();
        if (filters.isEmpty()) {
            filterSet.add(AssetAvailabilityCombinedStatus.ACTIVE);
            filterSet.add(AssetAvailabilityCombinedStatus.INACTIVE);
            filterSet.add(AssetAvailabilityCombinedStatus.OPTED_OUT);
            filterSet.add(AssetAvailabilityCombinedStatus.UNAVAILABLE);
        } else {
            if (filters.contains("ACTIVE")) {
                filterSet.add(AssetAvailabilityCombinedStatus.ACTIVE);
            }
            if (filters.contains("INACTIVE")) {
                filterSet.add(AssetAvailabilityCombinedStatus.INACTIVE);
            }
            if (filters.contains("OPTED_OUT")) {
                filterSet.add(AssetAvailabilityCombinedStatus.OPTED_OUT);
            }
            if (filters.contains("UNAVAILABLE")) {
                filterSet.add(AssetAvailabilityCombinedStatus.UNAVAILABLE);
            }
        }
        return filterSet;
    }

    /*
     * Used to sort the (filtered) results list by a particular column.
     */
    protected void sortAssetDetails(List<AssetAvailabilityDetails> assetDetails,
                                    AssetDetailsColumn sortBy,
                                    final boolean descending,
                                    final YukonUserContext userContext) {
        switch(sortBy) {
        default:
        case SERIAL_NUM:
            Collections.sort(assetDetails, new Comparator<AssetAvailabilityDetails>() {
                @Override public int compare(AssetAvailabilityDetails o1, AssetAvailabilityDetails o2) {
                    int compare = naturalOrder.compare(o1.getSerialNumber(), o2.getSerialNumber());
                    return descending ? -compare : compare;
                }
            });
            break;
        case TYPE:
            Collections.sort(assetDetails, new Comparator<AssetAvailabilityDetails>() {
                @Override public int compare(AssetAvailabilityDetails o1, AssetAvailabilityDetails o2) {
                    String typeStr1 = objectFormatingService.formatObjectAsString(o1.getType(), userContext);
                    String typeStr2 = objectFormatingService.formatObjectAsString(o2.getType(), userContext);
                    int compare = typeStr1.compareToIgnoreCase(typeStr2);
                    return descending ? -compare : compare;
                }
            });
            break;
        case LAST_COMM:
            Collections.sort(assetDetails, new Comparator<AssetAvailabilityDetails>() {
                @Override public int compare(AssetAvailabilityDetails o1, AssetAvailabilityDetails o2) {
                    Instant t1 = o1.getLastComm();
                    Instant t2 = o2.getLastComm();
                    if (t1 == t2) {
                        return 0;
                    } else if (t1 == null && t2 != null) {
                        return descending ? 1 : -1;
                    } else if (t1 != null && t2 == null) {
                        return descending ? -1 : 1;
                    } else {
                        int compare = t1.compareTo(t2);
                        return descending ? -compare : compare;
                    }
                }
            });
            break;
        case LAST_RUN:
            Collections.sort(assetDetails, new Comparator<AssetAvailabilityDetails>() {
                @Override public int compare(AssetAvailabilityDetails o1, AssetAvailabilityDetails o2) {
                    Instant t1 = o1.getLastRun();
                    Instant t2 = o2.getLastRun();
                    if (t1 == t2) {
                        return 0;
                    } else if (t1 == null && t2 != null) {
                        return descending ? 1 : -1;
                    } else if (t1 != null && t2 == null) {
                        return descending ? -1 : 1;
                    } else {
                        int compare = t1.compareTo(t2);
                        return descending ? -compare : compare;
                    }
                }
            });
            break;
        case APPLIANCES:
            Collections.sort(assetDetails, new Comparator<AssetAvailabilityDetails>() {
                @Override public int compare(AssetAvailabilityDetails o1, AssetAvailabilityDetails o2) {
                    int compare = o1.getAppliances().compareToIgnoreCase(o2.getAppliances());
                    return descending ? -compare : compare;
                }
            });
            break;
        case AVAILABILITY:
            Collections.sort(assetDetails, new Comparator<AssetAvailabilityDetails>() {
                @Override public int compare(AssetAvailabilityDetails o1, AssetAvailabilityDetails o2) {
                    String typeStr1 = objectFormatingService.formatObjectAsString(o1.getAvailability(), userContext);
                    String typeStr2 = objectFormatingService.formatObjectAsString(o2.getAvailability(), userContext);
                    int compare = typeStr1.compareToIgnoreCase(typeStr2);
                    return descending ? -compare : compare;
                }
            });
            break;
        }
    }
    
    /*
     * Used as part of the downloadToCsv feature in the controllers.
     */
    protected String[] getDownloadHeaderRow(YukonUserContext userContext) {
                                            
        MessageSourceAccessor msa = messageSourceResolver.getMessageSourceAccessor(userContext);

        // header row
        String[] headerRow = new String[6];
        headerRow[0] = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.serialNumber.linkText");
        headerRow[1] = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.type.linkText");
        headerRow[2] = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.lastCommunication.linkText");
        headerRow[3] = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.lastRuntime.linkText");
        headerRow[4] = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.appliances.linkText");
        headerRow[5] = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.availability.linkText");
        
        return headerRow;
    }

    /*
     * Used as part of the downloadToCsv feature in the controllers.
     */
    protected List<String[]> getDownloadDataRows(DisplayablePao dispPao,
                                                 String[] filters,
                                                 YukonUserContext userContext) {

        Set<AssetAvailabilityCombinedStatus> filterSet = getFilterSet(filters);
        MessageSourceAccessor msa = messageSourceResolver.getMessageSourceAccessor(userContext);

        Map<Integer, SimpleAssetAvailability> resultMap = 
                assetAvailabilityService.getAssetAvailability(dispPao.getPaoIdentifier());
        
        // Create set of applianceCategoryId's (& eliminates duplicates)
        Set<Integer> applianceCatIds = Sets.newHashSet();
        for (SimpleAssetAvailability entry : resultMap.values()) {
            ImmutableSet<ApplianceWithRuntime> appRuntime = entry.getApplianceRuntimes();
            for (ApplianceWithRuntime dispAppRun : appRuntime) {
                applianceCatIds.add(dispAppRun.getApplianceCategoryId());
            }
        }
        Map<Integer, ApplianceCategory> appCatMap = applianceCategoryDao.getByApplianceCategoryIds(applianceCatIds);
        Map<Integer, HardwareSummary> hwSumMap = inventoryDao.findHardwareSummariesById(resultMap.keySet());

        // data rows
        List<String[]> dataRows = Lists.newArrayList();
        
        for (Map.Entry<Integer, SimpleAssetAvailability> entry : resultMap.entrySet()) {

            String[] dataRow = new String[6];
            HardwareSummary hwSum = hwSumMap.get(entry.getKey());

            // Set the Serial Number and Type columns
            dataRow[0] = hwSum.getSerialNumber();
            dataRow[1] = hwSum.getHardwareType().toString();
            
            SimpleAssetAvailability simpleAA = entry.getValue();
            // set the Last Communication column
            Instant lastComm = simpleAA.getLastCommunicationTime();
            String dateStr = (lastComm == null) ? "" : dateFormattingService.format(lastComm, DateFormatEnum.BOTH, userContext);
            dataRow[2] = dateStr; 
            
            ImmutableSet<ApplianceWithRuntime> appRuntime = simpleAA.getApplianceRuntimes();
            if (appRuntime.size() == 0) {
                dataRow[3] = dataRow[4] = "";
                AssetAvailabilityCombinedStatus aaStatus = simpleAA.getCombinedStatus();
                if (filterSet.contains(aaStatus)) {
                    dataRow[5] = msa.getMessage(aaStatus);
                    dataRows.add(dataRow);
                } else {
                    dataRow = null;
                }
            } else {
                for (ApplianceWithRuntime awr: appRuntime) {
                    Instant lnzrt = awr.getLastNonZeroRuntime();
                    String lastRun = (lnzrt == null) ? "" : dateFormattingService.format(lnzrt, DateFormatEnum.BOTH, userContext);
                    dataRow[3] = lastRun;
                    ApplianceCategory appCat = appCatMap.get(awr.getApplianceCategoryId());
                    String appStr = (appCat == null) ? "" : appCat.getDisplayName();
                    dataRow[4] = appStr;
                
                    AssetAvailabilityCombinedStatus aaStatus = simpleAA.getCombinedStatus();
                    if (filterSet.contains(aaStatus)) {
                        dataRow[5] = msa.getMessage(aaStatus);
                        dataRows.add(dataRow);
                    } else {
                        dataRow = null;
                    }
                }
            }
        }
        return dataRows;
    }

}
