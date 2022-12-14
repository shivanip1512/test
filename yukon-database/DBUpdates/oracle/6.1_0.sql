/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12863 */
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_A_PEAK_DEMAND', 'PEAK_DEMAND_RATE_A') WHERE AttributeName LIKE '%TOU_RATE_A_PEAK_DEMAND%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_B_PEAK_DEMAND', 'PEAK_DEMAND_RATE_B') WHERE AttributeName LIKE '%TOU_RATE_B_PEAK_DEMAND%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_C_PEAK_DEMAND', 'PEAK_DEMAND_RATE_C') WHERE AttributeName LIKE '%TOU_RATE_C_PEAK_DEMAND%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_D_PEAK_DEMAND', 'PEAK_DEMAND_RATE_D') WHERE AttributeName LIKE '%TOU_RATE_D_PEAK_DEMAND%';

UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_A_USAGE', 'USAGE_RATE_A') WHERE AttributeName LIKE '%TOU_RATE_A_USAGE%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_B_USAGE', 'USAGE_RATE_B') WHERE AttributeName LIKE '%TOU_RATE_B_USAGE%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_C_USAGE', 'USAGE_RATE_C') WHERE AttributeName LIKE '%TOU_RATE_C_USAGE%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_D_USAGE', 'USAGE_RATE_D') WHERE AttributeName LIKE '%TOU_RATE_D_USAGE%';

UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_A_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_A') WHERE AttributeName LIKE '%TOU_RATE_A_ENERGY_GENERATED%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_B_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_B') WHERE AttributeName LIKE '%TOU_RATE_B_ENERGY_GENERATED%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_C_ENERGY_GENERATED', 'RECEIVED_KWH_C') WHERE AttributeName LIKE '%TOU_RATE_C_ENERGY_GENERATED%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'TOU_RATE_D_ENERGY_GENERATED', 'RECEIVED_KWH_D') WHERE AttributeName LIKE '%TOU_RATE_D_ENERGY_GENERATED%';
UPDATE ArchiveValuesExportAttribute SET AttributeName = REPLACE(AttributeName, 'ENERGY_GENERATED', 'RECEIVED_KWH') WHERE AttributeName LIKE '%ENERGY_GENERATED%';

UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_PEAK_DEMAND', 'PEAK_DEMAND_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_PEAK_DEMAND%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_PEAK_DEMAND', 'PEAK_DEMAND_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_PEAK_DEMAND%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_PEAK_DEMAND', 'PEAK_DEMAND_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_PEAK_DEMAND%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_PEAK_DEMAND', 'PEAK_DEMAND_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_PEAK_DEMAND%';

UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_USAGE', 'USAGE_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_USAGE%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_USAGE', 'USAGE_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_USAGE%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_USAGE', 'USAGE_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_USAGE%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_USAGE', 'USAGE_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_USAGE%';

UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_ENERGY_GENERATED%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_ENERGY_GENERATED%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_ENERGY_GENERATED', 'RECEIVED_KWH_C') WHERE Attribute LIKE '%TOU_RATE_C_ENERGY_GENERATED%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_ENERGY_GENERATED', 'RECEIVED_KWH_D') WHERE Attribute LIKE '%TOU_RATE_D_ENERGY_GENERATED%';
UPDATE ArchiveDataAnalysis SET Attribute = REPLACE(Attribute, 'ENERGY_GENERATED', 'RECEIVED_KWH') WHERE Attribute LIKE '%ENERGY_GENERATED%';

UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_PEAK_DEMAND', 'PEAK_DEMAND_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_PEAK_DEMAND%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_PEAK_DEMAND', 'PEAK_DEMAND_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_PEAK_DEMAND%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_PEAK_DEMAND', 'PEAK_DEMAND_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_PEAK_DEMAND%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_PEAK_DEMAND', 'PEAK_DEMAND_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_PEAK_DEMAND%';

UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_USAGE', 'USAGE_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_USAGE%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_USAGE', 'USAGE_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_USAGE%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_USAGE', 'USAGE_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_USAGE%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_USAGE', 'USAGE_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_USAGE%';

UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_ENERGY_GENERATED%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_ENERGY_GENERATED%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_ENERGY_GENERATED', 'RECEIVED_KWH_C') WHERE Attribute LIKE '%TOU_RATE_C_ENERGY_GENERATED%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_ENERGY_GENERATED', 'RECEIVED_KWH_D') WHERE Attribute LIKE '%TOU_RATE_D_ENERGY_GENERATED%';
UPDATE DeviceDataMonitorProcessor SET Attribute = REPLACE(Attribute, 'ENERGY_GENERATED', 'RECEIVED_KWH') WHERE Attribute LIKE '%ENERGY_GENERATED%';

UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_PEAK_DEMAND', 'PEAK_DEMAND_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_PEAK_DEMAND%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_PEAK_DEMAND', 'PEAK_DEMAND_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_PEAK_DEMAND%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_PEAK_DEMAND', 'PEAK_DEMAND_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_PEAK_DEMAND%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_PEAK_DEMAND', 'PEAK_DEMAND_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_PEAK_DEMAND%';

UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_USAGE', 'USAGE_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_USAGE%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_USAGE', 'USAGE_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_USAGE%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_USAGE', 'USAGE_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_USAGE%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_USAGE', 'USAGE_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_USAGE%';

UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_ENERGY_GENERATED%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_ENERGY_GENERATED%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_ENERGY_GENERATED', 'RECEIVED_KWH_C') WHERE Attribute LIKE '%TOU_RATE_C_ENERGY_GENERATED%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_ENERGY_GENERATED', 'RECEIVED_KWH_D') WHERE Attribute LIKE '%TOU_RATE_D_ENERGY_GENERATED%';
UPDATE ExtraPaoPointAssignment SET Attribute = REPLACE(Attribute, 'ENERGY_GENERATED', 'RECEIVED_KWH') WHERE Attribute LIKE '%ENERGY_GENERATED%';

UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_PEAK_DEMAND', 'PEAK_DEMAND_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_PEAK_DEMAND%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_PEAK_DEMAND', 'PEAK_DEMAND_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_PEAK_DEMAND%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_PEAK_DEMAND', 'PEAK_DEMAND_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_PEAK_DEMAND%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_PEAK_DEMAND', 'PEAK_DEMAND_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_PEAK_DEMAND%';

UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_USAGE', 'USAGE_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_USAGE%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_USAGE', 'USAGE_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_USAGE%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_USAGE', 'USAGE_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_USAGE%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_USAGE', 'USAGE_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_USAGE%';

UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_ENERGY_GENERATED%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_ENERGY_GENERATED%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_ENERGY_GENERATED', 'RECEIVED_KWH_C') WHERE Attribute LIKE '%TOU_RATE_C_ENERGY_GENERATED%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_ENERGY_GENERATED', 'RECEIVED_KWH_D') WHERE Attribute LIKE '%TOU_RATE_D_ENERGY_GENERATED%';
UPDATE PorterResponseMonitor SET Attribute = REPLACE(Attribute, 'ENERGY_GENERATED', 'RECEIVED_KWH') WHERE Attribute LIKE '%ENERGY_GENERATED%';

UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_PEAK_DEMAND', 'PEAK_DEMAND_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_PEAK_DEMAND%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_PEAK_DEMAND', 'PEAK_DEMAND_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_PEAK_DEMAND%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_PEAK_DEMAND', 'PEAK_DEMAND_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_PEAK_DEMAND%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_PEAK_DEMAND', 'PEAK_DEMAND_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_PEAK_DEMAND%';

UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_USAGE', 'USAGE_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_USAGE%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_USAGE', 'USAGE_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_USAGE%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_USAGE', 'USAGE_RATE_C') WHERE Attribute LIKE '%TOU_RATE_C_USAGE%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_USAGE', 'USAGE_RATE_D') WHERE Attribute LIKE '%TOU_RATE_D_USAGE%';

UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_A_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_A') WHERE Attribute LIKE '%TOU_RATE_A_ENERGY_GENERATED%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_B_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_B') WHERE Attribute LIKE '%TOU_RATE_B_ENERGY_GENERATED%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_C_ENERGY_GENERATED', 'RECEIVED_KWH_C') WHERE Attribute LIKE '%TOU_RATE_C_ENERGY_GENERATED%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'TOU_RATE_D_ENERGY_GENERATED', 'RECEIVED_KWH_D') WHERE Attribute LIKE '%TOU_RATE_D_ENERGY_GENERATED%';
UPDATE StatusPointMonitor SET Attribute = REPLACE(Attribute, 'ENERGY_GENERATED', 'RECEIVED_KWH') WHERE Attribute LIKE '%ENERGY_GENERATED%';
/* End YUK-12863 */

