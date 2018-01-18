/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-17045 */
INSERT INTO YukonListEntry 
SELECT (SELECT MAX(EntryId) + 1 
        FROM   YukonListEntry 
        WHERE  EntryId < 10000), 
       1005, 
       0, 
       'LCR-6700(RFN)', 
       1337 
FROM   dual 
WHERE  NOT EXISTS (SELECT * 
                   FROM   YukonListEntry 
                   WHERE  EntryText = 'LCR-6700(RFN)'); 
/* End YUK-17045 */

/* Start YUK-17051 */
ALTER TABLE DISPLAY2WAYDATA DROP CONSTRAINT FK_Display2WayData_Display;
ALTER TABLE DISPLAYCOLUMNS DROP CONSTRAINT FK_DisplayColumns_Display;
ALTER TABLE TemplateDisplay DROP CONSTRAINT FK_TemplateDisplay_DISPLAY;

ALTER TABLE DISPLAY2WAYDATA
   ADD CONSTRAINT FK_Display2WayData_Display FOREIGN KEY (DISPLAYNUM)
      REFERENCES DISPLAY (DISPLAYNUM)
      ON DELETE CASCADE;

ALTER TABLE DISPLAYCOLUMNS
   ADD CONSTRAINT FK_DisplayColumns_Display FOREIGN KEY (DISPLAYNUM)
      REFERENCES DISPLAY (DISPLAYNUM)
      ON DELETE CASCADE;

ALTER TABLE TemplateDisplay
   ADD CONSTRAINT FK_TemplateDisplay_DISPLAY FOREIGN KEY (DisplayNum)
      REFERENCES DISPLAY (DISPLAYNUM)
      ON DELETE CASCADE;
/* End YUK-17051 */

/* Start YUK-17096 */
DROP INDEX INDX_CCEventLog_PointId;
/* End YUK-17096 */

/* Start YUK-17113 */
CREATE TABLE DBUpdates  (
   UpdateId             VARCHAR2(50)                    NOT NULL,
   Version              VARCHAR2(10),
   InstallDate          DATE,
   CONSTRAINT PK_DBUPDATES PRIMARY KEY (UpdateId)
);
/* End YUK-17113 */

/* Start YUK-17066 */
UPDATE 
(SELECT PU.decimalplaces FROM POINT P 
    JOIN YukonPAObject YPO ON P.PAObjectID = YPO.PAObjectID
    JOIN POINTUNIT PU ON PU.POINTID = P.POINTID 
WHERE YPO.Type IN ('RFW-201', 'RFW-205') AND P.POINTTYPE = 'Analog' 
    AND P.POINTOFFSET = 6 AND PU.DECIMALPLACES = 3) t 
SET t.decimalplaces = 0;
/* End YUK-17066 */

/* Start YUK-17159 */
UPDATE ExtraPaoPointAssignment
SET Attribute = 'VOLTAGE'
WHERE Attribute = 'VOLTAGE_Y';

UPDATE ExtraPaoPointAssignment
SET Attribute = 'SOURCE_VOLTAGE'
WHERE Attribute = 'VOLTAGE_X';

CREATE INDEX INDX_EPPA_PointId ON ExtraPaoPointAssignment (
    PointId ASC
);
/* End YUK-17159 */

/* Start YUK-17166 */
UPDATE YukonGroupRole 
SET Value = 'CREATE' 
WHERE RolePropertyID = -21200;

UPDATE YukonRoleProperty
SET 
    KeyName = 'MACS Scripts', 
    DefaultValue = 'UPDATE', 
    Description = 'Controls the ability to view, start/stop, enable/disable, edit, create, delete for MACS Script.'
WHERE RolePropertyID = -21200;
/* End YUK-17166 */

/* Start YUK-17126 */
CREATE TABLE SmartNotificationSub  (
   SubscriptionId       NUMBER                          NOT NULL,
   UserId               NUMBER                          NOT NULL,
   Type                 VARCHAR2(30)                    NOT NULL,
   Media                VARCHAR2(30)                    NOT NULL,
   Frequency            VARCHAR2(30)                    NOT NULL,
   Verbosity            VARCHAR2(30)                    NOT NULL,
   Recipient            VARCHAR2(100)                   NOT NULL,
   CONSTRAINT PK_SmartNotificationSub PRIMARY KEY (SubscriptionId)
);

CREATE TABLE SmartNotificationSubParam  (
   SubscriptionId       NUMBER                          NOT NULL,
   Name                 VARCHAR2(30)                    NOT NULL,
   Value                VARCHAR2(100)                   NOT NULL,
   CONSTRAINT PK_SmartNotificationSubParam PRIMARY KEY (SubscriptionId, Name, Value)
);

