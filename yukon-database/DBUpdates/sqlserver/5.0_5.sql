/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8250 */
ALTER TABLE DynamicCCOriginalParent
    DROP CONSTRAINT FK_DynCCOrigParent_YukonPAO;
ALTER TABLE DynamicCCOriginalParent
    ADD CONSTRAINT FK_DynCCOrigParent_YukonPAO FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
/* End YUK-8250 */

/* Start YUK-8414 */
UPDATE CapBank 
SET ControlPointId = (SELECT P.PointId 
                      FROM POINT P 
                      WHERE CapBank.ControlDeviceId = P.PAObjectId 
                      AND P.PointOffset = 1 
                      AND P.PointType = 'Status' 
                      AND CapBank.ControlDeviceId > 0)
WHERE ControlDeviceId > 0;
/* End YUK-8414 */

/* Start YUK-8416 */
ALTER TABLE CommandRequestExec
ADD ExecutionStatus VARCHAR(100);
GO
UPDATE CommandRequestExec
SET ExecutionStatus = 'FAILED'
WHERE StopTime IS NULL;
UPDATE CommandRequestExec
SET ExecutionStatus = 'COMPLETE'
WHERE StopTime IS NOT NULL;
GO
ALTER TABLE CommandRequestExec
ALTER COLUMN ExecutionStatus VARCHAR(100) NOT NULL;
GO
/* End YUK-8416 */ 

/* Start YUK-8433 */
INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type)
SELECT DG1.DeviceGroupId, 'Substations', DG2.ParentDeviceGroupId, 'NOEDIT_NOMOD', 'SUBSTATION_TO_ROUTE'
FROM (SELECT MAX(DG.DeviceGroupID)+1 DeviceGroupId
       FROM DeviceGroup DG
       WHERE DG.DeviceGroupId < 100) DG1,
      (SELECT MAX(DG.DeviceGroupId) ParentDeviceGroupId
       FROM DeviceGroup DG
       WHERE DG.GroupName = 'System'
       AND DG.ParentDeviceGroupId = 0) DG2;
/* End YUK-8433 */

/* Start YUK-8431 */
UPDATE YukonRoleProperty
SET DefaultValue = 'AUTO_METER_NUMBER_FIRST',
    Description = 'Defines the field used to lookup a meter by in Yukon. Valid values: AUTO_METER_NUMBER_FIRST, AUTO_DEVICE_NAME_FIRST, METER_NUMBER, DEVICE_NAME, or ADDRESS.'
WHERE RolePropertyId = -1604;

INSERT INTO YukonGroupRole values(-274,-1,-7,-1604,'(none)'); 
/* End YUK-8431 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.0', 'Matt K', '25-FEB-2010', 'Latest Update', 5);
