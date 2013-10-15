package com.cannontech.web.common.search;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.node.POJONode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.common.search.service.SiteSearchService;

@Controller
@RequestMapping("/*")
// @CheckRole({YukonRole.METERING,YukonRole.APPLICATION_BILLING,YukonRole.SCHEDULER,YukonRole.DEVICE_ACTIONS})
public class SiteSearchController {
    @Autowired private SiteSearchService siteSearchService;

    @RequestMapping(value="/autocomplete.json", method=RequestMethod.GET)
    public @ResponseBody POJONode autoComplete(String type, @RequestParam(value="q") String searchString,
            YukonUserContext userContext) {
        List<String> results = siteSearchService.autocomplete(searchString, userContext);
        POJONode response = new POJONode(results);
        return response;
    }

    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String search(@RequestParam(value="q", required=false) String searchString,
            @RequestParam(defaultValue="10") int itemsPerPage, @RequestParam(defaultValue="1") int page,
            ModelMap model, YukonUserContext userContext) {
        int start = (page - 1) * itemsPerPage;

        searchString = StringUtils.trimToEmpty(searchString);
        SearchResults<Page> results;
        if (searchString.length() != 0) {
            results = siteSearchService.search(searchString, start, itemsPerPage, userContext);
        } else {
            results = SearchResults.emptyResult();
        }

        // Forward to the single result's URL
        if (results.getResultCount() == 1) {
            String url = results.getResultList().get(0).getPath();
            return "redirect:" + url;
        }

        model.addAttribute("searchString", searchString);
        model.addAttribute("itemsPerPage", itemsPerPage);
        model.addAttribute("results", results);

        return "siteSearch.jsp";
    }
}