ALTER TABLE SmartNotificationSub
   ADD CONSTRAINT FK_SmartNotifSub_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserID)
      ON DELETE CASCADE;

ALTER TABLE SmartNotificationSubParam
   ADD CONSTRAINT FK_SmartNotifSP_SmartNotifS FOREIGN KEY (SubscriptionId)
      REFERENCES SmartNotificationSub (SubscriptionId)
      ON DELETE CASCADE;

CREATE INDEX INDX_SmartNotifSub_UserId_Type ON SmartNotificationSub (
   UserId ASC,
   Type ASC
);
/* End YUK-17126 */

/* Start YUK-17120 */
CREATE TABLE SmartNotificationEvent  (
    EventId                NUMBER              NOT NULL,
    Type                   VARCHAR2(30)        NOT NULL,
    Timestamp              DATE                NOT NULL,
    GroupProcessTime       DATE,
    ImmediateProcessTime   DATE,
    CONSTRAINT PK_SmartNotificationEvent PRIMARY KEY (EventId)
);

CREATE TABLE SmartNotificationEventParam  (
    EventId         NUMBER              NOT NULL,
    Name            VARCHAR2(30)        NOT NULL,
    Value           VARCHAR2(100)       NOT NULL,
    CONSTRAINT PK_SmartNotificationEventParam PRIMARY KEY (EventId, Name, Value)
);      

ALTER TABLE SmartNotificationEventParam
    ADD CONSTRAINT FK_SmartNotifEP_SmartNotifE FOREIGN KEY (EventId)
        REFERENCES SmartNotificationEvent (EventId)
        ON DELETE CASCADE;
/* End YUK-17120 */

/* Start YUK-17273 */
CREATE INDEX INDX_SmartNotifiEvt_Timestamp ON SmartNotificationEvent (
   Timestamp DESC
);
/* End YUK-17273 */

/* Start YUK-17233 */
INSERT INTO CCStrategyTargetSettings  
    SELECT
        C.StrategyID, 
        'Comm Reporting Percentage' AS SettingName, 
        '1' AS SettingValue, 
        'CONSIDER_PHASE' AS SettingType
    FROM CapControlStrategy C
    WHERE C.ControlUnits = 'INTEGRATED_VOLT_VAR';
/* End YUK-17233 */

/* Start YUK-17234 */
ALTER TABLE PointToZoneMapping
    ADD Ignore VARCHAR2(1);

UPDATE PointToZoneMapping
    SET Ignore = '0';

ALTER TABLE PointToZoneMapping
    MODIFY Ignore VARCHAR2(1) NOT NULL;
/* End YUK-17234 */

/* Start YUK-17309 */
CREATE TABLE CommandRequestExecRequest  (
   CommandRequestExecRequestId  NUMBER                          NOT NULL,
   CommandRequestExecId         NUMBER,
   DeviceId                     NUMBER,
   CONSTRAINT PK_CommandRequestExecRequest PRIMARY KEY (CommandRequestExecRequestId)
);

ALTER TABLE CommandRequestExecRequest
   ADD CONSTRAINT FK_ComReqExRequest_ComReqExec FOREIGN KEY (CommandRequestExecId)
      REFERENCES CommandRequestExec (CommandRequestExecId)
      ON DELETE CASCADE;

CREATE INDEX INDX_CREReq_ExecId_DevId ON CommandRequestExecRequest 
(
    CommandRequestExecId,
    DeviceId
);
/* End YUK-17309 */

/* Start YUK-17237 */
UPDATE YukonListEntry SET EntryText = 'LCR-6200 (ExpressCom)' WHERE EntryText = 'LCR-6200(EXPRESSCOM)' AND YukonDefinitionID = 1321;
UPDATE YukonListEntry SET EntryText = 'LCR-6200 (RFN)' WHERE EntryText = 'LCR-6200(RFN)' AND YukonDefinitionID = 1324;
UPDATE YukonListEntry SET EntryText = 'LCR-6200 (ZigBee)' WHERE EntryText = 'LCR-6200(ZIGBEE)' AND YukonDefinitionID = 1320;
UPDATE YukonListEntry SET EntryText = 'LCR-6600 (ExpressCom)' WHERE EntryText = 'LCR-6600(EXPRESSCOM)' AND YukonDefinitionID = 1323;
UPDATE YukonListEntry SET EntryText = 'LCR-6600 (ZigBee)' WHERE EntryText = 'LCR-6600(ZIGBEE)' AND YukonDefinitionID = 1322;
UPDATE YukonListEntry SET EntryText = 'LCR-6600 (RFN)' WHERE EntryText = 'LCR-6600(RFN)' AND YukonDefinitionID = 1325;
UPDATE YukonListEntry SET EntryText = 'LCR-6700 (RFN)' WHERE EntryText = 'LCR-6700(RFN)' AND YukonDefinitionID = 1337;
UPDATE YukonListEntry SET EntryText = 'LCR-5000 (VERSACOM)' WHERE EntryText = 'LCR-5000(VERSACOM)' AND YukonDefinitionID = 1311;
UPDATE YukonListEntry SET EntryText = 'LCR-5000 (ExpressCom)' WHERE EntryText = 'LCR-5000(EXPRESSCOM)' AND YukonDefinitionID = 1302;
/* End YUK-17237 */

