/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-16542 */
UPDATE Point 
SET PointName = 'Sum kVAh', PointOffset = 263
WHERE PointType = 'Analog' 
AND PointOffset = 152
AND PointName = 'kVAh Sum'
AND PaobjectId IN (
    SELECT DISTINCT PaobjectId 
    FROM YukonPaobject
    WHERE Type IN ('RFN-430KV','RFN-430A3K')
);
/* End YUK-16542 */

/* Start YUK-16894 */
ALTER TABLE ControlEvent ADD ProgramId NUMBER DEFAULT 0 NOT NULL;

ALTER TABLE ControlEvent
   ADD CONSTRAINT FK_CONTROLEVENT_YUKONPAOBJECT FOREIGN KEY (ProgramId)
      REFERENCES YukonPAObject (PAObjectID);

MERGE INTO ControlEvent CE
USING (
    SELECT DeviceId, LMGroupId
    FROM LMHardwareControlGroup lmhcg 
        JOIN LMProgramWebPublishing lmpwb ON lmpwb.ProgramID = lmhcg.ProgramID
    WHERE lmhcg.ControlEntryId IN ((SELECT MAX(ControlEntryId) FROM LMHardwareControlGroup WHERE LMGroupId != -9999 
        GROUP BY LMGroupId)) 
    ) DLG
    ON (CE.GroupId = DLG.LMGroupId)
    WHEN MATCHED THEN
    UPDATE
    SET ProgramId = DLG.DeviceId;
/* End YUK-16894 */

/* Start YUK-16606 */
ALTER TABLE UserPaoPermission DROP CONSTRAINT FK_USERPAOP_REF_YUKPA_YUKONPAO;

ALTER TABLE UserPaoPermission
   ADD CONSTRAINT FK_USERPAOP_REF_YUKPA_YUKONPAO FOREIGN KEY (PaoID)
      REFERENCES YukonPAObject (PAObjectID)
      ON DELETE CASCADE;

ALTER TABLE GroupPaoPermission DROP CONSTRAINT FK_GroupPaoPerm_PAO;

ALTER TABLE GroupPaoPermission
   ADD CONSTRAINT FK_GroupPaoPerm_PAO FOREIGN KEY (PaoId)
      REFERENCES YukonPAObject (PAObjectID)
      ON DELETE CASCADE;
/* End YUK-16606 */

/* Start YUK-16931 */
UPDATE DeviceConfigDeviceTypes SET PaoType = 'RFN-530S4eAX' WHERE PaoType = 'RFN-530S4eAD';
UPDATE DeviceConfigDeviceTypes SET PaoType = 'RFN-530S4eAXR' WHERE PaoType = 'RFN-530S4eAT';
UPDATE DeviceConfigDeviceTypes SET PaoType = 'RFN-530S4eRX' WHERE PaoType = 'RFN-530S4eRD';
UPDATE DeviceConfigDeviceTypes SET PaoType = 'RFN-530S4eRXR' WHERE PaoType = 'RFN-530S4eRT';
/* End YUK-16931 */

/* Start YUK-16983 */
UPDATE CalcBase SET PeriodicRate = 180 WHERE PeriodicRate = 240 AND UpdateType IN ('On Timer', 'On Timer+Change');
UPDATE CalcBase SET PeriodicRate = 300 WHERE PeriodicRate = 420 AND UpdateType IN ('On Timer', 'On Timer+Change');
UPDATE CalcBase SET PeriodicRate = 600 WHERE PeriodicRate = 720 AND UpdateType IN ('On Timer', 'On Timer+Change');
UPDATE CalcBase SET PeriodicRate = 900 WHERE PeriodicRate IN (1200, 1500) AND UpdateType IN ('On Timer', 'On Timer+Change');

