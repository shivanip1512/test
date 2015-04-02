/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14171 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId =  -20903;
  
DELETE FROM YukonRoleProperty
WHERE RolePropertyId =  -20903;
/* End YUK-14171 */

/* Start YUK-14167 */
CREATE TABLE RegulatorEvents  (
   RegulatorEventId     NUMBER                          NOT NULL,
   EventType            VARCHAR2(64)                    NOT NULL,
   RegulatorId          NUMBER                          NOT NULL,
   TimeStamp            DATE                            NOT NULL,
   UserName             VARCHAR2(64)                    NOT NULL,
   SetPointValue        FLOAT,
   TapPosition          NUMBER,
   Phase                CHAR(1),
   CONSTRAINT PK_RegulatorEvents PRIMARY KEY (RegulatorEventId)
);

ALTER TABLE RegulatorEvents
   ADD CONSTRAINT FK_RegulatorEvents_Regulator FOREIGN KEY (RegulatorId)
      REFERENCES Regulator (RegulatorId)
      ON DELETE CASCADE;
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
    v_currentConfigNumber            NUMBER;
    
    v_currentConfigId                NUMBER;
    v_newConfigId                    NUMBER;
    
    v_currentConfigCategoryId        NUMBER;
    v_newConfigCategoryId            NUMBER;
    
    v_defaultHeartbeatCategoryId     NUMBER;
    
    CURSOR regulator_curs IS SELECT DeviceConfigurationID FROM DeviceConfiguration WHERE Name LIKE 'Generated Regulator Config%';
BEGIN
    SELECT DeviceConfigCategoryId INTO v_defaultHeartbeatCategoryId FROM DeviceConfigCategory WHERE Name = 'Default Regulator Heartbeat Category';
    OPEN regulator_curs;
    LOOP
        FETCH regulator_curs into v_currentConfigId;
        EXIT WHEN regulator_curs%NOTFOUND;
    
        SELECT CAST(SUBSTR((SELECT Name FROM DeviceConfiguration WHERE DeviceConfigurationID = v_currentConfigId), 27) AS NUMERIC) INTO v_currentConfigNumber FROM DUAL;
        SELECT DeviceConfigCategoryId INTO v_currentConfigCategoryId FROM DeviceConfigCategoryMap WHERE DeviceConfigurationId = v_currentConfigId;
        
        /* Add new heartbeat category to current config (will become LTC specific configuration category) */
        SELECT MAX(DeviceConfigCategoryId) + 1 INTO v_newConfigCategoryId FROM DeviceConfigCategory;
        INSERT INTO DeviceConfigCategory
        VALUES (v_newConfigCategoryId, 'heartbeat', 'Generated Regulator Heartbeat Category ' || CAST(v_currentConfigNumber AS VARCHAR2(16)), NULL);
        
        /* Map new category to new config for LTC regulators */
        INSERT INTO DeviceConfigCategoryMap
        VALUES (v_currentConfigId, v_newConfigCategoryId);
        
        /* Transfer existing heartbeat values from current regulator category to current heartbeat category */
        UPDATE DeviceConfigCategoryItem
        SET DeviceConfigCategoryId = v_newConfigCategoryId
        WHERE DeviceConfigCategoryId = v_currentConfigCategoryId
          AND ItemName IN ('heartbeatPeriod', 'heartbeatValue');
        
        INSERT INTO DeviceConfigCategoryItem
        VALUES ((SELECT MAX(DeviceConfigCategoryItemId) + 1 FROM DeviceConfigCategoryItem),
                v_newConfigCategoryId, 'heartbeatMode', 'COUNTDOWN');
        
        
        /* Create new configuration to put gang operated/phase operated regulators from the current configuration into. */
        SELECT MAX(DeviceConfigurationId) + 1 INTO v_newConfigId FROM DeviceConfiguration;
        INSERT INTO DeviceConfiguration
        VALUES (v_newConfigId, 'Generated Regulator Config ' || CAST(v_currentConfigNumber AS VARCHAR2(16)) || 'a', NULL);
         
        /* Map existing regulator category and GO/PO heartbeat cateogry to new config for GO/PO regulators */
        INSERT INTO DeviceConfigCategoryMap
        VALUES (v_newConfigId, v_currentConfigCategoryId);

        INSERT INTO DeviceConfigCategoryMap 
        VALUES (v_newConfigId, v_defaultHeartbeatCategoryId);
       
        /* Fix category device types for both current and new categories */
        DELETE FROM DeviceConfigDeviceTypes
        WHERE DeviceConfigurationId = v_currentConfigId
          AND PaoType IN ('GO_REGULATOR', 'PO_REGULATOR');
        
        INSERT INTO DeviceConfigDeviceTypes 
        VALUES ((SELECT MAX(DeviceConfigDeviceTypeId) + 1 FROM DeviceConfigDeviceTypes), v_newConfigId, 'GO_REGULATOR');
        
        INSERT INTO DeviceConfigDeviceTypes
        VALUES ((SELECT MAX(DeviceConfigDeviceTypeId) + 1 FROM DeviceConfigDeviceTypes), v_newConfigId, 'PO_REGULATOR');
        
        /* Update DeviceConfigurationDeviceMap to move GO/PO regulators from old config to new config */
        UPDATE DeviceConfigurationDeviceMap
        SET DeviceConfigurationId = v_newConfigId
        WHERE DeviceConfigurationId = v_currentConfigId
          AND DeviceID IN (SELECT PAObjectId FROM YukonPAObject WHERE Type IN ('GO_REGULATOR', 'PO_REGULATOR'));
        
    END LOOP;
    CLOSE regulator_curs;
END;
/
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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-MAR-2015', 'Latest Update', 1, SYSDATE);*/