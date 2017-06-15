/******************************************/
/**** SQL Server DBupdates             ****/
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
ADD Version VARCHAR(12);
GO

UPDATE MSPInterface 
SET Version = '3.0';

ALTER TABLE MSPInterface
ALTER COLUMN Version VARCHAR(12) NOT NULL;
GO

ALTER TABLE MSPInterface
ADD CONSTRAINT PK_MSPINTERFACE PRIMARY KEY (VendorID,Interface,Version);

ALTER TABLE MSPInterface
ALTER COLUMN Endpoint VARCHAR(255) NOT NULL;
GO

UPDATE MSPInterface 
SET endpoint = MV.URL + MI.Endpoint
FROM MSPInterface MI, MSPVendor MV
WHERE MI.VendorID = MV.VendorID;
GO

ALTER TABLE MSPVendor DROP COLUMN URL;
GO

INSERT INTO MSPInterface VALUES (1, 'MR_Server', 'http://127.0.0.1:8080/multispeak/v5/MR_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'OD_Server', 'http://127.0.0.1:8080/multispeak/v5/OD_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'CD_Server', 'http://127.0.0.1:8080/multispeak/v5/CD_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'NOT_Server', 'http://127.0.0.1:8080/multispeak/v5/NOT_Server', '5.0');
/* End YUK-16173 */

/* Start YUK-16225 */
/* @error ignore-begin */
ALTER TABLE HoneywellWifiThermostat
   ADD CONSTRAINT AK_HONEYWELLWIFITHERMOSTAT_MAC UNIQUE (MacAddress);
GO
/* @error ignore-end */
/* End YUK-16225 */

/* Start YUK-16164 */
ALTER TABLE DEVICEMETERGROUP
    DROP CONSTRAINT FK_DeviceMeterGroup_Device;
GO

ALTER TABLE DEVICEMETERGROUP
   ADD CONSTRAINT FK_DeviceMeterGroup_Device FOREIGN KEY (DEVICEID)
      REFERENCES DEVICE (DEVICEID)
         ON DELETE CASCADE;
GO
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
ALTER TABLE UserPreference
    ALTER COLUMN Value VARCHAR(1275) NOT NULL;
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
IF (SELECT DISTINCT 1 FROM CTIDatabase WHERE 6.7 <= (SELECT MAX(version) FROM CTIDatabase)) IS NULL
BEGIN
	UPDATE RAWPOINTHISTORY SET TIMESTAMP = DATEADD(SECOND, -VALUE, TIMESTAMP)
	WHERE pointid IN (SELECT pointid FROM point p JOIN YukonPAObject ypo ON p.PAObjectID = ypo.PAObjectID 
	AND PointType = 'Analog' AND POINTOFFSET = 100
	AND Type LIKE 'RFN%')
END;
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

GO

/* @start-block */
DECLARE
    @newConfigCategoryId        NUMERIC,
    @newConfigCategoryItemId    NUMERIC,
    @existingConfigurationId    NUMERIC;

    /* Find non-default config categories used by existing CBC 8000 and CBC DNP Devices and stick them into a cursor */
    DECLARE config_cursor CURSOR STATIC FOR (
        SELECT DISTINCT C.DeviceConfigurationID
        FROM YukonPAObject Y, DeviceConfigurationDeviceMap C, DeviceConfiguration DC
        WHERE Y.Type IN ('CBC 8020', 'CBC 8024', 'CBC DNP')
        AND Y.PAObjectID = C.DeviceID
        AND C.DeviceConfigurationId = DC.DeviceConfigurationID
        AND DC.DeviceConfigurationID > -1
    );

BEGIN
    SET @newConfigCategoryId =      (SELECT MAX(DeviceConfigCategoryId) + 1 FROM DeviceConfigCategory)
    SET @newConfigCategoryItemId =  (SELECT MAX(DeviceConfigCategoryItemId) + 1 FROM DeviceConfigCategoryItem)

    INSERT INTO DeviceConfigCategory        VALUES (@newConfigCategoryId, 'cbcHeartbeat', 'Default CBC Heartbeat Category', NULL);

    INSERT INTO DeviceConfigCategoryItem    VALUES (@newConfigCategoryItemId,      @newConfigCategoryId, 'cbcHeartbeatPeriod', 0);
    INSERT INTO DeviceConfigCategoryItem    VALUES (@newConfigCategoryItemId + 1,  @newConfigCategoryId, 'cbcHeartbeatValue',  0);
    INSERT INTO DeviceConfigCategoryItem    VALUES (@newConfigCategoryItemId + 2,  @newConfigCategoryId, 'cbcHeartbeatMode',   'DISABLED');

    INSERT INTO DeviceConfigCategoryMap     VALUES (-1, @newConfigCategoryId); /* -1 is the ID for the default DNP Configuration */

    /* Add @newConfigCategoryId to DeviceConfigCategoryMap for each of these found ID's (if any) */
    OPEN config_cursor;
    FETCH NEXT FROM config_cursor INTO @existingConfigurationId
    WHILE @@FETCH_STATUS = 0
    BEGIN
        INSERT INTO DeviceConfigCategoryMap VALUES (@existingConfigurationId, @newConfigCategoryId);
        FETCH NEXT FROM config_cursor INTO @existingConfigurationId
    END
    CLOSE config_cursor;
    DEALLOCATE config_cursor;
END;
/* @end-block */
/* End YUK-16593 */

/* Start YUK-16614 */
INSERT INTO YukonRoleProperty VALUES(-20021,-200,'Manage Dashboards','false','Controls access to manage all user defined dashboards.');
/* End YUK-16614 */

/* Start YUK-16434 */
CREATE TABLE Dashboard (
   DashboardId          NUMERIC              NOT NULL,
   Name                 VARCHAR(100)         NOT NULL,
   Description          VARCHAR(500)         NULL,
   OwnerId              NUMERIC              NULL,
   Visibility           VARCHAR(20)          NOT NULL,
   CONSTRAINT PK_Dashboard PRIMARY KEY (DashboardId)
);

CREATE TABLE UserDashboard (
   UserId               NUMERIC              NOT NULL,
   DashboardId          NUMERIC              NOT NULL,
   PageAssignment       VARCHAR(50)          NOT NULL,
   CONSTRAINT PK_UserDashboard PRIMARY KEY (UserId, DashboardId)
);

CREATE TABLE Widget (
   WidgetId             NUMERIC              NOT NULL,
   WidgetType           VARCHAR(50)          NOT NULL,
   DashboardId          NUMERIC              NOT NULL,
   Ordering             NUMERIC              NOT NULL,
   CONSTRAINT PK_Widget PRIMARY KEY (WidgetId)
);

CREATE TABLE WidgetSettings (
   SettingId            NUMERIC              NOT NULL,
   WidgetId             NUMERIC              NOT NULL,
   Name                 VARCHAR(50)          NOT NULL,
   Value                VARCHAR(500)         NOT NULL,
   CONSTRAINT PK_WidgetSettings PRIMARY KEY (SettingId)
);
GO

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
GO
/* End YUK-16434 */

/* Start YUK-16646 */
CREATE TABLE RecentPointValue (
   PAObjectID           NUMERIC              NOT NULL,
   PointID              NUMERIC              NOT NULL,
   Timestamp            DATETIME             NOT NULL,
   Quality              NUMERIC              NOT NULL,
   Value                FLOAT                NOT NULL,
   CONSTRAINT PK_RecentPointValue PRIMARY KEY (PAObjectID)
);
GO

ALTER TABLE RecentPointValue
   ADD CONSTRAINT FK_RPV_Point FOREIGN KEY (PointID)
      REFERENCES POINT (POINTID)
         ON DELETE CASCADE;

ALTER TABLE RecentPointValue
   ADD CONSTRAINT FK_RPV_YukonPAObject FOREIGN KEY (PAObjectID)
      REFERENCES YukonPAObject (PAObjectID)
         ON DELETE CASCADE;
GO
/* End YUK-16646 */

/* Start YUK-16660 */
DROP TABLE UserSystemMetric
GO
/* End YUK-16660 */

/* Start YUK-16540 */
CREATE TABLE ControlEvent (
   ControlEventId       NUMERIC              NOT NULL,
   StartTime            DATETIME             NOT NULL,
   ScheduledStopTime    DATETIME             NOT NULL,
   GroupId              NUMERIC              NOT NULL,
   LMControlHistoryId   NUMERIC              NULL,
   CONSTRAINT PK_ControlEvent PRIMARY KEY (ControlEventId)
);

ALTER TABLE ControlEvent
   ADD CONSTRAINT FK_ContEvent_LMContHist FOREIGN KEY (LMControlHistoryId)
      REFERENCES LMControlHistory (LMCtrlHistID);


ALTER TABLE ControlEvent
   ADD CONSTRAINT FK_ContEvent_LMGroup FOREIGN KEY (GroupId)
      REFERENCES LMGroup (DeviceID)
         ON DELETE CASCADE;

GO

CREATE TABLE ControlEventDevice (
   DeviceId             NUMERIC              NOT NULL,
   ControlEventId       NUMERIC              NOT NULL,
   OptOutEventId        NUMERIC              NULL,
   Result               VARCHAR(30)          NOT NULL,
   DeviceReceivedTime   DATETIME             NULL,
   CONSTRAINT PK_ControlEventDevice PRIMARY KEY (DeviceId, ControlEventId)
);

ALTER TABLE ControlEventDevice
   ADD CONSTRAINT FK_ContEventDev_ContEvent FOREIGN KEY (ControlEventId)
      REFERENCES ControlEvent (ControlEventId)
         ON DELETE CASCADE;

ALTER TABLE ControlEventDevice
   ADD CONSTRAINT FK_ContEventDev_OptOutEvent FOREIGN KEY (OptOutEventId)
      REFERENCES OptOutEvent (OptOutEventId)
         ON DELETE CASCADE;

ALTER TABLE ControlEventDevice
   ADD CONSTRAINT FK_ContEventDev_YukonPAObject FOREIGN KEY (DeviceId)
      REFERENCES YukonPAObject (PAObjectID)
         ON DELETE CASCADE;

GO
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
UPDATE YukonRoleProperty SET KeyName = 'Use Pao Permissions', 
    Description='Allow access to all load management objects. Set to true to force the use of per pao permissions.' 
        WHERE RolePropertyID = -90009 AND RoleID = -900;

UPDATE YukonGroupRole SET Value = 'changeToTrue' WHERE Value = 'false' AND RolePropertyID = -90009 AND RoleID = -900;

UPDATE YukonGroupRole SET Value = 'false' WHERE Value = 'true' AND RolePropertyID = -90009 AND RoleID = -900;

UPDATE YukonGroupRole SET Value = 'true' WHERE Value = 'changeToTrue' AND RolePropertyID = -90009 AND RoleID = -900;

/* End YUK-16604 */

/* Start YUK-16740 */
/* @start-block */
/* We'll need to take the next five ID's, so we'll make sure we don't hit 100 by looking for ID's less than 96 */
/* If this value overlaps with an existing one, we'll make sure to not ignore the insert error so tech services can handle the special case */
/* Most of the dynamic values we'll calculate will be based on this @MaxDeviceGroupId */
BEGIN
DECLARE @MaxDeviceGroupId NUMERIC = (SELECT MAX(DG.DeviceGroupId) FROM DeviceGroup DG WHERE DG.DeviceGroupId < 96)
DECLARE @RootParentGroupId NUMERIC = (SELECT MAX(DG.DeviceGroupId) FROM DeviceGroup DG WHERE SystemGroupEnum = 'SYSTEM_METERS')

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(@MaxDeviceGroupId + 1, 'All Meters', @RootParentGroupId, 'NOEDIT_NOMOD', 'STATIC', GETDATE(), 'ALL_METERS')

/* The next two's parent should be 'All Meters', so @MaxDeviceGroupId + 1 */
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(@MaxDeviceGroupId + 2, 'All MCT Meters', @MaxDeviceGroupId + 1, 'NOEDIT_NOMOD', 'METERS_ALL_PLC_METERS', GETDATE(), 'ALL_MCT_METERS')

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(@MaxDeviceGroupId + 3, 'All RFN Meters', @MaxDeviceGroupId + 1, 'NOEDIT_NOMOD', 'STATIC', GETDATE(), 'ALL_RFN_METERS')
    
/* The last two's parent should be 'All RF Meters', so @MaxDeviceGroupId + 3 */
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(@MaxDeviceGroupId + 4, 'All RF Electric Meters', @MaxDeviceGroupId + 3, 'NOEDIT_NOMOD', 'METERS_ALL_RF_ELECTRIC_METERS', GETDATE(), 'ALL_RF_ELECTRIC_METERS')

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(@MaxDeviceGroupId + 5, 'All RFW Meters', @MaxDeviceGroupId + 3, 'NOEDIT_NOMOD', 'METERS_ALL_RFW_METERS', GETDATE(), 'ALL_RFW_METERS')
END;
GO
/* @end-block */
/* End YUK-16740 */

/* Start YUK-16765 */
CREATE TABLE UsageThresholdReport (
   UsageThresholdReportId NUMERIC              NOT NULL,
   Attribute            VARCHAR(60)          NOT NULL,
   StartDate            DATETIME             NOT NULL,
   EndDate              DATETIME             NOT NULL,
   RunTime              DATETIME             NOT NULL,
   DevicesDescription   VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_UTReport PRIMARY KEY (UsageThresholdReportId)
);

CREATE TABLE UsageThresholdReportRow (
   UsageThresholdReportId NUMERIC              NOT NULL,
   PaoId                NUMERIC              NOT NULL,
   PointId              NUMERIC              NULL,
   FirstTimestamp       DATETIME             NULL,
   FirstValue           FLOAT                NULL,
   LastTimestamp        DATETIME             NULL,
   LastValue            FLOAT                NULL,
   Delta                FLOAT                NULL,
   CONSTRAINT PK_UTReportRow PRIMARY KEY (UsageThresholdReportId, PaoId)
);
GO

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
GO
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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '4-JAN-2017', 'Latest Update', 0, GETDATE());*/
