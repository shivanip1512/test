package com.cannontech.web.bulk.model;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.search.SearchResult;

public class ArchiveAnalysisResult {

    private DeviceCollection deviceCollection;
    private Analysis analysis;
    private SearchResult<DeviceArchiveData> searchResult;
    
    public ArchiveAnalysisResult(Analysis analysis) {
        this.analysis = analysis;
    }
    
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    
    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public void setSearchResult(SearchResult<DeviceArchiveData> searchResult) {
        this.searchResult = searchResult;
    }

    public SearchResult<DeviceArchiveData> getSearchResult() {
        return searchResult;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

}