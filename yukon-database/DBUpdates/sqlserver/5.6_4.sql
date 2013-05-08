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
        SELECT @itemId = ISNULL(MAX(DeviceConfigurationItemId) + 1, 0) FROM DeviceConfigurationItem;

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
        SELECT @itemId = ISNULL(MAX(DeviceConfigurationItemId) + 1, 0) FROM DeviceConfigurationItem;

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
        SELECT @itemId = ISNULL(MAX(DeviceConfigurationItemId) + 1, 0) FROM DeviceConfigurationItem;

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

/* Start YUK-11468 */
INSERT INTO Command VALUES (-189, 'getvalue instant line data', 'Read intant line data', 'MCT-440-2131B');
INSERT INTO Command VALUES (-190, 'getvalue outage ?''Outage Log (1 - 10)''', 'Read two outages per read.  Specify 1(1&2), 3(3&4), 5(5&6), 7(7&8), 9(9&10)', 'MCT-440-2131B');
INSERT INTO Command VALUES (-191, 'putstatus reset alarms', 'Reset meter alarms', 'MCT-440-2131B');

INSERT INTO DeviceTypeCommand VALUES (-981, -13, 'MCT-440-2131B', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-982, -13, 'MCT-440-2132B', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-983, -13, 'MCT-440-2133B', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-984, -12, 'MCT-440-2131B', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-985, -12, 'MCT-440-2132B', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-986, -12, 'MCT-440-2133B', 2, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-987, -18, 'MCT-440-2131B', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-988, -18, 'MCT-440-2132B', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-989, -18, 'MCT-440-2133B', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-990, -19, 'MCT-440-2131B', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-991, -19, 'MCT-440-2132B', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-992, -19, 'MCT-440-2133B', 4, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-993, -11, 'MCT-440-2131B', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-994, -11, 'MCT-440-2132B', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-995, -11, 'MCT-440-2133B', 5, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-996, -6, 'MCT-440-2131B', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-997, -6, 'MCT-440-2132B', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-998, -6, 'MCT-440-2133B', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-999, -189, 'MCT-440-2131B', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1000, -189, 'MCT-440-2132B', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1001, -189, 'MCT-440-2133B', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1002, -1, 'MCT-440-2131B', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1003, -1, 'MCT-440-2132B', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1004, -1, 'MCT-440-2133B', 8, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1005, -190, 'MCT-440-2131B', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1006, -190, 'MCT-440-2132B', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1007, -190, 'MCT-440-2133B', 9, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1008, -154, 'MCT-440-2131B', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1009, -154, 'MCT-440-2132B', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1010, -154, 'MCT-440-2133B', 10, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1011, -83, 'MCT-440-2131B', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1012, -83, 'MCT-440-2132B', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1013, -83, 'MCT-440-2133B', 11, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1014, -191, 'MCT-440-2131B', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1015, -191, 'MCT-440-2132B', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1016, -191, 'MCT-440-2133B', 12, 'Y', -1);
/* End YUK-11468 */

/* Start YUK-12096 */
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate)
SELECT DG1.DeviceGroupId, 'Auto', DG2.ParentDeviceGroupId, 'HIDDEN', 'STATIC', GETDATE()
FROM (SELECT MAX(DG.DeviceGroupId)+1 DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.GroupName = 'System'
      AND DG.ParentDeviceGroupId = 0) DG2;

UPDATE DeviceGroup
SET ParentDeviceGroupId = (SELECT DeviceGroupId 
                           FROM   DEVICEGROUP 
                           WHERE  GroupName = 'AUTO' 
                             AND  PERMISSION = 'HIDDEN')
FROM DeviceGroup DG
JOIN JobProperty JP ON JP.Value = DG.GroupName
JOIN Job J ON (J.JobId = JP.JobId
           AND JP.Name = 'uniqueIdentifier'
           AND J.BeanName = 'scheduledArchivedDataFileExportJobDefinition'
           AND DG.ParentDeviceGroupId = 0);
/* End YUK-12096 */

/* Start YUK-12150 */
UPDATE Point
SET PointOffset = 214
WHERE PointId IN
   (SELECT P.PointId
    FROM Point P, YukonPAObject YPAO 
    WHERE P.PAObjectID = YPAO.PAObjectID
      AND P.PointOffset = 70
      AND P.PointName = 'Voltage'
      AND UPPER(YPAO.Type) IN ('RFN-420CL', 'RFN-420CD'));
/* End YUK-12150 */

/* Start YUK-11959 */
UPDATE ROLE
SET ROLE.Value = ' '
FROM YukonGroupRole ROLE
JOIN YukonRoleProperty PROP ON (ROLE.RoleId = PROP.RoleId
                            AND ROLE.RolePropertyId = PROP.RolePropertyId)
WHERE (PROP.KeyName = 'msg_priority'
    OR PROP.KeyName = 'Maximum Daily Scans'
    OR PROP.KeyName = 'Minimum Scan Frequency')
   AND ROLE.Value NOT LIKE ' '
   AND ISNUMERIC(ROLE.Value) = 0;
 
UPDATE ROLE
SET ROLE.Value = ' '
FROM YukonGroupRole ROLE
JOIN YukonRoleProperty PROP ON (ROLE.RoleId = PROP.RoleId
                            AND ROLE.RolePropertyId = PROP.RolePropertyId)
WHERE (PROP.KeyName = 'allow_member_programs'
    OR PROP.KeyName = 'dbeditor_lm' 
    OR PROP.KeyName = 'dbeditor_system' 
    OR PROP.KeyName = 'dbeditor_trans_exclusion'
    OR PROP.KeyName = 'permit_login_edit'
    OR PROP.KeyName = 'graph_edit_graphdefinition' 
    OR PROP.KeyName = 'Scan Now Enabled'
    OR PROP.KeyName = 'Auto Process Batch Configs') 
   AND ROLE.Value NOT IN ('true', 'false', ' ');
/* End YUK-11959 */

/* Start YUK-12153 */
DELETE FROM Job
WHERE JobId NOT IN (SELECT JobId 
                    FROM JobProperty 
                    WHERE Name = 'defaultYukonExternalUrl')
  AND BeanName IN 
    ('scheduledBillingFileExportJobDefinition', 
     'scheduledArchivedDataFileExportJobDefinition',
     'scheduledWaterLeakFileExportJobDefinition',
     'scheduledMeterEventsFileExportJobDefinition');
/* End YUK-12153 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('5.6', '10-MAY-2013', 'Latest Update', 4, GETDATE());