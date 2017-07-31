/******************************************/
/**** SQL Server DBupdates             ****/
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
GO
/* End YUK-16542 */

/* Start YUK-16894 */
ALTER TABLE ControlEvent ADD ProgramId NUMERIC NOT NULL DEFAULT 0;
GO

ALTER TABLE ControlEvent
   ADD CONSTRAINT FK_CONTROLEVENT_YUKONPAOBJECT FOREIGN KEY (ProgramId)
      REFERENCES YukonPAObject (PAObjectID);
GO

/* @start-block */
MERGE INTO ControlEvent CE
USING (
    SELECT DeviceId, LMGroupId
    FROM LMHardwareControlGroup lmhcg 
        JOIN LMProgramWebPublishing lmpwb ON lmpwb.ProgramID = lmhcg.ProgramID
    WHERE lmhcg.ControlEntryId IN ((SELECT MAX(ControlEntryId) FROM LMHardwareControlGroup WHERE LMGroupId != -9999 
        GROUP BY LMGroupId)) 
    ) DLG
    ON CE.GroupId = DLG.LMGroupId
    WHEN MATCHED THEN
    UPDATE
    SET ProgramId = DLG.DeviceId;
GO
/* @end-block */
/* End YUK-16894 */

/* Start YUK-16606 */
ALTER TABLE UserPaoPermission DROP CONSTRAINT FK_USERPAOP_REF_YUKPA_YUKONPAO;
GO

ALTER TABLE UserPaoPermission
   ADD CONSTRAINT FK_USERPAOP_REF_YUKPA_YUKONPAO FOREIGN KEY (PaoID)
      REFERENCES YukonPAObject (PAObjectID)
      ON DELETE CASCADE;
GO

ALTER TABLE GroupPaoPermission DROP CONSTRAINT FK_GroupPaoPerm_PAO;
GO

ALTER TABLE GroupPaoPermission
   ADD CONSTRAINT FK_GroupPaoPerm_PAO FOREIGN KEY (PaoId)
      REFERENCES YukonPAObject (PAObjectID)
      ON DELETE CASCADE;
GO
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
                           GETDATE()
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

/* Start YUK-16963 */

/* If we run 6.7.0 updates immediately prior to these updates, the temp tables will still exist and must be dropped. */
/* @error ignore-begin */
DROP TABLE #DeviceConfigsToSplit;
DROP TABLE #DeviceConfigCategoryIDs;
DROP TABLE #DeviceConfigurationIDs;
GO
/* @error ignore-end */

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
UPDATE BehaviorValue
SET 
    Value='DELIVERED_KVAR'
FROM 
    BehaviorValue BV 
    JOIN (
        SELECT DISTINCT
            BV.BehaviorId
        FROM BehaviorValue BV
            JOIN Behavior B ON BV.BehaviorId=B.BehaviorId
            JOIN DeviceBehaviorMap DBM ON B.BehaviorId=DBM.BehaviorId
            JOIN YukonPAObject Y ON DBM.DeviceId=Y.PAObjectID
        WHERE 
            B.BehaviorType='DATA_STREAMING'
            AND Y.Type IN ('RFN-430A3R','RFN-530S4X')
        ) RFNDS ON BV.BehaviorId=RFNDS.BehaviorId
WHERE 
    BV.Value='KVAR';


 /* Splitting/Updating DeviceConfiguration */
 /* @start-block */
