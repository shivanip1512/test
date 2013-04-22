/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-11880 */
ALTER TABLE ECToAcctThermostatSchedule
    DROP CONSTRAINT FK_ECToAccThermSch_AccThermSch;
GO

ALTER TABLE ECToAcctThermostatSchedule
    ADD CONSTRAINT FK_ECToAccThermSch_AccThermSch FOREIGN KEY (AcctThermostatScheduleId)
        REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
            ON DELETE CASCADE;
/* End YUK-11880 */

/* Start YUK-11880 */
ALTER TABLE AcctThermostatSchedule
ADD Archived CHAR(1);
GO
UPDATE AcctThermostatSchedule
SET Archived = 'N';

ALTER TABLE AcctThermostatSchedule 
ALTER COLUMN Archived CHAR(1) NOT NULL;
/* End YUK-11880 */

/* Start YUK-12116 */
/* @start-block */
BEGIN 
    DECLARE @configId INT;
    DECLARE @itemId INT;

    /* Find all of the 420 configurations without a Display Digits field and add it with the default value of 5. */
    SET @configId = 
       (SELECT MIN(DC.DeviceConfigurationId)
        FROM DeviceConfiguration DC
        WHERE DC.Type = 'MCT420'
        EXCEPT
        SELECT DC.DeviceConfigurationId
        FROM DeviceConfiguration DC
            JOIN DeviceConfigurationItem DCI ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId 
        WHERE DC.Type = 'MCT420' AND DCI.FieldName = 'Display Digits');

    WHILE(@configId IS NOT NULL)
    BEGIN
        SELECT @itemId = MAX(DeviceConfigurationItemId) + 1 FROM DeviceConfigurationItem;

        INSERT INTO DeviceConfigurationItem VALUES (@itemId, @configId, 'Display Digits', '5');
        
        SET @configId = 
           (SELECT MIN(DC.DeviceConfigurationId)
            FROM DeviceConfiguration DC
            WHERE DC.Type = 'MCT420'
            EXCEPT
            SELECT DC.DeviceConfigurationId
            FROM DeviceConfiguration DC
                JOIN DeviceConfigurationItem DCI ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId 
            WHERE DC.Type = 'MCT420' AND DCI.FieldName = 'Display Digits');
    END;

    /* Find all of the 420 configurations without an LCD Cycle Time field and add it with the default value of 8. */
    SET @configId = 
       (SELECT MIN(DC.DeviceConfigurationId)
        FROM DeviceConfiguration DC
        WHERE DC.Type = 'MCT420'
        EXCEPT
        SELECT DC.DeviceConfigurationId
        FROM DeviceConfiguration DC
            JOIN DeviceConfigurationItem DCI ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId 
        WHERE DC.Type = 'MCT420' AND DCI.FieldName = 'LCD Cycle Time');

    WHILE(@configId IS NOT NULL)
    BEGIN
        SELECT @itemId = MAX(DeviceConfigurationItemId) + 1 FROM DeviceConfigurationItem;

        INSERT INTO DeviceConfigurationItem VALUES (@itemId, @configId, 'LCD Cycle Time', '8');

        SET @configId = 
           (SELECT MIN(DC.DeviceConfigurationId)
            FROM DeviceConfiguration DC
            WHERE DC.Type = 'MCT420'
            EXCEPT
            SELECT DC.DeviceConfigurationId
            FROM DeviceConfiguration DC
                JOIN DeviceConfigurationItem DCI ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId 
            WHERE DC.Type = 'MCT420' AND DCI.FieldName = 'LCD Cycle Time');
    END;

    /* Find all of the 420 configurations without a Disconnect Display Disabled field and add it with the default value of false. */
    SET @configId = 
       (SELECT MIN(DC.DeviceConfigurationId)
        FROM DeviceConfiguration DC
        WHERE DC.Type = 'MCT420'
        EXCEPT
        SELECT DC.DeviceConfigurationId
        FROM DeviceConfiguration DC
            JOIN DeviceConfigurationItem DCI ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId 
        WHERE DC.Type = 'MCT420' AND DCI.FieldName = 'Disconnect Display Disabled');

    WHILE(@configId IS NOT NULL)
    BEGIN
        SELECT @itemId = MAX(DeviceConfigurationItemId) + 1 FROM DeviceConfigurationItem;

        INSERT INTO DeviceConfigurationItem VALUES (@itemId, @configId, 'Disconnect Display Disabled', 'false');
        
        SET @configId = 
           (SELECT MIN(DC.DeviceConfigurationId)
            FROM DeviceConfiguration DC
            WHERE DC.Type = 'MCT420'
            EXCEPT
            SELECT DC.DeviceConfigurationId
            FROM DeviceConfiguration DC
                JOIN DeviceConfigurationItem DCI ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId 
            WHERE DC.Type = 'MCT420' AND DCI.FieldName = 'Disconnect Display Disabled');
    END;
END;
/* @end-block */
/* End YUK-12116 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('5.6', '03-APR-2013', 'Latest Update', 4, GETDATE()); */