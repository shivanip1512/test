/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-16106 */
UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
UPDATE state SET foregroundcolor = 9 WHERE stategroupid = -28 AND rawstate = 2;
/* End YUK-16106 */

/* Start YUK-16173 */
ALTER TABLE MSPInterface
DROP CONSTRAINT PK_MSPINTERFACE;

/* @start-block */
DECLARE
    v_indexCount NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_indexCount
    FROM USER_INDEXES
    WHERE INDEX_NAME = 'PK_MSPINTERFACE';

    IF v_indexCount > 0 THEN
        EXECUTE IMMEDIATE 'DROP INDEX PK_MSPINTERFACE';
    END IF;
END;
/
/* @end-block */

ALTER TABLE MSPInterface
ADD Version VARCHAR2(12);

UPDATE MSPInterface 
SET Version = '3.0';

ALTER TABLE MSPInterface
MODIFY (Version VARCHAR2(12) NOT NULL);

ALTER TABLE MSPInterface
ADD CONSTRAINT PK_MSPINTERFACE PRIMARY KEY (VendorID,Interface,Version);

ALTER TABLE MSPInterface
MODIFY (Endpoint VARCHAR2(255));

UPDATE MSPInterface MI
SET endpoint = (
    SELECT MV.URL || MI.Endpoint
    FROM MSPVendor MV
    WHERE MI.VendorID = MV.VendorID);

ALTER TABLE MSPVendor 
DROP COLUMN URL;

INSERT INTO MSPInterface VALUES (1, 'MR_Server', 'http://127.0.0.1:8080/multispeak/v5/MR_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'OD_Server', 'http://127.0.0.1:8080/multispeak/v5/OD_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'CD_Server', 'http://127.0.0.1:8080/multispeak/v5/CD_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'NOT_Server', 'http://127.0.0.1:8080/multispeak/v5/NOT_Server', '5.0');
/* End YUK-16173 */

/* Start YUK-16225 */
/* @error ignore-begin */
ALTER TABLE HoneywellWifiThermostat
   ADD CONSTRAINT AK_HONEYWELLWIFITHERMOSTAT_MAC UNIQUE (MacAddress);
/* @error ignore-end */
/* End YUK-16225 */

/* Start YUK-16164 */
ALTER TABLE DEVICEMETERGROUP
    DROP CONSTRAINT FK_DeviceMeterGroup_Device;

ALTER TABLE DEVICEMETERGROUP
   ADD CONSTRAINT FK_DeviceMeterGroup_Device FOREIGN KEY (DEVICEID)
      REFERENCES DEVICE (DEVICEID)
      ON DELETE CASCADE;
/* End YUK-16164 */

/* Start YUK-16205 */
UPDATE Point 
SET PointName = 'Reverse Power Alarm'
WHERE PointType = 'Status' 
AND PointOffset = 47
AND PointName = 'Reverse Power'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('MCT-410cL', 'MCT-410gL', 'MCT-410iL')
);

UPDATE Point 
SET 
    PointName = 'Outages', 
    PointOffset = 100
WHERE PointType = 'Analog' 
AND PointOffset = 24
AND PointName = 'Outage Log'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-440-2131TD', 'RFN-440-2132TD', 'RFN-440-2132TD')
);

UPDATE Point 
SET PointName = 'Delivered kVAh'
WHERE PointType = 'Analog' 
AND PointOffset = 150
AND PointName = 'kVAh Delivered'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-420cL','RFN-430A3K','RFN-420cD','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Received kVAh'
WHERE PointType = 'Analog' 
AND PointOffset = 151
AND PointName = 'kVAh Received'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-420cL','RFN-430A3K','RFN-420cD','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Delivered kVArh (Rate A kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 161
AND PointName = 'Delivered Rate A kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-430SL4','RFN-430SL3','RFN-430SL2','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Delivered kVArh (Rate B kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 162
AND PointName = 'Delivered Rate B kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-430SL4','RFN-430SL3','RFN-430SL2','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Delivered kVArh (Rate C kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 163
AND PointName = 'Delivered Rate C kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-430SL4','RFN-430SL3','RFN-430SL2','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Delivered kVArh (Rate D kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 164
AND PointName = 'Delivered Rate D kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-430SL4','RFN-430SL3','RFN-430SL2','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Received kVArh (Rate A kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 202
AND PointName = 'Received Rate A kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-430SL4','RFN-430SL3','RFN-430SL2','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Received kVArh (Rate B kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 203
AND PointName = 'Received Rate B kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-430SL4','RFN-430SL3','RFN-430SL2','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Received kVArh (Rate C kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 204
AND PointName = 'Received Rate C kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-430SL4','RFN-430SL3','RFN-430SL2','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Received kVArh (Rate D kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 205
AND PointName = 'Received Rate D kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-430SL4','RFN-430SL3','RFN-430SL2','RFN-430A3R')
);

UPDATE Point 
SET PointName = 'Sum kVArh'
WHERE PointType = 'Analog' 
AND PointOffset = 215
AND PointName = 'Total kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-420fRX','RFN-420fRD')
);

UPDATE Point 
SET PointOffset = 349
WHERE PointType = 'Analog' 
AND PointOffset = 226
AND PointName = 'Current Angle'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-520fAX','RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD')
);

UPDATE Point 
SET 
    PointOffset = 350, 
    PointName = 'Net Delivered kVArh (Rate A kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 230
AND PointName = 'Rate A Net Delivered kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4')
);

UPDATE Point 
SET 
    PointOffset = 351, 
    PointName = 'Net Delivered kVArh (Rate B kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 231
AND PointName = 'Rate B Net Delivered kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4')
);

UPDATE Point 
SET 
    PointOffset = 352, 
    PointName = 'Net Delivered kVArh (Rate C kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 232
AND PointName = 'Rate C Net Delivered kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4')
);

UPDATE Point 
SET 
    PointOffset = 353, 
    PointName = 'Net Delivered kVArh (Rate D kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 233
AND PointName = 'Rate D Net Delivered kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4')
);

UPDATE Point 
SET PointName = 'Delivered kVAh (Rate A kVAh)'
WHERE PointType = 'Analog' 
AND PointOffset = 241
AND PointName = 'kVAh Rate A'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4', 'RFN-430SL3', 'RFN-430SL2', 'RFN-430SL1')
);

UPDATE Point 
SET PointName = 'Delivered kVAh (Rate B kVAh)'
WHERE PointType = 'Analog' 
AND PointOffset = 242
AND PointName = 'kVAh Rate B'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4', 'RFN-430SL3', 'RFN-430SL2', 'RFN-430SL1')
);

UPDATE Point 
SET PointName = 'Delivered kVAh (Rate C kVAh)'
WHERE PointType = 'Analog' 
AND PointOffset = 243
AND PointName = 'kVAh Rate C'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4', 'RFN-430SL3', 'RFN-430SL2', 'RFN-430SL1')
);

UPDATE Point 
SET PointName = 'Delivered kVAh (Rate D kVAh)'
WHERE PointType = 'Analog' 
AND PointOffset = 244
AND PointName = 'kVAh Rate D'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4', 'RFN-430SL3', 'RFN-430SL2', 'RFN-430SL1')
);

UPDATE Point 
SET PointName = 'Delivered Peak kVA (Rate A kVA)'
WHERE PointType = 'Analog' 
AND PointOffset = 251
AND PointName = 'Peak kVA Rate A'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4', 'RFN-430SL3', 'RFN-430SL2', 'RFN-430SL1')
);

