package com.cannontech.web.dr.setup;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.setup.GearFilterResult;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao.SortBy;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.google.common.collect.Lists;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/setup")
public class LMSetupFilterController {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ApiControllerHelper helper;
    
    private static final Logger log = YukonLogManager.getLogger(LMSetupFilterController.class);
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    
    @GetMapping("/list")
    public String list(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flash) {
        LMSetupFilter lmSetupFilter = null;
        if (!model.containsAttribute("lmSetupFilter")) {
            lmSetupFilter = new LMSetupFilter();
            lmSetupFilter.setFilterByType(LmSetupFilterType.CONTROL_AREA);
        } else {
            lmSetupFilter = (LMSetupFilter) model.get("lmSetupFilter");
        }
        filter(lmSetupFilter, SortingParameters.of("NAME", Direction.asc), PagingParameters.EVERYTHING, model,
            userContext, request, flash);
        return "dr/setup/list.jsp";
    }
    
    @GetMapping("/filter")
    public String filter(@ModelAttribute LMSetupFilter lmSetupFilter,
            @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request, FlashScope flash) {
        FilterCriteria<LMSetupFilter> filterCriteria =
            new FilterCriteria<LMSetupFilter>(lmSetupFilter, sorting, paging);

        SortBy sortByValue = LMFilterSortBy.valueOf(sorting.getSort()).getValue();
        filterCriteria.setSortingParameters(
            SortingParameters.of(sortByValue.getDbString(), filterCriteria.getSortingParameters().getDirection()));

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Direction dir = sorting.getDirection();
        if (lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_GROUP
            || lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_PROGRAM) {
            LMFilterSortBy sortBy = LMFilterSortBy.valueOf(sorting.getSort());
            for (LMFilterSortBy column : LMFilterSortBy.values()) {
                String text = accessor.getMessage(column);
                SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
                model.addAttribute(column.name(), col);
            }
        } else if (lmSetupFilter.getFilterByType() == LmSetupFilterType.GEAR) {
            //TODO: These logs will be removed after YUK-19938 is implemented.
            log.info("Gear Name: " + lmSetupFilter.getName());
            log.info("Gear Types: " + lmSetupFilter.getGearTypes());
            log.info("Program Ids: " + lmSetupFilter.getProgramIds());
            
            LMFilterGearSortBy sortBy = LMFilterGearSortBy.valueOf(sorting.getSort());
            for (LMFilterGearSortBy column : LMFilterGearSortBy.values()) {
                String text = accessor.getMessage(column);
                SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
                model.addAttribute(column.name(), col);
            }
        }
        else {
            LMFilterSortBy sortBy = LMFilterSortBy.valueOf(sorting.getSort());
            String text = accessor.getMessage(LMFilterSortBy.NAME);
            SortableColumn col =
                SortableColumn.of(dir, LMFilterSortBy.NAME == sortBy, text, LMFilterSortBy.NAME.name());
            model.addAttribute(SortBy.NAME.name(), col);
        }
        model.addAttribute("filterByTypes", LmSetupFilterType.values());
        model.addAttribute("lmSetupFilter", lmSetupFilter);
        model.addAttribute("loadGroupTypes", PaoType.getAllLMGroupTypes());
        model.addAttribute("loadProgramTypes", PaoType.getDirectLMProgramTypes());
        model.addAttribute("gearTypes", GearControlMethod.values());
        model.addAttribute("isFilterByGearSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.GEAR);
        model.addAttribute("isFilterByLoadProgramSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_PROGRAM);
        model.addAttribute("isFilterByLoadGroupSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_GROUP);
        
        if (lmSetupFilter.getFilterByType() != LmSetupFilterType.GEAR) {
            ResponseEntity<? extends Object> response = null;
            // Make API call to get filtered result.
            try {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.drSetupFilterUrl);
                response = apiRequestHelper.callAPIForParameterizedTypeObject(userContext, request, url, HttpMethod.POST,
                    LMPaoDto.class, filterCriteria);
            } catch (ApiCommunicationException e) {
                log.error(e.getMessage());
                flash.setError(new YukonMessageSourceResolvable(communicationKey));
                return "redirect:/dr/setup/list";
            } catch (RestClientException ex) {
                log.error("Error retrieving details: " + ex.getMessage());
                flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.setup.filter.error", ex.getMessage()));
                return "redirect:/dr/setup/list";
            }
            SearchResults<LMPaoDto> filteredResults  = (SearchResults<LMPaoDto>) response.getBody();
            model.addAttribute("filteredResults", filteredResults);
        } else {
            // TODO: Call to mockGearFilterResult() will be removed after YUK-19938 is implemented.
            SearchResults<GearFilterResult> filteredResults  = mockGearFilterResult();
            model.addAttribute("filteredResults", filteredResults);
        }

        // Build setup model
        model.addAttribute("viewUrlPrefix", lmSetupFilter.getFilterByType().getViewUrl());
        
        return "dr/setup/list.jsp";
    }
    
    
    public enum LMFilterSortBy implements DisplayableEnum {

        NAME(SortBy.NAME),
        TYPE(SortBy.TYPE);
        
        private final SortBy value;

        private LMFilterSortBy(SortBy value) {
            this.value = value;
        }

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.setup.filter." + name();

        }
    }
    
    public enum LMFilterGearSortBy implements DisplayableEnum {

        //TODO: The enum type here needs to be changed.
        NAME(SortBy.NAME),
        TYPE(SortBy.TYPE),
        ORDER(null),
        PROGRAM(null);
        
        //TODO: This will be changed after YUK-19938 in implemented.
        private final SortBy value;

        private LMFilterGearSortBy(SortBy value) {
            this.value = value;
        }

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.setup.filter." + name();

        }
    }
    
    //TODO: This will be removed after YUK-19938.
    private SearchResults<GearFilterResult> mockGearFilterResult() {
        SearchResults<GearFilterResult> results = SearchResults.emptyResult();
        List<GearFilterResult> list = Lists.newArrayList();
        GearFilterResult result1 = new GearFilterResult();
        result1.setGearDetails("This is a test string.This is a test string.This is a test string.This is a test string.");
        result1.setProgramId(57);
        result1.setProgramName("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWW WWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        result1.setProgramGear(mockProgramGear(1));
        list.add(result1);
        GearFilterResult result2 = new GearFilterResult();
        result2.setGearDetails("This is a test string");
        result2.setProgramId(38691);
        result2.setProgramName("Program Name2");
        result2.setProgramGear(mockProgramGear(2));
        list.add(result2);
        GearFilterResult result3 = new GearFilterResult();
        result3.setGearDetails("This is a test string");
        result3.setProgramId(39070);
        result3.setProgramName("Program Name3");
        result3.setProgramGear(mockProgramGear(3));
        list.add(result3);
        results.setResultList(list);
        results.setEndIndex(2);
        results.setHitCount(3);
        results.setStartIndex(0);
        results.setBounds(0, 20, 3);
        return results;
    }

     //TODO: This will be removed after YUK-19938.
    private ProgramGear mockProgramGear(int i) {
        ProgramGear gear = new ProgramGear();
        gear.setControlMethod(GearControlMethod.SimpleThermostatRamping);
        gear.setGearId(i);
        gear.setGearName("WWWWWWWWWWWWWWWWWWWWWWWWWWWW" + i);
        gear.setGearNumber(i);
        return gear;
    }
}