/* Start YUK-17370 */
INSERT INTO Job (Jobid, BeanName, Disabled, JobGroupId) VALUES (-4, 'deviceConfigVerificationJobDefinition', 'N', -4);
INSERT INTO JobScheduledRepeating VALUES (-4, '0 01 0 ? * *');
/* End YUK-17370 */

/* Start YUK-17426 */
/* @start-block */
DECLARE 
    v_MaxDeviceGroupId NUMBER; 
    v_RootParentGroupId NUMBER; 
BEGIN
    SELECT MAX(DG.DeviceGroupId) INTO v_MaxDeviceGroupId FROM DeviceGroup DG WHERE DG.DeviceGroupId < 98;
    SELECT MAX(DG.DeviceGroupId) INTO v_RootParentGroupId FROM DeviceGroup DG WHERE DG.SystemGroupEnum = 'SYSTEM';

    INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
        VALUES(v_MaxDeviceGroupId + 1, 'Demand Response', v_RootParentGroupId, 'NOEDIT_NOMOD', 'STATIC', SYSDATE(), 'DEMAND_RESPONSE');

    INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
        VALUES(v_MaxDeviceGroupId + 2, 'Load Groups', v_MaxDeviceGroupId + 1, 'NOEDIT_NOMOD', 'LOAD_GROUPS', SYSDATE(), 'LOAD_GROUPS');

    INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
        VALUES(v_MaxDeviceGroupId + 3, 'Load Programs', v_MaxDeviceGroupId + 1, 'NOEDIT_NOMOD', 'LOAD_PROGRAMS', SYSDATE(), 'LOAD_PROGRAMS');

END;
/
/* @end-block */
/* End YUK-17426 */

/* Start YUK-17002 */
/* @start-block */
DECLARE v_MAX_PID INT;
BEGIN

SELECT MAX(POINTID) INTO v_MAX_PID FROM POINT;

INSERT INTO POINT
SELECT
    (v_MAX_PID) + ROW_NUMBER() OVER (ORDER BY PT.PointId),
    'Analog',
    'Port Queue Count',
    PAO.PAObjectID,
    'Default',
    0,
    'N',
    'N',
    'R',
    1,
    'On Update', 
    0 
FROM YukonPAObject PAO
LEFT JOIN Point PT
    ON  PT.PAObjectID = PAO.PAObjectID
    AND PT.POINTTYPE = 'Analog'
    AND PT.POINTOFFSET = 1
WHERE PAO.PAOClass = 'PORT'
AND PT.PointID IS NULL;
END;
/
/* @end-block */

INSERT INTO PointAnalog
    SELECT PT.POINTID, -1, 1, 1
    FROM Point PT
    WHERE PT.POINTNAME = 'Port Queue Count'
    AND NOT EXISTS(SELECT POINTID
                   FROM PointAnalog
                   WHERE PT.POINTID = POINTID
                   AND POINTOFFSET = 1);

INSERT INTO PointUnit 
    SELECT PT.POINTID, 9, 0, 1.0E+30, -1.0E+30, 0
    FROM Point PT
    WHERE PT.POINTNAME = 'Port Queue Count'
    AND NOT EXISTS(SELECT POINTID
                   FROM PointUnit
                   WHERE PT.POINTID = POINTID
                   AND POINTOFFSET = 1);
/* End YUK-17002 */

/* Start YUK-17348 */
CREATE TABLE MaintenanceTask  (
   TaskId               NUMBER(10)                      NOT NULL,
   TaskName             VARCHAR2(50)                    NOT NULL,
   Disabled             CHAR(1)                         NOT NULL,
   CONSTRAINT PK_MaintenanceTask PRIMARY KEY (TaskId)
);