UPDATE Point 
SET PointName = 'Delivered Peak kVA (Rate B kVA)'
WHERE PointType = 'Analog' 
AND PointOffset = 252
AND PointName = 'Peak kVA Rate B'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4', 'RFN-430SL3', 'RFN-430SL2', 'RFN-430SL1')
);

UPDATE Point 
SET PointName = 'Delivered Peak kVA (Rate C kVA)'
WHERE PointType = 'Analog' 
AND PointOffset = 253
AND PointName = 'Peak kVA Rate C'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4', 'RFN-430SL3', 'RFN-430SL2', 'RFN-430SL1')
);

UPDATE Point 
SET PointName = 'Delivered Peak kVA (Rate D kVA)'
WHERE PointType = 'Analog' 
AND PointOffset = 254
AND PointName = 'Peak kVA Rate D'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4', 'RFN-430SL3', 'RFN-430SL2', 'RFN-430SL1')
);

UPDATE Point 
SET PointOffset = 250
WHERE PointType = 'Analog' 
AND PointOffset = 149
AND PointName = 'Peak kVA'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV', 'RFN-430A3K', 'RFN-430A3R')
);

UPDATE Point 
SET PointOffset = 215
WHERE PointType = 'Analog' 
AND PointOffset = 165
AND PointName = 'Sum kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV', 'RFN-430A3R')
);

UPDATE Point 
SET PointOffset = 354
WHERE PointType = 'Analog' 
AND PointOffset = 327
AND PointName IN ('Voltage (Single Phase)', 'Voltage', 'Voltage Phase A')
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX', 'RFN-520fRXD')
);

UPDATE Point 
SET PointOffset = 355
WHERE PointType = 'Analog' 
AND PointOffset = 330
AND PointName = 'Min Voltage'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX', 'RFN-520fRXD')
);

UPDATE Point 
SET PointOffset = 356
WHERE PointType = 'Analog' 
AND PointOffset = 333
AND PointName = 'Max Voltage'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX', 'RFN-520fRXD')
);

UPDATE Point 
SET PointOffset = 357
WHERE PointType = 'Analog' 
AND PointOffset = 336
AND PointName = 'Current'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX', 'RFN-520fRXD')
);

UPDATE Point 
SET PointOffset = 244
WHERE PointType = 'Analog' 
AND PointOffset = 308
AND PointName = ' Delivered kVAh (Rate D kVAh)'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430S4x')
);
/* End YUK-16205 */

/* Start YUK-16336 */
ALTER TABLE UserPreference MODIFY Value VARCHAR2(1275);
/* End YUK-16336 */

/* Start YUK-16470 */
/* Delivered kVA (Offset 324) */
/* Received kVA (Offset 325) */
DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (324, 325) AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3R'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (324, 325) AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3R'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (324, 325) AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3R'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (324, 325) AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3R'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (324, 325) AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3R'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (324, 325) AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3R'));


/* Received kVAr (Offset 322) */
DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 322 AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3K'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 322 AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3K'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 322 AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3K'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 322 AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3K'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 322 AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3K'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 322 AND YP.Type IN ('RFN-430A3D', 'RFN-430A3T', 'RFN-430A3K'));


