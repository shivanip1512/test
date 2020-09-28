/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-21642 */
/* errors are ignored for an edge case where the tables had been already modified */
/* @error ignore-begin */
DROP INDEX INDX_DynRfnDevData_GatewayId;

ALTER TABLE DynamicRfnDeviceData
MODIFY GatewayId NUMBER NOT NULL;

CREATE INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData (
GatewayId ASC
);

ALTER TABLE DynamicRfnDeviceData
ADD DescendantCount NUMBER NULL;

UPDATE DynamicRfnDeviceData
SET DescendantCount = -1;

ALTER TABLE DynamicRfnDeviceData
MODIFY DescendantCount NUMBER NOT NULL;

ALTER TABLE DynamicRfnDeviceData
ADD LastTransferTimeNew DATE NULL;

UPDATE DynamicRfnDeviceData
SET LastTransferTimeNew = LastTransferTime;

ALTER TABLE DynamicRfnDeviceData
MODIFY LastTransferTimeNew DATE NOT NULL;

ALTER TABLE DynamicRfnDeviceData
DROP COLUMN LastTransferTime;

ALTER TABLE DynamicRfnDeviceData
RENAME COLUMN LastTransferTimeNew
TO LastTransferTime;

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.4.1', SYSDATE);
/* @error ignore-end */
/* @end YUK-21642 */

/* @start YUK-21621 */
INSERT INTO StateGroup VALUES(-29, 'Meter Programming', 'Status');
INSERT INTO State VALUES(-29, 0, 'Success', 0, 6, 0);
INSERT INTO State VALUES(-29, 1, 'Failure', 1, 6, 0);

INSERT INTO DBUpdates VALUES ('YUK-21621', '7.4.1', SYSDATE);
/* @end YUK-21621 */

/* @start YUK-21777 */
DELETE FROM PointStatusControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointStatus WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM GraphDataSeries WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM CalcComponent WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Display2WayData WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointUnit WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Point WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

INSERT INTO DBUpdates VALUES ('YUK-21777', '7.4.1', SYSDATE);
/* @end YUK-21777 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.4', '01-MAY-2020', 'Latest Update', 1, SYSDATE);
