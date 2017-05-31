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
SET PointName = 'Outages', PointOffset = 100
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
SET PointOffset = 350, PointName = 'Net Delivered kVArh (Rate A kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 230
AND PointName = 'Rate A Net Delivered kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4')
);

UPDATE Point 
SET PointOffset = 351, PointName = 'Net Delivered kVArh (Rate B kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 231
AND PointName = 'Rate B Net Delivered kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4')
);

UPDATE Point 
SET PointOffset = 352, PointName = 'Net Delivered kVArh (Rate C kVArh)'
WHERE PointType = 'Analog' 
AND PointOffset = 232
AND PointName = 'Rate C Net Delivered kVArh'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430SL4')
);

UPDATE Point 
SET PointOffset = 353, PointName = 'Net Delivered kVArh (Rate D kVArh)'
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
  SELECT CASE WHEN (SELECT DISTINCT 1  FROM CTIDatabase WHERE 6.7 <= (SELECT MAX(version) FROM CTIDatabase)) IS NULL THEN 0 ELSE 1  END 
    AS temp INTO v_exists FROM dual;
        IF v_exists != 1 THEN
            UPDATE RAWPOINTHISTORY SET TIMESTAMP = TIMESTAMP - numtodsinterval(VALUE, 'SECOND') 
            WHERE pointid IN (SELECT pointid FROM point p JOIN YukonPAObject ypo ON p.PAObjectID = ypo.PAObjectID 
            AND PointType = 'Analog' AND POINTOFFSET = 100
            AND Type LIKE 'RFN%');
        END IF;
END;
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
/* End YUK-16299 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '04-JAN-2017', 'Latest Update', 0, SYSDATE);*/
