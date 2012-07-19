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


/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 