/* Start YUK-12791 */
DELETE FROM YukonGroupRole WHERE RolePropertyID = -10821;
DELETE FROM YukonRoleProperty WHERE RolePropertyID = -10821;
/* End YUK-12791 */

/* Start YUK-12951 */
ALTER TABLE UserPage
MODIFY Module VARCHAR2(64);

ALTER TABLE UserPage
MODIFY PageName VARCHAR2(256);
/* End YUK-12951 */

/* Start YUK-12914 */
CREATE TABLE RfnBroadcastEventDeviceStatus  (
   DeviceId                 NUMBER                          NOT NULL,
   RfnBroadcastEventId      NUMBER                          NOT NULL,
   Result                   VARCHAR2(30)                    NOT NULL,
   DeviceReceivedTime       DATE,
   CONSTRAINT PK_RfnBroadcastEventDevStatus PRIMARY KEY (DeviceId, RfnBroadcastEventId)
);

CREATE INDEX Indx_RfnBcstEvntDev_DevIdMsgId ON RfnBroadcastEventDeviceStatus (
   RfnBroadcastEventId ASC,
   Result ASC
);

CREATE TABLE RfnBroadcastEvent  (
   RfnBroadcastEventId       NUMBER                          NOT NULL,
   EventSendTime             DATE                            NOT NULL,
   CONSTRAINT PK_RfnBroadcastEventId PRIMARY KEY (RfnBroadcastEventId)
);

ALTER TABLE RfnBroadcastEventDeviceStatus
   ADD CONSTRAINT FK_RfnBcstEvntDev_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;

ALTER TABLE RfnBroadcastEventDeviceStatus
   ADD CONSTRAINT FK_RfnBcstEvntDev_RfnBcstEvnt FOREIGN KEY (RfnBroadcastEventId)
      REFERENCES RfnBroadcastEvent (RfnBroadcastEventId)
         ON DELETE CASCADE;

ALTER TABLE RfnAddress
RENAME CONSTRAINT PK_RFNADD TO PK_RfnAddress;

ALTER TABLE RfnAddress
RENAME CONSTRAINT FK_RFNADD_DEVICE TO FK_RfnAddress_Device;
/* End YUK-12914 */

/* Start YUK-12961 */
CREATE TABLE DynamicLcrCommunications  (
   DeviceId             NUMBER                          NOT NULL,
   LastCommunication    DATE,
   LastNonZeroRuntime   DATE,
   Relay1Runtime        DATE,
   Relay2Runtime        DATE,
   Relay3Runtime        DATE,
   Relay4Runtime        DATE,
   CONSTRAINT PK_DynamicLcrCommunications PRIMARY KEY (DeviceId)
);

ALTER TABLE DynamicLcrCommunications
   ADD CONSTRAINT FK_Device_DynamicLcrComms FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId)
      ON DELETE CASCADE;
/* End YUK-12961 */

/* Start YUK-12995 */
ALTER TABLE PasswordHistory
MODIFY Password NVARCHAR2(128);

ALTER TABLE YukonUser
MODIFY Password NVARCHAR2(128);
/* End YUK-12995 */

/* Start YUK-12999 */
ALTER TABLE Job
MODIFY UserID NUMBER NULL;

ALTER TABLE Job
MODIFY Locale VARCHAR2(10) NULL;

ALTER TABLE Job
MODIFY TimeZone VARCHAR2(40) NULL;

ALTER TABLE Job
MODIFY ThemeName VARCHAR2(60) NULL;
/* End YUK-12999 */

/* Start YUK-13016 */
INSERT INTO Job (Jobid, BeanName, Disabled) VALUES (-2, 'rfnPerformanceVerificationEmailJobDefinition', 'N');
INSERT INTO JobScheduledRepeating VALUES (-2, '0 0 6 ? * *');
/* End YUK-13016 */

/* Start YUK-13008 */
INSERT INTO Job (Jobid, BeanName, Disabled) VALUES (-1, 'rfnPerformanceVerificationJobDefinition', 'N');
INSERT INTO JobScheduledRepeating VALUES (-1, '0 15 0 ? * *'); 
/* End YUK-13008 */

