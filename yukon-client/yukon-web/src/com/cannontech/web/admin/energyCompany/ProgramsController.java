package com.cannontech.web.admin.energyCompany;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramNameFilter;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper.SortBy;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.appliance.service.AssignedProgramService;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/energyCompany/applianceCategory/*")
public class ProgramsController {
    
    @Autowired private EnergyCompanyService ecService;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private AssignedProgramService assignedProgramService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private Function<AssignedProgram, Integer> idFromCategory = new Function<AssignedProgram, Integer>() {
        @Override
        public Integer apply(AssignedProgram from) {
            return from.getApplianceCategoryId();
        }
    };
    
    private String baseKey = "yukon.web.modules.adminSetup.applianceCategory.PROGRAMS";
    
    @RequestMapping("programs")
    public String programs(ModelMap model, YukonUserContext userContext, EnergyCompanyInfoFragment ecInfo) {
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        ecService.verifyViewPageAccess(userContext.getYukonUser(), ecId);
        
        List<UiFilter<AssignedProgram>> filters = Lists.newArrayList();
        Iterable<Integer> categoryIds = applianceCategoryDao.getApplianceCategoryIdsByEC(ecId);
        
        SortingParameters sorting = SortingParameters.of(SortBy.PROGRAM_NAME.name(), Direction.asc);
        PagingParameters paging = PagingParameters.of(25, 1);
        
        SearchResults<AssignedProgram> programs =
            assignedProgramService.filter(categoryIds, UiFilterList.wrap(filters), sorting, paging);
        model.addAttribute("assignedPrograms", programs);
        
        categoryIds = Iterables.transform(programs.getResultList(), idFromCategory);
        Map<Integer, ApplianceCategory> categories = applianceCategoryDao.getByApplianceCategoryIds(categoryIds);
        model.addAttribute("applianceCategoriesById", categories);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String programHeader = accessor.getMessage(baseKey + ".programNameHeader.linkText");
        String categoryHeader = accessor.getMessage(baseKey + ".applianceCategoryNameHeader.linkText");
        model.addAttribute("program", SortableColumn.of(sorting, programHeader, SortBy.PROGRAM_NAME.name()));
        model.addAttribute("category", SortableColumn.of(sorting, categoryHeader, SortBy.APPLIANCE_CATEGORY_NAME.name()));
        
        return "applianceCategory/programs.jsp";
    }
    
    @RequestMapping("programs/list")
    public String programsList(ModelMap model, 
            YukonUserContext userContext, 
            EnergyCompanyInfoFragment ecInfo,
            PagingParameters paging,
            SortingParameters sorting,
            String filterBy) {
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        ecService.verifyViewPageAccess(userContext.getYukonUser(), ecId);
        
        List<UiFilter<AssignedProgram>> filters = Lists.newArrayList();
        if (StringUtils.isNotBlank(filterBy)) filters.add(new AssignedProgramNameFilter(filterBy));
        
        if (sorting == null) {
            sorting = SortingParameters.of(SortBy.PROGRAM_NAME.name(), Direction.asc);
        }
        
        Iterable<Integer> categoryIds = applianceCategoryDao.getApplianceCategoryIdsByEC(ecId);
        SearchResults<AssignedProgram> assignedPrograms =
            assignedProgramService.filter(categoryIds, UiFilterList.wrap(filters), sorting, paging);
        model.addAttribute("assignedPrograms", assignedPrograms);
        
        categoryIds = Iterables.transform(assignedPrograms.getResultList(), idFromCategory);
        Map<Integer, ApplianceCategory> categories = applianceCategoryDao.getByApplianceCategoryIds(categoryIds);
        model.addAttribute("applianceCategoriesById", categories);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String programHeader = accessor.getMessage(baseKey + ".programNameHeader.linkText");
        String categoryHeader = accessor.getMessage(baseKey + ".applianceCategoryNameHeader.linkText");
        model.addAttribute("program", SortableColumn.of(sorting, programHeader, SortBy.PROGRAM_NAME.name()));
        model.addAttribute("category", SortableColumn.of(sorting, categoryHeader, SortBy.APPLIANCE_CATEGORY_NAME.name()));
        
        return "applianceCategory/programs.list.jsp";
    }
    
}