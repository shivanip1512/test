/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-15182 */
/* @error ignore-begin */
INSERT INTO State VALUES(-20, 14, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 15, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 16, 'N/A', 9, 6, 0);
/* @error ignore-end */
/* End YUK-15182 */

/* Start YUK-15180 */
DELETE FROM UserPage WHERE Module = 'commercialcurtailment';

UPDATE YukonGroupRole SET Value = '/dr/cc/user/overview' 
WHERE RolePropertyId = -10800 
AND Value = '/cc/user/overview.jsf';

UPDATE YukonGroupRole SET Value = '/dr/cc/home' 
WHERE RolePropertyId = -10800 
AND Value = '/cc/ciSetup.jsf';
/* End YUK-15180 */

/* Start YUK-15216 */
UPDATE DeviceConfigCategoryItem 
SET ItemName = 'timeOffset', 
    ItemValue = CASE 
                  WHEN ItemValue='true' THEN 'LOCAL' 
                  ELSE 'UTC' 
                END
WHERE ItemName = 'localTime';
/* End YUK-15216 */

/* Start YUK-15201 */
/* @start-block */
DECLARE 
    v_sql VARCHAR2(1024);
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM USER_TABLES WHERE TABLE_NAME = UPPER('StoredProcedureLog');
    IF (v_count = 0)
    THEN 
        v_sql := 'CREATE TABLE StoredProcedureLog (' ||
          'EntryId           NUMBER          NOT NULL,'       ||
          'ProcedureName     VARCHAR2(50)    NOT NULL,'       ||
          'LogDate           DATE            NOT NULL,'       ||
          'LogString         VARCHAR2(500)   NOT NULL,'       ||
          'CONSTRAINT PK_StoredProcedureLog PRIMARY KEY (EntryId) )';
        EXECUTE IMMEDIATE v_sql;
    END IF;
END;
/
/* @end-block */
/* End YUK-15201 */

/* Start YUK-15280 */
CREATE TABLE UserSystemMetric  (
   UserId               NUMBER                          not null,
   SystemHealthMetricId VARCHAR2(64)                    not null,
   CONSTRAINT PK_UserSystemMetric PRIMARY KEY (UserId, SystemHealthMetricId)
);

ALTER TABLE UserSystemMetric
   ADD CONSTRAINT FK_UserSystemMetric_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserID)
      ON DELETE CASCADE;
/* End YUK-15280 */

/* Start YUK-15374 */
create table RfnBroadcastEventSummary  (
   RfnBroadcastEventId  NUMBER                          not null,
   Success              NUMBER                          not null,
   SuccessUnenrolled    NUMBER                          not null,
   Failure              NUMBER                          not null,
   Unknown              NUMBER                          not null,
   constraint PK_RFNBROADCASTEVENTSUMMARY primary key (RfnBroadcastEventId)
);

alter table RfnBroadcastEventSummary
   add constraint FK_RFNBROAD_REFERENCE_RFNBROAD foreign key (RfnBroadcastEventId)
      references RfnBroadcastEvent (RfnBroadcastEventId)
      on delete cascade;

go
/* End YUK-15374 */
      
/* Start YUK-15251 */
UPDATE UserPage
SET PagePath = '/capcontrol/substations/' || SUBSTR(PagePath, 39, LENGTH(PagePath)-38)
WHERE PageName = 'substation';

UPDATE UserPage
SET PageName = 'substation.VIEW'
WHERE PageName = 'substation';
/* End YUK-15251 */

/* Start YUK-15268 */
/* @start-block */
DECLARE
  doAdd NUMBER;
BEGIN
    SELECT COUNT(*) INTO doAdd FROM GlobalSetting WHERE Name = 'CIS_DETAIL_TYPE';
    IF (doAdd = 0) THEN 
        INSERT INTO GlobalSetting (GlobalSettingId, Name, Value) 
            SELECT DISTINCT (SELECT NVL(MAX(GlobalSettingId) + 1,1) FROM GlobalSetting), 'CIS_DETAIL_TYPE', 'CAYENTA' 
            FROM YukonGroupRole 
            WHERE RolePropertyId = -20212 AND Value = 'CAYENTA';
    END IF;
