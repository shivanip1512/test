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

/* Start YUK-11142 */
CREATE TABLE PointControl(
    PointId NUMBER NOT NULL,
    ControlOffset NUMBER NOT NULL,
    ControlInhibit VARCHAR2(1) NOT NULL,
    CONSTRAINT PK_PointControl PRIMARY KEY (PointId),
    CONSTRAINT FK_Point_PointControl FOREIGN KEY(PointId)
        REFERENCES POINT(POINTID)
);

CREATE TABLE PointStatusControl (
    PointId NUMBER NOT NULL,
    ControlType VARCHAR2(12) NOT NULL,
    CloseTime1 NUMBER NOT NULL,
    CloseTime2 NUMBER NOT NULL,
    StateZeroControl VARCHAR2(100) NOT NULL,
    StateOneControl VARCHAR2(100) NOT NULL,
    CommandTimeOut NUMBER NOT NULL,
    CONSTRAINT PK_PointStatusControl PRIMARY KEY (POINTID),
    CONSTRAINT FK_PointCntrl_PointStatusCntrl FOREIGN KEY(POINTID)
        REFERENCES PointControl (PointId)
);

INSERT INTO PointControl (PointId, ControlOffset, ControlInhibit)
   (SELECT PointId, ControlOffset, ControlInhibit
    FROM PointStatus
    WHERE LOWER(ControlType) != 'none');   

INSERT INTO PointStatusControl (PointId, ControlType, CloseTime1, CloseTime2, StateZeroControl, StateOneControl, CommandTimeOut)
   (SELECT PointId, ControlType, CloseTime1, CloseTime2, StateZeroControl, StateOneControl, CommandTimeOut
    FROM PointStatus
    WHERE LOWER(ControlType) != 'none');

ALTER TABLE PointStatus DROP COLUMN ControlOffset;
ALTER TABLE PointStatus DROP COLUMN ControlType;
ALTER TABLE PointStatus DROP COLUMN ControlInhibit;
ALTER TABLE PointStatus DROP COLUMN CloseTime1;
ALTER TABLE PointStatus DROP COLUMN CloseTime2;
ALTER TABLE PointStatus DROP COLUMN StateZeroControl;
ALTER TABLE PointStatus DROP COLUMN StateOneControl;
ALTER TABLE PointStatus DROP COLUMN CommandTimeout;
/* End YUK-11142 */

/* Start YUK-11149 */
/* @error ignore-begin */
DROP INDEX INDX_YukonUser_Username_FB;
/* @error ignore-end */
ALTER TABLE YukonUser
MODIFY Username NVARCHAR2(64);
ALTER TABLE YukonUser
MODIFY Password NVARCHAR2(64);
/* @error ignore-begin */
CREATE INDEX INDX_YukonUser_Username_FB ON YukonUser (
    LOWER(Username)
);
/* @error ignore-end */
/* End YUK-11149 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 