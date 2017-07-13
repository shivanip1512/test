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
/* End YUK-16606 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '27-JUL-2017', 'Latest Update', 1, GETDATE());*/