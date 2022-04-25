package com.cannontech.web.api.picker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.Picker;
import com.cannontech.web.picker.service.PickerFactory;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/picker")
public class PickerApiController {

    @Autowired private PickerFactory pickerFactory;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PickerIdSearchApiValidator pickerIdSearchValidator;
    @Autowired private PickerSearchApiValidator pickerSearchValidator;

    @GetMapping("/build/{type}")
    public ResponseEntity<Object> search(@PathVariable String type) {

        Picker<?> picker = pickerFactory.getPicker(type);

        return new ResponseEntity<>(picker, HttpStatus.OK);

    }

    @PostMapping("/idSearch")
    public ResponseEntity<Object> idSearch(@Valid @RequestBody PickerIdSearchCriteria searchIdCriteria,
            HttpServletRequest request, YukonUserContext userContext) {

        Picker<?> picker = pickerFactory.getPicker(searchIdCriteria.getType());
        SearchResults<?> searchResult = picker.search(Lists.newArrayList(searchIdCriteria.getInitialIds()),
                searchIdCriteria.getExtraArgs(), userContext);

        searchResult = resolveDisplayables(searchResult, userContext);

        return new ResponseEntity<>(searchResult, HttpStatus.OK);

    }

    @PostMapping("/search")
    public ResponseEntity<Object> search(@Valid @RequestBody PickerSearchCriteria searchCriteria, HttpServletRequest request,
            YukonUserContext userContext) {

        Picker<?> picker = pickerFactory.getPicker(searchCriteria.getType());
        SearchResults<?> searchResult = picker.search(searchCriteria.getQueryString(), searchCriteria.getStartCount(),
                searchCriteria.getCount(), searchCriteria.getExtraArgs(), "pao", Direction.asc, userContext);

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

    @InitBinder("pickerSearchCriteria")
    public void setupPickerSearchBinder(WebDataBinder binder) {
        binder.addValidators(pickerSearchValidator);
    }

    @InitBinder("pickerIdSearchCriteria")
    public void setupPickerSearchIdBinder(WebDataBinder binder) {
        binder.addValidators(pickerIdSearchValidator);
    }
}
