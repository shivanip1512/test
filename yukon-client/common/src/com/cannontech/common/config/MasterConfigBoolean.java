package com.cannontech.common.config;

public enum MasterConfigBoolean {
    
    BULK_IMPORTER_SINGLE_GROUP, // Option to limit membership to only 1 child group per parent group.
    CYME_ENABLED,
    DEFAULT_ENERGY_COMPANY_EDIT,
    DEVELOPMENT_MODE,
    DB_JAVA_TEST_ON_BORROW,
    DB_JAVA_TEST_ON_RETURN,
    DB_JAVA_TEST_WHILE_IDLE,
    DB_SSL_ENABLED,
    DIGI_ENABLED,
    DIGI_SMARTPOLL_ENABLED,
    DISABLE_ANALYTICS,
    ENABLE_GENERIC_UPLOAD, // YUK-11376
    ENABLE_INVENTORY_SAVE_TO_FILE,
    ENABLE_MIGRATE_ENROLLMENT, // YUK-11376
    ENABLE_SETTLEMENTS,
    ENABLE_WEB_DEBUG_PAGES,
    ENABLE_ESTIMATED_LOAD,
    I18N_DESIGN_MODE,
    MAP_DEVICES_DEV_MODE,  //If true, use Dev Key for Mapping, If false, use Prod key.  If key is not found, check for DEVELOPMENT_MODE OR DISABLE_ANALYTICS.  If either are true, use Dev Key otherwise Prod key
    MSP_ENABLE_ALTGROUP_EXTENSION, // YUK-10787
    MSP_ENABLE_SUBSTATIONNAME_EXTENSION, // YUK-10787
    MSP_EXACT_SEARCH_PAONAME, // YUK-12369
    MULTISPEAK_DISABLED,
    PAUSE_FOR_DISPATCH_MESSAGE_BACKUP,
    POINT_UPDATE_REGISTRATION, //YUK-14972
    RF_DATA_STREAMING_ENABLED,  //Enables Data Streaming Functionality
    SCHEDULED_REQUEST_TIMEOUT_IN_MINUTES, //Used for debugging Web schedules (YUK-14711)
    SEND_INDIVIDUAL_SWITCH_CONFIG, // YUK-13985 For GRE's old config by range feature
    SIMULATOR_DISCREPANCY_REPORT_IGNORE_TIME_CHECK, //pending reports and remove option will show up without the time check 
    USE_OLD_FORGOTTEN_PASSWORD_PAGE,
    USER_FEEDBACK_ENABLED,
    VIRTUAL_PROGRAMS,
    ;
    
}