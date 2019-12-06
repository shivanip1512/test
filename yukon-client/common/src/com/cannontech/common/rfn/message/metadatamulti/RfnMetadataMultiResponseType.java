package com.cannontech.common.rfn.message.metadatamulti;

public enum RfnMetadataMultiResponseType {
    OK,
    YUKON_INPUT_ERROR, // e.x., null or invalid parameters in the request
    NM_DB_ERROR,
    NM_SERVICE_ERROR,
}