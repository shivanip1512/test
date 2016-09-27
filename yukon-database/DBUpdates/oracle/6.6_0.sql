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
   constraint PK_BEHAVIORREPORT primary key (BehaviorReportId)
);

create table BehaviorReportValue  (
   BehaviorReportId     NUMBER                          not null,
   Name                 VARCHAR2(60)                    not null,
   Value                VARCHAR2(100)                   not null,
   constraint PK_BEHAVIORREPORTVALUE primary key (BehaviorReportId, Name)
);

create table DeviceBehaviorMap  (
   BehaviorId           NUMBER                          not null,
   DeviceId             NUMBER                          not null,
   constraint PK_DEVICEBEHAVIORMAP primary key (BehaviorId, DeviceId)
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

alter table BehaviorReport
   add constraint FK_Device_BehaviorReport foreign key (DeviceId)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table BehaviorReportValue
   add constraint FK_BehaviorRptVal_BehaviorRpt foreign key (BehaviorReportId)
      references BehaviorReport (BehaviorReportId)
      on delete cascade;

alter table DeviceBehaviorMap
   add constraint FK_Behavior_DeviceBehaviorMap foreign key (BehaviorId)
      references Behavior (BehaviorId)
      on delete cascade;

alter table DeviceBehaviorMap
   add constraint FK_Device_DeviceBehaviorMap foreign key (DeviceId)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table BehaviorValue
   add constraint FK_BehaviorValue_Behavior foreign key (BehaviorId)
      references Behavior (BehaviorId)
      on delete cascade;

/* End YUK-15438 */

/* Start YUK-13440 */
/* @start-block */
DECLARE
  doAdd NUMBER;
BEGIN
    SELECT COUNT(*) INTO doAdd FROM USER_TABLES WHERE TABLE_NAME = UPPER('temp_DeviceConfigCategoryItem');
    IF doAdd = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE temp_DeviceConfigCategoryItem (
               DEVICECONFIGCATEGORYITEMID decimal(22,0) PRIMARY KEY NOT NULL,
               DEVICECONFIGCATEGORYID decimal(22,0),
               ITEMNAME varchar2(60) NOT NULL,
               ITEMVALUE varchar2(60) NOT NULL)';
        EXECUTE IMMEDIATE '
            INSERT INTO temp_DeviceConfigCategoryItem
                (DEVICECONFIGCATEGORYITEMID,DEVICECONFIGCATEGORYID,ITEMNAME,ITEMVALUE)
                SELECT DEVICECONFIGCATEGORYITEMID,DEVICECONFIGCATEGORYID,ITEMNAME,ITEMVALUE 
                    FROM DEVICECONFIGCATEGORYITEM';
        EXECUTE IMMEDIATE '
            UPDATE DeviceConfigCategoryItem SET ItemValue = ItemValue + 1
                WHERE ItemName like ''channel%PhysicalChannel'' ';
        EXECUTE IMMEDIATE '
            UPDATE DeviceConfigCategoryItem SET ItemValue = to_char(to_number(ItemValue, ''FM9999.90'') / 4, ''fm9999.90'')
                WHERE ItemName like ''relay%Timer'' ';
        EXECUTE IMMEDIATE '
            UPDATE DeviceConfigCategoryItem SET ItemValue = ItemValue * 15
                WHERE ItemName = ''tableReadInterval'' ';
        EXECUTE IMMEDIATE '
            UPDATE DeviceConfigCategoryItem SET ItemValue = 
                CASE
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''0''  THEN ''SLOT_DISABLED'' 
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''1''  THEN ''NO_SEGMENTS''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''2''  THEN ''ALL_SEGMENTS''
                    /* 3 is unused */
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''4''  THEN ''CURRENT_LOCAL_TIME''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''5''  THEN ''CURRENT_LOCAL_DATE''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''6''  THEN ''TOTAL_KWH''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''7''  THEN ''NET_KWH''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''8''  THEN ''DELIVERED_KWH''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''9''  THEN ''RECEIVED_KWH''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''10'' THEN ''LAST_INTERVAL_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''11'' THEN ''PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''12'' THEN ''PEAK_KW_DATE''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''13'' THEN ''PEAK_KW_TIME''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''14'' THEN ''LAST_INTERVAL_VOLTAGE''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''15'' THEN ''PEAK_VOLTAGE''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''16'' THEN ''PEAK_VOLTAGE_DATE''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''17'' THEN ''PEAK_VOLTAGE_TIME''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''18'' THEN ''MINIMUM_VOLTAGE''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''19'' THEN ''MINIMUM_VOLTAGE_DATE''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''20'' THEN ''MINIMUM_VOLTAGE_TIME''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''21'' THEN ''TOU_RATE_A_KWH''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''22'' THEN ''TOU_RATE_A_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''23'' THEN ''TOU_RATE_A_DATE_OF_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''24'' THEN ''TOU_RATE_A_TIME_OF_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''25'' THEN ''TOU_RATE_B_KWH''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''26'' THEN ''TOU_RATE_B_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''27'' THEN ''TOU_RATE_B_DATE_OF_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''28'' THEN ''TOU_RATE_B_TIME_OF_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''29'' THEN ''TOU_RATE_C_KWH''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''30'' THEN ''TOU_RATE_C_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''31'' THEN ''TOU_RATE_C_DATE_OF_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''32'' THEN ''TOU_RATE_C_TIME_OF_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''33'' THEN ''TOU_RATE_D_KWH''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''34'' THEN ''TOU_RATE_D_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''35'' THEN ''TOU_RATE_D_DATE_OF_PEAK_KW''
                    WHEN ItemName like ''displayItem%'' AND ItemValue=''36'' THEN ''TOU_RATE_D_TIME_OF_PEAK_KW''
                            /* */
                    WHEN ItemName like ''channel%PeakKWResolution'' AND ItemValue=''1.0'' THEN ''ZERO'' 
                    WHEN ItemName like ''channel%PeakKWResolution'' AND ItemValue=''10.0'' THEN ''ONE'' 
                            /* */
                    WHEN ItemName like ''channel%ProfileResolution'' AND ItemValue=''.1'' THEN ''MINUS_ONE'' 
                    WHEN ItemName like ''channel%ProfileResolution'' AND ItemValue=''1.0'' THEN ''ZERO'' 
                    WHEN ItemName like ''channel%ProfileResolution'' AND ItemValue=''10.0'' THEN ''ONE'' 
                            /* */
                    WHEN ItemName like ''channel%LastIntervalDemandResolution'' AND ItemValue=''.01'' THEN ''MINUS_TWO'' 
                    WHEN ItemName like ''channel%LastIntervalDemandResolution'' AND ItemValue=''.1'' THEN ''MINUS_ONE'' 
                    WHEN ItemName like ''channel%LastIntervalDemandResolution'' AND ItemValue=''1.0'' THEN ''ZERO'' 
                    WHEN ItemName like ''channel%LastIntervalDemandResolution'' AND ItemValue=''10.0'' THEN ''ONE'' 
                            /* */
                    WHEN ItemName = ''electronicMeter'' AND ItemValue=''0'' THEN ''NONE''
                    WHEN ItemName = ''electronicMeter'' AND ItemValue=''1'' THEN ''S4''
                    WHEN ItemName = ''electronicMeter'' AND ItemValue=''2'' THEN ''ALPHA_A3''
                    WHEN ItemName = ''electronicMeter'' AND ItemValue=''3'' THEN ''ALPHA_P_PLUS''
                    WHEN ItemName = ''electronicMeter'' AND ItemValue=''4'' THEN ''GEKV''
                    WHEN ItemName = ''electronicMeter'' AND ItemValue=''5'' THEN ''GEKV2''
                    WHEN ItemName = ''electronicMeter'' AND ItemValue=''6'' THEN ''DNP''
                    WHEN ItemName = ''electronicMeter'' AND ItemValue=''7'' THEN ''SENTINEL''
                    WHEN ItemName = ''electronicMeter'' AND ItemValue=''8'' THEN ''GEKV2C''
                            /* */
                    WHEN ItemName like ''channel%Type'' AND ItemValue=''0'' THEN ''CHANNEL_NOT_USED''
                    WHEN ItemName like ''channel%Type'' AND ItemValue=''1'' THEN ''ELECTRONIC_METER''
                    WHEN ItemName like ''channel%Type'' AND ItemValue=''2'' THEN ''TWO_WIRE_KYZ_FORM_A''
                    WHEN ItemName like ''channel%Type'' AND ItemValue=''3'' THEN ''THREE_WIRE_KYZ_FORM_C''
                            /* */
                    WHEN ItemName = ''sunday'' AND ItemValue = ''Schedule 1'' THEN ''SCHEDULE_1''
                    WHEN ItemName = ''monday'' AND ItemValue = ''Schedule 1'' THEN ''SCHEDULE_1''
                    WHEN ItemName = ''tuesday'' AND ItemValue = ''Schedule 1'' THEN ''SCHEDULE_1''
                    WHEN ItemName = ''wednesday'' AND ItemValue = ''Schedule 1'' THEN ''SCHEDULE_1''
                    WHEN ItemName = ''thursday'' AND ItemValue = ''Schedule 1'' THEN ''SCHEDULE_1''
                    WHEN ItemName = ''friday'' AND ItemValue = ''Schedule 1'' THEN ''SCHEDULE_1''
                    WHEN ItemName = ''saturday'' AND ItemValue = ''Schedule 1'' THEN ''SCHEDULE_1''
                    WHEN ItemName = ''holiday'' AND ItemValue = ''Schedule 1'' THEN ''SCHEDULE_1''
                            /* */
                    WHEN ItemName = ''sunday'' AND ItemValue = ''Schedule 2'' THEN ''SCHEDULE_2''
                    WHEN ItemName = ''monday'' AND ItemValue = ''Schedule 2'' THEN ''SCHEDULE_2''
                    WHEN ItemName = ''tuesday'' AND ItemValue = ''Schedule 2'' THEN ''SCHEDULE_2''
                    WHEN ItemName = ''wednesday'' AND ItemValue = ''Schedule 2'' THEN ''SCHEDULE_2''
                    WHEN ItemName = ''thursday'' AND ItemValue = ''Schedule 2'' THEN ''SCHEDULE_2''
                    WHEN ItemName = ''friday'' AND ItemValue = ''Schedule 2'' THEN ''SCHEDULE_2''
                    WHEN ItemName = ''saturday'' AND ItemValue = ''Schedule 2'' THEN ''SCHEDULE_2''
                    WHEN ItemName = ''holiday'' AND ItemValue = ''Schedule 2'' THEN ''SCHEDULE_2''
                            /* */
                    WHEN ItemName = ''sunday'' AND ItemValue = ''Schedule 3'' THEN ''SCHEDULE_3''
                    WHEN ItemName = ''monday'' AND ItemValue = ''Schedule 3'' THEN ''SCHEDULE_3''
                    WHEN ItemName = ''tuesday'' AND ItemValue = ''Schedule 3'' THEN ''SCHEDULE_3''
                    WHEN ItemName = ''wednesday'' AND ItemValue = ''Schedule 3'' THEN ''SCHEDULE_3''
                    WHEN ItemName = ''thursday'' AND ItemValue = ''Schedule 3'' THEN ''SCHEDULE_3''
                    WHEN ItemName = ''friday'' AND ItemValue = ''Schedule 3'' THEN ''SCHEDULE_3''
                    WHEN ItemName = ''saturday'' AND ItemValue = ''Schedule 3'' THEN ''SCHEDULE_3''
                    WHEN ItemName = ''holiday'' AND ItemValue = ''Schedule 3'' THEN ''SCHEDULE_3''
                            /* */
                    WHEN ItemName = ''sunday'' AND ItemValue = ''Schedule 4'' THEN ''SCHEDULE_4''
                    WHEN ItemName = ''monday'' AND ItemValue = ''Schedule 4'' THEN ''SCHEDULE_4''
                    WHEN ItemName = ''tuesday'' AND ItemValue = ''Schedule 4'' THEN ''SCHEDULE_4''
                    WHEN ItemName = ''wednesday'' AND ItemValue = ''Schedule 4'' THEN ''SCHEDULE_4''
                    WHEN ItemName = ''thursday'' AND ItemValue = ''Schedule 4'' THEN ''SCHEDULE_4''
                    WHEN ItemName = ''friday'' AND ItemValue = ''Schedule 4'' THEN ''SCHEDULE_4''
                    WHEN ItemName = ''saturday'' AND ItemValue = ''Schedule 4'' THEN ''SCHEDULE_4''
                    WHEN ItemName = ''holiday'' AND ItemValue = ''Schedule 4'' THEN ''SCHEDULE_4''
                            /* */
                    WHEN ItemName = ''timeZoneOffset'' AND ItemValue = ''-2'' THEN ''NORONHA''
                    WHEN ItemName = ''timeZoneOffset'' AND ItemValue = ''-3'' THEN ''SAO_PAULO''
                    WHEN ItemName = ''timeZoneOffset'' AND ItemValue = ''-4'' THEN ''MANAUS''
                    WHEN ItemName = ''timeZoneOffset'' AND ItemValue = ''-5'' THEN ''NEW_YORK''
                    WHEN ItemName = ''timeZoneOffset'' AND ItemValue = ''-6'' THEN ''CHICAGO''
                    WHEN ItemName = ''timeZoneOffset'' AND ItemValue = ''-7'' THEN ''DENVER''
                    WHEN ItemName = ''timeZoneOffset'' AND ItemValue = ''-8'' THEN ''LOS_ANGELES''
                    WHEN ItemName = ''timeZoneOffset'' AND ItemValue = ''-9'' THEN ''ANCHORAGE''
                    WHEN ItemName = ''timeZoneOffset'' AND ItemValue = ''-10'' THEN ''HONOLULU''
                    ELSE ItemValue
                END ';
    END IF;
END;
/
/* @end-block */
/* End YUK-13440 */

/* Start YUK-15428 */
DELETE FROM YukonServices WHERE ServiceId IN (6, -6);
/* End YUK-15428 */

/* Start YUK-15548 */
ALTER TABLE 
   FDRTranslation 
MODIFY 
   ( 
   Destination VARCHAR2(256),
   Translation VARCHAR2(500)
   )
;
/* End YUK-15548 */

/* Start YUK-15611 */
INSERT INTO StateGroup VALUES (-21, 'CBC Door Status', 'Status');
INSERT INTO StateGroup VALUES (-22, 'CBC-8 Hardware Type', 'Status');
INSERT INTO StateGroup VALUES (-23, 'Var Voltage Input', 'Status');
INSERT INTO StateGroup VALUES (-24, 'SCADA TripClose', 'Status');
INSERT INTO StateGroup VALUES (-25, 'YesNo', 'Status');
INSERT INTO StateGroup VALUES (-26, 'SCADA Override Type', 'Status');
GO

INSERT INTO State VALUES (-21, 0, 'Closed', 0, 6, 0);
INSERT INTO State VALUES (-21, 1, 'Open', 1, 6, 0);

INSERT INTO State VALUES (-22, 0, 'N/A', 0, 6, 0);
INSERT INTO State VALUES (-22, 1, 'Old Board/Old Box', 1, 6, 0);
INSERT INTO State VALUES (-22, 2, 'New Board/Old Box', 2, 6, 0);
INSERT INTO State VALUES (-22, 3, 'New Board/New Box', 3, 6, 0);

INSERT INTO State VALUES (-23, 0, 'CBC Line', 0, 6, 0);
INSERT INTO State VALUES (-23, 1, 'Phase A', 1, 6, 0);
INSERT INTO State VALUES (-23, 2, 'Phase B', 2, 6, 0);
INSERT INTO State VALUES (-23, 3, 'Phase C', 3, 6, 0);

INSERT INTO State VALUES (-24, 0, 'Trip', 0, 6, 0);
INSERT INTO State VALUES (-24, 1, 'Close', 1, 6, 0);

INSERT INTO State VALUES (-25, 0, 'No', 0, 6, 0);
INSERT INTO State VALUES (-25, 1, 'Yes', 1, 6, 0);

INSERT INTO State VALUES (-26, 0, 'Time of Day', 0, 6, 0);
INSERT INTO State VALUES (-26, 1, 'Countdown Timer', 1, 6, 0);
GO
/* End YUK-15611 */

/* Start YUK-15502 */
/* @error ignore-begin */
INSERT INTO DeviceTypeCommand VALUES (-1157, -193, 'RFN-510fL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1158, -194, 'RFN-510fL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1159, -195, 'RFN-510fL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1160, -196, 'RFN-510fL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1161, -197, 'RFN-510fL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1162, -198, 'RFN-510fL', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1163, -193, 'RFN-520fAX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1164, -194, 'RFN-520fAX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1165, -195, 'RFN-520fAX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1166, -196, 'RFN-520fAX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1167, -197, 'RFN-520fAX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1168, -198, 'RFN-520fAX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1169, -193, 'RFN-520fRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1170, -194, 'RFN-520fRX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1171, -195, 'RFN-520fRX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1172, -196, 'RFN-520fRX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1173, -197, 'RFN-520fRX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1174, -198, 'RFN-520fRX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1175, -193, 'RFN-520fAXD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1176, -194, 'RFN-520fAXD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1177, -195, 'RFN-520fAXD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1178, -196, 'RFN-520fAXD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1179, -197, 'RFN-520fAXD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1180, -198, 'RFN-520fAXD', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1181, -193, 'RFN-520fRXD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1182, -194, 'RFN-520fRXD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1183, -195, 'RFN-520fRXD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1184, -196, 'RFN-520fRXD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1185, -197, 'RFN-520fRXD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1186, -198, 'RFN-520fRXD', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1187, -193, 'RFN-530fAX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1188, -194, 'RFN-530fAX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1189, -195, 'RFN-530fAX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1190, -196, 'RFN-530fAX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1191, -197, 'RFN-530fAX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1192, -198, 'RFN-530fAX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1193, -193, 'RFN-530fRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1194, -194, 'RFN-530fRX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1195, -195, 'RFN-530fRX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1196, -196, 'RFN-530fRX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1197, -197, 'RFN-530fRX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1198, -198, 'RFN-530fRX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1199, -193, 'RFN-530S4x', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1200, -194, 'RFN-530S4x', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1201, -195, 'RFN-530S4x', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1202, -196, 'RFN-530S4x', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1203, -197, 'RFN-530S4x', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1204, -198, 'RFN-530S4x', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1205, -193, 'RFN-530S4eAD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1206, -194, 'RFN-530S4eAD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1207, -195, 'RFN-530S4eAD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1208, -196, 'RFN-530S4eAD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1209, -197, 'RFN-530S4eAD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1210, -198, 'RFN-530S4eAD', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1211, -193, 'RFN-530S4eAT', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1212, -194, 'RFN-530S4eAT', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1213, -195, 'RFN-530S4eAT', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1214, -196, 'RFN-530S4eAT', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1215, -197, 'RFN-530S4eAT', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1216, -198, 'RFN-530S4eAT', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1217, -193, 'RFN-530S4eRD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1218, -194, 'RFN-530S4eRD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1219, -195, 'RFN-530S4eRD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1220, -196, 'RFN-530S4eRD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1221, -197, 'RFN-530S4eRD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1222, -198, 'RFN-530S4eRD', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1223, -193, 'RFN-530S4eRT', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1224, -194, 'RFN-530S4eRT', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1225, -195, 'RFN-530S4eRT', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1226, -196, 'RFN-530S4eRT', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1227, -197, 'RFN-530S4eRT', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1228, -198, 'RFN-530S4eRT', 6, 'Y', -1);
/* @error ignore-end */
/* End YUK-15502 */

/* Start YUK-15671 */
INSERT INTO Command VALUES (-212, 'getconfig model', 'Read Meter Config', 'All Two Way LCR');
INSERT INTO Command VALUES (-213, 'ping', 'Ping', 'All Two Way LCR');

INSERT INTO DeviceTypeCommand VALUES (-1229, -212, 'LCR-3102', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1230, -213, 'LCR-3102', 17, 'Y', -1);
/* End YUK-15671 */

/* Start YUK-15633 */
ALTER TABLE LMControlHistory DROP CONSTRAINT FK_LmCtrlHis_YPAO;

ALTER TABLE LMControlHistory
   ADD CONSTRAINT FK_LmCtrlHis_YPAO FOREIGN KEY (PAObjectID)
      REFERENCES YukonPAObject (PAObjectID)
         ON DELETE CASCADE;
/* End YUK-15633 */

/* Start YUK-15729 */
ALTER TABLE DYNAMICPOINTDISPATCH DROP (STALECOUNT, LastAlarmLogID);
GO
/* End YUK-15729 */

/* Start YUK-15723*/
UPDATE JobStatus SET Message=' ' WHERE Message IS NULL;
/* End YUK-15723*/

/* Start YUK-15720*/
INSERT INTO YukonRoleProperty VALUES (-21316, -213, 'RF Data Streaming', 'false', 'Controls access to RF data streaming configuration actions.');
/* End YUK-15720*/

/* Start YUK-15712 */
/* @error ignore-begin */
INSERT INTO Command VALUES (-214, 'putconfig install all', 'Send configuration', 'ALL RFNs');
/* @error ignore-end */
/* End YUK-15712 */

/* Start YUK-15711 */
/* @error ignore-begin */
INSERT INTO DeviceTypeCommand VALUES (-1234, -214, 'RFN-410fX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1232, -214, 'RFN-410fD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1238, -214, 'RFN-420fL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1241, -214, 'RFN-420fX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1237, -214, 'RFN-420fD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1240, -214, 'RFN-420fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1239, -214, 'RFN-420fRD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-1231, -214, 'RFN-410cL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1236, -214, 'RFN-420cL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1235, -214, 'RFN-420cD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1242, -214, 'RFN-430A3D', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1245, -214, 'RFN-430A3T', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1243, -214, 'RFN-430A3K', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1244, -214, 'RFN-430A3R', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1246, -214, 'RFN-430KV', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1247, -214, 'RFN-430SL0', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1248, -214, 'RFN-430SL1', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1249, -214, 'RFN-430SL2', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1250, -214, 'RFN-430SL3', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1251, -214, 'RFN-430SL4', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1255, -214, 'RFN-510fL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1256, -214, 'RFN-520fAX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1257, -214, 'RFN-520fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1258, -214, 'RFN-520fAXD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1259, -214, 'RFN-520fRXD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1260, -214, 'RFN-530fAX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1261, -214, 'RFN-530fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1262, -214, 'RFN-530S4x', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1263, -214, 'RFN-530S4eAD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1264, -214, 'RFN-530S4eAT', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1265, -214, 'RFN-530S4eRD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1266, -214, 'RFN-530S4eRT', 2, 'Y', -1);

DELETE FROM DeviceTypeCommand WHERE DeviceCommandID IN (-1045, -1051, -1057, -1063, -1075, -1081, -1087, -1088, -1089,
    -1090, -1092, -1093, -1094, -1095, -1097, -1098, -1099, -1100, -1102, -1103, -1104, -1105, -1112, -1113, -1114,
    -1115, -1117, -1118, -1119, -1120, -1122, -1123, -1124, -1125, -1127, -1128, -1129, -1130, -1132, -1133, -1134,
    -1135, -1206, -1207, -1208, -1209, -1210, -1212, -1213, -1214, -1215, -1216, -1218, -1219, -1220, -1221, -1222,
    -1224, -1225, -1226, -1227, -1228 );
/* @error ignore-end */
/* End YUK-15711 */

/* Start YUK-15746 */
INSERT INTO YukonListEntry VALUES (2030, 1005, 0, 'Honeywell Wi-Fi 9000', 1332);
INSERT INTO YukonListEntry VALUES (2031, 1005, 0, 'Honeywell Wi-Fi VisionPRO 8000', 1333);
INSERT INTO YukonListEntry VALUES (2032, 1005, 0, 'Honeywell Wi-Fi FocusPRO', 1334);
INSERT INTO YukonListEntry VALUES (2033, 1005, 0, 'Honeywell Wi-Fi Thermostat', 1335);
/* End YUK-15746 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '31-JUN-2016', 'Latest Update', 0, SYSDATE);*/
