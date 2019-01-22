package com.cannontech.common.bulk.collection.device.model;

/**
 * This enum defines the URLs used for redirecting to Collection Actions
 */
public enum CollectionActionUrl {

    COLLECTION_ACTIONS("/bulk/collectionActions"),
    MAPPING("/tools/map"),
    SEND_COMMAND("/group/commander/collectionProcessing"),
    READ_ATTRIBUTE("/group/groupMeterRead/homeCollection");
    
    private String url;
    
    private CollectionActionUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
