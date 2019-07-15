package com.cannontech.web.api.picker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.Picker;
import com.cannontech.web.picker.service.PickerFactory;
import com.cannontech.web.util.YukonUserContextResolver;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/picker")
public class PickerApiController {
    
    @Autowired private PickerFactory pickerFactory;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private YukonUserContextResolver contextResolver;

    @GetMapping("/build/{type}")
    public ResponseEntity<Object> search(@PathVariable String type) {

        Picker<?> picker = pickerFactory.getPicker(type);
        
        return new ResponseEntity<>(picker, HttpStatus.OK);

    }
    
    @PostMapping("/idSearch")
    public ResponseEntity<Object> idSearch(@RequestBody PickerIdSearchCriteria searchCriteria, HttpServletRequest request) {
        
        LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();
        YukonUserContext userContext = contextResolver.resolveContext(user, request);

        Picker<?> picker = pickerFactory.getPicker(searchCriteria.getType());
        SearchResults<?> searchResult = picker.search(Lists.newArrayList(searchCriteria.getInitialIds()), 
                                                      searchCriteria.getExtraArgs(), userContext);

        searchResult = resolveDisplayables(searchResult, userContext);
        
        return new ResponseEntity<>(searchResult, HttpStatus.OK);

    }
    
    @PostMapping("/search")
    public ResponseEntity<Object> search(@RequestBody PickerSearchCriteria searchCriteria, HttpServletRequest request) {
        
        LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();
        YukonUserContext userContext = contextResolver.resolveContext(user, request);

        Picker<?> picker = pickerFactory.getPicker(searchCriteria.getType());
        SearchResults<?> searchResult = picker.search(searchCriteria.getQueryString(), searchCriteria.getStartCount(), searchCriteria.getCount(), 
                                                      searchCriteria.getExtraArgs(), userContext);

        searchResult = resolveDisplayables(searchResult, userContext);
        
        return new ResponseEntity<>(searchResult, HttpStatus.OK);

    }

    /**
     * Special support for maps. This allows us to support enums and resolve displayable objs
     */
    private SearchResults<?> resolveDisplayables(SearchResults<?> searchResult, YukonUserContext context) {
        if (searchResult.getResultList().isEmpty()) {
            return searchResult;
        }

        if (searchResult.getResultList().get(0) instanceof Map) {
            List<Map<String, String>> newHits = new ArrayList<>();
            for (Object hitObj : searchResult.getResultList()) {
                Map<String, String> newHit = new HashMap<>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) hitObj).entrySet()) {
                    Object propertyName = entry.getKey();
                    Object propertyValue = entry.getValue();
                    String translatedValue = objectFormattingService.formatObjectAsString(propertyValue, context);
                    newHit.put((String) propertyName, translatedValue);
                }
                newHits.add(newHit);
            }
            searchResult = SearchResults.pageBasedForSublist(newHits, searchResult.getCurrentPage(),
                searchResult.getCount(), searchResult.getHitCount());
        }
        
        return searchResult;
    }
}
