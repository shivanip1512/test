/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11144 */
/* @start-block */
DECLARE @newDevConfigId NUMERIC;
SELECT @newDevConfigId = -1;

INSERT INTO DEVICECONFIGURATION VALUES (@newDevConfigId, 'Default DNP Configuration', 'DNP');

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
    SET @localTime = 'false';
ELSE
    SET @localTime = 'true';

DECLARE @newItemId NUMERIC;
SELECT @newItemId = ISNULL(MAX(DeviceConfigurationItemId)+1,0) FROM DEVICECONFIGURATIONITEM;

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

DECLARE @count NUMERIC = (SELECT COUNT(*) 
                          FROM YukonPAObject 
                          WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%'));
                          
DECLARE @deviceID NUMERIC = (SELECT MIN(PAObjectID) 
                             FROM YukonPAObject 
                             WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%'));
WHILE(@count > 0)
BEGIN
    INSERT INTO DEVICECONFIGURATIONDEVICEMAP VALUES (@deviceID, @newDevConfigId)
    SET @deviceID = (SELECT MIN(PAObjectID) 
                     FROM YukonPAObject 
                     WHERE (Type LIKE 'CBC 702%' OR Type LIKE 'CBC 802%' OR TYPE LIKE '%DNP%') AND
                            PAObjectID > @deviceID);
    SET @count -= 1;
END;
/* @end-block */
/* End YUK-11144 */

/* Start YUK-11142 */
CREATE TABLE PointControl(
    PointId NUMERIC NOT NULL,
    ControlOffset NUMERIC NOT NULL,
    ControlInhibit VARCHAR(1) NOT NULL,
    CONSTRAINT PK_PointControl PRIMARY KEY (PointId)
);

ALTER TABLE PointControl
    ADD CONSTRAINT FK_PointCont_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId);
GO
CREATE TABLE PointStatusControl (
    PointId NUMERIC NOT NULL,
    ControlType VARCHAR(12) NOT NULL,
    CloseTime1 NUMERIC NOT NULL,
    CloseTime2 NUMERIC NOT NULL,
    StateZeroControl VARCHAR(100) NOT NULL,
    StateOneControl VARCHAR(100) NOT NULL,
    CommandTimeOut NUMERIC NOT NULL,
    CONSTRAINT PK_PointStatusControl PRIMARY KEY (PointId)
);

ALTER TABLE PointStatusControl
    ADD CONSTRAINT FK_PointStatusCont_PointCont FOREIGN KEY (PointId)
        REFERENCES PointControl (PointId);
GO        
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
CREATE INDEX Indx_YukonUser_Username ON YukonUser(
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
ALTER TABLE SettlementConfig DROP COLUMN CTISettlement;
ALTER TABLE SettlementConfig DROP COLUMN YukonDefId;
ALTER TABLE SettlementConfig DROP COLUMN EntryId;
GO
/* @start-block */
IF 0 = (SELECT COUNT (*) 
        FROM SettlementConfig
        WHERE ConfigId >= 0)
BEGIN

        DELETE FROM YukonListEntry WHERE ListId IN (SELECT ListId 
                                                    FROM YukonSelectionList 
                                                    WHERE ListName = 'Settlement');
        DELETE FROM YukonSelectionList WHERE ListName = 'Settlement';
        
        DROP TABLE SettlementConfig;

END;
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

/* Start YUK-11123 */
INSERT INTO DeviceTypeCommand VALUES(-975, -27, 'RFN EXPRESSCOM GROUP', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-976, -28, 'RFN EXPRESSCOM GROUP', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-977, -133, 'RFN EXPRESSCOM GROUP', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-978, -134, 'RFN EXPRESSCOM GROUP', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-979, -135, 'RFN EXPRESSCOM GROUP', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-980, -143, 'RFN EXPRESSCOM GROUP', 6, 'Y', -1);
/* End YUK-11123 */

/* Start YUK-11086 */
ALTER TABLE RegulatorToZoneMapping
DROP COLUMN Phase;
ALTER TABLE PointToZoneMapping
DROP COLUMN Phase;
/* End YUK-11086 */

/* Start YUK-11062 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId = -10009; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -10009; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -10009;
/* End YUK-11062 */

/* Start YUK-11154 */
INSERT INTO YukonGroup SELECT MAX(GroupId)+1, 'Password Policy Grp', 'A set of roles that define the password policy rules.' FROM YukonGroup;

/* Add a set of (default) Password Policy Role Properties */
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11001, '0' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11002, '0' FROM YukonGroup YG ;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11003, '0' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11004, '0' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11005, '0' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11006, '0' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11050, '0' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11051, 'true' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11052, 'true' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11053, 'true' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11054, 'true' FROM YukonGroup YG;
INSERT INTO YukonGroupRole SELECT (SELECT MAX(GroupRoleID)+1 FROM YukonGroupRole), MAX(GroupId), -110, -11055, 'true' FROM YukonGroup YG;

INSERT INTO YukonUserGroup
SELECT Distinct YUG.UserId, (SELECT MAX(GroupID) FROM YukonGroup)
FROM YukonUserGroup YUG
JOIN YukonGroupRole YGR ON YUG.GroupId = YGR.GroupId
WHERE YUG.UserId NOT IN (SELECT YUG2.UserId
                         FROM YukonUserGroup YUG2
                           JOIN YukonGroupRole YGR2 ON YUG2.GroupId = YGR2.GroupId
                         WHERE YGR2.RoleId = -110);
/* End YUK-11154 */

/* Start YUK-11151 */
ALTER TABLE CCSEASONSTRATEGYASSIGNMENT 
DROP CONSTRAINT FK_CCSSA_PAOID;

ALTER TABLE CCSEASONSTRATEGYASSIGNMENT
    ADD CONSTRAINT FK_CCSeasonStratAssign_PAO FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
GO
ALTER TABLE CCHOLIDAYSTRATEGYASSIGNMENT 
DROP CONSTRAINT FK_CCHSA_PAOID;

ALTER TABLE CCHOLIDAYSTRATEGYASSIGNMENT
    ADD CONSTRAINT FK_CCHolidayStratAssign_PAO FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
GO
/* End YUK-11151 */
                         
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.5', 'Garrett D', '22-JUL-2012', 'Latest Update', 2 );