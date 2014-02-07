/******************************************/
/**** SQL Server DBupdates             ****/
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
ALTER COLUMN Module VARCHAR(64) NOT NULL;

ALTER TABLE UserPage
ALTER COLUMN PageName VARCHAR(256) NOT NULL;
/* End YUK-12951 */

/* Start YUK-12914 */
CREATE TABLE RfnBroadcastEventDeviceStatus (
    DeviceId                 NUMERIC              NOT NULL,
    RfnBroadcastEventId      NUMERIC              NOT NULL,
    Result                   VARCHAR(30)          NOT NULL,
    DeviceReceivedTime             DATETIME             NULL,
    CONSTRAINT PK_RfnBroadcastEventDevStatus PRIMARY KEY (DeviceId, RfnBroadcastEventId)
);
GO

CREATE INDEX Indx_RfnBcstEvntDev_DevIdMsgId ON RfnBroadcastEventDeviceStatus (
    RfnBroadcastEventId ASC,
    Result ASC
);
GO

CREATE TABLE RfnBroadcastEvent (
    RfnBroadcastEventId   NUMERIC              NOT NULL,
    EventSendTime         DATETIME             NOT NULL,
    CONSTRAINT PK_RfnBroadcastEventId PRIMARY KEY (RfnBroadcastEventId)
);
GO

ALTER TABLE RfnBroadcastEventDeviceStatus
   ADD CONSTRAINT FK_RfnBcstEvntDev_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
GO

ALTER TABLE RfnBroadcastEventDeviceStatus
   ADD CONSTRAINT FK_RfnBcstEvntDev_RfnBcstEvnt FOREIGN KEY (RfnBroadcastEventId)
      REFERENCES RfnBroadcastEvent (RfnBroadcastEventId)
         ON DELETE CASCADE;
GO

ALTER TABLE Job
ALTER COLUMN UserId NUMERIC NULL;

ALTER TABLE Job
ALTER COLUMN Locale VARCHAR(10) NULL;

ALTER TABLE Job
ALTER COLUMN TimeZone VARCHAR(40) NULL;

ALTER TABLE Job
ALTER COLUMN ThemeName VARCHAR(60) NULL;

GO
sp_rename 'RFNAddress', 'RfnAddress', 'OBJECT';
GO
sp_rename 'PK_RFNAdd', 'PK_RfnAddress', 'OBJECT';
GO
sp_rename 'FK_RFNAdd_Device', 'FK_RfnAddress_Device', 'OBJECT';
GO
sp_rename 'RfnAddress.Indx_RFNAdd_SerNum_Man_Mod_UNQ', 'Indx_RfnAdd_SerNum_Man_Mod_UNQ', 'INDEX';
/* End YUK-12914 */

/* Start YUK-12961 */
CREATE TABLE DynamicLcrCommunications (
   DeviceId             NUMERIC              NOT NULL,
   LastCommunication    DATETIME             NULL,
   LastNonZeroRuntime   DATETIME             NULL,
   Relay1Runtime        DATETIME             NULL,
   Relay2Runtime        DATETIME             NULL,
   Relay3Runtime        DATETIME             NULL,
   Relay4Runtime        DATETIME             NULL,
   CONSTRAINT PK_DynamicLcrCommunications PRIMARY KEY (DeviceId)
);
GO

ALTER TABLE DynamicLcrCommunications
   ADD CONSTRAINT FK_YukonPAO_DynamicLcrComms FOREIGN KEY (DeviceId)
      REFERENCES YukonPAObject (PAObjectID)
      ON DELETE CASCADE;
/* End YUK-12961 */
      
/* Start YUK-12506 */      
ALTER TABLE Yukonuser
ALTER COLUMN Password NVARCHAR(128) NOT NULL;
/* Start YUK-12506 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.1', '15-FEB-2014', 'Latest Update', 0, GETDATE());*/