BEGIN
    DECLARE
        @maxDeviceConfigurationId       NUMERIC = (SELECT MAX(DeviceConfigurationId) FROM DeviceConfiguration),
        @maxDeviceConfigCategoryId      NUMERIC = (SELECT MAX(DeviceConfigCategoryId) FROM DeviceConfigCategory),
        @maxDeviceConfigCategoryItemId  NUMERIC = (SELECT MAX(DeviceConfigCategoryItemId) FROM DeviceConfigCategoryItem),
        @maxDeviceConfigDeviceTypeId    NUMERIC = (SELECT MAX(DeviceConfigDeviceTypeId) FROM DeviceConfigDeviceTypes);

    /* Find all rfnChannelConfiguration DeviceConfigCategories that contain 'KVAR' and are assigned to devices that need to stay as KVAR and devices that need to move to DELIVERED_KVAR */
    SELECT 
        DISTINCT DC.DeviceConfigurationID, 
        DCC.DeviceConfigCategoryId
    INTO #DeviceConfigsToSplit
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
        AND DCCI.ItemValue='KVAR'
        AND DCDT1.PaoType IN ('RFN-430A3R', 'RFN-530S4X')
        AND DCDT2.PaoType IN ('RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')

    /* Calculate the new Device Config IDs */
    SELECT 
        DCID.DeviceConfigurationID, 
        @maxDeviceConfigurationId + ROW_NUMBER() OVER(ORDER BY DeviceConfigurationID ASC) AS NewDeviceConfigurationID
    INTO #DeviceConfigurationIDs
    FROM (SELECT DISTINCT DeviceConfigurationID FROM #DeviceConfigsToSplit) DCID

    /* Calculate the new Device Config Category IDs */
    SELECT 
        DeviceConfigCategoryID, 
        @maxDeviceConfigCategoryId + ROW_NUMBER() OVER(ORDER BY DeviceConfigCategoryID ASC) AS NewDeviceConfigCategoryID
    INTO #DeviceConfigCategoryIDs
    FROM (SELECT DISTINCT DeviceConfigCategoryID FROM #DeviceConfigsToSplit) DCID

    /*  Create a new RFN-430SL rfnChannelConfiguration DeviceConfigCategory from the existing one */
    INSERT INTO DeviceConfigCategory 
        SELECT
            DCCID.NewDeviceConfigCategoryId,
            DCC.CategoryType,
            DCC.Name + ' (RFN DELIVERED_KVAR)',
            CASE
                WHEN DCC.Description IS NULL THEN 'Auto created for select RFN meters during 6.7.1 upgrade'
                ELSE DCC.Description + char(13) + char(10) + 'Auto created for select RFN meters during 6.7.1 upgrade'
            END
        FROM DeviceConfigCategory DCC
            JOIN #DeviceConfigCategoryIDs DCCID ON DCC.DeviceConfigCategoryId=DCCID.DeviceConfigCategoryId

    INSERT INTO DeviceConfigCategoryItem
        SELECT
            @maxDeviceConfigCategoryItemId + ROW_NUMBER() OVER(ORDER BY DCCI.DeviceConfigCategoryItemId ASC),
            DCCID.NewDeviceConfigCategoryId,
            DCCI.ItemName,
            DCCI.ItemValue
        FROM 
            DeviceConfigCategoryItem DCCI
            JOIN #DeviceConfigCategoryIDs DCCID ON DCCI.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId

    /*  Create new device configs for the proper meters */
    INSERT INTO DeviceConfiguration 
        SELECT
            DCID.NewDeviceConfigurationID,
            DC.Name + ' (auto-created)',
            CASE
                WHEN DC.Description IS NULL THEN 'Auto created for select RFN meters during 6.7.1 upgrade'
                ELSE DC.Description + char(13) + char(10) + 'Auto created for select RFN meters during 6.7.1 upgrade'
            END
        FROM 
            DeviceConfiguration DC
            JOIN #DeviceConfigurationIDs DCID ON DC.DeviceConfigurationID = DCID.DeviceConfigurationId

    /*  Insert copies of all device config categories into the new device configs */
    INSERT INTO DeviceConfigCategoryMap
        SELECT
            DCID.NewDeviceConfigurationId,
            DCCM.DeviceConfigCategoryId
        FROM 
            DeviceConfigCategoryMap DCCM
            JOIN #DeviceConfigurationIDs DCID ON DCCM.DeviceConfigurationId = DCID.DeviceConfigurationId

    /*  Update the new device configs to use the new categories */
    UPDATE DeviceConfigCategoryMap
    SET DeviceConfigCategoryId = DCCID.NewDeviceConfigCategoryId
    FROM 
        DeviceConfigCategoryMap DCCM
        JOIN #DeviceConfigurationIDs DCID ON DCCM.DeviceConfigurationId = DCID.NewDeviceConfigurationId
        JOIN #DeviceConfigCategoryIDs DCCID ON DCCM.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId
                
    /*  Insert the new config RFN's into the new device config typelists */
    INSERT INTO DeviceConfigDeviceTypes
        SELECT
            @maxDeviceConfigDeviceTypeId + ROW_NUMBER() OVER(ORDER BY DCDT.DeviceConfigDeviceTypeId ASC),
            DCID.NewDeviceConfigurationId,
            DCDT.PaoType
        FROM 
            DeviceConfigDeviceTypes DCDT
            JOIN #DeviceConfigurationIDs DCID ON DCDT.DeviceConfigurationId=DCID.DeviceConfigurationId
        WHERE
            DCDT.PaoType IN ('RFN-430A3R', 'RFN-530S4X')

    /*  Delete the new config RFN's from the old device config typelists */
    DELETE DCDT
    FROM
        DeviceConfigDeviceTypes DCDT
        JOIN #DeviceConfigsToSplit DCTS ON DCDT.DeviceConfigurationId = DCTS.DeviceConfigurationID
    WHERE 
        DCDT.PaoType IN ('RFN-430A3R', 'RFN-530S4X')

    /*  Point the new config RFN's to the new configs */
    UPDATE DeviceConfigurationDeviceMap
    SET DeviceConfigurationId = DCID.NewDeviceConfigurationId
    FROM DeviceConfigurationDeviceMap DCDM
            JOIN #DeviceConfigurationIDs DCID ON DCDM.DeviceConfigurationId = DCID.DeviceConfigurationId
            JOIN YukonPAObject Y ON DCDM.DeviceId = Y.PAObjectID
    WHERE
        Y.Type IN ('RFN-430A3R', 'RFN-530S4X');
END;
/* @end-block */


 /* Update all rfnChannelConfiguration categories assigned to RFN-430SL1+ to use DELIVERED_KVAR instead of KVAR */
UPDATE DCCI
SET ItemValue='DELIVERED_KVAR'
FROM 
    DeviceConfigCategoryItem DCCI
    JOIN (
        SELECT DISTINCT DCC.DeviceConfigCategoryId 
        FROM
            DeviceConfigCategory DCC
            JOIN DeviceConfigCategoryMap DCCM ON DCC.DeviceConfigCategoryId=DCCM.DeviceConfigCategoryId
            JOIN DeviceConfiguration DC ON DCCM.DeviceConfigurationId=DC.DeviceConfigurationID
            JOIN DeviceConfigDeviceTypes DCDT ON DC.DeviceConfigurationID=DCDT.DeviceConfigurationId
        WHERE
            DCC.CategoryType='rfnChannelConfiguration'
            AND DCDT.PaoType IN ('RFN-430A3R', 'RFN-530S4X')) RFNDC ON DCCI.DeviceConfigCategoryId=RFNDC.DeviceConfigCategoryId
WHERE
    DCCI.ItemName LIKE 'enabledChannels%attribute'
    AND DCCI.ItemValue='KVAR';

/* Drop temp tables to ensure that they don't interfere with the next set of attribute updates */
DROP TABLE #DeviceConfigsToSplit;
DROP TABLE #DeviceConfigCategoryIDs;
DROP TABLE #DeviceConfigurationIDs;
GO


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

/* Update all RFN Data Streaming Device Behaviors with appropriate meter types to use DELIVERED_DEMAND instead of DEMAND */

UPDATE BehaviorValue
SET 
    Value='DELIVERED_DEMAND'
FROM 
    BehaviorValue BV 
    JOIN (
        SELECT DISTINCT
            BV.BehaviorId
        FROM BehaviorValue BV
            JOIN Behavior B ON BV.BehaviorId=B.BehaviorId
            JOIN DeviceBehaviorMap DBM ON B.BehaviorId=DBM.BehaviorId
            JOIN YukonPAObject Y ON DBM.DeviceId=Y.PAObjectID
        WHERE 
            B.BehaviorType='DATA_STREAMING'
            AND Y.Type IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                           'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                           'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                           'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')
        ) RFNDS ON BV.BehaviorId=RFNDS.BehaviorId
WHERE 
    BV.Value='DEMAND';

 /* Splitting/Updating DeviceConfiguration */
 /* @start-block */
BEGIN
    DECLARE
        @maxDeviceConfigurationId       NUMERIC = (SELECT MAX(DeviceConfigurationId) FROM DeviceConfiguration),
        @maxDeviceConfigCategoryId      NUMERIC = (SELECT MAX(DeviceConfigCategoryId) FROM DeviceConfigCategory),
        @maxDeviceConfigCategoryItemId  NUMERIC = (SELECT MAX(DeviceConfigCategoryItemId) FROM DeviceConfigCategoryItem),
        @maxDeviceConfigDeviceTypeId    NUMERIC = (SELECT MAX(DeviceConfigDeviceTypeId) FROM DeviceConfigDeviceTypes);

    /* Find all rfnChannelConfiguration DeviceConfigCategories that contain 'DEMAND' and are assigned to devices that need to stay as DEMAND and devices that need to move to DELIVERED_DEMAND */
    SELECT 
        DISTINCT DC.DeviceConfigurationID, 
        DCC.DeviceConfigCategoryId
    INTO #DeviceConfigsToSplit
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
        AND DCCI.ItemValue='DEMAND'
        AND DCDT1.PaoType IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                              'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                              'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                              'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')
        AND DCDT2.PaoType IN ('RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')

    /* Calculate the new Device Config IDs */
    SELECT 
        DCID.DeviceConfigurationID, 
        @maxDeviceConfigurationId + ROW_NUMBER() OVER(ORDER BY DeviceConfigurationID ASC) AS NewDeviceConfigurationID
    INTO #DeviceConfigurationIDs
    FROM (SELECT DISTINCT DeviceConfigurationID FROM #DeviceConfigsToSplit) DCID

    /* Calculate the new Device Config Category IDs */
    SELECT 
        DeviceConfigCategoryID, 
        @maxDeviceConfigCategoryId + ROW_NUMBER() OVER(ORDER BY DeviceConfigCategoryID ASC) AS NewDeviceConfigCategoryID
    INTO #DeviceConfigCategoryIDs
    FROM (SELECT DISTINCT DeviceConfigCategoryID FROM #DeviceConfigsToSplit) DCID

    /*  Create a new RFN-430SL rfnChannelConfiguration DeviceConfigCategory from the existing one */
    INSERT INTO DeviceConfigCategory 
        SELECT
            DCCID.NewDeviceConfigCategoryId,
            DCC.CategoryType,
            DCC.Name + ' (RFN DELIVERED_DEMAND)',
            CASE
                WHEN DCC.Description IS NULL THEN 'Auto created for select RFN meters during 6.7.1 upgrade'
                ELSE DCC.Description + char(13) + char(10) + 'Auto created for select RFN meters during 6.7.1 upgrade'
            END
        FROM DeviceConfigCategory DCC
            JOIN #DeviceConfigCategoryIDs DCCID ON DCC.DeviceConfigCategoryId=DCCID.DeviceConfigCategoryId

    INSERT INTO DeviceConfigCategoryItem
        SELECT
            @maxDeviceConfigCategoryItemId + ROW_NUMBER() OVER(ORDER BY DCCI.DeviceConfigCategoryItemId ASC),
            DCCID.NewDeviceConfigCategoryId,
            DCCI.ItemName,
            DCCI.ItemValue
        FROM 
            DeviceConfigCategoryItem DCCI
            JOIN #DeviceConfigCategoryIDs DCCID ON DCCI.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId

    /*  Create new device configs for the proper meters */
    INSERT INTO DeviceConfiguration 
        SELECT
            DCID.NewDeviceConfigurationID,
            DC.Name + ' (auto-created)',
            CASE
                WHEN DC.Description IS NULL THEN 'Auto created for select RFN meters during 6.7.1 upgrade'
                ELSE DC.Description + char(13) + char(10) + 'Auto created for select RFN meters during 6.7.1 upgrade'
            END
        FROM 
            DeviceConfiguration DC
            JOIN #DeviceConfigurationIDs DCID ON DC.DeviceConfigurationID = DCID.DeviceConfigurationId

    /*  Insert copies of all device config categories into the new device configs */
    INSERT INTO DeviceConfigCategoryMap
        SELECT
            DCID.NewDeviceConfigurationId,
            DCCM.DeviceConfigCategoryId
        FROM 
            DeviceConfigCategoryMap DCCM
            JOIN #DeviceConfigurationIDs DCID ON DCCM.DeviceConfigurationId = DCID.DeviceConfigurationId

    /*  Update the new device configs to use the new categories */
    UPDATE DeviceConfigCategoryMap
    SET DeviceConfigCategoryId = DCCID.NewDeviceConfigCategoryId
    FROM 
        DeviceConfigCategoryMap DCCM
        JOIN #DeviceConfigurationIDs DCID ON DCCM.DeviceConfigurationId = DCID.NewDeviceConfigurationId
        JOIN #DeviceConfigCategoryIDs DCCID ON DCCM.DeviceConfigCategoryId = DCCID.DeviceConfigCategoryId
                
    /*  Insert the new config RFN's into the new device config typelists */
    INSERT INTO DeviceConfigDeviceTypes
        SELECT
            @maxDeviceConfigDeviceTypeId + ROW_NUMBER() OVER(ORDER BY DCDT.DeviceConfigDeviceTypeId ASC),
            DCID.NewDeviceConfigurationId,
            DCDT.PaoType
        FROM 
            DeviceConfigDeviceTypes DCDT
            JOIN #DeviceConfigurationIDs DCID ON DCDT.DeviceConfigurationId=DCID.DeviceConfigurationId
        WHERE
            DCDT.PaoType IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                             'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                             'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                             'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')

    /*  Delete the new config RFN's from the old device config typelists */
    DELETE DCDT
    FROM
        DeviceConfigDeviceTypes DCDT
        JOIN #DeviceConfigsToSplit DCTS ON DCDT.DeviceConfigurationId = DCTS.DeviceConfigurationID
    WHERE 
        DCDT.PaoType IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                         'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                         'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                         'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')

    /*  Point the new config RFN's to the new configs */
    UPDATE DeviceConfigurationDeviceMap
    SET DeviceConfigurationId = DCID.NewDeviceConfigurationId
    FROM DeviceConfigurationDeviceMap DCDM
            JOIN #DeviceConfigurationIDs DCID ON DCDM.DeviceConfigurationId = DCID.DeviceConfigurationId
            JOIN YukonPAObject Y ON DCDM.DeviceId = Y.PAObjectID
    WHERE
        Y.Type IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                   'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                   'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                   'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X');
