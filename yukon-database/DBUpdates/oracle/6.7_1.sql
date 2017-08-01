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
WHERE Name = 'SMTP_TLS_ENABLED';
/* End YUK-16952 */

/* Start YUK-16986 */
ALTER TABLE Dashboard
   ADD CONSTRAINT AK_Dashboard_OwnerId_Name UNIQUE (OwnerId, Name);
/* End YUK-16986 */

/* Start YUK-16782 */

/* Converting KVAR to DELIVERED_KVAR */

/* Device List:
KVAR -> DELIVERED KVAR
 RFN-430A3R
 RFN-530S4X
 
KVAR -> KVAR
 RFN-430SL1
 RFN-430SL2
 RFN-430SL3
 RFN-430SL4
*/

/* Update all RFN Data Streaming Device Behaviors with appropriate meter types to use DELIVERED_KVAR instead of KVAR */
UPDATE BehaviorReportValue BRV
SET BRV.Value = 'DELIVERED_KVAR'
WHERE BRV.BehaviorReportId IN (
    SELECT BR.BehaviorReportId 
    FROM BehaviorReport BR 
    JOIN YukonPAObject Y 
        ON BR.DeviceID = Y.PAObjectID
    WHERE Y.Type IN ('RFN-430A3R', 'RFN-530S4X') )
AND BRV.Value='KVAR';


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
    JOIN BehaviorValue BV       ON B.BehaviorId = BV.BehaviorId
    JOIN DeviceBehaviorMap DBM1 ON B.BehaviorId = DBM1.BehaviorId
    JOIN DeviceBehaviorMap DBM2 ON B.BehaviorId = DBM2.BehaviorId
    JOIN YukonPAObject Y1       ON DBM1.DeviceId = Y1.PAObjectID
    JOIN YukonPAObject Y2       ON DBM2.DeviceId = Y2.PAObjectID
    WHERE B.BehaviorType='DATA_STREAMING'
    AND BV.Value='KVAR'
    AND Y1.Type IN ('RFN-430A3R', 'RFN-530S4X')
    AND Y2.Type IN ('RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4');

/* @start-block */
DECLARE 
    v_loopCounter NUMBER;
    v_BTSRowCount NUMBER;
    v_maxBehaviorId NUMBER;
    v_newBehaviorId NUMBER;
BEGIN

    SELECT COALESCE(MAX(BehaviorId), 0) INTO v_maxBehaviorId FROM Behavior;

    /*  Find any behaviors containing KVAR that are shared by updating and static config devices  */
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
        JOIN YukonPAObject Y        ON DBM.DeviceId = Y.PAObjectID
        JOIN t_BehaviorsToSplit BTS ON DBM.BehaviorId = BTS.BehaviorId
        WHERE Y.Type IN ('RFN-430A3R', 'RFN-530S4X')
        AND DBM.BehaviorId = BTS.BehaviorID )
    WHERE DBM.DeviceID IN (
        SELECT DBM.DeviceID
        FROM DeviceBehaviorMap DBM
        JOIN YukonPAObject Y        ON DBM.DeviceId=Y.PAObjectID
        JOIN t_BehaviorsToSplit BTS ON DBM.BehaviorId=BTS.BehaviorId
        WHERE Y.Type IN ('RFN-430A3R', 'RFN-530S4X')
        AND DBM.BehaviorId = BTS.BehaviorID );
END;
/
/* @end-block */
TRUNCATE TABLE t_BehaviorsToSplit;
DROP TABLE t_BehaviorsToSplit;

/* Update all RFN Data Streaming Device Behaviors with appropriate device types to use DELIVERED_KVAR instead of KVAR */
UPDATE BehaviorValue BV
SET BV.Value = 'DELIVERED_KVAR'
WHERE BV.BehaviorId IN (
    SELECT DISTINCT BV.BehaviorId
    FROM BehaviorValue BV
    JOIN Behavior B            ON BV.BehaviorId = B.BehaviorId
    JOIN DeviceBehaviorMap DBM ON B.BehaviorId = DBM.BehaviorId
    JOIN YukonPAObject Y       ON DBM.DeviceId = Y.PAObjectID
    WHERE B.BehaviorType='DATA_STREAMING'
    AND Y.Type IN ('RFN-430A3R', 'RFN-530S4X') )
