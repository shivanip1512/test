package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.api.dr.setup.service.impl.LMSetupFilterServiceImpl;
import com.cannontech.web.security.annotation.CheckRole;

@RestController
@CheckRole(YukonRole.DEMAND_RESPONSE)
@RequestMapping("/dr/setup")
public class LMSetupFilterApiController {
    @Autowired private LMSetupFilterServiceImpl filterService;
    
    @PostMapping("/filter")
    public ResponseEntity<Object> filter(@RequestBody FilterCriteria<LMSetupFilter> filterCriteria) {
        SearchResults<LMPaoDto> filteredResults = filterService.filter(filterCriteria);
        return new ResponseEntity<>(filteredResults, HttpStatus.OK);
    }
}