/* Start YUK-12485 */
DELETE FROM GlobalSetting
WHERE Name IN ('DISPATCH_MACHINE', 'DISPATCH_PORT', 'PORTER_MACHINE', 'PORTER_PORT',
'MACS_MACHINE', 'MACS_PORT', 'CAP_CONTROL_MACHINE', 'CAP_CONTROL_PORT', 'LOADCONTROL_MACHINE',
'LOADCONTROL_PORT', 'NOTIFICATION_PORT', 'NOTIFICATION_HOST');
/* End YUK-12485 */

/* Start YUK-12662 */
UPDATE YukonGroupRole SET Value = REPLACE (Value, '''', '') WHERE RolePropertyId = -40056;
UPDATE YukonGroupRole SET Value = REPLACE (Value, '"', '')  WHERE RolePropertyId = -40056;
UPDATE YukonGroupRole SET Value = REPLACE (Value, CHR(59), ',') WHERE RolePropertyId = -40056;
UPDATE YukonGroupRole SET Value = REPLACE (Value, 'start', '"start"') WHERE RolePropertyId = -40056;
UPDATE YukonGroupRole SET Value = REPLACE (Value, 'stop', '"stop"')   WHERE RolePropertyId = -40056;
UPDATE YukonGroupRole SET Value = REPLACE (Value, 'limit', '"limit"') WHERE RolePropertyId = -40056;
/* End YUK-12662 */

/* Start YUK-12892 */
/* @start-block */
DECLARE v_errorCount NUMBER := 0;
        v_count NUMBER;
        v_serialNumber VARCHAR2(60);
        v_truncatedSN VARCHAR2(60);
    CURSOR potential_duplicates_curs IS (
        SELECT LMHB.ManufacturerSerialNumber
        FROM LMHardwareBase LMHB
        JOIN InventoryBase IB ON LMHB.InventoryId = IB.InventoryId
        JOIN YukonPAObject YPAO ON IB.DeviceID = YPAO.PAObjectID
        WHERE YPAO.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN')
          AND REGEXP_LIKE (LMHB.ManufacturerSerialNumber, '^0[0-9]*$'));
BEGIN
    OPEN potential_duplicates_curs;
    LOOP
        FETCH potential_duplicates_curs INTO v_serialNumber;
        EXIT WHEN potential_duplicates_curs%NOTFOUND;
        
        SELECT COUNT(LMHB.ManufacturerSerialNumber) INTO v_count
        FROM  LMHardwareBase LMHB
        WHERE LMHB.ManufacturerSerialNumber = CAST(CAST(v_serialNumber AS NUMBER) AS VARCHAR2(60))
          AND v_serialNumber <> CAST(CAST(v_serialNumber AS NUMBER) AS VARCHAR2(60));
          
        IF v_count > 0
        THEN
            v_errorCount := v_errorCount + 1;
        END IF;
    END LOOP;
    IF v_errorCount > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'There are devices in the YukonPAObject table and related tables that have duplicate serial numbers that differ only in number of leading zeros.  These duplicates should be resolved by deleting one of the devices so that the truncation of leading zeros can proceed. See YUK-12892 for more information.');
    ELSE
        CLOSE potential_duplicates_curs;
        OPEN potential_duplicates_curs;
        LOOP
            FETCH potential_duplicates_curs INTO v_serialNumber;
            EXIT WHEN potential_duplicates_curs%NOTFOUND;
            SELECT CAST(CAST(v_serialNumber AS NUMBER) AS VARCHAR2(60)) INTO v_truncatedSN FROM dual;
            DBMS_OUTPUT.PUT_LINE('Converting SN: ' || v_serialNumber || ' to: ' || v_truncatedSN);
            
            UPDATE RfnAddress
            SET SerialNumber = v_truncatedSN
            WHERE DeviceId = (SELECT DeviceId 
                        FROM InventoryBase IB
                        JOIN LMHardwareBase LMHB ON IB.InventoryID = LMHB.InventoryID
                        WHERE LMHB.ManufacturerSerialNumber = v_serialNumber);
            
            UPDATE InventoryBase
            SET DeviceLabel = v_truncatedSN
            WHERE DeviceLabel = v_serialNumber;
            
            UPDATE LMHardwareBase
            SET ManufacturerSerialNumber = v_truncatedSN
            WHERE ManufacturerSerialNumber = v_serialNumber;
            
            UPDATE YukonPAObject
            SET PAOName = v_truncatedSN
            WHERE PAOName = v_serialNumber;

        END LOOP;
    END IF;
END;
/
/* @end-block */
/* End YUK-12892 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.1', '15-FEB-2014', 'Latest Update', 0, SYSDATE);