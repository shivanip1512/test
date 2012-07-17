/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11144 */
DECLARE 
    v_newDevConfigId NUMBER;
    v_internalRetries NUMBER;
    v_localTimeInt NUMBER;
    v_localTime VARCHAR2(60);
    v_enableTimeSyncs VARCHAR2(60);
    v_newItemId NUMBER;
BEGIN
    SELECT NVL(MAX(DeviceConfigurationID)+1,0) INTO v_newDevConfigId FROM DEVICECONFIGURATION;
    INSERT INTO DEVICECONFIGURATION VALUES (v_newDevConfigId, 'Default DNP Configuration', 'DNP');

    /* @start-cparm YUKON_DNP_INTERNAL_RETRIES */
    v_internalRetries := 2;
    /* @end-cparm */

    /* @start-cparm YUKON_DNP_LOCALTIME */    
    v_localTimeInt := 0;
    /* @end-cparm */

    /* @start-cparm YUKON_DNP_TIMESYNCS */
    v_enableTimeSyncs := 'false';
    /* @end-cparm */

    IF v_localTimeInt = 0 THEN
        v_localTime := 'false';
    ELSE
        v_localTime := 'true';
    END IF;

    SELECT NVL(MAX(DeviceConfigurationItemId)+1,0) INTO v_newItemId FROM DEVICECONFIGURATIONITEM;

    INSERT INTO DEVICECONFIGURATIONITEM VALUES (v_newItemId,   v_newDevConfigId, 'Internal Retries', v_internalRetries);
    SELECT MAX(DeviceConfigurationItemId)+1 INTO v_newItemId FROM DEVICECONFIGURATIONITEM;

    INSERT INTO DEVICECONFIGURATIONITEM VALUES (v_newItemId, v_newDevConfigId, 'Omit Time Request', 'false');
    SELECT MAX(DeviceConfigurationItemId)+1 INTO v_newItemId FROM DEVICECONFIGURATIONITEM;

    INSERT INTO DEVICECONFIGURATIONITEM VALUES (v_newItemId, v_newDevConfigId, 'Enable DNP Timesyncs', v_enableTimeSyncs);
    SELECT MAX(DeviceConfigurationItemId)+1 INTO v_newItemId FROM DEVICECONFIGURATIONITEM;

    INSERT INTO DEVICECONFIGURATIONITEM VALUES (v_newItemId, v_newDevConfigId, 'Local Time', v_localTime);
    SELECT MAX(DeviceConfigurationItemId)+1 INTO v_newItemId FROM DEVICECONFIGURATIONITEM;

    INSERT INTO DEVICECONFIGURATIONITEM VALUES (v_newItemId, v_newDevConfigId, 'Enable Unsolicited Messages', 'true');
    SELECT MAX(DeviceConfigurationItemId)+1 INTO v_newItemId FROM DEVICECONFIGURATIONITEM;
END;
/*
 * Find all DNP devices in the database and assign them to the new device configuration.
 */
DECLARE 
    v_newDevConfigId NUMBER;
    v_count NUMBER;
    v_deviceID NUMBER;
BEGIN
    SELECT MAX(DeviceConfigurationID) INTO v_newDevConfigId FROM DEVICECONFIGURATION;

    SELECT COUNT(*) INTO v_count
    FROM YukonPAObject 
    WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%');

    SELECT MIN(PAObjectID) INTO v_deviceID
    FROM YukonPAObject 
    WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%');

    WHILE(v_count > 0)
    LOOP
        INSERT INTO DEVICECONFIGURATIONDEVICEMAP VALUES (v_deviceID, v_newDevConfigId);

        SELECT MIN(PAObjectID) INTO v_deviceID
        FROM YukonPAObject 
        WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%') 
          AND PAObjectID > v_deviceID;

        v_count := v_count - 1;
    END LOOP;
END;
/* End YUK-11144 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 