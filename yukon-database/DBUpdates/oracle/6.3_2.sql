/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13917 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId IN (-10803, -10804, -10807,-10808);
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId IN (-10803, -10804, -10807,-10808);
/* End YUK-13917 */

/* Start YUK-13905 */
/* @start-block */
DECLARE 
    v_NextDeviceConfigCategoryId NUMBER;
    v_Name VARCHAR2(100);
    v_Description VARCHAR2(1024);
 
BEGIN
    FOR CurrentCategory IN (SELECT DeviceConfigCategoryId
                            FROM DeviceConfigCategory
                            WHERE CategoryType = 'demandProfile'
                            ORDER BY DeviceConfigCategoryId ASC)
    LOOP
       /* Create new row (demand) */
       SELECT
          (SELECT MAX(DeviceConfigCategoryID) + 1 from DeviceConfigCategory) AS NextDeviceConfigCategoryId,
           Name, 
           Description 
       INTO v_NextDeviceConfigCategoryId, v_Name, v_Description 
       FROM DeviceConfigCategory
       WHERE DeviceConfigCategoryId = CurrentCategory.DeviceConfigCategoryId;

       INSERT INTO DeviceConfigCategory VALUES (
           v_NextDeviceConfigCategoryId,
           'demand',
           SUBSTR(v_Name, 1, 84) || ' (Demand Split)',
           SUBSTR(v_Description, 1, 984) || ' (Demand Split)'
       );
 
       UPDATE DeviceConfigCategoryItem 
       SET DeviceConfigCategoryId = v_NextDeviceConfigCategoryId
       WHERE DeviceConfigCategoryId = CurrentCategory.DeviceConfigCategoryId
       AND ItemName = 'demandInterval';
 
       INSERT INTO DeviceConfigCategoryMap
           SELECT DeviceConfigurationId, v_NextDeviceConfigCategoryId
           FROM DeviceConfigCategoryMap
           WHERE DeviceConfigCategoryId = CurrentCategory.DeviceConfigCategoryId;
 
       /* Fix current row (profile) */
       UPDATE DeviceConfigCategory 
       SET CategoryType = 'profile',
           Name = SUBSTR(Name, 1, 84) || ' (Profile Split)',
           Description = SUBSTR(Description, 1, 984) || ' (Profile Split)'
       WHERE DeviceConfigCategoryId = CurrentCategory.DeviceConfigCategoryId;

    END LOOP;
END;
/
/* @end-block */
/* End YUK-13905 */

/* Start YUK-13910 */
ALTER TABLE RawPointHistoryDependentJob
ADD JobGroupId INT;
 
UPDATE RawPointHistoryDependentJob 
SET JobGroupId = 
    (SELECT JobGroupId FROM Job WHERE Job.JobId = RawPointHistoryDependentJob.JobId);

ALTER TABLE RawPointHistoryDependentJob
MODIFY JobGroupId INT NOT NULL;

ALTER TABLE Job
ADD CONSTRAINT AK_Job_JobId_JobGroupId UNIQUE (JobId, JobGroupId);

ALTER TABLE RawPointHistoryDependentJob
    DROP CONSTRAINT FK_RPHDependentJob_Job;

ALTER TABLE RawPointHistoryDependentJob
   ADD CONSTRAINT FK_RPHDependentJob_Job FOREIGN KEY (JobId, JobGroupId)
      REFERENCES Job (JobId, JobGroupId)
      ON DELETE CASCADE;
/* End YUK-13910 */

/* Start YUK-13945 */
UPDATE CCEventLog 
SET Text = REPLACE(Text, 'sent,', 'Sent,');

UPDATE CCEventLog 
SET Text = REPLACE(Text, ', Closed', ', Close');
/* End YUK-13945 */

/* Start YUK-10432 */
UPDATE FDRInterface
SET PossibleDirections = 'Send,Receive for control'
WHERE InterfaceID = 28;
/* End YUK-10432 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.3', '14-DEC-2014', 'Latest Update', 2, SYSDATE);*/