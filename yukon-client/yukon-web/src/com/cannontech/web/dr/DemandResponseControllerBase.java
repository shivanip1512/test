package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.NaturalOrderComparator;
import com.cannontech.web.common.chart.service.AssetAvailabilityChartService;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRole;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;

@CheckRole(YukonRole.DEMAND_RESPONSE)
public abstract class DemandResponseControllerBase {
    
    private static final Logger log = YukonLogManager.getLogger(DemandResponseControllerBase.class);
    
    @Autowired private AssetAvailabilityChartService assetAvailabilityChartService;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
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

    protected static enum AssetDetailsColumn implements DisplayableEnum {
        
        SERIAL_NUM, 
        TYPE,
        LAST_COMM,
        LAST_RUN,
        APPLIANCES,
        AVAILABILITY;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.hardware.assetAvailability." + name();
        }
    };
    
    protected void addAssetColumns(ModelMap model, MessageSourceAccessor accessor, SortingParameters sorting) {
        Direction dir = sorting.getDirection();
        for (AssetDetailsColumn col : AssetDetailsColumn.values()) {
            AssetDetailsColumn sort = AssetDetailsColumn.valueOf(sorting.getSort());
            String text = accessor.getMessage(col);
            boolean active = sort == col;
            SortableColumn sortable = SortableColumn.of(dir, active, text, col.name());
            model.addAttribute(col.name(), sortable);
        }
    }
    
    protected static NaturalOrderComparator naturalOrder = new NaturalOrderComparator();


    protected int getAssetAvailabilityInfo(DisplayablePao dispPao, ModelMap model, YukonUserContext userContext) {
        AssetAvailabilitySummary aaSummary =
            assetAvailabilityService.getAssetAvailabilityFromDrGroup(dispPao.getPaoIdentifier());
        int assetTotal = aaSummary.getTotalSize();
        model.addAttribute("assetAvailabilitySummary", aaSummary);
        model.addAttribute("assetTotal", assetTotal);
        model.addAttribute("pieJSONData", assetAvailabilityChartService.getJsonPieData(aaSummary, userContext));
        model.addAttribute("colorMap", colorMap);
        model.addAttribute("maxPingableDevices", AssetAvailabilityPingService.PING_MAXIMUM_DEVICES);
        return assetTotal;
    }

    /*
     * Used as part of the downloadToCsv feature in the controllers.
     */
    protected String[] getDownloadHeaderRow(YukonUserContext userContext) {
                                            
        MessageSourceAccessor msa = messageSourceResolver.getMessageSourceAccessor(userContext);

        // header row
        String[] headerRow = new String[6];
        headerRow[0] = msa.getMessage(AssetDetailsColumn.SERIAL_NUM);
        headerRow[1] = msa.getMessage(AssetDetailsColumn.TYPE);
        headerRow[2] = msa.getMessage(AssetDetailsColumn.LAST_COMM);
        headerRow[3] = msa.getMessage(AssetDetailsColumn.LAST_RUN);
        headerRow[4] = msa.getMessage(AssetDetailsColumn.APPLIANCES);
        headerRow[5] = msa.getMessage(AssetDetailsColumn.AVAILABILITY);
        
        return headerRow;
    }

    /*
     * Used as part of the downloadToCsv feature in the controllers.
     */
    protected List<String[]> getDownloadDataRows(DisplayablePao dispPao,
                                                 AssetAvailabilityCombinedStatus[] filters,
                                                 YukonUserContext userContext) {

        Set<AssetAvailabilityCombinedStatus> filterSet = new HashSet<>();
        Collections.addAll(filterSet, filters != null ? filters : AssetAvailabilityCombinedStatus.values());

        MessageSourceAccessor msa = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        SearchResults<ApplianceAssetAvailabilityDetails> results =
            assetAvailabilityService.getAssetAvailabilityWithAppliance(dispPao.getPaoIdentifier(), PagingParameters.EVERYTHING,
                filters, null, userContext);

        List<ApplianceAssetAvailabilityDetails> resultList = results.getResultList();
        List<String[]> dataRows = Lists.newArrayList();
        for (ApplianceAssetAvailabilityDetails details : resultList) {
            String[] dataRow = new String[6];

            dataRow[0] = details.getSerialNumber();
            dataRow[1] = details.getType().toString();
            dataRow[2] = (details.getLastComm() == null) ? "" : dateFormattingService.format(details.getLastComm(),
                    DateFormatEnum.BOTH, userContext);
            dataRow[3] = (details.getLastRun() == null) ? "" : dateFormattingService.format(details.getLastRun(),
                    DateFormatEnum.BOTH, userContext);
            dataRow[4] = details.getAppliances();
            dataRow[5] = msa.getMessage(details.getAvailability());
            dataRows.add(dataRow);
        }
            
        return dataRows;
    }
    
    protected List<AssetAvailabilityCombinedStatus> getAssetAvailabilityFilters(String type) {
        
        List<AssetAvailabilityCombinedStatus> filters = new ArrayList<>();
        if (type.equalsIgnoreCase("all")) {
            filters = Lists.newArrayList(AssetAvailabilityCombinedStatus.values());
        } else {
            filters = Collections.singletonList(AssetAvailabilityCombinedStatus.valueOf(type.toUpperCase()));
        }
        
        return filters;
    }
    
 
    
}