END;
/
/* @end-block */

/* @start-block */
DECLARE
  doAdd NUMBER;
BEGIN
    SELECT COUNT(*) INTO doAdd FROM GlobalSetting WHERE Name = 'CIS_DETAIL_TYPE';
    IF (doAdd = 0) THEN
        INSERT INTO GlobalSetting (GlobalSettingId, Name, Value)
            SELECT (SELECT NVL(MAX(GlobalSettingId) + 1,1) FROM GlobalSetting), 'CIS_DETAIL_TYPE', 'MULTISPEAK'
            FROM GlobalSetting 
            WHERE Name = 'MSP_PRIMARY_CB_VENDORID' AND Value > 1;
    END IF;
END;
/
/* @end-block */

DELETE FROM YukonGroupRole
WHERE RolePropertyId =  -20212;
  
DELETE FROM YukonRoleProperty
WHERE RolePropertyId =  -20212;

DELETE FROM YukonGroupRole
WHERE RolePropertyId =  -20211;
  
DELETE FROM YukonRoleProperty
WHERE RolePropertyId =  -20211;
/* End YUK-15268 */

/* Start YUK-15173 */
/* @error warn-once */
/* @start-block */
DECLARE
    v_count NUMBER := 0;
    v_newLine VARCHAR2(2);
    v_errorText VARCHAR2(512);
BEGIN
    SELECT count(*) INTO v_count FROM USER_INDEXES WHERE INDEX_NAME = 'PKC_RAWPOINTHISTORY';
    IF v_count = 0 THEN 
        v_newLine := CHR(13) || CHR(10);
        v_errorText := 'Indexes on RawPointHistory are being modified to improve system performance.' || v_newLine
            || 'Setup has detected that these indexes have not yet been updated on this system.' || v_newLine
            || 'This may take up to a few hours to complete depending on available system resources and the table size.' || v_newLine
            || 'More information can be found in YUK-15173.';
        RAISE_APPLICATION_ERROR(-20001, v_errorText);
    END IF;
END;
/
/* @end-block */

/* @start-block */
DECLARE
    v_count NUMBER := 0;
BEGIN
    SELECT count(*) INTO v_count FROM USER_INDEXES WHERE INDEX_NAME = 'PKC_RAWPOINTHISTORY';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'DROP INDEX INDEX_POINTID';
        EXECUTE IMMEDIATE 'DROP INDEX INDX_TIMESTAMP';
        EXECUTE IMMEDIATE 'DROP INDEX INDX_RWPTHISTSTPTID';
        EXECUTE IMMEDIATE 'DROP INDEX INDX_RWPTHISPTIDTST';
        EXECUTE IMMEDIATE 'CREATE INDEX INDX_RAWPOINTHISTORY_PTID_TS ON RAWPOINTHISTORY (POINTID ASC, TIMESTAMP DESC)';
        EXECUTE IMMEDIATE 'ALTER TABLE RAWPOINTHISTORY RENAME CONSTRAINT PK_RAWPOINTHISTORY TO PKC_RAWPOINTHISTORY';
        EXECUTE IMMEDIATE 'ALTER INDEX PK_RAWPOINTHISTORY RENAME TO PKC_RAWPOINTHISTORY';
    END IF;
END;
/
/* @end-block */

/* End YUK-15173 */

/* Start YUK-15217 */
UPDATE YukonImage 
SET ImageId = -1 
WHERE ImageId = 1
  AND ImageCategory = 'logos'
  AND ImageName = 'eaton_logo.png';

UPDATE YukonImage 
SET ImageId = -2 
WHERE ImageId = 2
  AND ImageCategory = 'backgrounds'
  AND ImageName = 'yukon_background.jpg';

UPDATE ThemeProperty 
SET Value = -2 
WHERE Value = 2 
  AND Property = 'LOGIN_BACKGROUND';

UPDATE ThemeProperty 
SET Value = -1 
WHERE Value = 1 
  AND Property = 'LOGO';
/* End YUK-15217 */

