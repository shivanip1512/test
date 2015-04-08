/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14171 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId =  -20903;
  
DELETE FROM YukonRoleProperty
WHERE RolePropertyId =  -20903;
/* End YUK-14171 */


/* Start YUK-14167 */
CREATE TABLE RegulatorEvents (
   RegulatorEventId     NUMERIC              NOT NULL,
   EventType            VARCHAR(64)          NOT NULL,
   RegulatorId          NUMERIC              NOT NULL,
   TimeStamp            DATETIME             NOT NULL,
   UserName             VARCHAR(64)          NOT NULL,
   SetPointValue        FLOAT                NULL,
   TapPosition          NUMERIC              NULL,
   Phase                CHAR(1)              NULL,
   CONSTRAINT PK_RegulatorEvents PRIMARY KEY (RegulatorEventId)
);

ALTER TABLE RegulatorEvents
   ADD CONSTRAINT FK_RegulatorEvents_Regulator FOREIGN KEY (RegulatorId)
      REFERENCES Regulator (RegulatorId)
         ON DELETE CASCADE;
GO
/* End YUK-14167 */


/* Start YUK-14170 */
/* Create new default configuration heartbeat category */
INSERT INTO DeviceConfigCategory (DeviceConfigCategoryId, CategoryType, Name, Description)
VALUES ((SELECT MAX(DeviceConfigCategoryId) + 1 FROM DeviceConfigCategory), 'heartbeat', 'Default Regulator Heartbeat Category', NULL);

INSERT INTO DeviceConfigCategoryMap (DeviceConfigurationId, DeviceConfigCategoryId)
VALUES (-2, (SELECT DeviceConfigCategoryId FROM DeviceConfigCategory WHERE Name = 'Default Regulator Heartbeat Category'));

/* Move heartbeat default values into new category */
UPDATE DeviceConfigCategoryItem
SET DeviceConfigCategoryId = (SELECT DeviceConfigCategoryId FROM DeviceConfigCategory WHERE Name = 'Default Regulator Heartbeat Category')
WHERE DeviceConfigCategoryId = (SELECT DeviceConfigCategoryId FROM DeviceConfigCategory WHERE Name = 'Default Regulator Category')
  AND ItemName IN ('heartbeatPeriod', 'heartbeatValue');

INSERT INTO DeviceConfigCategoryItem (DeviceConfigCategoryItemId, DeviceConfigCategoryId, ItemName, ItemValue)
VALUES ((SELECT MAX(DeviceConfigCategoryItemId) + 1 FROM DeviceConfigCategoryItem),
        (SELECT DeviceConfigCategoryId FROM DeviceConfigCategory WHERE Name = 'Default Regulator Heartbeat Category'),
       'heartbeatMode', 'NONE');

/* @start-block */
DECLARE
    @currentConfigNumber            NUMERIC,

    @currentConfigId                NUMERIC,
    @newConfigId                    NUMERIC,

    @currentConfigCategoryId        NUMERIC,
    @newConfigCategoryId            NUMERIC,

    @defaultHeartbeatCategoryId     NUMERIC;

    DECLARE regulator_curs  CURSOR STATIC FOR (SELECT DeviceConfigurationID FROM DeviceConfiguration WHERE Name LIKE 'Generated Regulator Config%');
