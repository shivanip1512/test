/******************************************/ 
/**** Oracle DBupdates                 ****/ 
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
ADD ExecutionStatus VARCHAR2(100);
UPDATE CommandRequestExec
SET ExecutionStatus = 'FAILED'
WHERE StopTime IS NULL;
UPDATE CommandRequestExec
SET ExecutionStatus = 'COMPLETE'
WHERE StopTime IS NOT NULL;
ALTER TABLE CommandRequestExec
MODIFY ExecutionStatus VARCHAR2(100) NOT NULL;
/* End YUK-8416 */ 

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.0', 'Matt K', '25-FEB-2010', 'Latest Update', 5);