/* Start YUK-15352 */
UPDATE DynamicPointDispatch
SET Tags = BITAND(CAST(Tags AS NUMBER),  TO_NUMBER('FFFFFFEF', 'xxxxxxxx'))
WHERE PointId IN (
    SELECT dpd.PointId
    FROM DynamicPointDispatch dpd
    JOIN Point p ON p.PointId = dpd.PointId
    JOIN YukonPAObject y ON y.PAObjectID = p.PAObjectID
    WHERE y.DisableFlag = 'N' 
      AND BITAND(CAST(dpd.Tags AS NUMBER), TO_NUMBER('10', 'xx')) != 0);

UPDATE DynamicPointDispatch
SET Tags = BITAND(CAST(Tags AS NUMBER), TO_NUMBER('FFFFFFFE', 'xxxxxxxx'))
WHERE PointId IN (
    SELECT dpd.PointId
    FROM DynamicPointDispatch dpd
    JOIN Point p ON p.PointId = dpd.PointId
    JOIN YukonPAObject y ON y.PAObjectID = p.PAObjectID
    WHERE p.ServiceFlag = 'N'
      AND BITAND(CAST(dpd.Tags AS NUMBER), 1) != 0);
/* End YUK-15352 */

/* Start YUK-15271 */
INSERT INTO PointAlarming(PointId, AlarmStates, ExcludeNotifyStates, NotifyOnAcknowledge, NotificationGroupId)
SELECT PointId, '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1 
FROM Point p
WHERE p.PAObjectID = 0
  AND p.PointType != 'System'
  AND p.PointId NOT IN (SELECT PointId FROM PointAlarming);
/* End YUK-15271 */

/* Start YUK-15438 */
create table BehaviorReport  (
   BehaviorReportId     NUMBER                          not null,
   DeviceId             NUMBER                          not null,
   BehaviorType         VARCHAR2(60)                    not null,
   BehaviorStatus       VARCHAR2(60)                    not null,
   TimeStamp            DATE                            not null,
   constraint PK_BEHAVIORREPORT primary key (BehaviorReportId, DeviceId)
);

create table BehaviorReportValue  (
   BehaviorReportId     NUMBER                          not null,
   Name                 VARCHAR2(60)                    not null,
   Value                VARCHAR2(100)                   not null,
   constraint PK_BEHAVIORREPORTVALUE primary key (BehaviorReportId, Name)
);

create table DeviceBehaviorMap  (
   DeviceId             NUMBER                          not null,
   BehaviorId           NUMBER                          not null,
   constraint PK_DEVICEBEHAVIORMAP primary key (DeviceId, BehaviorId)
);

create table Behavior  (
   BehaviorId           NUMBER                          not null,
   BehaviorType         VARCHAR2(60)                    not null,
   constraint PK_BEHAVIOR primary key (BehaviorId)
);

create table BehaviorValue  (
   BehaviorId           NUMBER                          not null,
   Name                 VARCHAR2(60)                    not null,
   Value                VARCHAR2(100)                   not null,
   constraint PK_BEHAVIORVALUE primary key (BehaviorId, Name)
);

alter table Behavior
   add constraint FK_BehaviorValue_Behavior foreign key (BehaviorId)
      references BehaviorValue (BehaviorId)
      on delete cascade;

alter table DeviceBehaviorMap
   add constraint FK_Behavior_DeviceBehaviorMap foreign key (BehaviorId)
      references Behavior (BehaviorId)
      on delete cascade;

alter table DeviceBehaviorMap
   add constraint FK_Device_DeviceBehaviorMap foreign key (DeviceId)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table BehaviorReport
   add constraint FK_BehaviorReportValue_BehaviorReport foreign key (BehaviorReportId)
      references BehaviorReportValue (BehaviorReportId)
      on delete cascade;

alter table BehaviorReport
   add constraint FK_Device_BehaviorReport foreign key (DeviceId)
      references DEVICE (DEVICEID)
      on delete cascade;
/* End YUK-15438 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '31-JUN-2016', 'Latest Update', 0, SYSDATE);*/