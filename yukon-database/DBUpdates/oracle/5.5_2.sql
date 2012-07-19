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

ALTER TABLE POINTANALOG DROP COLUMN TRANSDUCERTYPE;
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

/* Start YUK-11129 */
UPDATE ContactNotification
SET Notification = replace(Notification,'-','')
WHERE SUBSTR(Notification, 4,1) = '-'
  AND SUBSTR(Notification, 8,1) = '-'
  AND LENGTH(REPLACE(Notification, '-', '')) = 10
  AND NotificationCategoryID IN (2,4,5,6,8,10);

UPDATE ContactNotification
SET Notification = REPLACE(Notification,'-','')
WHERE SUBSTR(Notification, 4,1) = '-'
  AND LENGTH(REPLACE(Notification, '-', '')) = 7
  AND NotificationCategoryID IN (2,4,5,6,8,10);

UPDATE ContactNotification
SET Notification = REPLACE(REPLACE(REPLACE(Notification,')',''),'(',''),'-','')
WHERE SUBSTR(Notification, 1,1) = '('
  AND SUBSTR(Notification, 5,1) = ')'
  AND SUBSTR(Notification, 9,1) = '-'
  AND LENGTH(REPLACE(REPLACE(REPLACE(Notification,')',''),'(',''),'-','')) = 10
  AND NotificationCategoryID IN (2,4,5,6,8,10);

UPDATE ContactNotification
SET Notification = REPLACE(REPLACE(REPLACE(REPLACE(Notification,' ',''),')',''),'(',''),'-','')
WHERE SUBSTR(Notification, 1,1) = '('
  AND SUBSTR(Notification, 5,1) = ')'
  AND SUBSTR(Notification, 10,1) = '-'
  AND LENGTH(REPLACE(REPLACE(REPLACE(REPLACE(Notification,' ',''),')',''),'(',''),'-','')) = 10
  AND NotificationCategoryID IN (2,4,5,6,8,10);
/* End YUK-11129 */
  
/* Start YUK-11087 */
/* @start-block */
ALTER TABLE SettlementConfig DROP COLUMN CTISettlement;
ALTER TABLE SettlementConfig DROP COLUMN YukonDefId;
ALTER TABLE SettlementConfig DROP COLUMN EntryId;

DECLARE
    v_activeSettlementsCount INT;
BEGIN
    SELECT COUNT(*) INTO v_activeSettlementsCount
    FROM SettlementConfig
    WHERE ConfigId >= 0;
    
    /* If settements aren't being used by the customer lets delete their settlement tables */ 
    IF 1 > v_activeSettlementsCount THEN
        DELETE FROM YukonListEntry WHERE ListId IN (SELECT ListId 
                                                    FROM YukonSelectionList 
                                                    WHERE ListName = 'Settlement');
        DELETE FROM YukonSelectionList WHERE ListName = 'Settlement';
        
        EXECUTE IMMEDIATE 'DROP TABLE SettlementConfig';
    END IF;
END;
/
/* @end-block */
/* End YUK-11087 */

/* Start YUK-10999 */
DELETE FROM SystemLog WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM RawPointHistory WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM PointAnalog WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM PointUnit WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM PointLimits WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM PointPropertyValue WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM FDRTranslation WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM DynamicPointDispatch WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM DynamicAccumulator WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM GraphDataSeries WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM DynamicPointAlarming WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM CalcComponent WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM TagLog WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM DynamicTags WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM Display2WayData WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM CCEventLog WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM PointAlarming WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');

DELETE FROM Point WHERE PointId IN (
	SELECT PointId FROM Point p JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID
	WHERE PointType = 'Analog' AND POINTOFFSET = 40 AND yp.Type LIKE 'RFN%');
/* End YUK-10999 */

