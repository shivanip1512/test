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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
