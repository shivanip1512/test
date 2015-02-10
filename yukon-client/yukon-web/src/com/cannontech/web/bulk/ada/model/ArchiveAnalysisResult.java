package com.cannontech.web.bulk.ada.model;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.search.result.SearchResults;

public class ArchiveAnalysisResult {

    private DeviceCollection deviceCollection;
    private Analysis analysis;
    private SearchResults<AdaDevice> searchResult;
    
    public ArchiveAnalysisResult(Analysis analysis) {
        this.analysis = analysis;
    }
    
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    
    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public void setSearchResult(SearchResults<AdaDevice> searchResult) {
        this.searchResult = searchResult;
    }

    public SearchResults<AdaDevice> getSearchResult() {
        return searchResult;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

}