/* Start YUK-11130 */
UPDATE YukonPAObject SET Type = 'MCT-410iL' WHERE LOWER(Type) = LOWER('MCT-410IL');
UPDATE YukonPAObject SET Type = 'MCT-410cL' WHERE LOWER(Type) = LOWER('MCT-410CL'); 
UPDATE YukonPAObject SET Type = 'MCT-430A' WHERE LOWER(Type) = LOWER('MCT-430A');
UPDATE YukonPAObject SET Type = 'MCT-430S4' WHERE LOWER(Type) = LOWER('MCT-430S4');
UPDATE YukonPAObject SET Type = 'MCT-410fL' WHERE LOWER(Type) = LOWER('MCT-410FL'); 
UPDATE YukonPAObject SET Type = 'MCT-410gL' WHERE LOWER(Type) = LOWER('MCT-410GL'); 
UPDATE YukonPAObject SET Type = 'MCT-430SL' WHERE LOWER(Type) = LOWER('MCT-430SL'); 
UPDATE YukonPAObject SET Type = 'MCT-430A3' WHERE LOWER(Type) = LOWER('MCT-430A3');
UPDATE YukonPAObject SET Type = 'MCT-420fL' WHERE LOWER(Type) = LOWER('MCT-420FL'); 
UPDATE YukonPAObject SET Type = 'MCT-420cL' WHERE LOWER(Type) = LOWER('MCT-420CL'); 
UPDATE YukonPAObject SET Type = 'MCT-420fD' WHERE LOWER(Type) = LOWER('MCT-420FLD'); 
UPDATE YukonPAObject SET Type = 'MCT-420cD' WHERE LOWER(Type) = LOWER('MCT-420CLD'); 
UPDATE YukonPAObject SET Type = 'IPC-410fL' WHERE LOWER(Type) = LOWER('IPC-410AL'); 
UPDATE YukonPAObject SET Type = 'IPC-420fD' WHERE LOWER(Type) = LOWER('IPC-420AD'); 
UPDATE YukonPAObject SET Type = 'IPC-430S4e' WHERE LOWER(Type) = LOWER('IPC-430S4'); 

UPDATE DeviceTypeCommand SET DeviceType = 'MCT-420FD' WHERE DeviceType='MCT-420FLD';
UPDATE DeviceTypeCommand SET DeviceType = 'MCT-420CD' WHERE DeviceType='MCT-420CLD';
	
/* @error ignore-begin */
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-410iL' WHERE LOWER(PAOType) = LOWER('MCT-410IL');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-410cL' WHERE LOWER(PAOType) = LOWER('MCT-410CL');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-430A' WHERE LOWER(PAOType) = LOWER('MCT-430A');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-430S4' WHERE LOWER(PAOType) = LOWER('MCT-430S4');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-410fL' WHERE LOWER(PAOType) = LOWER('MCT-410FL');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-410gL' WHERE LOWER(PAOType) = LOWER('MCT-410GL');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-430SL' WHERE LOWER(PAOType) = LOWER('MCT-430SL');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-430A3' WHERE LOWER(PAOType) = LOWER('MCT-430A3');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-420fL' WHERE LOWER(PAOType) = LOWER('MCT-420FL');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-420cL' WHERE LOWER(PAOType) = LOWER('MCT-420CL');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-420fD' WHERE LOWER(PAOType) = LOWER('MCT-420FLD');
UPDATE AMIBillingReadLookup SET PAOType = 'MCT-420cD' WHERE LOWER(PAOType) = LOWER('MCT-420CLD');
/* @error ignore-end */
/* End YUK-11130 */

/* Start YUK-11135 */
ALTER TABLE DYNAMICCCTWOWAYCBC 
MODIFY UvSetPoint FLOAT;
ALTER TABLE DYNAMICCCTWOWAYCBC 
MODIFY OvSetPoint FLOAT;
ALTER TABLE DYNAMICCCTWOWAYCBC 
MODIFY NeutralCurrentAlarmSetPoint FLOAT;
/* End YUK-11135 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 