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
import com.google.common.collect.ImmutableMap;
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
    private Map<String, SortBy> sorters = ImmutableMap.of(
            "program", SortBy.PROGRAM_NAME, 
            "category", SortBy.APPLIANCE_CATEGORY_NAME);
    
    private String baseKey = "yukon.web.modules.adminSetup.applianceCategory.PROGRAMS";
    
    @RequestMapping("programs")
    public String programs(ModelMap model, YukonUserContext userContext, EnergyCompanyInfoFragment ecInfo) {
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        ecService.verifyViewPageAccess(userContext.getYukonUser(), ecId);

        List<UiFilter<AssignedProgram>> filters = Lists.newArrayList();
        SortBy sortBy = SortBy.PROGRAM_NAME;
        Iterable<Integer> categoryIds = applianceCategoryDao.getApplianceCategoryIdsByEC(ecId);
        SearchResults<AssignedProgram> programs =
            assignedProgramService.filter(categoryIds, UiFilterList.wrap(filters), sortBy, false, 0, 25);
        model.addAttribute("assignedPrograms", programs);

        categoryIds = Iterables.transform(programs.getResultList(), idFromCategory);
        Map<Integer, ApplianceCategory> categories = applianceCategoryDao.getByApplianceCategoryIds(categoryIds);
        model.addAttribute("applianceCategoriesById", categories);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String programHeader = accessor.getMessage(baseKey + ".programNameHeader.linkText");
        String categoryHeader = accessor.getMessage(baseKey + ".applianceCategoryNameHeader.linkText");
        model.addAttribute("program", SortableColumn.of(Direction.asc, true, programHeader, "program"));
        model.addAttribute("category", SortableColumn.of(Direction.asc, false, categoryHeader, "category"));

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
        
        boolean desc = false;
        SortBy sortBy = SortBy.PROGRAM_NAME;
        if (sorting != null) {
            sortBy = sorters.get(sorting.getSort());
            desc = sorting.getDirection() == Direction.desc;
        }
        
        Direction dir = desc ? Direction.desc : Direction.asc;

        Iterable<Integer> categoryIds = applianceCategoryDao.getApplianceCategoryIdsByEC(ecId);
        SearchResults<AssignedProgram> assignedPrograms =
            assignedProgramService.filter(categoryIds, UiFilterList.wrap(filters), sortBy, desc, paging.getStartIndex(), 
                    paging.getItemsPerPage());
        model.addAttribute("assignedPrograms", assignedPrograms);

        categoryIds = Iterables.transform(assignedPrograms.getResultList(), idFromCategory);
        Map<Integer, ApplianceCategory> categories = applianceCategoryDao.getByApplianceCategoryIds(categoryIds);
        model.addAttribute("applianceCategoriesById", categories);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String programHeader = accessor.getMessage(baseKey + ".programNameHeader.linkText");
        String categoryHeader = accessor.getMessage(baseKey + ".applianceCategoryNameHeader.linkText");
        model.addAttribute("program", SortableColumn.of(dir, sortBy == SortBy.PROGRAM_NAME, programHeader, "program"));
        model.addAttribute("category", SortableColumn.of(dir, sortBy != SortBy.PROGRAM_NAME, categoryHeader, "category"));
        
        return "applianceCategory/programs.list.jsp";
    }
}