AND BV.Value='KVAR';


/* Splitting/Updating DeviceConfiguration */
/* Find all rfnChannelConfiguration DeviceConfigCategories that contain 'KVAR' and are assigned to the channel-renaming device types and another type supporting KVAR */
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
    FROM DeviceConfiguration DC
    JOIN DeviceConfigCategoryMap DCCM  ON DC.DeviceConfigurationID=DCCM.DeviceConfigurationId
    JOIN DeviceConfigCategory DCC      ON DCCM.DeviceConfigCategoryId=DCC.DeviceConfigCategoryId
    JOIN DeviceConfigCategoryItem DCCI ON DCC.DeviceConfigCategoryId=DCCI.DeviceConfigCategoryId
    JOIN DeviceConfigDeviceTypes DCDT1 ON DC.DeviceConfigurationID=DCDT1.DeviceConfigurationId
    JOIN DeviceConfigDeviceTypes DCDT2 ON DC.DeviceConfigurationID=DCDT2.DeviceConfigurationId
    WHERE DCC.CategoryType='rfnChannelConfiguration'
    AND DCCI.ItemName LIKE 'enabledChannels%attribute'
    AND DCCI.ItemValue = 'KVAR'
    AND DCDT1.PaoType IN ('RFN-430A3R', 'RFN-530S4X')
    AND DCDT2.PaoType IN ('RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4') );

CREATE GLOBAL TEMPORARY TABLE t_DeviceConfigurationIDs (DeviceConfigurationID NUMBER NOT NULL, NewDeviceConfigurationID Number) ON COMMIT PRESERVE ROWS;
CREATE GLOBAL TEMPORARY TABLE t_DeviceConfigCategoryIDs (DeviceConfigCategoryID NUMBER NOT NULL, NewDeviceConfigCategoryID Number) ON COMMIT PRESERVE ROWS;

