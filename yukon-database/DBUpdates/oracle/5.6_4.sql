/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11880 */
ALTER TABLE ECToAcctThermostatSchedule
    DROP CONSTRAINT FK_ECToAccThermSch_AccThermSch;

ALTER TABLE ECToAcctThermostatSchedule
    ADD CONSTRAINT FK_ECToAccThermSch_AccThermSch FOREIGN KEY (AcctThermostatScheduleId)
        REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
            ON DELETE CASCADE;
/* End YUK-11880 */

/* Start YUK-12012 */
ALTER TABLE AcctThermostatSchedule
ADD Archived CHAR(1);

UPDATE AcctThermostatSchedule
SET Archived = 'N';

ALTER TABLE AcctThermostatSchedule
MODIFY Archived CHAR(1) NOT NULL;
/* End YUK-12012 */

/* Start YUK-12116 */
/* @start-block */
DECLARE 
    v_configId NUMBER;
    v_itemId NUMBER;

    /* Find all of the 420 configurations without a Display Digits field and add it with the default value of 5. */
BEGIN 

    SELECT MIN(DC.DeviceConfigurationId) INTO v_configId
    FROM DeviceConfiguration DC
    WHERE DC.Type = 'MCT420'
    MINUS
    SELECT DC.DeviceConfigurationId
    FROM DeviceConfiguration DC, DeviceConfigurationItem DCI
    WHERE DC.Type = 'MCT420' 
      AND DCI.FieldName = 'Display Digits'
      AND DCI.DeviceConfigurationId = DC.DeviceConfigurationId;

    WHILE(v_configId IS NOT NULL)
    LOOP
        SELECT NVL(MAX(DeviceConfigurationItemId) + 1, 0) INTO v_itemId FROM DeviceConfigurationItem;

        INSERT INTO DeviceConfigurationItem VALUES (v_itemId, v_configId, 'Display Digits', '5');
        
        SELECT MIN(DC.DeviceConfigurationId) INTO v_configId
        FROM DeviceConfiguration DC
        WHERE DC.Type = 'MCT420'
        MINUS
        SELECT DC.DeviceConfigurationId
        FROM DeviceConfiguration DC, DeviceConfigurationItem DCI
        WHERE DC.Type = 'MCT420' 
          AND DCI.FieldName = 'Display Digits'
          AND DCI.DeviceConfigurationId = DC.DeviceConfigurationId ;

    END LOOP;

    /* Find all of the 420 configurations without an LCD Cycle Time field and add it with the default value of 8. */
    SELECT MIN(DC.DeviceConfigurationId) INTO v_configId
    FROM DeviceConfiguration DC
    WHERE DC.Type = 'MCT420'
    MINUS
    SELECT DC.DeviceConfigurationId
    FROM DeviceConfiguration DC, DeviceConfigurationItem DCI 
    WHERE DC.Type = 'MCT420' 
      AND DCI.FieldName = 'LCD Cycle Time'
      AND DCI.DeviceConfigurationId = DC.DeviceConfigurationId;

    WHILE(v_configId IS NOT NULL)
    LOOP
        SELECT NVL(MAX(DeviceConfigurationItemId) + 1, 0) INTO v_itemId FROM DeviceConfigurationItem;

        INSERT INTO DeviceConfigurationItem VALUES (v_itemId, v_configId, 'LCD Cycle Time', '8');

        SELECT MIN(DC.DeviceConfigurationId) INTO v_configId
        FROM DeviceConfiguration DC
        WHERE DC.Type = 'MCT420'
        MINUS
        SELECT DC.DeviceConfigurationId
        FROM DeviceConfiguration DC, DeviceConfigurationItem DCI 
        WHERE DC.Type = 'MCT420' 
          AND DCI.FieldName = 'LCD Cycle Time'
          AND DCI.DeviceConfigurationId = DC.DeviceConfigurationId;
    END LOOP;

    /* Find all of the 420 configurations without a Disconnect Display Disabled field and add it with the default value of false. */
    SELECT MIN(DC.DeviceConfigurationId) INTO v_configId
        FROM DeviceConfiguration DC
        WHERE DC.Type = 'MCT420'
        MINUS
        SELECT DC.DeviceConfigurationId
        FROM DeviceConfiguration DC, DeviceConfigurationItem DCI 
        WHERE DC.Type = 'MCT420' 
          AND DCI.FieldName = 'Disconnect Display Disabled'
          AND DCI.DeviceConfigurationId = DC.DeviceConfigurationId;

    WHILE(v_configId IS NOT NULL)
    LOOP
        SELECT NVL(MAX(DeviceConfigurationItemId) + 1, 0) INTO v_itemId FROM DeviceConfigurationItem;

        INSERT INTO DeviceConfigurationItem VALUES (v_itemId, v_configId, 'Disconnect Display Disabled', 'false');
        
        SELECT MIN(DC.DeviceConfigurationId) INTO v_configId
        FROM DeviceConfiguration DC
        WHERE DC.Type = 'MCT420'
        MINUS
        SELECT DC.DeviceConfigurationId
        FROM DeviceConfiguration DC, DeviceConfigurationItem DCI 
        WHERE DC.Type = 'MCT420' 
          AND DCI.FieldName = 'Disconnect Display Disabled'
          AND DCI.DeviceConfigurationId = DC.DeviceConfigurationId;
    END LOOP;
END;
/
/* @end-block */
/* End YUK-12116 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('5.6', '03-APR-2013', 'Latest Update', 4, SYSDATE); */