END;
/* @end-block */

 /* Update all rfnChannelConfiguration categories assigned to RFN-430SL1+ to use DELIVERED_DEMAND instead of DEMAND */
UPDATE DCCI
SET ItemValue='DELIVERED_DEMAND'
FROM 
    DeviceConfigCategoryItem DCCI
    JOIN (
        SELECT DISTINCT DCC.DeviceConfigCategoryId 
        FROM
            DeviceConfigCategory DCC
            JOIN DeviceConfigCategoryMap DCCM ON DCC.DeviceConfigCategoryId=DCCM.DeviceConfigCategoryId
            JOIN DeviceConfiguration DC ON DCCM.DeviceConfigurationId=DC.DeviceConfigurationID
            JOIN DeviceConfigDeviceTypes DCDT ON DC.DeviceConfigurationID=DCDT.DeviceConfigurationId
        WHERE
            DCC.CategoryType='rfnChannelConfiguration'
            AND DCDT.PaoType IN ('RFN-410CL','RFN-410FD','RFN-410FL','RFN-410FX','RFN-420CD','RFN-420CL',
                                 'RFN-420FD','RFN-420FL','RFN-420FRD','RFN-420FRX','RFN-420FX','RFN-430A3D',
                                 'RFN-430A3K','RFN-430A3T','RFN-430A3R','RFN-430KV','RFN-510FL','RFN-520FAX',
                                 'RFN-520FAXD','RFN-520FRX','RFN-520FRXD','RFN-530FAX','RFN-530FRX','RFN-530S4X')) RFNDC ON DCCI.DeviceConfigCategoryId=RFNDC.DeviceConfigCategoryId
WHERE
    DCCI.ItemName LIKE 'enabledChannels%attribute'
    AND DCCI.ItemValue='DEMAND';
/* End YUK-16963 */

/* Start YUK-16969 */
CREATE INDEX Indx_RPV_PointID ON RecentPointValue (PointID ASC);
/* End YUK-16969 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '27-JUL-2017', 'Latest Update', 1, GETDATE());*/