CREATE TABLE MaintenanceTaskSettings  (
   TaskPropertyId       NUMBER(10)                      NOT NULL,
   TaskId               NUMBER(10)                      NOT NULL,
   Attribute            VARCHAR2(50)                    NOT NULL,
   Value                VARCHAR2(50)                    NOT NULL,
   CONSTRAINT PK_MaintenanceTaskSettings PRIMARY KEY (TaskPropertyId)
);

ALTER TABLE MaintenanceTaskSettings
   ADD CONSTRAINT FK_MTaskSettings_MTask FOREIGN KEY (TaskId)
      REFERENCES MaintenanceTask (TaskId)
      ON DELETE CASCADE;
/* End YUK-17348 */

/* Start YUK-17417 */
INSERT INTO DeviceTypeCommand VALUES (-1267, -30, 'CBC 8020', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1268, -31, 'CBC 8020', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1269, -173, 'CBC 8020', 11, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1270, -30, 'CBC 8024', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1271, -31, 'CBC 8024', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1272, -173, 'CBC 8024', 11, 'Y', -1);
/* End YUK-17417 */

/* Start YUK-17498 */
CREATE INDEX INDX_CRE_ExType_ExId_ExStart ON CommandRequestExec 
(
    CommandRequestExecType,
    CommandRequestExecId,
    StartTime
);

CREATE INDEX INDX_CRERes_ExecId_DevId ON CommandRequestExecResult 
(
   CommandRequestExecId,
   DeviceId,
   ErrorCode,
   CompleteTime
);


/* @start-block */
DECLARE
    v_maxId NUMBER;
BEGIN
    SELECT COALESCE((SELECT MAX(CommandRequestExecRequestId) FROM CommandRequestExecRequest), 0) INTO v_maxId FROM dual;

    INSERT INTO CommandRequestExecRequest ( CommandRequestExecRequestId, CommandRequestExecId, DeviceId )
    SELECT v_maxId + ROW_NUMBER() OVER ( ORDER BY req.CommandRequestExecRequestId ) AS CommandRequestExecRequestId, cre.CommandRequestExecId, res.DeviceId
    FROM CommandRequestExec cre
    JOIN CommandRequestExecResult res ON cre.CommandRequestExecId = res.CommandRequestExecId 
    LEFT JOIN CommandRequestExecRequest req ON res.CommandRequestExecId = req.CommandRequestExecId AND res.DeviceId = req.DeviceId
    WHERE cre.CommandRequestExecType IN ( 'GROUP_DEVICE_CONFIG_VERIFY', 'GROUP_DEVICE_CONFIG_SEND', 'GROUP_DEVICE_CONFIG_READ' )
    AND req.CommandRequestExecId IS NULL
    AND cre.CommandRequestExecId = ( SELECT MAX(cre2.CommandRequestExecId)
                                     FROM CommandRequestExec cre2
                                     JOIN CommandRequestExecResult res2
                                         ON cre2.CommandRequestExecId = res2.CommandRequestExecId
                                     WHERE res.DeviceId = res2.DeviceId
                                     AND cre2.CommandRequestExecType IN ( 'GROUP_DEVICE_CONFIG_VERIFY', 'GROUP_DEVICE_CONFIG_SEND', 'GROUP_DEVICE_CONFIG_READ' ) );
END;
/
/* @end-block */

/* @start-block */
DECLARE
    v_maxId NUMBER;
BEGIN
    SELECT COALESCE((SELECT MAX(CommandRequestExecRequestId) FROM CommandRequestExecRequest), 0) INTO v_maxId FROM dual;

    INSERT INTO CommandRequestExecRequest ( CommandRequestExecRequestId, CommandRequestExecId, DeviceId )
    SELECT v_maxId + ROW_NUMBER() OVER ( ORDER BY req.CommandRequestExecRequestId ) AS CommandRequestExecRequestId, cre.CommandRequestExecId, res.DeviceId
    FROM CommandRequestExec cre
    JOIN CommandRequestExecResult res ON cre.CommandRequestExecId = res.CommandRequestExecId 
    LEFT JOIN CommandRequestExecRequest req ON res.CommandRequestExecId = req.CommandRequestExecId AND res.DeviceId = req.DeviceId
    WHERE cre.CommandRequestExecType = 'GROUP_DEVICE_CONFIG_VERIFY'
    AND req.CommandRequestExecId IS NULL
    AND cre.CommandRequestExecId = ( SELECT MAX(cre2.CommandRequestExecId)
                                     FROM CommandRequestExec cre2
                                     JOIN CommandRequestExecResult res2
                                         ON cre2.CommandRequestExecId = res2.CommandRequestExecId
                                     WHERE res.DeviceId = res2.DeviceId
                                     AND cre2.CommandRequestExecType = 'GROUP_DEVICE_CONFIG_VERIFY' );
