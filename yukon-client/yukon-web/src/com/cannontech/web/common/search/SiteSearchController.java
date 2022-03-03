package com.cannontech.web.common.search;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.common.search.service.SiteSearchService;
import com.cannontech.web.search.lucene.index.IndexBeingBuiltException;

@Controller
public class SiteSearchController {
    
    private static final Logger log = YukonLogManager.getLogger(SiteSearchController.class);
    private final static String baseKey = "yukon.web.modules.tools.search.";

    @Autowired private SiteSearchService siteSearchService;

    @RequestMapping(value="/search/autocomplete.json", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Map<String, Object>> autoComplete(@RequestParam(value="q") String query,
            YukonUserContext userContext) {
        List<Map<String,Object>> results = siteSearchService.autocomplete(query, userContext);
        return results;
    }

    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String search(@RequestParam(value="q", required=false) String query,
                         @DefaultItemsPerPage(10) PagingParameters paging,
                         ModelMap model, 
                         YukonUserContext userContext) {
        
        query = siteSearchService.sanitizeQuery(query);
        
        SearchResults<Page> results = SearchResults.emptyResult();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        
        if (startIndex + itemsPerPage > SiteSearchService.MAX_SEARCH_ITEMS) {
            model.addAttribute("error", new YukonMessageSourceResolvable(baseKey + "queryOutOfRange", startIndex,
                SiteSearchService.MAX_SEARCH_ITEMS));
        } else if (query.length() == 0) {
            model.addAttribute("error", new YukonMessageSourceResolvable(baseKey + "emptySearch"));
        } else {
            
            log.debug("searching");
            
            try {
                results = siteSearchService.search(query, startIndex, itemsPerPage, userContext);
            } catch (IndexBeingBuiltException e) {
                model.addAttribute("error", new YukonMessageSourceResolvable(baseKey + "indexBeingBuilt"));
                log.debug("got index being built exception");
            }
            
            log.debug("done searching");
            
            // Forward to the single result's URL
            if (results.getResultCount() == 1) {
                String url = results.getResultList().get(0).getPath();
                return "redirect:" + url;
            }
        }
        // Remove the \ character before returning query text to UI. \ gets added for MAC address search.
        model.addAttribute("query", query.replace("\\", ""));
        model.addAttribute("itemsPerPage", itemsPerPage);
        model.addAttribute("results", results);
        
        return "search.jsp";
    }
    
}
