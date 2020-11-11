package com.cannontech.web.dev;

import java.util.List;

import org.springframework.http.HttpStatus;

// Data retrieval URLs
public enum RetrievalUrl {
    DEVICE_PROFILE_BY_GUID_V1("/api/v1/deviceprofile/{id}",
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#device-profile",
            List.of(HttpStatus.OK)),
    DEVICES_BY_SITE_V1("/api/v1/sites/{id}/devices{?recursive,includeDetail}",
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#device-profile",
            List.of(HttpStatus.OK, HttpStatus.NOT_FOUND));



    private String suffix;
    private String doc;
    private List<HttpStatus> statuses;



    RetrievalUrl(String suffix, String doc, List<HttpStatus> statuses) {
        this.suffix = suffix;
        this.doc = doc;
        this.statuses = statuses;
    }



    public String getSuffix() {
        return suffix;
    }
    
    public String getDoc() {
        return doc;
    }
    
    public List<HttpStatus> getStatuses() {
        return statuses;
    }
}
