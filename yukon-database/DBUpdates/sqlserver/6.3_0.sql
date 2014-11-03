/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13472 */
INSERT INTO YukonRoleProperty VALUES (-21315, -213, 'Demand Reset', 'true', 'Controls access to Demand Reset collection action.');
/* End YUK-13472 */

/* Start YUK-13709 */
INSERT INTO YukonRoleProperty VALUES (-90047, -900, 'Allow DR Enable/Disable', 'true', 'Controls access to enable or disable control areas,load programs and load groups. Requires Allow DR Control.');
INSERT INTO YukonRoleProperty VALUES (-90048, -900, 'Allow Change Gears', 'true', 'Controls access to change gears for scenarios, control areas, and load programs. Requires Allow DR Control.');
/* End YUK-13709 */

/* Start YUK-13354 */
UPDATE CommandRequestExec 
SET    CommandRequestExecType = 'METER_CONNECT_DISCONNECT_WIDGET'
WHERE  CommandRequestExecType = 'CONTROL_CONNECT_DISCONNECT_COMAMND';
/* End YUK-13354 */

/* Start YUK-13801 */
UPDATE POINT
SET POINTOFFSET = 3
WHERE POINTOFFSET = 2 
  AND PointType = 'Analog'
  AND PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE PAOClass = 'LOADMANAGEMENT');
 
UPDATE POINT
SET POINTOFFSET = 2
WHERE POINTOFFSET = 1 
  AND PointType = 'Analog'
  AND PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE PAOClass = 'LOADMANAGEMENT');
/* End YUK-13801 */

/* Start YUK-13806 */
DELETE FROM GlobalSetting WHERE Name = 'SMTP_HOST' AND Value = '127.0.0.1';
/* End YUK-13806 */

/* Start YUK-13758 */
INSERT INTO YukonListEntry VALUES (2026, 1005, 0, 'ecobee3', 1330);
INSERT INTO YukonListEntry VALUES (2027, 1005, 0, 'ecobee Smart', 1331);
/* End YUK-13758 */

/* Start YUK-13552 */
CREATE TABLE GatewayCertificateUpdate (
    UpdateId        NUMERIC         NOT NULL,
    CertificateId   VARCHAR(100)    NOT NULL,
    SendDate        DATETIME        NOT NULL,
    FileName        VARCHAR(100)    NOT NULL,
    CONSTRAINT PK_GatewayCertificateUpdate PRIMARY KEY (UpdateId)
);
GO

CREATE TABLE GatewayCertificateUpdateEntry (
    EntryId         NUMERIC         NOT NULL,
    UpdateId        NUMERIC         NOT NULL,
    GatewayId       NUMERIC         NOT NULL,
    UpdateStatus    VARCHAR(40)     NOT NULL,
    CONSTRAINT PK_GatewayCertificateUpdEntry PRIMARY KEY (EntryId)
);
GO

ALTER TABLE GatewayCertificateUpdateEntry 
    ADD CONSTRAINT FK_GatewayCUEnt_GatewayCertUpd FOREIGN KEY (UpdateId)
        REFERENCES GatewayCertificateUpdate (UpdateId)
            ON DELETE CASCADE;
GO

ALTER TABLE GatewayCertificateUpdateEntry
    ADD CONSTRAINT FK_GatewayCertUpdateEnt_Device FOREIGN KEY (GatewayId)
        REFERENCES Device (DeviceId)
            ON DELETE CASCADE;
GO
/* End YUK-13552 */

/* Start YUK-13804 */
ALTER TABLE Job ADD JobGroupId INT;
GO
UPDATE Job SET JobGroupId = JobId;
GO
ALTER TABLE Job ALTER COLUMN JobGroupId INT NOT NULL;

sp_rename 'FileExportHistory.Initiator', 'JobName', 'COLUMN';

UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Billing Schedule: ', '');
UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Archived Data Export Schedule: ', '');
UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Data Export Schedule: ', '');
UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Water Leak Report Schedule: ', '');
UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Meter Events Report Schedule: ', '');

CREATE TABLE temp_JobFileExport (
   JobID                INT                  NOT NULL,
   BeanName             VARCHAR(250)         NOT NULL,
   Value                VARCHAR(4000)        NOT NULL
);
GO
 
INSERT INTO temp_JobFileExport (JobID, BeanName, Value)
    SELECT MAX(J.JobId), BeanName, Value FROM Job J JOIN JobProperty JP ON J.JobId = JP.JobId
    WHERE Name = 'name'
        AND (BeanName = 'scheduledBillingFileExportJobDefinition'
         OR  BeanName = 'scheduledArchivedDataFileExportJobDefinition'
         OR  BeanName = 'scheduledWaterLeakFileExportJobDefinition'
         OR  BeanName = 'scheduledMeterEventsFileExportJobDefinition')
    GROUP BY BeanName, Value;

ALTER TABLE FileExportHistory ADD JobGroupId INT;
GO

UPDATE FileExportHistory SET FileExportHistory.JobGroupId =
    (SELECT DISTINCT T.JobId FROM temp_JobFileExport T WHERE T.value = JobName
        AND BeanName = 'scheduledBillingFileExportJobDefinition')
WHERE FileExportType = 'BILLING';
  
UPDATE FileExportHistory SET FileExportHistory.JobGroupId =
    (SELECT DISTINCT T.JobId FROM temp_JobFileExport T WHERE T.value = JobName
        AND BeanName = 'scheduledArchivedDataFileExportJobDefinition')
WHERE FileExportType = 'ARCHIVED_DATA_EXPORT';
  
UPDATE FileExportHistory SET FileExportHistory.JobGroupId =
    (SELECT DISTINCT T.JobId FROM temp_JobFileExport T WHERE T.value = JobName
        AND BeanName = 'scheduledWaterLeakFileExportJobDefinition')
WHERE FileExportType = 'WATER_LEAK';
  
UPDATE FileExportHistory SET FileExportHistory.JobGroupId =
    (SELECT DISTINCT T.JobId FROM temp_JobFileExport T WHERE T.value = JobName
        AND BeanName = 'scheduledMeterEventsFileExportJobDefinition')
WHERE FileExportType = 'METER_EVENTS';

ALTER TABLE FileExportHistory ALTER COLUMN JobGroupId INT NOT NULL;
GO
DROP TABLE temp_JobFileExport;
/* End YUK-13804 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.3', '30-SEP-2014', 'Latest Update', 0, GETDATE());*/