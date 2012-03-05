/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10669 */
UPDATE YukonListEntry
SET EntryOrder = SubQuery.Sort_Order
FROM (SELECT EntryId, Row_Number() OVER (ORDER BY EntryText) as SORT_ORDER
      FROM YukonListEntry
      WHERE ListID = 1) SubQuery
JOIN YukonListEntry ON SubQuery.EntryID = YukonListEntry.EntryID
WHERE ListID = 1;
/* End YUK-10669 */

/* Start YUK-10659 */
ALTER TABLE DynamicCCMonitorBankHistory
    DROP CONSTRAINT FK_DYN_CCMONBNKHIST_BNKID;
ALTER TABLE DynamicCCMonitorBankHistory
    DROP CONSTRAINT FK_DYN_CCMONBNKHIST_PTID;
ALTER TABLE DynamicCCMonitorPointResponse
    DROP CONSTRAINT FK_DYN_CCMONPTRSP_BNKID;
ALTER TABLE DynamicCCMonitorPointResponse
    DROP CONSTRAINT FK_DYN_CCMONPTRSP_PTID;

ALTER TABLE DynamicCCMonitorBankHistory
    ADD CONSTRAINT FK_DynCCMonBankHistory_CapBank FOREIGN KEY (BankId)
        REFERENCES CapBank (DeviceId)
            ON DELETE CASCADE;
GO
ALTER TABLE DynamicCCMonitorBankHistory
    ADD CONSTRAINT FK_DynCCMonBankHistory_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
GO
ALTER TABLE DynamicCCMonitorPointResponse
    ADD CONSTRAINT FK_DynCCMonPointResp_CapBank FOREIGN KEY (BankId)
        REFERENCES CapBank (DeviceId)
            ON DELETE CASCADE;
GO
ALTER TABLE DynamicCCMonitorPointResponse
    ADD CONSTRAINT FK_DynCCMonPointResp_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
GO
/* End YUK-10659 */

/* Start YUK-10674 */
UPDATE Point 
SET PointName = 'Capacitor Bank State'
FROM Point p, YukonPaobject yp
WHERE p.PAObjectID = yp.PAObjectID 
  AND yp.Type LIKE 'CBC%'
  AND p.PointName = 'Bank Status';
/* End YUK-10674 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 