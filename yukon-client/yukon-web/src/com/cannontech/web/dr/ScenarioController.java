package com.cannontech.web.dr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.model.DisplayablePaoComparator;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.ProgramControllerHelper.ProgramListBackingBean;

@Controller
public class ScenarioController {
    private ScenarioDao scenarioDao = null;
    private FilterService filterService;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;

    private static RowMapperWithBaseQuery<DisplayablePao> rowMapper =
        new RowMapperWithBaseQuery<DisplayablePao>() {

            @Override
            public SqlFragmentSource getBaseQuery() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("SELECT paObjectId, paoName FROM yukonPAObject"
                    + " WHERE type = ");
                retVal.appendArgument(PaoType.LM_SCENARIO.getDbString());
                return retVal;
            }

            @Override
            public boolean needsWhere() {
                return false;
            }

            @Override
            public DisplayablePao mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                        PaoType.LM_SCENARIO);
                DisplayablePao retVal = new DisplayableDevice(paoId,
                                                              rs.getString("paoName"));
                return retVal;
            }
        };

    @RequestMapping("/scenario/list")
    public String list(ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("filter") ListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        // TODO:  validation on backing bean

        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new NameFilter(backingBean.getName()));
        }

        Comparator<DisplayablePao> sorter =
            new DisplayablePaoComparator(userContext, backingBean.getDescending());
        int startIndex = (backingBean.getPage() - 1) * backingBean.getItemsPerPage();
        SearchResult<DisplayablePao> searchResult =
            filterService.filter(filters, sorter, null, startIndex,
                                 backingBean.getItemsPerPage(), rowMapper);

        modelMap.addAttribute("searchResult", searchResult);
        modelMap.addAttribute("scenarios", searchResult.getResultList());
        modelMap.addAttribute("backingBean", backingBean);

        return "/dr/scenario/list.jsp";
    }

    @RequestMapping("/scenario/detail")
    public String detail(int scenarioId, ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("filter") ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        DisplayablePao scenario = scenarioDao.getScenario(scenarioId);
        if (false && !paoAuthorizationService.isAuthorized(userContext.getYukonUser(),
                                                 Permission.LM_VISIBLE, scenario)) {
            throw new NotAuthorizedException("Scenario " + scenarioId
                                             + " is not visible to user.");
        }
        modelMap.addAttribute("scenario", scenario);

        UiFilter<DisplayablePao> detailFilter = new ForScenarioFilter(scenarioId);
        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               result, status, detailFilter);

        return "/dr/scenario/detail.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext);
    }

    @Autowired
    public void setScenarioDao(ScenarioDao scenarioDao) {
        this.scenarioDao = scenarioDao;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

    @Autowired
    public void setProgramControllerHelper(
            ProgramControllerHelper programControllerHelper) {
        this.programControllerHelper = programControllerHelper;
    }
}