UPDATE Point SET ArchiveInterval = 180 WHERE ArchiveInterval = 240 AND ArchiveType IN ('On Timer', 'timer|update');
UPDATE Point SET ArchiveInterval = 300 WHERE ArchiveInterval = 420 AND ArchiveType IN ('On Timer', 'timer|update');
UPDATE Point SET ArchiveInterval = 600 WHERE ArchiveInterval = 720 AND ArchiveType IN ('On Timer', 'timer|update');
UPDATE Point SET ArchiveInterval = 900 WHERE ArchiveInterval IN (1200, 1500) AND ArchiveType IN ('On Timer', 'timer|update');
/* End YUK-16983 */

/* Start YUK-16964 */
/* Delivered kW (Offset 101)               *
 * Peak kW (Offset 105)                    *
 * Delivered kW Load Profile (Offset 188)  *
 * Delivered kWh Per Interval (Offset 182) */
DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (101, 105, 182, 188) AND YP.Type IN ('RFN-530S4EAX', 'RFN-530S4ERX'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (101, 105, 182, 188) AND YP.Type IN ('RFN-530S4EAX', 'RFN-530S4ERX'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (101, 105, 182, 188) AND YP.Type IN ('RFN-530S4EAX', 'RFN-530S4ERX'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (101, 105, 182, 188) AND YP.Type IN ('RFN-530S4EAX', 'RFN-530S4ERX'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (101, 105, 182, 188) AND YP.Type IN ('RFN-530S4EAX', 'RFN-530S4ERX'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'ANALOG' AND PointOffset IN (101, 105, 182, 188) AND YP.Type IN ('RFN-530S4EAX', 'RFN-530S4ERX'));
/* End YUK-16964 */

/* Start YUK-16952 */
INSERT INTO GlobalSetting (GlobalSettingId, Name, Value, Comments, LastChangedDate)
  (SELECT
     (SELECT MAX(GlobalSettingId)+1
      FROM GlobalSetting), 'SMTP_ENCRYPTION_TYPE',
                           'NONE',
                           '6.7 DB Update Conversion',
                           SYSDATE
   FROM GlobalSetting
   WHERE Name = 'SMTP_HOST'
     AND Value IS NOT NULL);

UPDATE GlobalSetting
SET value = 'NONE'
WHERE Name = 'SMTP_ENCRYPTION_TYPE'
  AND (
         (SELECT VALUE
          FROM GlobalSetting
          WHERE Name = 'SMTP_USERNAME') IS NULL
       OR
         (SELECT VALUE
          FROM GlobalSetting
          WHERE Name = 'SMTP_PASSWORD') IS NULL)
  AND (
         (SELECT VALUE
          FROM GlobalSetting
          WHERE Name = 'SMTP_TLS_ENABLED') = '0'
       OR
         (SELECT VALUE
          FROM GlobalSetting
          WHERE Name = 'SMTP_TLS_ENABLED') IS NULL);

UPDATE GlobalSetting
SET value = 'SSL'
WHERE Name = 'SMTP_ENCRYPTION_TYPE'
  AND (
         (SELECT VALUE
          FROM GlobalSetting
          WHERE Name = 'SMTP_USERNAME') IS NOT NULL
       AND
         (SELECT VALUE
          FROM GlobalSetting
          WHERE Name = 'SMTP_PASSWORD') IS NOT NULL)
  AND (
         (SELECT VALUE
          FROM GlobalSetting
          WHERE Name = 'SMTP_TLS_ENABLED') = '0'
       OR
         (SELECT VALUE
          FROM GlobalSetting
          WHERE Name = 'SMTP_TLS_ENABLED') IS NULL);

UPDATE GlobalSetting
SET value = 'TLS'
WHERE Name = 'SMTP_ENCRYPTION_TYPE'
  AND (
         (SELECT VALUE
          FROM GlobalSetting
          WHERE Name = 'SMTP_TLS_ENABLED') = '1');

DELETE
FROM GlobalSetting
WHERE value = 'SMTP_TLS_ENABLED';
/* End YUK-16952 */

/* Start YUK-16986 */
ALTER TABLE Dashboard
   ADD CONSTRAINT AK_Dashboard_OwnerId_Name UNIQUE (OwnerId, Name);
/* End YUK-16986 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '27-JUL-2017', 'Latest Update', 1, SYSDATE);*/
