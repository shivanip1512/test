package com.cannontech.dr.pxmw.model;

import java.util.List;

import org.springframework.http.HttpStatus;

//Data retrieval URLs
public enum PxMWRetrievalUrl {
    DEVICE_PROFILE_BY_GUID_V1(PxMWVersion.V1, "/api/v1/deviceprofile/{id}",
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#device-profile",
            List.of(HttpStatus.OK)),
    DEVICES_BY_SITE_V1(PxMWVersion.V1, "/api/v1/sites/{id}/devices",
            "http://wordpress-prod.tcc.etn.com/wordpress/wp-content/docs/RestApi/IoT.html#site-site-get-1",
            List.of(HttpStatus.OK, HttpStatus.NOT_FOUND)),
    DEVICE_CHANNEL_DETAILS_V1(PxMWVersion.V1, "/api/v1/device/{deviceId}/channels",
            "https://was-all-apim-eus-dev.portal.azure-api.net/docs/services/device-management-function-app/operations/get-getdevicechanneldetails-1?",
            List.of(HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND, HttpStatus.FORBIDDEN));

    private PxMWVersion version;
    private String suffix;
    private String doc;
    private List<HttpStatus> statuses;

    PxMWRetrievalUrl(PxMWVersion version, String suffix, String doc, List<HttpStatus> statuses) {
        this.suffix = suffix;
        this.doc = doc;
        this.statuses = statuses;
        this.version = version;
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

    public PxMWVersion getVersion() {
        return version;
    }
}

