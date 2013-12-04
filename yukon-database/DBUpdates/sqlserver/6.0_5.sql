/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12765 */
INSERT INTO GlobalSetting (GlobalSettingId, Name, Value, Comments, LastChangedDate)
VALUES ((SELECT ISNULL(MAX(GlobalSettingId)+1,1) FROM GlobalSetting),  
        'CLIENT_APPLICATION_REMEMBER_ME', 'USERNAME_AND_PASSWORD', null, null);
/* End YUK-12765 */

/* Start YUK-12767 */
CREATE TABLE DeviceCollection (
   CollectionId         NUMERIC              NOT NULL,
   CollectionType       VARCHAR(50)          NOT NULL,
   PersistenceType      VARCHAR(50)          NOT NULL,
   CONSTRAINT PK_DeviceCollect PRIMARY KEY (CollectionId)
);
GO

CREATE TABLE DeviceCollectionByField (
   CollectionId         NUMERIC              NOT NULL,
   FieldName            VARCHAR(50)          NOT NULL,
   FieldValue           VARCHAR(100)         NOT NULL,
   CONSTRAINT PK_DeviceCollectByField PRIMARY KEY (CollectionId, FieldName)
);
GO

CREATE TABLE DeviceCollectionById (
   CollectionId         NUMERIC              NOT NULL,
   DeviceId             NUMERIC              NOT NULL,
   CONSTRAINT PK_DeviceCollectById PRIMARY KEY (CollectionId, DeviceId)
);
GO

ALTER TABLE DeviceCollectionByField
   ADD CONSTRAINT FK_DevCollByField_DevColl FOREIGN KEY (CollectionId)
      REFERENCES DeviceCollection (CollectionId)
         ON DELETE CASCADE;
GO

ALTER TABLE DeviceCollectionById
   ADD CONSTRAINT FK_DevCollById_DevColl FOREIGN KEY (CollectionId)
      REFERENCES DeviceCollection (CollectionId)
         ON DELETE CASCADE;
GO

/* @start-block */
DECLARE @JobId AS NUMERIC,
        @GroupName AS VARCHAR(1000),
        @JobPropertyId AS NUMERIC,
        @NewCollectionId AS NUMERIC,
        @NewJobPropertyId AS NUMERIC;
 
DECLARE jobs_curs CURSOR FOR(
    SELECT JobId
    FROM Job
    WHERE BeanName IN (
        'scheduledArchivedDataFileExportJobDefinition', 
        'scheduledWaterLeakFileExportJobDefinition',
        'scheduledMeterEventsFileExportJobDefinition'
    )
    AND Disabled <> 'D');
     
OPEN jobs_curs;
FETCH NEXT FROM jobs_curs INTO @JobId;
WHILE @@FETCH_STATUS = 0
BEGIN
    SELECT @GroupName = (SELECT '/System/Auto/' + Value), @JobPropertyId = JobPropertyId
    FROM JobProperty
    WHERE Name = 'uniqueIdentifier'
    AND JobId = @JobId;
     
    SELECT @NewCollectionId = (SELECT ISNULL(MAX(CollectionId) + 1, 1) FROM DeviceCollection);
    INSERT INTO DeviceCollection
    VALUES (
        @NewCollectionId,
        'GROUP',
        'FIELD'
    );
    INSERT INTO DeviceCollectionByField
    VALUES (
        @NewCollectionId,
        'name',
        @GroupName
    );
    INSERT INTO DeviceCollectionByField
    VALUES (
        @NewCollectionId,
        'description',
        ' '
    );
     
    SELECT @NewJobPropertyId = (SELECT MAX(JobPropertyId) + 1 FROM JobProperty);
    INSERT INTO JobProperty
    VALUES (
        @NewJobPropertyId,
        @JobId,
        'deviceCollectionId',
        @NewCollectionId
    )
     
    DELETE FROM JobProperty
    WHERE JobPropertyId = @JobPropertyId;
     
    FETCH NEXT FROM jobs_curs INTO @JobId;
END
CLOSE jobs_curs;
DEALLOCATE jobs_curs;
/* @end-block */
/* End YUK-12767 */

/* Start YUK-12754 */
ALTER TABLE AcctThermostatSchedule
DROP CONSTRAINT FK_AcctThermSch_CustAcct;

ALTER TABLE AcctThermostatScheduleEntry
DROP CONSTRAINT FK_AccThermSchEnt_AccThermSch;

ALTER TABLE InventoryToAcctThermostatSch
DROP CONSTRAINT FK_InvToAccThermSch_AccThermSc;

ALTER TABLE ThermostatEventHistory
DROP CONSTRAINT FK_ThermEventHist_AcctThermSch;

ALTER TABLE AcctThermostatSchedule
   ADD CONSTRAINT FK_AcctThermSch_CustAcct FOREIGN KEY (AccountId)
      REFERENCES CustomerAccount (AccountID)
      ON DELETE CASCADE;
GO

ALTER TABLE AcctThermostatScheduleEntry
   ADD CONSTRAINT FK_AccThermSchEnt_AccThermSch FOREIGN KEY (AcctThermostatScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
      ON DELETE CASCADE;
GO
 
ALTER TABLE InventoryToAcctThermostatSch
   ADD CONSTRAINT FK_InvToAccThermSch_AccThermSc FOREIGN KEY (AcctThermostatScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
      ON DELETE CASCADE;
GO

ALTER TABLE ThermostatEventHistory
   ADD CONSTRAINT FK_ThermEventHist_AcctThermSch FOREIGN KEY (ScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
         ON DELETE CASCADE;
GO
/* End YUK-12754 */

/* Start YUK-12789 */
UPDATE GlobalSetting 
SET Name = 'JMS_BROKER_HOST'
WHERE Name = 'DISPATCH_MACHINE';
/* End YUK-12789 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '9-DEC-2013', 'Latest Update', 5, GETDATE());*/