/* @start-block */
DECLARE
    v_maxDeviceConfigurationId NUMBER;
    v_maxDeviceConfigCatId NUMBER;
    v_maxDeviceConfigCatItemId NUMBER;
    v_maxDeviceConfigDeviceTypeId NUMBER;
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

    /*  Create a new rfnChannelConfiguration DeviceConfigCategory from the existing one */
    INSERT INTO DeviceConfigCategory 
    SELECT
        DCCID.NewDeviceConfigCategoryId,
        DCC.CategoryType,
        DCC.Name || ' (auto-created - DELIVERED_KVAR)',
        CASE
            WHEN DCC.Description IS NULL THEN 'Auto created to split DELIVERED_KVAR from KVAR during 6.7.1 upgrade'
            ELSE DCC.Description || CHR(13) || CHR(10) || 'Auto created to split DELIVERED_KVAR from KVAR during 6.7.1 upgrade'
        END
    FROM DeviceConfigCategory DCC
    JOIN t_DeviceConfigCategoryIDs DCCID ON DCC.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId;

    INSERT INTO DeviceConfigCategoryItem
    SELECT
        v_maxDeviceConfigCatItemId + ROW_NUMBER() OVER(ORDER BY DCCI.DeviceConfigCategoryItemId ASC),
        DCCID.NewDeviceConfigCategoryId,
        DCCI.ItemName,
        DCCI.ItemValue
    FROM DeviceConfigCategoryItem DCCI
    JOIN t_DeviceConfigCategoryIDs DCCID ON DCCI.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId;

    /*  Create new device configs */
    INSERT INTO DeviceConfiguration 
    SELECT
        DCID.NewDeviceConfigurationID,
        DC.Name || ' (auto-created - DELIVERED_KVAR)',
        CASE
            WHEN DC.Description IS NULL THEN 'Auto created to split DELIVERED_KVAR from KVAR during 6.7.1 upgrade'
            ELSE DC.Description || CHR(13) || CHR(10) || 'Auto created to split DELIVERED_KVAR from KVAR during 6.7.1 upgrade'
        END
    FROM DeviceConfiguration DC
    JOIN t_DeviceConfigurationIDs DCID ON DC.DeviceConfigurationID = DCID.DeviceConfigurationId;

    /*  Insert copies of all device config categories into the new device configs */
    INSERT INTO DeviceConfigCategoryMap
    SELECT
        DCID.NewDeviceConfigurationId,
        DCCM.DeviceConfigCategoryId
    FROM DeviceConfigCategoryMap DCCM
    JOIN t_DeviceConfigurationIDs DCID ON DCCM.DeviceConfigurationId = DCID.DeviceConfigurationId;

    /*  Update the new device configs to use the new categories */
    UPDATE DeviceConfigCategoryMap DCCMO
    SET DCCMO.DeviceConfigCategoryId = (
        SELECT DCCID.NewDeviceConfigCategoryId
        FROM DeviceConfigCategoryMap DCCM
        JOIN t_DeviceConfigurationIDs DCID   ON DCCM.DeviceConfigurationId = DCID.NewDeviceConfigurationId
        JOIN t_DeviceConfigCategoryIDs DCCID ON DCCM.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        WHERE DCCMO.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        AND DCCMO.DeviceConfigurationId=DCID.NewDeviceConfigurationId)
    WHERE EXISTS (
        SELECT *
        FROM DeviceConfigCategoryMap DCCM
        JOIN t_DeviceConfigurationIDs DCID   ON DCCM.DeviceConfigurationId = DCID.NewDeviceConfigurationId
        JOIN t_DeviceConfigCategoryIDs DCCID ON DCCM.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        WHERE DCCMO.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        AND DCCMO.DeviceConfigurationId=DCID.NewDeviceConfigurationId);
                
    /*  Insert the new config RFN's into the new device config typelists */
    INSERT INTO DeviceConfigDeviceTypes
    SELECT
        v_maxDeviceConfigDeviceTypeId + ROW_NUMBER() OVER(ORDER BY DCDT.DeviceConfigDeviceTypeId ASC),
        DCID.NewDeviceConfigurationId,
        DCDT.PaoType
    FROM DeviceConfigDeviceTypes DCDT
    JOIN t_DeviceConfigurationIDs DCID ON DCDT.DeviceConfigurationId=DCID.DeviceConfigurationId
    WHERE DCDT.PaoType IN ('RFN-430A3R', 'RFN-530S4X');

    /*  Delete the new config RFN's from the old device config typelists */
    DELETE FROM DeviceConfigDeviceTypes DCDT
    WHERE DCDT.DeviceConfigurationId IN (
        SELECT DCTS.DeviceConfigurationID
        FROM t_DeviceConfigsToSplit DCTS )
    AND DCDT.PaoType IN ('RFN-430A3R', 'RFN-530S4X');

    /*  Point the the new config RFN's to the new configs */
    UPDATE DeviceConfigurationDeviceMap DCDMO
    SET DCDMO.DeviceConfigurationId = (
        SELECT DCID.NewDeviceConfigurationId
        FROM DeviceConfigurationDeviceMap DCDM
        JOIN t_DeviceConfigurationIDs DCID ON DCDM.DeviceConfigurationId = DCID.DeviceConfigurationId
        JOIN YukonPAObject Y               ON DCDM.DeviceId = Y.PAObjectID
        WHERE Y.Type IN ('RFN-430A3R', 'RFN-530S4X')
        AND DCDMO.DeviceConfigurationId = DCID.DeviceConfigurationId
        AND DCDMO.DeviceId = DCDM.DeviceId )
    WHERE EXISTS (
        SELECT *
        FROM DeviceConfigurationDeviceMap DCDM
        JOIN t_DeviceConfigurationIDs DCID ON DCDM.DeviceConfigurationId = DCID.DeviceConfigurationId
        JOIN YukonPAObject Y               ON DCDM.DeviceId = Y.PAObjectID
        WHERE Y.Type IN ('RFN-430A3R', 'RFN-530S4X')
        AND DCDMO.DeviceConfigurationId = DCID.DeviceConfigurationId
        AND DCDMO.DeviceId = DCDM.DeviceId );
