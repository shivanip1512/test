/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11144 */
DECLARE @newDevConfigId NUMERIC;
SELECT @newDevConfigId = MAX(DeviceConfigurationID)+1 FROM DEVICECONFIGURATION;

INSERT INTO DEVICECONFIGURATION VALUES (@newDevConfigId, 'Default DNP Configuration', 'DNP')

DECLARE @internalRetries NUMERIC;
DECLARE @localTimeInt NUMERIC;
DECLARE @enableTimeSyncs varchar(60);

/* @start-cparm YUKON_DNP_INTERNAL_RETRIES */
SELECT @internalRetries = 2;
/* @end-cparm */

/* @start-cparm YUKON_DNP_LOCALTIME */
SELECT @localTimeInt = 0;
/* @end-cparm */

/* @start-cparm YUKON_DNP_TIMESYNCS */
SELECT @enableTimeSyncs = 'false';
/* @end-cparm */

DECLARE @localTime varchar(60);

IF @localTimeInt = 0
    SET @localTime = 'false'
ELSE
    SET @localTime = 'true'

DECLARE @newItemId NUMERIC;
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId,   @newDevConfigId, 'Internal Retries', @internalRetries);
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId, @newDevConfigId, 'Omit Time Request', 'false');
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId, @newDevConfigId, 'Enable DNP Timesyncs', @enableTimeSyncs);
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId, @newDevConfigId, 'Local Time', @localTime);
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;

INSERT INTO DEVICECONFIGURATIONITEM VALUES (@newItemId, @newDevConfigId, 'Enable Unsolicited Messages', 'true');
SELECT @newItemId = MAX(DeviceConfigurationItemId)+1 FROM DEVICECONFIGURATIONITEM;
/*
 * Find all DNP devices in the database and assign them to the new device configuration.
 */
DECLARE @count NUMERIC = (SELECT COUNT(*) 
                          FROM YukonPAObject 
                          WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%'))
                          
DECLARE @deviceID NUMERIC = (SELECT MIN(PAObjectID) 
                             FROM YukonPAObject 
                             WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%'))

WHILE(@count > 0)
BEGIN
    INSERT INTO DEVICECONFIGURATIONDEVICEMAP VALUES (@deviceID, @newDevConfigId)
    SET @deviceID = (SELECT MIN(PAObjectID) 
                     FROM YukonPAObject 
                     WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%') AND
                            PAObjectID > @deviceID);
    SET @count -= 1;
END
/* End YUK-11144 */

/* Start YUK-11142 */
CREATE TABLE PointControl(
    PointId NUMERIC NOT NULL,
    ControlOffset NUMERIC NOT NULL,
    ControlInhibit VARCHAR(1) NOT NULL,
    CONSTRAINT PK_PointControl PRIMARY KEY (PointId),
    CONSTRAINT FK_Point_PointControl FOREIGN KEY(PointId)
        REFERENCES POINT(POINTID)
);

CREATE TABLE PointStatusControl (
    PointId NUMERIC NOT NULL,
    ControlType VARCHAR(12) NOT NULL,
    CloseTime1 NUMERIC NOT NULL,
    CloseTime2 NUMERIC NOT NULL,
    StateZeroControl VARCHAR(100) NOT NULL,
    StateOneControl VARCHAR(100) NOT NULL,
    CommandTimeOut NUMERIC NOT NULL,
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
DROP INDEX YukonUser.Indx_YkUsIDNm;
ALTER TABLE YukonUser
ALTER COLUMN Username NVARCHAR(64) NOT NULL;
ALTER TABLE YukonUser
ALTER COLUMN Password NVARCHAR(64) NOT NULL;
CREATE INDEX Indx_YkUsIDNm ON YukonUser(
    Username ASC
);
/* End YUK-11149 */

/* Start YUK-11129 */
UPDATE ContactNotification
SET Notification = REPLACE(Notification,'-','')
WHERE SUBSTRING(Notification, 4,1) = '-'
  AND SUBSTRING(Notification, 8,1) = '-'
  AND LEN(REPLACE(Notification, '-', '')) = 10
  AND NotificationCategoryID IN (2,4,5,6,8,10);

UPDATE ContactNotification
SET Notification = REPLACE(Notification,'-','')
WHERE SUBSTRING(Notification, 4,1) = '-'
  AND LEN(REPLACE(Notification, '-', '')) = 7
  AND NotificationCategoryID IN (2,4,5,6,8,10);

UPDATE ContactNotification
SET Notification = REPLACE(REPLACE(REPLACE(Notification,')',''),'(',''),'-','')
WHERE SUBSTRING(Notification, 1,1) = '('
  AND SUBSTRING(Notification, 5,1) = ')'
  AND SUBSTRING(Notification, 9,1) = '-'
  AND LEN(REPLACE(REPLACE(REPLACE(Notification,')',''),'(',''),'-','')) = 10
  AND NotificationCategoryID IN (2,4,5,6,8,10);

UPDATE ContactNotification
SET Notification = REPLACE(REPLACE(REPLACE(REPLACE(Notification,' ',''),')',''),'(',''),'-','')
WHERE SUBSTRING(Notification, 1,1) = '('
  AND SUBSTRING(Notification, 5,1) = ')'
  AND SUBSTRING(Notification, 10,1) = '-'
  AND LEN(REPLACE(REPLACE(REPLACE(REPLACE(Notification,' ',''),')',''),'(',''),'-','')) = 10
  AND NotificationCategoryID IN (2,4,5,6,8,10);
/* End YUK-11129 */
  
/* Start YUK-11087 */
/* @start-block */
ALTER TABLE SettlementConfig DROP COLUMN CTISettlement;
ALTER TABLE SettlementConfig DROP COLUMN YukonDefId;
ALTER TABLE SettlementConfig DROP COLUMN EntryId;
GO

IF 0 = (SELECT COUNT (*) 
        FROM SettlementConfig
        WHERE ConfigId >= 0)
BEGIN

        DELETE FROM YukonListEntry WHERE ListId IN (SELECT ListId 
                                                    FROM YukonSelectionList 
                                                    WHERE ListName = 'Settlement');
        DELETE FROM YukonSelectionList WHERE ListName = 'Settlement';
        
        DROP TABLE SettlementConfig;

END
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
ALTER COLUMN UvSetPoint FLOAT NOT NULL;
ALTER TABLE DYNAMICCCTWOWAYCBC 
ALTER COLUMN OvSetPoint FLOAT NOT NULL;
ALTER TABLE DYNAMICCCTWOWAYCBC 
ALTER COLUMN NeutralCurrentAlarmSetPoint FLOAT NOT NULL;
/* End YUK-11135 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 