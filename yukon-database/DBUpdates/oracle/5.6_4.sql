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

/* Start YUK-12906 */
INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type,CreatedDate)
SELECT DG1.DeviceGroupId, 'Auto', DG2.ParentDeviceGroupId, 'HIDDEN', 'STATIC', SYSDATE
FROM (SELECT MAX(DG.DeviceGroupId)+1 DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.GroupName = 'System'
        AND DG.ParentDeviceGroupId = 0) DG2;
 
UPDATE DeviceGroup 
SET ParentDeviceGroupId = 
   (SELECT DG.DeviceGroupId
    FROM DeviceGroup DG
    WHERE DG.GroupName = 'Auto' and DG.Permission ='HIDDEN')
WHERE EXISTS (SELECT GroupName
              FROM JobProperty JP, Job J
              WHERE JP.Value = GroupName 
                AND J.JobId = JP.JobId
                AND JP.name = 'uniqueIdentifier'
                AND J.BeanName = 'scheduledArchivedDataFileExportJobDefinition'
                AND ParentDeviceGroupId = 0);
/* End YUK-12906 */

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
UPDATE YukonGroupRole ROLE
SET ROLE.Value = ' '
WHERE EXISTS (SELECT PROP.KeyName
              FROM YukonRoleProperty PROP
              WHERE ROLE.RolePropertyId = PROP.RolePropertyId
                AND ROLE.RoleId = PROP.RoleId
                AND (LOWER(PROP.KeyName)= 'msg_priority'
                 OR  LOWER(PROP.KeyName) = 'maximum daily scans'
                 OR  LOWER(PROP.KeyName) = 'minimum scan frequency'))
  AND ROLE.Value NOT LIKE ' '
  AND LENGTH(TRIM(TRANSLATE(ROLE.Value, '0123456789', ' '))) > 0;
 
UPDATE YukonGroupRole ROLE
SET ROLE.Value = ' '
WHERE EXISTS (SELECT PROP.KeyName
              FROM YukonRoleProperty PROP
              WHERE ROLE.RolePropertyId = PROP.RolePropertyId
                AND ROLE.RoleId = PROP.RoleId
                AND (LOWER(PROP.KeyName) = 'allow_member_programs'
                 OR  LOWER(PROP.KeyName) = 'dbeditor_lm' 
                 OR  LOWER(PROP.KeyName) = 'dbeditor_system' 
                 OR  LOWER(PROP.KeyName) = 'dbeditor_trans_exclusion'
                 OR  LOWER(PROP.KeyName) = 'permit_login_edit'
                 OR  LOWER(PROP.KeyName) = 'graph_edit_graphdefinition' 
                 OR  LOWER(PROP.KeyName) = 'scan now enabled'
                 OR  LOWER(PROP.KeyName) = 'auto process batch configs'))
  AND LOWER(ROLE.Value) NOT IN ('true', 'false', ' ');
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
/* INSERT INTO CTIDatabase VALUES ('5.6', '03-APR-2013', 'Latest Update', 4, SYSDATE); */