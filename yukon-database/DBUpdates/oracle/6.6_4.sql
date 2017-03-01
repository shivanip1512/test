/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-16233 */
UPDATE LMProgramDirectGear 
SET MethodOptionType = 'Optional' 
WHERE ControlMethod = 'EcobeeCycle'
AND MethodOptionType != 'Mandatory';
/* End YUK-16233 */

/* Start YUK-16321 */
DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Status' AND PointOffset = 81 AND YP.Type IN ('RFW-201', 'RFW-205'));
 
DELETE FROM POINTSTATUS WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Status' AND PointOffset = 81 AND YP.Type IN ('RFW-201', 'RFW-205'));
 
DELETE FROM PointAlarming WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Status' AND PointOffset = 81 AND YP.Type IN ('RFW-201', 'RFW-205'));
 
DELETE FROM Point WHERE PointId IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Status' AND PointOffset = 81 AND YP.Type IN ('RFW-201', 'RFW-205'));
/* End YUK-16321 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '15-FEB-2017', 'Latest Update', 4, SYSDATE);*/