BEGIN
    SET @defaultHeartbeatCategoryId = (SELECT DeviceConfigCategoryId FROM DeviceConfigCategory WHERE Name = 'Default Regulator Heartbeat Category');
    OPEN regulator_curs;
    FETCH FROM regulator_curs INTO @currentConfigId;
    WHILE @@FETCH_STATUS = 0
    BEGIN
        SET @currentConfigNumber = CAST(SUBSTRING((SELECT Name FROM DeviceConfiguration WHERE DeviceConfigurationID = @currentConfigId), 27, 16) AS NUMERIC);
        SET @currentConfigCategoryId = (SELECT DeviceConfigCategoryId FROM DeviceConfigCategoryMap WHERE DeviceConfigurationId = @currentConfigId);
        
        /* Add new heartbeat category to current config (will become LTC specific configuration category) */
        SET @newConfigCategoryId = (SELECT MAX(DeviceConfigCategoryId) + 1 FROM DeviceConfigCategory);
        INSERT INTO DeviceConfigCategory (DeviceConfigCategoryId, CategoryType, Name, Description)
        VALUES (@newConfigCategoryId, 'heartbeat', 'Generated Regulator Heartbeat Category ' + CAST(@currentConfigNumber AS VARCHAR(16)), NULL);
        
        /* Map new category to current config for LTC regulators */
        INSERT INTO DeviceConfigCategoryMap (DeviceConfigurationId, DeviceConfigCategoryId)
        VALUES (@currentConfigId, @newConfigCategoryId);
        
        /* Transfer existing heartbeat values from current regulator category to current heartbeat category and add heartbeat mode */
        UPDATE DeviceConfigCategoryItem
        SET DeviceConfigCategoryId = @newConfigCategoryId
        WHERE DeviceConfigCategoryId = @currentConfigCategoryId
          AND ItemName IN ('heartbeatPeriod', 'heartbeatValue');
        
        INSERT INTO DeviceConfigCategoryItem (DeviceConfigCategoryItemId, DeviceConfigCategoryId, ItemName, ItemValue)
        VALUES ((SELECT MAX(DeviceConfigCategoryItemId) + 1 FROM DeviceConfigCategoryItem),
              @newConfigCategoryId, 'heartbeatMode', 'COUNTDOWN');
        
        
        /* Create new configuration to put gang operated/phase operated regulators from the current configuration into. */
        SET @newConfigId = (SELECT MAX(DeviceConfigurationId) + 1 FROM DeviceConfiguration);
        INSERT INTO DeviceConfiguration (DeviceConfigurationId, Name, Description)
        VALUES (@NewConfigId, 'Generated Regulator Config ' + CAST(@currentConfigNumber AS VARCHAR(32)) + 'a', NULL);
         
        /* Map existing regulator category and GO/PO heartbeat cateogry to new config for GO/PO regulators */
        INSERT INTO DeviceConfigCategoryMap (DeviceConfigurationId, DeviceConfigCategoryId)
        VALUES (@newConfigId, @currentConfigCategoryId);
        
        INSERT INTO DeviceConfigCategoryMap (DeviceConfigurationId, DeviceConfigCategoryId)
        VALUES (@newConfigId, @defaultHeartbeatCategoryId);
        
        /* Fix category device types for both current and new configurations */
        DELETE FROM DeviceConfigDeviceTypes
        WHERE DeviceConfigurationId = @currentConfigId
          AND PaoType IN ('GO_REGULATOR', 'PO_REGULATOR');
        
        INSERT INTO DeviceConfigDeviceTypes (DeviceConfigDeviceTypeId, DeviceConfigurationId, PaoType)
        VALUES ((SELECT MAX(DeviceConfigDeviceTypeId) + 1 FROM DeviceConfigDeviceTypes), @newConfigId, 'GO_REGULATOR');
        
        INSERT INTO DeviceConfigDeviceTypes (DeviceConfigDeviceTypeId, DeviceConfigurationId, PaoType)
        VALUES ((SELECT MAX(DeviceConfigDeviceTypeId) + 1 FROM DeviceConfigDeviceTypes), @newConfigId, 'PO_REGULATOR');
        
        /* Update DeviceConfigurationDeviceMap to move GO/PO regulators from old config to new config */
        UPDATE DeviceConfigurationDeviceMap
        SET DeviceConfigurationId = @newConfigId
        WHERE DeviceConfigurationId = @currentConfigId
          AND DeviceID IN (SELECT PAObjectId FROM YukonPAObject WHERE Type IN ('GO_REGULATOR', 'PO_REGULATOR'));
        
        /* Grab the next configuration to be split */
        FETCH FROM regulator_curs INTO @currentConfigId
    END
    CLOSE regulator_curs;
    DEALLOCATE regulator_curs;