/* Power Factor (Offset 133) */
DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 133 AND YP.Type IN ('RFN-430A3K', 'RFN-430A3R'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 133 AND YP.Type IN ('RFN-430A3K', 'RFN-430A3R'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 133 AND YP.Type IN ('RFN-430A3K', 'RFN-430A3R'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 133 AND YP.Type IN ('RFN-430A3K', 'RFN-430A3R'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 133 AND YP.Type IN ('RFN-430A3K', 'RFN-430A3R'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 133 AND YP.Type IN ('RFN-430A3K', 'RFN-430A3R'));


/* Sum kVAh (Offset 263) */
/* Sum kVA Load Profile (Offset 191) */
/* Sum kVAh per Interval (Offset 184) */
DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (263, 191, 184) AND YP.Type IN ('RFN-430A3R'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (263, 191, 184) AND YP.Type IN ('RFN-430A3R'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (263, 191, 184) AND YP.Type IN ('RFN-430A3R'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (263, 191, 184) AND YP.Type IN ('RFN-430A3R'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (263, 191, 184) AND YP.Type IN ('RFN-430A3R'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (263, 191, 184) AND YP.Type IN ('RFN-430A3R'));


/* Delivered kWh (Offset 1) */
DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 1 AND YP.Type IN ('RFN-530S4eAX', 'RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 1 AND YP.Type IN ('RFN-530S4eAX', 'RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 1 AND YP.Type IN ('RFN-530S4eAX', 'RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 1 AND YP.Type IN ('RFN-530S4eAX', 'RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 1 AND YP.Type IN ('RFN-530S4eAX', 'RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset = 1 AND YP.Type IN ('RFN-530S4eAX', 'RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR'));
/* End YUK-16470 */

/* Start YUK-16502 */
/* @start-block */
DECLARE
    v_exists NUMBER;
BEGIN
    SELECT CASE WHEN (SELECT DISTINCT 1 FROM CTIDatabase WHERE 6.7 <= (SELECT MAX(version) FROM CTIDatabase)) IS NULL 
        THEN 0 
        ELSE 1 
    END AS temp INTO v_exists FROM dual;

    IF v_exists != 1 THEN
        DELETE FROM RAWPOINTHISTORY 
        WHERE pointid IN (
            SELECT p.pointid 
            FROM point p 
            JOIN YukonPAObject ypo 
                ON p.PAObjectID = ypo.PAObjectID 
            WHERE p.PointType = 'Analog' 
            AND p.POINTOFFSET = 100
            AND ypo.Type LIKE 'RFN%')
        AND (
            VALUE > 2147483647 /* This is the max value of a signed int */
            OR VALUE < 0
            OR TIMESTAMP < '01-JAN-1970' );

        UPDATE RAWPOINTHISTORY 
        SET TIMESTAMP = TIMESTAMP - numtodsinterval(VALUE, 'SECOND') 
        WHERE pointid IN (
            SELECT pointid 
            FROM point p 
            JOIN YukonPAObject ypo 
                ON p.PAObjectID = ypo.PAObjectID 
            WHERE PointType = 'Analog' 
            AND POINTOFFSET = 100
            AND Type LIKE 'RFN%');
    END IF;
END;
/
/* @end-block */
/* End YUK-16502 */

/* Start YUK-16618 */
UPDATE FDRInterface
SET PossibleDirections='Send,Receive,Receive for control'
WHERE InterfaceId = 28;
/* End YUK-16618 */

/* Start YUK-16593 */
UPDATE  DeviceConfigCategory
SET     CategoryType = 'regulatorHeartbeat'
WHERE   CategoryType = 'heartbeat';

UPDATE  DeviceConfigCategoryItem
SET     ItemName = 'regulatorHeartbeatPeriod'
WHERE   ItemName = 'heartbeatPeriod';

UPDATE  DeviceConfigCategoryItem
SET     ItemName = 'regulatorHeartbeatValue'
WHERE   ItemName = 'heartbeatValue';

UPDATE  DeviceConfigCategoryItem
SET     ItemName = 'regulatorHeartbeatMode'
WHERE   ItemName = 'heartbeatMode';

/* @start-block */
DECLARE
    v_newConfigCategoryId       NUMBER;
    v_newConfigCategoryItemId   NUMBER;
    v_existingConfigurationId   NUMBER;

    /* Find non-default config categories used by existing CBC 8000 and CBC DNP Devices and stick them into a cursor */
    CURSOR config_cursor IS (
        SELECT DISTINCT C.DeviceConfigurationID
        FROM YukonPAObject Y, DeviceConfigurationDeviceMap C, DeviceConfiguration DC
        WHERE Y.Type IN ('CBC 8020', 'CBC 8024', 'CBC DNP')
        AND Y.PAObjectID = C.DeviceID
        AND C.DeviceConfigurationId = DC.DeviceConfigurationID
        AND DC.DeviceConfigurationID > -1
    );

BEGIN
    SELECT MAX(DeviceConfigCategoryId) + 1      INTO v_newConfigCategoryId      FROM DeviceConfigCategory;
    SELECT MAX(DeviceConfigCategoryItemId) + 1  INTO v_newConfigCategoryItemId  FROM DeviceConfigCategoryItem;

    INSERT INTO DeviceConfigCategory        VALUES (v_newConfigCategoryId, 'cbcHeartbeat', 'Default CBC Heartbeat Category', NULL);

    INSERT INTO DeviceConfigCategoryItem    VALUES (v_newConfigCategoryItemId,      v_newConfigCategoryId, 'cbcHeartbeatPeriod', 0);
    INSERT INTO DeviceConfigCategoryItem    VALUES (v_newConfigCategoryItemId + 1,  v_newConfigCategoryId, 'cbcHeartbeatValue',  0);
    INSERT INTO DeviceConfigCategoryItem    VALUES (v_newConfigCategoryItemId + 2,  v_newConfigCategoryId, 'cbcHeartbeatMode',   'DISABLED');

    INSERT INTO DeviceConfigCategoryMap     VALUES (-1, v_newConfigCategoryId); /* -1 is the ID for the default DNP Configuration */

    /* Add @newConfigCategoryId to DeviceConfigCategoryMap for each of these found ID's (if any) */
    OPEN config_cursor;
    LOOP
        FETCH config_cursor INTO v_existingConfigurationId;
        EXIT WHEN config_cursor%NOTFOUND;

        INSERT INTO DeviceConfigCategoryMap VALUES (v_existingConfigurationId, v_newConfigCategoryId);

    END LOOP;
    CLOSE config_cursor;
END;
/
/* @end-block */
/* End YUK-16593 */

/* Start YUK-16614 */
INSERT INTO YukonRoleProperty VALUES(-20021,-200,'Manage Dashboards','false','Controls access to manage all user defined dashboards.');
/* End YUK-16614 */

/* Start YUK-16434 */
CREATE TABLE Dashboard  (
   DashboardId          NUMBER                          NOT NULL,
   Name                 VARCHAR2(100)                   NOT NULL,
   Description          VARCHAR2(500),
   OwnerId              NUMBER,
   Visibility           VARCHAR2(20)                    NOT NULL,
   CONSTRAINT PK_Dashboard PRIMARY KEY (DashboardId)
);

CREATE TABLE UserDashboard  (
   UserId               NUMBER                          NOT NULL,
   DashboardId          NUMBER                          NOT NULL,
   PageAssignment       VARCHAR2(50)                    NOT NULL,
   CONSTRAINT PK_UserDashboard PRIMARY KEY (UserId, DashboardId)
);

CREATE TABLE Widget  (
   WidgetId             NUMBER                          NOT NULL,
   WidgetType           VARCHAR2(50)                    NOT NULL,
   DashboardId          NUMBER                          NOT NULL,
   Ordering             NUMBER                          NOT NULL,
   CONSTRAINT PK_Widget PRIMARY KEY (WidgetId)
);

CREATE TABLE WidgetSettings  (
   SettingId            NUMBER                          NOT NULL,
   WidgetId             NUMBER                          NOT NULL,
   Name                 VARCHAR2(50)                    NOT NULL,
   Value                VARCHAR2(500)                   NOT NULL,
   CONSTRAINT PK_WidgetSettings PRIMARY KEY (SettingId)
);

ALTER TABLE UserDashboard
   ADD CONSTRAINT FK_UserDashboard_Dashboard FOREIGN KEY (DashboardId)
      REFERENCES Dashboard (DashboardId)
      ON DELETE CASCADE;

ALTER TABLE UserDashboard
   ADD CONSTRAINT FK_UserDashboard_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserID)
      ON DELETE CASCADE;

ALTER TABLE Widget
   ADD CONSTRAINT FK_Widget_Dashboard FOREIGN KEY (DashboardId)
      REFERENCES Dashboard (DashboardId)
      ON DELETE CASCADE;

ALTER TABLE WidgetSettings
   ADD CONSTRAINT FK_WidgetSettings_Widget FOREIGN KEY (WidgetId)
      REFERENCES Widget (WidgetId)
      ON DELETE CASCADE;
/* End YUK-16434 */

/* Start YUK-16646 */
CREATE TABLE RecentPointValue  (
   PAObjectID           NUMBER                          NOT NULL,
   PointID              NUMBER                          NOT NULL,
   Timestamp            DATE                            NOT NULL,
   Quality              NUMBER                          NOT NULL,
   Value                FLOAT                           NOT NULL,
   CONSTRAINT PK_RecentPointValue PRIMARY KEY (PAObjectID)
);

ALTER TABLE RecentPointValue
   ADD CONSTRAINT FK_RPV_Point FOREIGN KEY (PointID)
      REFERENCES POINT (POINTID)
      ON DELETE CASCADE;

ALTER TABLE RecentPointValue
   ADD CONSTRAINT FK_RPV_YukonPAObject FOREIGN KEY (PAObjectID)
      REFERENCES YukonPAObject (PAObjectID)
      ON DELETE CASCADE;
/* End YUK-16646 */

/* Start YUK-16660 */
DROP TABLE UserSystemMetric;
/* End YUK-16660 */

/* Start YUK-16540 */
CREATE TABLE ControlEvent  (
   ControlEventId       NUMBER                          NOT NULL,
   StartTime            DATE                            NOT NULL,
   ScheduledStopTime    DATE                            NOT NULL,
   GroupId              NUMBER                          NOT NULL,
   LMControlHistoryId   NUMBER,
   CONSTRAINT PK_ControlEvent PRIMARY KEY (ControlEventId)
);

ALTER TABLE ControlEvent
   ADD CONSTRAINT FK_ContEvent_LMGroup FOREIGN KEY (GroupId)
      REFERENCES LMGroup (DeviceID)
      ON DELETE CASCADE;

ALTER TABLE ControlEvent
   ADD CONSTRAINT FK_ContEvent_LMContHist FOREIGN KEY (LMControlHistoryId)
      REFERENCES LMControlHistory (LMCtrlHistID);

CREATE TABLE ControlEventDevice  (
   DeviceId             NUMBER                          NOT NULL,
   ControlEventId       NUMBER                          NOT NULL,
   OptOutEventId        NUMBER,
   Result               VARCHAR2(30)                    NOT NULL,
   DeviceReceivedTime   DATE,
   CONSTRAINT PK_ControlEventDevice PRIMARY KEY (DeviceId, ControlEventId)
);

ALTER TABLE ControlEventDevice
   ADD CONSTRAINT FK_ContEventDev_YukonPAObject FOREIGN KEY (DeviceId)
      REFERENCES YukonPAObject (PAObjectID)
      ON DELETE CASCADE;

ALTER TABLE ControlEventDevice
   ADD CONSTRAINT FK_ContEventDev_ContEvent FOREIGN KEY (ControlEventId)
      REFERENCES ControlEvent (ControlEventId)
      ON DELETE CASCADE;

ALTER TABLE ControlEventDevice
   ADD CONSTRAINT FK_ContEventDev_OptOutEvent FOREIGN KEY (OptOutEventId)
      REFERENCES OptOutEvent (OptOutEventId)
      ON DELETE CASCADE;
/* End YUK-16540 */

/* Start YUK-16299 */
DELETE FROM DeviceAddress WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'CBC 6510');

DELETE FROM DeviceCBC WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'CBC 6510');

DELETE FROM DeviceDirectCommSettings WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'CBC 6510');

DELETE FROM DeviceWindow WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'CBC 6510');

DELETE FROM DEVICE WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'CBC 6510');

DELETE FROM POINTSTATUS WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
            (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'CBC 6510'));

DELETE FROM PointAlarming WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
            (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'CBC 6510'));

DELETE FROM POINT WHERE PAObjectID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'CBC 6510');

DELETE FROM YukonPAObject WHERE TYPE = 'CBC 6510';

DELETE FROM DeviceConfigDeviceTypes WHERE PaoType = 'CBC 6510';

DELETE FROM DeviceTypeCommand WHERE DeviceType = 'CBC 6510';
/* End YUK-16299 */

/* Start YUK-16604 */
UPDATE YukonRoleProperty 
SET 
    KeyName = 'Use Pao Permissions', 
    Description='Allow access to all load management objects. Set to true to force the use of per pao permissions.' 
WHERE RolePropertyID = -90009 
AND RoleID = -900;

UPDATE YukonGroupRole 
SET Value = 'changeToTrue' 
WHERE Value = 'false' 
AND RolePropertyID = -90009 
AND RoleID = -900;

UPDATE YukonGroupRole 
SET Value = 'false' 
WHERE Value = 'true' 
AND RolePropertyID = -90009 
AND RoleID = -900;

UPDATE YukonGroupRole 
SET Value = 'true' 
WHERE Value = 'changeToTrue' 
AND RolePropertyID = -90009 
AND RoleID = -900;
/* End YUK-16604 */

/* Start YUK-16740 */
/* @start-block */
/* We'll need to take the next five ID's, so we'll make sure we don't hit 100 by looking for ID's less than 96 */
/* If this value overlaps with an existing one, we'll make sure to not ignore the insert error so tech services can handle the special case */
/* Most of the dynamic values we'll calculate will be based on this v_MaxDeviceGroupId */

DECLARE 
    v_MaxDeviceGroupId NUMBER; 
    v_RootParentGroupId NUMBER; 
BEGIN

SELECT MAX(DG.DeviceGroupId) INTO v_MaxDeviceGroupId FROM DeviceGroup DG WHERE DG.DeviceGroupId < 96;
SELECT MAX(DG.DeviceGroupId) INTO v_RootParentGroupId FROM DeviceGroup DG WHERE SystemGroupEnum = 'SYSTEM_METERS';

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(v_MaxDeviceGroupId + 1, 'All Meters', v_RootParentGroupId, 'NOEDIT_NOMOD', 'STATIC', SYSDATE(), 'ALL_METERS');

/* The next two's parent should be 'All Meters', so v_MaxDeviceGroupId + 1 */
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(v_MaxDeviceGroupId + 2, 'All MCT Meters', v_MaxDeviceGroupId + 1, 'NOEDIT_NOMOD', 'METERS_ALL_PLC_METERS', SYSDATE(), 'ALL_MCT_METERS');

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(v_MaxDeviceGroupId + 3, 'All RFN Meters', v_MaxDeviceGroupId + 1, 'NOEDIT_NOMOD', 'STATIC', SYSDATE(), 'ALL_RFN_METERS');
    
/* The last two's parent should be 'All RF Meters', so v_MaxDeviceGroupId + 3 */
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(v_MaxDeviceGroupId + 4, 'All RF Electric Meters', v_MaxDeviceGroupId + 3, 'NOEDIT_NOMOD', 'METERS_ALL_RF_ELECTRIC_METERS', SYSDATE(), 'ALL_RF_ELECTRIC_METERS');

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(v_MaxDeviceGroupId + 5, 'All RFW Meters', v_MaxDeviceGroupId + 3, 'NOEDIT_NOMOD', 'METERS_ALL_RFW_METERS', SYSDATE(), 'ALL_RFW_METERS');
END;
/
/* @end-block */
/* End YUK-16740 */

/* Start YUK-16765 */
CREATE TABLE UsageThresholdReport  (
   UsageThresholdReportId NUMBER                          NOT NULL,
   Attribute            VARCHAR2(60)                    NOT NULL,
   StartDate            DATE                            NOT NULL,
   EndDate              DATE                            NOT NULL,
   RunTime              DATE                            NOT NULL,
   DevicesDescription   VARCHAR2(255)                   NOT NULL,
   CONSTRAINT PK_UTReport PRIMARY KEY (UsageThresholdReportId)
);

CREATE TABLE UsageThresholdReportRow  (
   UsageThresholdReportId NUMBER                          NOT NULL,
   PaoId                NUMBER                          NOT NULL,
   PointId              NUMBER,
   FirstTimestamp       DATE,
   FirstValue           FLOAT,
   LastTimestamp        DATE,
   LastValue            FLOAT,
   Delta                FLOAT,
   CONSTRAINT PK_UTReportRow PRIMARY KEY (UsageThresholdReportId, PaoId)
);

ALTER TABLE UsageThresholdReportRow
   ADD CONSTRAINT FK_UTReportRow_Point FOREIGN KEY (PointId)
      REFERENCES POINT (POINTID)
      ON DELETE CASCADE;

ALTER TABLE UsageThresholdReportRow
   ADD CONSTRAINT FK_UTReportRow_UTReport FOREIGN KEY (UsageThresholdReportId)
      REFERENCES UsageThresholdReport (UsageThresholdReportId)
      ON DELETE CASCADE;

ALTER TABLE UsageThresholdReportRow
   ADD CONSTRAINT FK_UTReportRow_YukonPAObject FOREIGN KEY (PaoId)
      REFERENCES YukonPAObject (PAObjectID)
      ON DELETE CASCADE;
/* End YUK-16765 */

/* Start YUK-16814 */
/* Rate A kWh (Offset 25)                  *
 * Rate B kWh (Offset 27)                  *
 * Rate C kWh (Offset 29)                  *
 * Rate D kWh (Offset 31)                  *
 * Delivered kW (Offset 101)               *
 * Peak kW (Offset 105)                    *
 * Delivered kWh per Interval (Offset 182) *
 * Delivered kW Load Profile (Offset 188)  */
DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (25,27,29,31,101,105,182,188) AND YP.Type IN ('RFN-530S4EAXR', 'RFN-530S4ERXR'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (25,27,29,31,101,105,182,188) AND YP.Type IN ('RFN-530S4EAXR', 'RFN-530S4ERXR'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (25,27,29,31,101,105,182,188) AND YP.Type IN ('RFN-530S4EAXR', 'RFN-530S4ERXR'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (25,27,29,31,101,105,182,188) AND YP.Type IN ('RFN-530S4EAXR', 'RFN-530S4ERXR'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (25,27,29,31,101,105,182,188) AND YP.Type IN ('RFN-530S4EAXR', 'RFN-530S4ERXR'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (25,27,29,31,101,105,182,188) AND YP.Type IN ('RFN-530S4EAXR', 'RFN-530S4ERXR'));
/* End YUK-16814 */

/* Start YUK-16782 */
/* Small update to BehaviorReportValue */
UPDATE BehaviorReportValue BRV
SET BRV.Value = 'KVA'
WHERE BRV.BehaviorReportId IN (
    SELECT BR.BehaviorReportId 
    FROM 
        BehaviorReport BR 
    JOIN 
        YukonPAObject Y ON BR.DeviceID = Y.PAObjectID
    WHERE Y.Type IN ('RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4') )
AND BRV.Value='DELIVERED_KVA';


/* Splitting Behavior/Updating BehaviorValue */
/* @error ignore-begin */
/* This will almost definitely error, but it's a necessary safety check */
TRUNCATE TABLE t_BehaviorsToSplit;
DROP TABLE t_BehaviorsToSplit;
/* @error ignore-end */

CREATE GLOBAL TEMPORARY TABLE t_BehaviorsToSplit ON COMMIT PRESERVE ROWS AS
    SELECT 
        ROW_NUMBER() OVER(ORDER BY B.BehaviorId) AS RowNumber, 
        B.BehaviorId
    FROM Behavior B
        JOIN BehaviorValue BV ON B.BehaviorId=BV.BehaviorId
        JOIN DeviceBehaviorMap DBM1 ON B.BehaviorId=DBM1.BehaviorId
        JOIN DeviceBehaviorMap DBM2 ON B.BehaviorId=DBM2.BehaviorId
        JOIN YukonPAObject Y1 ON DBM1.DeviceId=Y1.PAObjectID
        JOIN YukonPAObject Y2 ON DBM2.DeviceId=Y2.PAObjectID
    WHERE 
        B.BehaviorType='DATA_STREAMING'
        AND BV.Value='DELIVERED_KVA'
        AND Y1.Type='RFN-430A3K'
        AND Y2.Type IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4');

/* @start-block */
DECLARE 
    v_loopCounter NUMBER;
    v_BTSRowCount NUMBER;
    v_maxBehaviorId NUMBER;
    v_newBehaviorId NUMBER;
BEGIN

    SELECT COALESCE(MAX(BehaviorId), 0) INTO v_maxBehaviorId FROM Behavior;

    /*  Find any behaviors containing DELIVERED_KVA that are shared by RFN-430SL1+ and RFN-A3K  */
    INSERT INTO Behavior 
        SELECT
            v_maxBehaviorId + RowNumber,
            'DATA_STREAMING'
        FROM t_BehaviorsToSplit;

    INSERT INTO BehaviorValue
        SELECT 
            v_maxBehaviorId + BTS.RowNumber, 
            BV.Name,
            BV.Value
        FROM BehaviorValue BV
        JOIN t_BehaviorsToSplit BTS ON BV.BehaviorId = BTS.BehaviorID;

    UPDATE DeviceBehaviorMap DBM
    SET DBM.BehaviorId = (
        SELECT v_maxBehaviorId + BTS.RowNumber
        FROM DeviceBehaviorMap DBM
            JOIN YukonPAObject Y ON DBM.DeviceId = Y.PAObjectID
            JOIN t_BehaviorsToSplit BTS ON DBM.BehaviorId = BTS.BehaviorId
        WHERE 
            Y.Type IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4')
            AND DBM.BehaviorId = BTS.BehaviorID )
    WHERE DBM.DeviceID IN (
        SELECT DBM.DeviceID
        FROM DeviceBehaviorMap DBM
            JOIN YukonPAObject Y ON DBM.DeviceId=Y.PAObjectID
            JOIN t_BehaviorsToSplit BTS ON DBM.BehaviorId=BTS.BehaviorId
        WHERE 
            Y.Type IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4')
            AND DBM.BehaviorId = BTS.BehaviorID );
END;
/
/* @end-block */
TRUNCATE TABLE t_BehaviorsToSplit;
DROP TABLE t_BehaviorsToSplit;

/* Update all RFN Data Streaming Device Behaviors with RFN-430SL1+ to use KVA instead of DELIVERED_KVA */
UPDATE BehaviorValue BV
SET 
    BV.Value='KVA'
WHERE
    BV.BehaviorId IN (
        SELECT DISTINCT
            BV.BehaviorId
        FROM BehaviorValue BV
            JOIN Behavior B ON BV.BehaviorId=B.BehaviorId
            JOIN DeviceBehaviorMap DBM ON B.BehaviorId=DBM.BehaviorId
            JOIN YukonPAObject Y ON DBM.DeviceId=Y.PAObjectID
        WHERE 
            B.BehaviorType='DATA_STREAMING'
            AND Y.Type IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4') )
AND
    BV.Value='DELIVERED_KVA';


/* Splitting/Updating DeviceConfiguration */
/* Find all rfnChannelConfiguration DeviceConfigCategories that contain 'DELIVERED_KVA' and are assigned to both RFN-430SL1+ and another type supporting DELIVERED_KVA */
/* @error ignore-begin */
/* These will almost definitely error, but it's a necessary safety check */
TRUNCATE TABLE t_DeviceConfigsToSplit;
DROP TABLE t_DeviceConfigsToSplit;
TRUNCATE TABLE t_DeviceConfigurationIDs;
DROP TABLE t_DeviceConfigurationIDs;
TRUNCATE TABLE t_DeviceConfigCategoryIDs;
DROP TABLE t_DeviceConfigCategoryIDs;
/* @error ignore-end */

CREATE GLOBAL TEMPORARY TABLE t_DeviceConfigsToSplit ON COMMIT PRESERVE ROWS AS (
    SELECT 
        DISTINCT DC.DeviceConfigurationID, 
        DCC.DeviceConfigCategoryId
    FROM 
        DeviceConfiguration DC
        JOIN DeviceConfigCategoryMap DCCM ON DC.DeviceConfigurationID=DCCM.DeviceConfigurationId
        JOIN DeviceConfigCategory DCC ON DCCM.DeviceConfigCategoryId=DCC.DeviceConfigCategoryId
        JOIN DeviceConfigCategoryItem DCCI ON DCC.DeviceConfigCategoryId=DCCI.DeviceConfigCategoryId
        JOIN DeviceConfigDeviceTypes DCDT1 ON DC.DeviceConfigurationID=DCDT1.DeviceConfigurationId
        JOIN DeviceConfigDeviceTypes DCDT2 ON DC.DeviceConfigurationID=DCDT2.DeviceConfigurationId
    WHERE 
        DCC.CategoryType='rfnChannelConfiguration'
        AND DCCI.ItemName LIKE 'enabledChannels%attribute'
        AND DCCI.ItemValue='DELIVERED_KVA'
        AND DCDT1.PaoType IN ('RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')
        AND DCDT2.PaoType IN ('RFN-420CD', 'RFN-420CL', 'RFN-430A3K', 'RFN-530S4X') );

CREATE GLOBAL TEMPORARY TABLE t_DeviceConfigurationIDs (DeviceConfigurationID NUMBER NOT NULL, NewDeviceConfigurationID Number) ON COMMIT PRESERVE ROWS;
CREATE GLOBAL TEMPORARY TABLE t_DeviceConfigCategoryIDs (DeviceConfigCategoryID NUMBER NOT NULL, NewDeviceConfigCategoryID Number) ON COMMIT PRESERVE ROWS;

/* @start-block */
DECLARE
    v_maxDeviceConfigurationId NUMBER;
    v_maxDeviceConfigCatId NUMBER;
    v_maxDeviceConfigCatItemId  NUMBER;
    v_maxDeviceConfigDeviceTypeId    NUMBER;
BEGIN
    
    SELECT MAX(DeviceConfigurationId) INTO v_maxDeviceConfigurationId FROM DeviceConfiguration;
    SELECT MAX(DeviceConfigCategoryId) INTO v_maxDeviceConfigCatId FROM DeviceConfigCategory;
    SELECT MAX(DeviceConfigCategoryItemId) INTO v_maxDeviceConfigCatItemId FROM DeviceConfigCategoryItem;
    SELECT MAX(DeviceConfigDeviceTypeId) INTO v_maxDeviceConfigDeviceTypeId FROM DeviceConfigDeviceTypes;

    /* Calculate the new Device Config IDs */
    INSERT INTO t_DeviceConfigurationIDs
        SELECT 
            DCID.DeviceConfigurationID, 
            v_maxDeviceConfigurationId + ROW_NUMBER() OVER(ORDER BY DeviceConfigurationID ASC) AS NewDeviceConfigurationID
        FROM (SELECT DISTINCT DeviceConfigurationID FROM t_DeviceConfigsToSplit) DCID;

    /* Calculate the new Device Config Category IDs */
    INSERT INTO t_DeviceConfigCategoryIDs
        SELECT 
            DeviceConfigCategoryID, 
            v_maxDeviceConfigCatId + ROW_NUMBER() OVER(ORDER BY DeviceConfigCategoryID ASC) AS NewDeviceConfigCategoryID
        FROM (SELECT DISTINCT DeviceConfigCategoryID FROM t_DeviceConfigsToSplit) DCID;

    /*  Create a new RFN-430SL rfnChannelConfiguration DeviceConfigCategory from the existing one */
    INSERT INTO DeviceConfigCategory 
        SELECT
            DCCID.NewDeviceConfigCategoryId,
            DCC.CategoryType,
            DCC.Name || ' (RFN-430 kVA)',
            CASE
                WHEN DCC.Description IS NULL THEN 'Auto created for RFN-430 Sentinel meters during 6.7 upgrade'
                ELSE DCC.Description || CHR(13) || CHR(10) || 'Auto created for RFN-430 Sentinel meters during 6.7 upgrade'
            END
        FROM DeviceConfigCategory DCC
            JOIN t_DeviceConfigCategoryIDs DCCID ON DCC.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId;

    INSERT INTO DeviceConfigCategoryItem
        SELECT
            v_maxDeviceConfigCatItemId + ROW_NUMBER() OVER(ORDER BY DCCI.DeviceConfigCategoryItemId ASC),
            DCCID.NewDeviceConfigCategoryId,
            DCCI.ItemName,
            DCCI.ItemValue
        FROM 
            DeviceConfigCategoryItem DCCI
            JOIN t_DeviceConfigCategoryIDs DCCID ON DCCI.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId;

    /*  Create new device configs for the RFN-430 Sentinels */
    INSERT INTO DeviceConfiguration 
        SELECT
            DCID.NewDeviceConfigurationID,
            DC.Name || ' (auto-created for RFN-430 Sentinel)',
            CASE
                WHEN DC.Description IS NULL THEN 'Auto created for RFN-430 Sentinel meters during 6.7 upgrade'
                ELSE DC.Description || CHR(13) || CHR(10) || 'Auto created for RFN-430 Sentinel meters during 6.7 upgrade'
            END
        FROM 
            DeviceConfiguration DC
            JOIN t_DeviceConfigurationIDs DCID ON DC.DeviceConfigurationID = DCID.DeviceConfigurationId;

    /*  Insert copies of all device config categories into the new device configs */
    INSERT INTO DeviceConfigCategoryMap
        SELECT
            DCID.NewDeviceConfigurationId,
            DCCM.DeviceConfigCategoryId
        FROM 
            DeviceConfigCategoryMap DCCM
            JOIN t_DeviceConfigurationIDs DCID ON DCCM.DeviceConfigurationId = DCID.DeviceConfigurationId;

    /*  Update the new device configs to use the new categories */
    UPDATE DeviceConfigCategoryMap DCCMO
    SET DCCMO.DeviceConfigCategoryId = (
        SELECT DCCID.NewDeviceConfigCategoryId
        FROM DeviceConfigCategoryMap DCCM
        JOIN t_DeviceConfigurationIDs DCID ON DCCM.DeviceConfigurationId = DCID.NewDeviceConfigurationId
        JOIN t_DeviceConfigCategoryIDs DCCID ON DCCM.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        WHERE DCCMO.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        AND DCCMO.DeviceConfigurationId=DCID.NewDeviceConfigurationId)
    WHERE EXISTS (
        SELECT *
        FROM DeviceConfigCategoryMap DCCM
        JOIN t_DeviceConfigurationIDs DCID ON DCCM.DeviceConfigurationId = DCID.NewDeviceConfigurationId
        JOIN t_DeviceConfigCategoryIDs DCCID ON DCCM.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        WHERE DCCMO.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        AND DCCMO.DeviceConfigurationId=DCID.NewDeviceConfigurationId);
                
    /*  Insert the RFN-430 Sentinels into the new device config typelists */
    INSERT INTO DeviceConfigDeviceTypes
        SELECT
            v_maxDeviceConfigDeviceTypeId + ROW_NUMBER() OVER(ORDER BY DCDT.DeviceConfigDeviceTypeId ASC),
            DCID.NewDeviceConfigurationId,
            DCDT.PaoType
        FROM 
            DeviceConfigDeviceTypes DCDT
            JOIN t_DeviceConfigurationIDs DCID ON DCDT.DeviceConfigurationId=DCID.DeviceConfigurationId
        WHERE
            DCDT.PaoType IN ('RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4');

    /*  Delete the RFN-430 Sentinels from the old device config typelists */
    DELETE FROM DeviceConfigDeviceTypes DCDT
    WHERE 
        DCDT.DeviceConfigurationId IN (
            SELECT DCTS.DeviceConfigurationID
            FROM t_DeviceConfigsToSplit DCTS )
    AND
        DCDT.PaoType IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4');

    /*  Point the RFN-430 Sentinels to the new configs */
    UPDATE DeviceConfigurationDeviceMap DCDMO
    SET DCDMO.DeviceConfigurationId = (
        SELECT DCID.NewDeviceConfigurationId
        FROM DeviceConfigurationDeviceMap DCDM
            JOIN t_DeviceConfigurationIDs DCID ON DCDM.DeviceConfigurationId = DCID.DeviceConfigurationId
            JOIN YukonPAObject Y ON DCDM.DeviceId = Y.PAObjectID
        WHERE Y.Type IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4')
            AND DCDMO.DeviceConfigurationId = DCID.DeviceConfigurationId
            AND DCDMO.DeviceId = DCDM.DeviceId )
    WHERE EXISTS (
        SELECT *
        FROM DeviceConfigurationDeviceMap DCDM
            JOIN t_DeviceConfigurationIDs DCID ON DCDM.DeviceConfigurationId = DCID.DeviceConfigurationId
            JOIN YukonPAObject Y ON DCDM.DeviceId = Y.PAObjectID
        WHERE Y.Type IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4')
            AND DCDMO.DeviceConfigurationId = DCID.DeviceConfigurationId
            AND DCDMO.DeviceId = DCDM.DeviceId );
END;
/
/* @end-block */

/* Update all rfnChannelConfiguration categories assigned to RFN-430SL1+ to use KVA instead of DELIVERED_KVA */
UPDATE DeviceConfigCategoryItem DCCI
SET ItemValue='KVA'
WHERE DCCI.DeviceConfigCategoryId IN (
        SELECT DISTINCT DCC.DeviceConfigCategoryId 
        FROM
            DeviceConfigCategory DCC
            JOIN DeviceConfigCategoryMap DCCM ON DCC.DeviceConfigCategoryId=DCCM.DeviceConfigCategoryId
            JOIN DeviceConfiguration DC ON DCCM.DeviceConfigurationId=DC.DeviceConfigurationID
            JOIN DeviceConfigDeviceTypes DCDT ON DC.DeviceConfigurationID=DCDT.DeviceConfigurationId
        WHERE
            DCC.CategoryType='rfnChannelConfiguration'
            AND DCDT.PaoType IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4') )
    AND DCCI.ItemName LIKE 'enabledChannels%attribute'
    AND DCCI.ItemValue='DELIVERED_KVA';

TRUNCATE TABLE t_DeviceConfigsToSplit;
DROP TABLE t_DeviceConfigsToSplit;
TRUNCATE TABLE t_DeviceConfigurationIDs;
DROP TABLE t_DeviceConfigurationIDs;
TRUNCATE TABLE t_DeviceConfigCategoryIDs;
DROP TABLE t_DeviceConfigCategoryIDs;
/* End YUK-16782 */

/* Start YUK-16802 */
CREATE TABLE InfrastructureWarnings  (
   PaoId                NUMBER                          NOT NULL,
   WarningType          VARCHAR2(50)                    NOT NULL,
   Severity             VARCHAR2(10)                    NOT NULL,
   Argument1            VARCHAR2(50),
   Argument2            VARCHAR2(50),
   Argument3            VARCHAR2(50),
   CONSTRAINT PK_InfrastructureWarnings PRIMARY KEY (PaoId, WarningType)
);

ALTER TABLE InfrastructureWarnings
   ADD CONSTRAINT FK_InfWarnings_YukonPAO FOREIGN KEY (PaoId)
      REFERENCES YukonPAObject (PAObjectID)
      ON DELETE CASCADE;

INSERT INTO YukonServices VALUES (24, 'InfrastructureWarnings', 'classpath:com/cannontech/services/infrastructure/infrastructureWarningsContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
/* End YUK-16802 */

/* Start YUK-16722 */
ALTER TABLE UserDashboard
DROP CONSTRAINT PK_UserDashboard;

/* @start-block */
DECLARE
    v_indexCount NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_indexCount
    FROM USER_INDEXES
    WHERE INDEX_NAME = 'PK_UserDashboard';

    IF v_indexCount > 0 THEN
        EXECUTE IMMEDIATE 'DROP INDEX PK_UserDashboard';
    END IF;
END;
/
/* @end-block */

ALTER TABLE UserDashboard
ADD CONSTRAINT PK_UserDashboard PRIMARY KEY (UserId, DashboardId, PageAssignment);
/* End YUK-16722 */

/* Start YUK-16821 */
INSERT INTO YukonRoleProperty VALUES(-20223,-202,'Usage Threshold Report','true','Controls access to the Usage Threshold Report.');
/* End YUK-16821 */

/* Start YUK-16640 */
INSERT INTO Dashboard VALUES (-1, 'Default Main Dashboard', 'Default Main Dashboard', -1, 'SYSTEM');
INSERT INTO Dashboard VALUES (-2, 'Default AMI Dashboard', 'Default AMI Dashboard', -1, 'SYSTEM');

INSERT INTO UserDashboard VALUES (-1, -1, 'MAIN'); 
INSERT INTO UserDashboard VALUES (-1, -2, 'AMI');

/* Default MAIN Dashboard widgets */
INSERT INTO Widget VALUES (-1, 'FAVORITES', -1, 100);
INSERT INTO Widget VALUES (-2, 'MONITOR_SUBSCRIPTIONS', -1, 200);

/* Default AMI Dashboard widgets */
INSERT INTO Widget VALUES (-3, 'MONITORS', -2, 100);
INSERT INTO Widget VALUES (-4, 'SCHEDULED_REQUESTS', -2, 101);
INSERT INTO Widget VALUES (-5, 'METER_SEARCH', -2, 200);
INSERT INTO Widget VALUES (-6, 'AMI_ACTIONS', -2, 201);
INSERT INTO Widget VALUES (-7, 'GATEWAY_STREAMING_CAPACITY', -2, 202);
/* End YUK-16640 */

/* Start YUK-16825 */
UPDATE GlobalSetting SET Value='(AUTO_ENCRYPTED)' || Value 
    WHERE Name = 'ECOBEE_PASSWORD' AND value NOT LIKE '(AUTO_ENCRYPTED)%';

UPDATE GlobalSetting SET Value='(AUTO_ENCRYPTED)' || Value 
    WHERE Name = 'ECOBEE_USERNAME' AND value NOT LIKE '(AUTO_ENCRYPTED)%';

UPDATE GlobalSetting SET Value='(AUTO_ENCRYPTED)' || Value 
    WHERE Name = 'RFN_FIRMWARE_UPDATE_SERVER_USER' AND value NOT LIKE '(AUTO_ENCRYPTED)%';

UPDATE GlobalSetting SET Value='(AUTO_ENCRYPTED)' || Value 
    WHERE Name = 'RFN_FIRMWARE_UPDATE_SERVER_PASSWORD' AND value NOT LIKE '(AUTO_ENCRYPTED)%';

UPDATE GlobalSetting SET Value='(AUTO_ENCRYPTED)' || Value 
    WHERE Name = 'HONEYWELL_WIFI_SERVICE_BUS_CONNECTION_STRING' AND value NOT LIKE '(AUTO_ENCRYPTED)%';

UPDATE GlobalSetting SET Value='(AUTO_ENCRYPTED)' || Value 
    WHERE Name = 'HONEYWELL_WIFI_SERVICE_BUS_QUEUE' AND value NOT LIKE '(AUTO_ENCRYPTED)%';
/* End YUK-16825 */
    
/* Start YUK-16844 */
DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (216, 217, 218, 219) AND YP.Type IN ('RFN-410cL', 'RFN-410fD', 'RFN-410fL',
    'RFN-410fX', 'RFN-420cD', 'RFN-420cL', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRD', 'RFN-420fRX', 'RFN-420fX', 'RFN-430A3D',
    'RFN-430A3K', 'RFN-430A3R', 'RFN-430A3T', 'RFN-430KV', 'RFN-510fL', 'RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD', 
    'RFN-530fAX', 'RFN-530fRX', 'RFN-530S4eAX','RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR', 'RFN-530S4x'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (216, 217, 218, 219) AND YP.Type IN ('RFN-410cL', 'RFN-410fD', 'RFN-410fL',
    'RFN-410fX', 'RFN-420cD', 'RFN-420cL', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRD', 'RFN-420fRX', 'RFN-420fX', 'RFN-430A3D',
    'RFN-430A3K', 'RFN-430A3R', 'RFN-430A3T', 'RFN-430KV', 'RFN-510fL', 'RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD', 
    'RFN-530fAX', 'RFN-530fRX', 'RFN-530S4eAX','RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR', 'RFN-530S4x'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (216, 217, 218, 219) AND YP.Type IN ('RFN-410cL', 'RFN-410fD', 'RFN-410fL',
    'RFN-410fX', 'RFN-420cD', 'RFN-420cL', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRD', 'RFN-420fRX', 'RFN-420fX', 'RFN-430A3D',
    'RFN-430A3K', 'RFN-430A3R', 'RFN-430A3T', 'RFN-430KV', 'RFN-510fL', 'RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD', 
    'RFN-530fAX', 'RFN-530fRX', 'RFN-530S4eAX','RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR', 'RFN-530S4x'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (216, 217, 218, 219) AND YP.Type IN ('RFN-410cL', 'RFN-410fD', 'RFN-410fL',
    'RFN-410fX', 'RFN-420cD', 'RFN-420cL', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRD', 'RFN-420fRX', 'RFN-420fX', 'RFN-430A3D',
    'RFN-430A3K', 'RFN-430A3R', 'RFN-430A3T', 'RFN-430KV', 'RFN-510fL', 'RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD', 
    'RFN-530fAX', 'RFN-530fRX', 'RFN-530S4eAX','RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR', 'RFN-530S4x'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (216, 217, 218, 219) AND YP.Type IN ('RFN-410cL', 'RFN-410fD', 'RFN-410fL',
    'RFN-410fX', 'RFN-420cD', 'RFN-420cL', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRD', 'RFN-420fRX', 'RFN-420fX', 'RFN-430A3D',
    'RFN-430A3K', 'RFN-430A3R', 'RFN-430A3T', 'RFN-430KV', 'RFN-510fL', 'RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD', 
    'RFN-530fAX', 'RFN-530fRX', 'RFN-530S4eAX','RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR', 'RFN-530S4x'));
    
DELETE FROM GRAPHDATASERIES WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (216, 217, 218, 219) AND YP.Type IN ('RFN-410cL', 'RFN-410fD', 'RFN-410fL',
    'RFN-410fX', 'RFN-420cD', 'RFN-420cL', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRD', 'RFN-420fRX', 'RFN-420fX', 'RFN-430A3D',
    'RFN-430A3K', 'RFN-430A3R', 'RFN-430A3T', 'RFN-430KV', 'RFN-510fL', 'RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD', 
    'RFN-530fAX', 'RFN-530fRX', 'RFN-530S4eAX','RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR', 'RFN-530S4x'));

DELETE FROM POINTPROPERTYVALUE WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (216, 217, 218, 219) AND YP.Type IN ('RFN-410cL', 'RFN-410fD', 'RFN-410fL',
    'RFN-410fX', 'RFN-420cD', 'RFN-420cL', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRD', 'RFN-420fRX', 'RFN-420fX', 'RFN-430A3D',
    'RFN-430A3K', 'RFN-430A3R', 'RFN-430A3T', 'RFN-430KV', 'RFN-510fL', 'RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD', 
    'RFN-530fAX', 'RFN-530fRX', 'RFN-530S4eAX','RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR', 'RFN-530S4x'));

DELETE FROM POINTLIMITS WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (216, 217, 218, 219) AND YP.Type IN ('RFN-410cL', 'RFN-410fD', 'RFN-410fL',
    'RFN-410fX', 'RFN-420cD', 'RFN-420cL', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRD', 'RFN-420fRX', 'RFN-420fX', 'RFN-430A3D',
    'RFN-430A3K', 'RFN-430A3R', 'RFN-430A3T', 'RFN-430KV', 'RFN-510fL', 'RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD', 
    'RFN-530fAX', 'RFN-530fRX', 'RFN-530S4eAX','RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR', 'RFN-530S4x'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (216, 217, 218, 219) AND YP.Type IN ('RFN-410cL', 'RFN-410fD', 'RFN-410fL',
    'RFN-410fX', 'RFN-420cD', 'RFN-420cL', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRD', 'RFN-420fRX', 'RFN-420fX', 'RFN-430A3D',
    'RFN-430A3K', 'RFN-430A3R', 'RFN-430A3T', 'RFN-430KV', 'RFN-510fL', 'RFN-520fAX', 'RFN-520fAXD', 'RFN-520fRX','RFN-520fRXD', 
    'RFN-530fAX', 'RFN-530fRX', 'RFN-530S4eAX','RFN-530S4eAXR', 'RFN-530S4eRX', 'RFN-530S4eRXR', 'RFN-530S4x'));

UPDATE JobProperty SET Value = REPLACE(Value, 'OVER_VOLTAGE_MEASURED,', '') WHERE Name = 'attributes' AND Value LIKE '%OVER_VOLTAGE_MEASURED,%';
UPDATE JobProperty SET Value = REPLACE(Value, ',OVER_VOLTAGE_MEASURED', '') WHERE Name = 'attributes' AND Value LIKE '%,OVER_VOLTAGE_MEASURED%';
UPDATE JobProperty SET Value = REPLACE(Value, 'OVER_VOLTAGE_MEASURED', '') WHERE Name = 'attributes' AND Value = 'OVER_VOLTAGE_MEASURED';

UPDATE JobProperty SET Value = REPLACE(Value, 'OVER_VOLTAGE_THRESHOLD,', '') WHERE Name = 'attributes' AND Value LIKE '%OVER_VOLTAGE_THRESHOLD,%';
UPDATE JobProperty SET Value = REPLACE(Value, ',OVER_VOLTAGE_THRESHOLD', '') WHERE Name = 'attributes' AND Value LIKE '%,OVER_VOLTAGE_THRESHOLD%';
UPDATE JobProperty SET Value = REPLACE(Value, 'OVER_VOLTAGE_THRESHOLD', '') WHERE Name = 'attributes' AND Value = 'OVER_VOLTAGE_THRESHOLD';

UPDATE JobProperty SET Value = REPLACE(Value, 'UNDER_VOLTAGE_MEASURED,', '') WHERE Name = 'attributes' AND Value LIKE '%UNDER_VOLTAGE_MEASURED,%';
UPDATE JobProperty SET Value = REPLACE(Value, ',UNDER_VOLTAGE_MEASURED', '') WHERE Name = 'attributes' AND Value LIKE '%,UNDER_VOLTAGE_MEASURED%';
UPDATE JobProperty SET Value = REPLACE(Value, 'UNDER_VOLTAGE_MEASURED', '') WHERE Name = 'attributes' AND Value = 'UNDER_VOLTAGE_MEASURED';

UPDATE JobProperty SET Value = REPLACE(Value, 'UNDER_VOLTAGE_THRESHOLD,', '') WHERE Name = 'attributes' AND Value LIKE '%UNDER_VOLTAGE_THRESHOLD,%';
UPDATE JobProperty SET Value = REPLACE(Value, ',UNDER_VOLTAGE_THRESHOLD', '') WHERE Name = 'attributes' AND Value LIKE '%,UNDER_VOLTAGE_THRESHOLD%';
UPDATE JobProperty SET Value = REPLACE(Value, 'UNDER_VOLTAGE_THRESHOLD', '') WHERE Name = 'attributes' AND Value = 'UNDER_VOLTAGE_THRESHOLD';
/* End YUK-16844 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.7', '05-JUL-2017', 'Latest Update', 0, SYSDATE);