END;
/
/* @end-block */

/* Update all rfnChannelConfiguration categories assigned to the new config RFN's to use DELIVERED_KVAR instead of KVAR */
UPDATE DeviceConfigCategoryItem DCCI
SET ItemValue='DELIVERED_KVAR'
WHERE DCCI.DeviceConfigCategoryId IN (
    SELECT DISTINCT DCC.DeviceConfigCategoryId 
    FROM deviceConfigCategory DCC
    JOIN DeviceConfigCategoryMap DCCM ON DCC.DeviceConfigCategoryId=DCCM.DeviceConfigCategoryId
    JOIN DeviceConfiguration DC       ON DCCM.DeviceConfigurationId=DC.DeviceConfigurationID
    JOIN DeviceConfigDeviceTypes DCDT ON DC.DeviceConfigurationID=DCDT.DeviceConfigurationId
    WHERE DCC.CategoryType = 'rfnChannelConfiguration'
    AND DCDT.PaoType IN ('RFN-430A3R', 'RFN-530S4X') )
AND DCCI.ItemName LIKE 'enabledChannels%attribute'
AND DCCI.ItemValue='KVAR';

TRUNCATE TABLE t_DeviceConfigsToSplit;
DROP TABLE t_DeviceConfigsToSplit;
TRUNCATE TABLE t_DeviceConfigurationIDs;
DROP TABLE t_DeviceConfigurationIDs;
TRUNCATE TABLE t_DeviceConfigCategoryIDs;
DROP TABLE t_DeviceConfigCategoryIDs;



/* Converting DEMAND to DELIVERED_DEMAND */

/*  Device List:
DEMAND -> DELIVERED_DEMAND
 RFN-410CL
 RFN-410FD
 RFN-410FL
 RFN-410FX
 RFN-420CD
 RFN-420CL
 RFN-420FD
 RFN-420FL
 RFN-420FRD
 RFN-420FRX
 RFN-420FX
 RFN-430A3D
 RFN-430A3K
 RFN-430A3T
 RFN-430A3R
 RFN-430KV
 RFN-510FL
 RFN-520FAX
 RFN-520FAXD
 RFN-520FRX
 RFN-520FRXD
 RFN-530FAX
 RFN-530FRX
 RFN-530S4X

DEMAND -> DEMAND
 RFN-430SL1
 RFN-430SL2
 RFN-430SL3
 RFN-430SL4
*/

/* Small update to BehaviorReportValue */
UPDATE BehaviorReportValue BRV
SET BRV.Value = 'DELIVERED_DEMAND'
WHERE BRV.BehaviorReportId IN (
    SELECT BR.BehaviorReportId 
    FROM BehaviorReport BR 
    JOIN YukonPAObject Y ON BR.DeviceID = Y.PAObjectID
    WHERE Y.Type IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                     'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                     'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                     'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X') )
AND BRV.Value = 'DEMAND';


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
    JOIN BehaviorValue BV       ON B.BehaviorId=BV.BehaviorId
    JOIN DeviceBehaviorMap DBM1 ON B.BehaviorId=DBM1.BehaviorId
    JOIN DeviceBehaviorMap DBM2 ON B.BehaviorId=DBM2.BehaviorId
    JOIN YukonPAObject Y1       ON DBM1.DeviceId=Y1.PAObjectID
    JOIN YukonPAObject Y2       ON DBM2.DeviceId=Y2.PAObjectID
    WHERE B.BehaviorType='DATA_STREAMING'
    AND BV.Value = 'DEMAND'
    AND Y1.Type = 'RFN-430A3K'
    AND Y2.Type IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                    'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                    'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                    'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X');

/* @start-block */
DECLARE 
    v_loopCounter NUMBER;
    v_BTSRowCount NUMBER;
    v_maxBehaviorId NUMBER;
    v_newBehaviorId NUMBER;
