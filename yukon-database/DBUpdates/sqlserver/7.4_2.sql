/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-22094 */
DELETE FROM PointStatusControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointStatus WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM GraphDataSeries WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM CalcComponent WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Display2WayData WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointUnit WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Point WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

INSERT INTO DBUpdates VALUES ('YUK-22094', '7.4.2', GETDATE());
/* @end YUK-22094 */

/* @start YUK-22110 */
DELETE FROM PointStatusControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointStatus WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM GraphDataSeries WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM CalcComponent WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Display2WayData WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointUnit WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Point WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

INSERT INTO DBUpdates VALUES ('YUK-22110', '7.4.2', GETDATE());
/* @end YUK-22110 */

/* @start YUK-22412 */
UPDATE Point 
SET PointName = 'Relay 1 Load State'
WHERE PointType = 'Status' AND PointOffset = 3
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId FROM YukonPaobject 
    WHERE Type IN ('LCR-6600S', 'LCR-6601S')
);

UPDATE Point 
SET PointName = 'Relay 2 Load State'
WHERE PointType = 'Status' AND PointOffset = 5
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId FROM YukonPaobject 
    WHERE Type IN ('LCR-6600S', 'LCR-6601S')
);

UPDATE Point 
SET PointName = 'Relay 3 Load State'
WHERE PointType = 'Status' AND PointOffset = 7
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId FROM YukonPaobject 
    WHERE Type IN ('LCR-6600S')
);

UPDATE Point 
SET PointName = 'Relay 4 Load State'
WHERE PointType = 'Status' AND PointOffset = 9
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId FROM YukonPaobject 
    WHERE Type IN ('LCR-6600S')
);

INSERT INTO DBUpdates VALUES ('YUK-22412', '7.4.2', GETDATE());
/* @end YUK-22412 */

/* @start YUK-22518 */
UPDATE GlobalSetting SET Name = 'ITRON_HCM_DATA_COLLECTION_MINUTES', Value = '15'
    WHERE Name = 'ITRON_HCM_DATA_COLLECTION_HOURS';

INSERT INTO DBUpdates VALUES ('YUK-22518', '7.4.2', GETDATE());
/* @end YUK-22518 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.4', '03-AUG-2020', 'Latest Update', 2, GETDATE());
