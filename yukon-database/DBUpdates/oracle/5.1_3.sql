/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8403 */
ALTER TABLE ApplianceCategory ADD ConsumerSelectable CHAR(1);
UPDATE ApplianceCategory SET ConsumerSelectable = 'Y';
ALTER TABLE ApplianceCategory MODIFY ConsumerSelectable CHAR(1) NOT NULL;
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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