BEGIN

    SELECT COALESCE(MAX(BehaviorId), 0) INTO v_maxBehaviorId FROM Behavior;

    /*  Find any behaviors containing DEMAND that are shared by updating and static config devices  */
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
        JOIN YukonPAObject Y        ON DBM.DeviceId = Y.PAObjectID
        JOIN t_BehaviorsToSplit BTS ON DBM.BehaviorId = BTS.BehaviorId
        WHERE Y.Type IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                         'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                         'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                         'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')
        AND DBM.BehaviorId = BTS.BehaviorID )
    WHERE DBM.DeviceID IN (
        SELECT DBM.DeviceID
        FROM DeviceBehaviorMap DBM
        JOIN YukonPAObject Y        ON DBM.DeviceId=Y.PAObjectID
        JOIN t_BehaviorsToSplit BTS ON DBM.BehaviorId=BTS.BehaviorId
        WHERE Y.Type IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                         'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                         'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                         'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')
        AND DBM.BehaviorId = BTS.BehaviorID );
END;
/
/* @end-block */
TRUNCATE TABLE t_BehaviorsToSplit;
DROP TABLE t_BehaviorsToSplit;

/* Update all RFN Data Streaming Device Behaviors with appropriate device types to use DELIVERED_DEMAND instead of DEMAND */
UPDATE BehaviorValue BV
SET BV.Value='DELIVERED_DEMAND'
WHERE BV.BehaviorId IN (
    SELECT DISTINCT BV.BehaviorId
    FROM BehaviorValue BV
    JOIN Behavior B            ON BV.BehaviorId=B.BehaviorId
    JOIN DeviceBehaviorMap DBM ON B.BehaviorId=DBM.BehaviorId
    JOIN YukonPAObject Y       ON DBM.DeviceId=Y.PAObjectID
    WHERE B.BehaviorType='DATA_STREAMING'
    AND Y.Type IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                   'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                   'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                   'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X') )
AND BV.Value='DEMAND';