END;
/
/* @end-block */
/* End YUK-17498 */

/* Start YUK-17579 */
CREATE TABLE DeviceParent  (
   DeviceID             NUMBER                          NOT NULL,
   ParentID             NUMBER                          NOT NULL,
   CONSTRAINT PK_DeviceParent PRIMARY KEY (DeviceID, ParentID)
);

ALTER TABLE DeviceParent
   ADD CONSTRAINT FK_DeviceParent_Device FOREIGN KEY (DeviceID)
      REFERENCES DEVICE (DEVICEID)
      ON DELETE CASCADE;

ALTER TABLE DeviceParent
   ADD CONSTRAINT FK_DeviceParent_Parent FOREIGN KEY (ParentID)
      REFERENCES DEVICE (DEVICEID);
/* End YUK-17579 */

/* Start YUK-17588 */
ALTER TABLE LmProgramDirectGear 
    ADD StopCommandRepeat NUMBER;

UPDATE LmProgramDirectGear 
    SET StopCommandRepeat = 0;

ALTER TABLE LmProgramDirectGear 
    MODIFY StopCommandRepeat NUMBER NOT NULL;
/* End YUK-17588 */

/* Start YUK-17634 */
INSERT INTO YukonRoleProperty VALUES(-21405, -214, 'Manage Point Data', 'UPDATE', 'Controls the ability to edit, delete or manually add point data values.');
INSERT INTO YukonRoleProperty VALUES(-21406, -214, 'Manage Points', 'UPDATE', 'Controls the ability to view, create, edit or delete points.');
/* End YUK-17634 */

/* Start YUK-17122 */
UPDATE
(SELECT PU.decimalplaces FROM POINT P
    JOIN YukonPAObject YPO ON P.PAObjectID = YPO.PAObjectID
    JOIN POINTUNIT PU ON PU.POINTID = P.POINTID
WHERE YPO.Type LIKE 'RFN%'
    AND P.POINTTYPE = 'Analog'
    AND P.POINTOFFSET IN (14, 15)
    AND PU.DECIMALPLACES = 3) t
SET t.decimalplaces = 1;
/* End YUK-17122 */

/* Start YUK-17764 */
CREATE TABLE DMVTest  (
   DMVTestID             NUMBER                          NOT NULL,
   DMVTestName           VARCHAR2(60)                    NOT NULL,
   PollingInterval       NUMBER                          NOT NULL,
   DataGatheringDuration NUMBER                          NOT NULL,
   StepSize              FLOAT                           NOT NULL,
   CommSuccessPercentage NUMBER                          NOT NULL,
   CONSTRAINT PK_DMVTest PRIMARY KEY (DMVTestID)
);

CREATE TABLE DMVTestExecution  (
   ExecutionID          NUMBER                          NOT NULL,
   DMVTestId            NUMBER                          NOT NULL,
   AreaID               NUMBER                          NOT NULL,
   SubstationID         NUMBER                          NOT NULL,
   BusID                NUMBER                          NOT NULL,
   StartTime            DATE                            NOT NULL,
   StopTime             DATE,
   TestStatus           VARCHAR2(30),
   CONSTRAINT PK_DMVTestExecution PRIMARY KEY (ExecutionID)
);

CREATE TABLE DMVMeasurementData  (
   ExecutionID          NUMBER                          NOT NULL,
   PointID              NUMBER                          NOT NULL,
   TimeStamp            DATE                            NOT NULL,
   Quality              NUMBER                          NOT NULL,
   Value                FLOAT                           NOT NULL,
   CONSTRAINT PK_DMVMeasurementData PRIMARY KEY (ExecutionID, PointID, TimeStamp)
);

CREATE INDEX INDX_DMVMeasurementData_TStamp ON DMVMeasurementData (
   TimeStamp DESC
);

ALTER TABLE DMVTest
   ADD CONSTRAINT AK_DMVTest_DMVTestName UNIQUE (DMVTestName);

ALTER TABLE DMVMeasurementData
   ADD CONSTRAINT FK_DMVTestExec_DMVMData FOREIGN KEY (ExecutionID)
      REFERENCES DMVTestExecution (ExecutionID)
      ON DELETE CASCADE;

ALTER TABLE DMVTestExecution
   ADD CONSTRAINT FK_DMVTestExec_DMVTest FOREIGN KEY (DMVTestId)
      REFERENCES DMVTest (DMVTestID)
      ON DELETE CASCADE;
/* End YUK-17764 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '01-AUG-2017', 'Latest Update', 0, SYSDATE);*/
