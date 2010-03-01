/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8403 */
ALTER TABLE ApplianceCategory ADD ConsumerSelectable CHAR(1);
GO
UPDATE ApplianceCategory SET ConsumerSelectable = 'Y';
GO
ALTER TABLE ApplianceCategory ALTER COLUMN ConsumerSelectable CHAR(1) NOT NULL;
GO
/* End YUK-8403 */

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
/* @error ignore-begin */
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
/* @error ignore-end */
/* End YUK-8416 */ 

/* Start YUK-8433 */
IF 1 > (SELECT Count(*)
        FROM DeviceGroup DG
        WHERE DG.GroupName = 'Substations'
        AND DG.ParentDeviceGroupId = (SELECT DG1.DeviceGroupId
                                       FROM DeviceGroup DG1
                                       WHERE DG1.GroupName = 'System'
                                       AND DG1.ParentDeviceGroupId = 0)
        AND DG.Permission = 'NOEDIT_NOMOD'
        AND DG.Type = 'SUBSTATION_TO_ROUTE')
    
    INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type)
    SELECT DG1.DeviceGroupId, 'Substations', DG2.ParentDeviceGroupId, 'NOEDIT_NOMOD', 'SUBSTATION_TO_ROUTE'
    FROM (SELECT MAX(DG.DeviceGroupID)+1 DeviceGroupId
           FROM DeviceGroup DG
           WHERE DG.DeviceGroupId < 100) DG1,
          (SELECT MAX(DG.DeviceGroupId) ParentDeviceGroupId
           FROM DeviceGroup DG
           WHERE DG.GroupName = 'System'
           AND DG.ParentDeviceGroupId = 0) DG2;

GO 
/* End YUK-8433 */ 

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