/* Splitting/Updating DeviceConfiguration */
/* Find all rfnChannelConfiguration DeviceConfigCategories that contain 'DEMAND' and are assigned to the channel-renaming device types and another type supporting DEMAND */
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
    FROM DeviceConfiguration DC
    JOIN DeviceConfigCategoryMap DCCM  ON DC.DeviceConfigurationID=DCCM.DeviceConfigurationId
    JOIN DeviceConfigCategory DCC      ON DCCM.DeviceConfigCategoryId=DCC.DeviceConfigCategoryId
    JOIN DeviceConfigCategoryItem DCCI ON DCC.DeviceConfigCategoryId=DCCI.DeviceConfigCategoryId
    JOIN DeviceConfigDeviceTypes DCDT1 ON DC.DeviceConfigurationID=DCDT1.DeviceConfigurationId
    JOIN DeviceConfigDeviceTypes DCDT2 ON DC.DeviceConfigurationID=DCDT2.DeviceConfigurationId
    WHERE DCC.CategoryType = 'rfnChannelConfiguration'
    AND DCCI.ItemName LIKE 'enabledChannels%attribute'
    AND DCCI.ItemValue = 'DEMAND'
    AND DCDT1.PaoType IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                          'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                          'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                          'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')
    AND DCDT2.PaoType IN ('RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4') );

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

    /*  Create a new rfnChannelConfiguration DeviceConfigCategory from the existing one */
    INSERT INTO DeviceConfigCategory 
    SELECT
        DCCID.NewDeviceConfigCategoryId,
        DCC.CategoryType,
        DCC.Name || ' (auto-created - DELIVERED_DEMAND)',
        CASE
            WHEN DCC.Description IS NULL THEN 'Auto created to split DELIVERED_DEMAND from DEMAND during 6.7.1 upgrade'
            ELSE DCC.Description || CHR(13) || CHR(10) || 'Auto created to split DELIVERED_DEMAND from DEMAND during 6.7.1 upgrade'
        END
    FROM DeviceConfigCategory DCC
    JOIN t_DeviceConfigCategoryIDs DCCID ON DCC.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId;

    INSERT INTO DeviceConfigCategoryItem
    SELECT
        v_maxDeviceConfigCatItemId + ROW_NUMBER() OVER(ORDER BY DCCI.DeviceConfigCategoryItemId ASC),
        DCCID.NewDeviceConfigCategoryId,
        DCCI.ItemName,
        DCCI.ItemValue
    FROM DeviceConfigCategoryItem DCCI
    JOIN t_DeviceConfigCategoryIDs DCCID ON DCCI.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId;

    /*  Create new device configs */
    INSERT INTO DeviceConfiguration 
    SELECT
        DCID.NewDeviceConfigurationID,
        DC.Name || ' (auto-created - DELIVERED_DEMAND)',
        CASE
            WHEN DC.Description IS NULL THEN 'Auto created to split DELIVERED_DEMAND from DEMAND during 6.7.1 upgrade'
            ELSE DC.Description || CHR(13) || CHR(10) || 'Auto created to split DELIVERED_DEMAND from DEMAND during 6.7.1 upgrade'
        END
    FROM DeviceConfiguration DC
    JOIN t_DeviceConfigurationIDs DCID ON DC.DeviceConfigurationID = DCID.DeviceConfigurationId;

    /*  Insert copies of all device config categories into the new device configs */
    INSERT INTO DeviceConfigCategoryMap
    SELECT
        DCID.NewDeviceConfigurationId,
        DCCM.DeviceConfigCategoryId
    FROM DeviceConfigCategoryMap DCCM
    JOIN t_DeviceConfigurationIDs DCID ON DCCM.DeviceConfigurationId = DCID.DeviceConfigurationId;

    /*  Update the new device configs to use the new categories */
    UPDATE DeviceConfigCategoryMap DCCMO
    SET DCCMO.DeviceConfigCategoryId = (
        SELECT DCCID.NewDeviceConfigCategoryId
        FROM DeviceConfigCategoryMap DCCM
        JOIN t_DeviceConfigurationIDs DCID   ON DCCM.DeviceConfigurationId = DCID.NewDeviceConfigurationId
        JOIN t_DeviceConfigCategoryIDs DCCID ON DCCM.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        WHERE DCCMO.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        AND DCCMO.DeviceConfigurationId=DCID.NewDeviceConfigurationId)
    WHERE EXISTS (
        SELECT *
        FROM DeviceConfigCategoryMap DCCM
        JOIN t_DeviceConfigurationIDs DCID   ON DCCM.DeviceConfigurationId = DCID.NewDeviceConfigurationId
        JOIN t_DeviceConfigCategoryIDs DCCID ON DCCM.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        WHERE DCCMO.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId 
        AND DCCMO.DeviceConfigurationId=DCID.NewDeviceConfigurationId);
                
    /*  Insert the new config RFN's into the new device config typelists */
    INSERT INTO DeviceConfigDeviceTypes
    SELECT
        v_maxDeviceConfigDeviceTypeId + ROW_NUMBER() OVER(ORDER BY DCDT.DeviceConfigDeviceTypeId ASC),
        DCID.NewDeviceConfigurationId,
        DCDT.PaoType
    FROM 
        DeviceConfigDeviceTypes DCDT
        JOIN t_DeviceConfigurationIDs DCID ON DCDT.DeviceConfigurationId=DCID.DeviceConfigurationId
    WHERE
        DCDT.PaoType IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                         'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                         'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                         'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X');

    /*  Delete the new config RFN's from the old device config typelists */
    DELETE FROM DeviceConfigDeviceTypes DCDT
    WHERE DCDT.DeviceConfigurationId IN (
        SELECT DCTS.DeviceConfigurationID
        FROM t_DeviceConfigsToSplit DCTS )
    AND DCDT.PaoType IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                         'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                         'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                         'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X');

    /*  Point the new config RFN's to the new configs */
    UPDATE DeviceConfigurationDeviceMap DCDMO
    SET DCDMO.DeviceConfigurationId = (
        SELECT DCID.NewDeviceConfigurationId
        FROM DeviceConfigurationDeviceMap DCDM
        JOIN t_DeviceConfigurationIDs DCID ON DCDM.DeviceConfigurationId = DCID.DeviceConfigurationId
        JOIN YukonPAObject Y               ON DCDM.DeviceId = Y.PAObjectID
        WHERE Y.Type IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                         'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                         'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                         'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')
        AND DCDMO.DeviceConfigurationId = DCID.DeviceConfigurationId
        AND DCDMO.DeviceId = DCDM.DeviceId )
    WHERE EXISTS (
        SELECT *
        FROM DeviceConfigurationDeviceMap DCDM
        JOIN t_DeviceConfigurationIDs DCID ON DCDM.DeviceConfigurationId = DCID.DeviceConfigurationId
        JOIN YukonPAObject Y               ON DCDM.DeviceId = Y.PAObjectID
        WHERE Y.Type IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                         'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                         'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                         'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')
        AND DCDMO.DeviceConfigurationId = DCID.DeviceConfigurationId
        AND DCDMO.DeviceId = DCDM.DeviceId );