END;
/* @end-block */

/* Delete any device configurations that do not have any devices mapped to them */
DELETE FROM DeviceConfiguration 
WHERE DeviceConfigurationID NOT IN (SELECT DeviceConfigurationId FROM DeviceConfigurationDeviceMap)
  AND Name LIKE 'Generated Regulator Config%';
  
DELETE FROM DeviceConfigCategory
WHERE DeviceConfigCategoryId NOT IN (SELECT DeviceConfigCategoryId FROM DeviceConfigCategoryMap)
  AND (Name LIKE 'Generated Regulator Heartbeat Category%'
    OR Name LIKE 'Generated Regulator Config Category%');  
/* End YUK-14170 */

/* Start YUK-13939 */
/* Alter Table structure to add new columns */
ALTER TABLE RPHTag
    ADD 
    PeakUp BIT,
    PeakDown BIT,
    UnreasonableUp BIT,
    UnreasonableDown BIT,
    ChangeOut BIT,
    Accepted BIT;
GO

/* Initialize new column values to zero */
UPDATE RPHTag SET PeakUp = 0, PeakDown = 0, UnreasonableUp = 0, UnreasonableDown = 0, ChangeOut = 0, Accepted = 0;

/* Update new columns using TagName column */
UPDATE RPHTag SET ChangeOut = 1
WHERE ChangeId IN (SELECT ChangeId FROM RPHTag WHERE TagName = 'UDC');

UPDATE RPHTag SET PeakUp = 1 
WHERE ChangeId IN  (SELECT ChangeId FROM RPHTag WHERE TagName = 'PU');

UPDATE RPHTag SET PeakDown = 1 
WHERE ChangeId IN  (SELECT ChangeId FROM RPHTag WHERE TagName = 'PD');

UPDATE RPHTag SET UnreasonableUp = 1
WHERE ChangeId IN  (SELECT ChangeId FROM RPHTag WHERE TagName = 'UU');

UPDATE RPHTag SET UnreasonableDown = 1 
WHERE ChangeId IN  (SELECT ChangeId FROM RPHTag WHERE TagName = 'UD');

UPDATE RPHTag SET Accepted = 1
WHERE ChangeId IN  (SELECT ChangeId FROM RPHTag WHERE TagName = 'OK');

/* Alter table to drop existing primary key constraint */
ALTER TABLE RPHTag
    DROP CONSTRAINT PK_RPHTag;

/* Delete duplicate rows */
WITH TempEmp (ChangeId, DuplicateRecCount) AS (
    SELECT ChangeId, ROW_NUMBER() OVER (PARTITION by ChangeId ORDER BY ChangeId) AS DuplicateRecCount
    FROM RPHTag)
DELETE FROM TempEmp WHERE DuplicateRecCount > 1;

/* Alter table to make ChangeId as Primary Key */
ALTER TABLE RPHTag
    ADD CONSTRAINT PK_RPHTag PRIMARY KEY (ChangeId);

/* Alter table to drop the TagName column */
ALTER TABLE RPHTag
    DROP COLUMN TagName;

ALTER TABLE RPHTag
    ALTER COLUMN PeakUp BIT NOT NULL;
ALTER TABLE RPHTag
    ALTER COLUMN PeakDown BIT NOT NULL;
ALTER TABLE RPHTag
    ALTER COLUMN UnreasonableUp BIT NOT NULL;
ALTER TABLE RPHTag
    ALTER COLUMN UnreasonableDown BIT NOT NULL;
ALTER TABLE RPHTag
    ALTER COLUMN ChangeOut BIT NOT NULL;
ALTER TABLE RPHTag
    ALTER COLUMN Accepted BIT NOT NULL;
/* End YUK-13939 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-MAR-2015', 'Latest Update', 1, GETDATE());*/