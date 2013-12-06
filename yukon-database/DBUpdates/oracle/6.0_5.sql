/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12765 */
INSERT INTO GlobalSetting (GlobalSettingId, Name, Value, Comments, LastChangedDate)
VALUES ((SELECT NVL(MAX(GlobalSettingId)+1,1) FROM GlobalSetting),  
        'CLIENT_APPLICATION_REMEMBER_ME', 'USERNAME_AND_PASSWORD', null, null);
/* End YUK-12765 */

/* Start YUK-12767 */
CREATE TABLE DeviceCollection  (
   CollectionId         NUMBER                          NOT NULL,
   CollectionType       VARCHAR2(50)                    NOT NULL,
   PersistenceType      VARCHAR2(50)                    NOT NULL,
   CONSTRAINT PK_DeviceCollect PRIMARY KEY (CollectionId)
);

CREATE TABLE DeviceCollectionByField  (
   CollectionId         NUMBER                          NOT NULL,
   FieldName            VARCHAR2(50)                    NOT NULL,
   FieldValue           VARCHAR2(100)                   NOT NULL,
   CONSTRAINT PK_DeviceCollectByField PRIMARY KEY (CollectionId, FieldName)
);

CREATE TABLE DeviceCollectionById  (
   CollectionId         NUMBER                          NOT NULL,
   DeviceId             NUMBER                          NOT NULL,
   CONSTRAINT PK_DeviceCollectById PRIMARY KEY (CollectionId, DeviceId)
);

ALTER TABLE DeviceCollectionByField
   ADD CONSTRAINT FK_DevCollByField_DevColl FOREIGN KEY (CollectionId)
      REFERENCES DeviceCollection (CollectionId)
      ON DELETE CASCADE;

ALTER TABLE DeviceCollectionById
   ADD CONSTRAINT FK_DevCollById_DevColl FOREIGN KEY (CollectionId)
      REFERENCES DeviceCollection (CollectionId)
      ON DELETE CASCADE;

/* @start-block */
DECLARE 
    v_JobId            NUMBER;
    v_GroupName        VARCHAR2(1000);
    v_JobPropertyId    NUMBER;
    v_NewCollectionId  NUMBER;
    v_NewJobPropertyId NUMBER;
    v_DeviceId         NUMBER;
    
    CURSOR jobs_curs IS (
        SELECT JobId
        FROM Job
        WHERE BeanName IN (
            'scheduledArchivedDataFileExportJobDefinition', 
            'scheduledWaterLeakFileExportJobDefinition',
            'scheduledMeterEventsFileExportJobDefinition')
        AND Disabled <> 'D');
    
    CURSOR devices_curs IS (
        SELECT YukonPaoId
        FROM DeviceGroupMember DGM
        JOIN DeviceGroup DG ON DG.DeviceGroupId = DGM.DeviceGroupId
        JOIN JobProperty JP ON JP.Value = DG.GroupName
        WHERE JP.JobID = v_JobId
        AND JP.Name = 'uniqueIdentifier');
BEGIN
    OPEN jobs_curs;
    LOOP
        FETCH jobs_curs INTO v_JobId;
        EXIT WHEN jobs_curs%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Beginning');
        SELECT JobPropertyId INTO v_JobPropertyId
        FROM JobProperty
        WHERE Name = 'uniqueIdentifier'
        AND JobId = v_JobId;
        
        SELECT NVL(MAX(CollectionId) + 1, 1) INTO v_NewCollectionId FROM DeviceCollection;
        
        INSERT INTO DeviceCollection
        VALUES (
            v_NewCollectionId,
            'idList',
            'DEVICE_LIST'
        );
        
        OPEN devices_curs;
        LOOP
            FETCH devices_curs INTO v_DeviceId;
            EXIT WHEN devices_curs%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE('Device Id: ' || v_DeviceId);
            INSERT INTO DeviceCollectionById
            VALUES (
                v_NewCollectionId,
                v_DeviceId
            );
        END LOOP;
        CLOSE devices_curs;
        
        SELECT NVL(MAX(JobPropertyId) + 1,1) INTO v_NewJobPropertyId FROM JobProperty;
        
        INSERT INTO JobProperty
        VALUES (
            v_NewJobPropertyId,
            v_JobId,
            'deviceCollectionId',
            v_NewCollectionId
        );

        DELETE FROM JobProperty
        WHERE JobPropertyId = v_JobPropertyId;
    END LOOP;
    CLOSE jobs_curs;
END;
/
/* @end-block */
DELETE FROM DeviceGroup
WHERE ParentDeviceGroupId = (SELECT DeviceGroupId 
                             FROM DeviceGroup 
                             WHERE SystemGroupEnum = 'AUTO');
DELETE FROM DeviceGroup
WHERE SystemGroupEnum = 'AUTO';
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

ALTER TABLE AcctThermostatScheduleEntry
   ADD CONSTRAINT FK_AccThermSchEnt_AccThermSch FOREIGN KEY (AcctThermostatScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
      ON DELETE CASCADE;
 
ALTER TABLE InventoryToAcctThermostatSch
   ADD CONSTRAINT FK_InvToAccThermSch_AccThermSc FOREIGN KEY (AcctThermostatScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
      ON DELETE CASCADE;

ALTER TABLE ThermostatEventHistory
   ADD CONSTRAINT FK_ThermEventHist_AcctThermSch FOREIGN KEY (ScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
         ON DELETE CASCADE;
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
/*INSERT INTO CTIDatabase VALUES ('6.0', '9-DEC-2013', 'Latest Update', 5, SYSDATE);*/