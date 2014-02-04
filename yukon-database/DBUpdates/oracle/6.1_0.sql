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
MODIFY PageName VARCHAR2(255);
/* End YUK-12951 */

/* Start YUK-12914 */
CREATE TABLE RfBroadcastEventDevice  (
   RfBroadcastEventDeviceId NUMBER                          NOT NULL,
   DeviceId                 NUMBER                          NOT NULL,
   RfBroadcastEventId       NUMBER                          NOT NULL,
   Result                   VARCHAR2(30)                    NOT NULL,
   ReceivedTime             DATE,
   CONSTRAINT PK_RfBroadcastEventDevice PRIMARY KEY (RfBroadcastEventDeviceId)
);

CREATE INDEX AK_RfBrdcstEvntDev_DevIdMsgId ON RfBroadcastEventDevice (
   DeviceId ASC,
   RfBroadcastEventId ASC,
   Result ASC
);

CREATE TABLE RfBroadcastEvent  (
   RfBroadcastEventId   NUMBER                          NOT NULL,
   SendTime             DATE                            NOT NULL,
   CONSTRAINT PK_RfBroadcastEventId PRIMARY KEY (RfBroadcastEventId)
);

ALTER TABLE RfBroadcastEventDevice
   ADD CONSTRAINT FK_RfBrdcstEvntDev_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;

ALTER TABLE RfBroadcastEventDevice
   ADD CONSTRAINT FK_RfBrdcstEvntDev_RfBrcstEvnt FOREIGN KEY (RfBroadcastEventId)
      REFERENCES RfBroadcastEvent (RfBroadcastEventId)
         ON DELETE CASCADE;
/* End YUK-12914 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.1', '15-FEB-2014', 'Latest Update', 0, SYSDATE);*/