END;
/
/* @end-block */

/* Update all rfnChannelConfiguration categories assigned to the new config RFN's to use DELIVERED_DEMAND instead of DEMAND */
UPDATE DeviceConfigCategoryItem DCCI
SET ItemValue='DELIVERED_DEMAND'
WHERE DCCI.DeviceConfigCategoryId IN (
    SELECT DISTINCT DCC.DeviceConfigCategoryId 
    FROM DeviceConfigCategory DCC
    JOIN DeviceConfigCategoryMap DCCM ON DCC.DeviceConfigCategoryId=DCCM.DeviceConfigCategoryId
    JOIN DeviceConfiguration DC       ON DCCM.DeviceConfigurationId=DC.DeviceConfigurationID
    JOIN DeviceConfigDeviceTypes DCDT ON DC.DeviceConfigurationID=DCDT.DeviceConfigurationId
    WHERE DCC.CategoryType='rfnChannelConfiguration'
    AND DCDT.PaoType IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                         'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                         'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                         'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X') )
AND DCCI.ItemName LIKE 'enabledChannels%attribute'
AND DCCI.ItemValue = 'DEMAND';

TRUNCATE TABLE t_DeviceConfigsToSplit;
DROP TABLE t_DeviceConfigsToSplit;
TRUNCATE TABLE t_DeviceConfigurationIDs;
DROP TABLE t_DeviceConfigurationIDs;
TRUNCATE TABLE t_DeviceConfigCategoryIDs;
DROP TABLE t_DeviceConfigCategoryIDs;
/* End YUK-16782 */

/* Start YUK-16969 */
CREATE INDEX Indx_RPV_PointID ON RecentPointValue (PointID ASC);
/* End YUK-16969 */

/* Start YUK-17038 */
CREATE GLOBAL TEMPORARY TABLE t_PointIDsToDelete (PointID NUMBER NOT NULL) ON COMMIT PRESERVE ROWS;
/* @start-block */
BEGIN
    INSERT INTO t_PointIDsToDelete
    SELECT PointId 
    FROM Point P 
    JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' 
    AND (  (PointOffset=6 AND YP.Type IN ('RFN-410fL', 'RFN-420fL', 'RFN-510fL')) 
        OR (PointOffset=5 AND YP.Type IN ('RFN-420fL', 'RFN-510fL'))
        );

    DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (SELECT PointID FROM t_PointIDsToDelete);
    DELETE FROM POINTUNIT            WHERE POINTID IN (SELECT PointID FROM t_PointIDsToDelete);
    DELETE FROM PointAlarming        WHERE POINTID IN (SELECT PointID FROM t_PointIDsToDelete);
    DELETE FROM DISPLAY2WAYDATA      WHERE POINTID IN (SELECT PointID FROM t_PointIDsToDelete);
    DELETE FROM POINTANALOG          WHERE POINTID IN (SELECT PointID FROM t_PointIDsToDelete);
    DELETE FROM GRAPHDATASERIES      WHERE POINTID IN (SELECT PointID FROM t_PointIDsToDelete);
    DELETE FROM POINT                WHERE POINTID IN (SELECT PointID FROM t_PointIDsToDelete);
END;
/
/* @end-block */
TRUNCATE TABLE t_PointIDsToDelete;
DROP TABLE t_PointIDsToDelete;
/* End YUK-17038 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '27-JUL-2017', 'Latest Update', 1, SYSDATE);*/
