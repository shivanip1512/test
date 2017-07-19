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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '27-JUL-2017', 'Latest Update', 1, SYSDATE);*/
