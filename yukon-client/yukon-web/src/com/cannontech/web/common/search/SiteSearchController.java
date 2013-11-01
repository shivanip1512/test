package com.cannontech.web.common.search;

import java.util.List;

import org.codehaus.jackson.node.POJONode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.common.search.service.SiteSearchService;

@Controller
@RequestMapping("/*")
public class SiteSearchController {
    private final static String baseKey = "yukon.web.modules.tools.search.";

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
            Integer itemsPerPage, @RequestParam(defaultValue="1") int page,
            ModelMap model, YukonUserContext userContext, FlashScope flashScope) {

        itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);
        int startIndex = (page - 1) * itemsPerPage;
        searchString = siteSearchService.sanitizeSearchStr(searchString);

        SearchResults<Page> results = SearchResults.emptyResult();
        if (startIndex + itemsPerPage > SiteSearchService.MAX_SEARCH_ITEMS) {
            model.addAttribute("error", new YukonMessageSourceResolvable(baseKey + "queryOutOfRange", startIndex,
                SiteSearchService.MAX_SEARCH_ITEMS));
        } else if (searchString.length() == 0) {
            model.addAttribute("error", new YukonMessageSourceResolvable(baseKey + "emptySearch"));
        } else {
            results = siteSearchService.search(searchString, startIndex, itemsPerPage, userContext);

            // Forward to the single result's URL
            if (results.getResultCount() == 1) {
                String url = results.getResultList().get(0).getPath();
                return "redirect:" + url;
            }
        }

        model.addAttribute("searchString", searchString);
        model.addAttribute("itemsPerPage", itemsPerPage);
        model.addAttribute("results", results);

        return "siteSearch.jsp";
    }
}
