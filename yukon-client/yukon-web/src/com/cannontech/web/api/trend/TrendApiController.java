package com.cannontech.web.api.trend;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.service.TrendService;

@RestController
@RequestMapping("/trends")
public class TrendApiController {
    @Autowired TrendService trendService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody TrendModel trend) {
        TrendModel createdTrend = trendService.create(trend);
        return new ResponseEntity<>(createdTrend, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, Integer>> delete(@PathVariable int id) {
        int trendId = trendService.delete(id);
        HashMap<String, Integer> trendIdMap = new HashMap<>();
        trendIdMap.put("trendId", trendId);
        return new ResponseEntity<>(trendIdMap, HttpStatus.OK);
    }
    
    
}
