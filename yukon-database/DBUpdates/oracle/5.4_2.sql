/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10669 */
UPDATE YukonListEntry
SET EntryOrder = (SELECT SubQuery.Sort_Order
                  FROM (SELECT EntryId, Row_Number() OVER (ORDER BY EntryText) as SORT_ORDER
                        FROM YukonListEntry
                        WHERE ListID = 1) SubQuery
                  WHERE SubQuery.EntryID = YukonListEntry.EntryID)
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
ALTER TABLE DynamicCCMonitorBankHistory
    ADD CONSTRAINT FK_DynCCMonBankHistory_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
ALTER TABLE DynamicCCMonitorPointResponse
    ADD CONSTRAINT FK_DynCCMonPointResp_CapBank FOREIGN KEY (BankId)
        REFERENCES CapBank (DeviceId)
            ON DELETE CASCADE;
ALTER TABLE DynamicCCMonitorPointResponse
    ADD CONSTRAINT FK_DynCCMonPointResp_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
/* End YUK-10659 */
            
/* Start YUK-10674 */
UPDATE Point 
SET PointName = 'Capacitor Bank State'
WHERE pointId IN (SELECT p.pointId 
                  FROM Point p, YukonPaobject yp
                  WHERE p.PAObjectID = yp.PAObjectID 
                    AND p.PointName = 'Bank Status'
                    AND yp.Type LIKE 'CBC%');
/* End YUK-10674 */

/* Start YUK-10709 */
INSERT INTO BillingFileFormats VALUES (-27, 'CMEP', 1);

CREATE TABLE RPHServiceTag  (
    RPHServiceTagId         NUMBER                 NOT NULL,
    ChangeId                NUMBER                 NOT NULL,
    ServiceName             VARCHAR2(150)          NOT NULL,
    ServiceNameRef          VARCHAR2(150)          NULL,
    CONSTRAINT PK_RPHServiceTag PRIMARY KEY (RPHServiceTagId)
);
/* End YUK-10709 */

/* Start YUK-10724 */
UPDATE ThermostatEventHistory 
SET ManualCoolTemp = NULL 
WHERE ManualMode = 'HEAT' AND ManualHeatTemp IS NOT NULL;

UPDATE ThermostatEventHistory 
SET ManualHeatTemp = NULL 
WHERE ManualMode = 'COOL' AND ManualCoolTemp IS NOT NULL;
/* End YUK-10724 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 