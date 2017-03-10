/******************************************/
/**** SQL Server DBupdates             ****/
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

/* Start YUK-16368 */
UPDATE YukonPaobject SET Type = 'RFN-530S4eAX' WHERE Type ='RFN-530S4eAD';
UPDATE YukonPaobject SET Type = 'RFN-530S4eAXR' WHERE Type ='RFN-530S4eAT';
UPDATE YukonPaobject SET Type = 'RFN-530S4eRX' WHERE Type ='RFN-530S4eRD';
UPDATE YukonPaobject SET Type = 'RFN-530S4eRXR' WHERE Type ='RFN-530S4eRT';

UPDATE DeviceTypeCommand  SET DeviceType = 'RFN-530S4eAX' WHERE DeviceType = 'RFN-530S4eAD';
UPDATE DeviceTypeCommand  SET DeviceType = 'RFN-530S4eAXR' WHERE DeviceType = 'RFN-530S4eAT';
UPDATE DeviceTypeCommand  SET DeviceType = 'RFN-530S4eRX' WHERE DeviceType = 'RFN-530S4eRD';
UPDATE DeviceTypeCommand  SET DeviceType = 'RFN-530S4eRXR' WHERE DeviceType = 'RFN-530S4eRT';
/* End YUK-16368 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '15-FEB-2017', 'Latest Update', 4, GETDATE());*/
