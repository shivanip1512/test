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
GO

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
  
/* Start YUK-10709 */
INSERT INTO BillingFileFormats VALUES (-27, 'CMEP', 1);

CREATE TABLE RPHServiceTag  (
    RPHServiceTagId         NUMERIC               NOT NULL,
    ChangeId                NUMERIC               NOT NULL,
    ServiceName             VARCHAR(150)          NOT NULL,
    ServiceNameRef          VARCHAR(150)          NULL,
    CONSTRAINT PK_RPHServiceTag PRIMARY KEY (RPHServiceTagId)
);
GO
/* End YUK-10709 */

/* Start YUK-10724 */
UPDATE ThermostatEventHistory 
SET ManualCoolTemp = NULL 
WHERE ManualMode = 'HEAT' AND ManualHeatTemp IS NOT NULL;

UPDATE ThermostatEventHistory 
SET ManualHeatTemp = NULL 
WHERE ManualMode = 'COOL' AND ManualCoolTemp IS NOT NULL;
/* End YUK-10724 */

/* Start YUK-10725 */
CREATE TABLE ExtToYukonMessageIdMapping  (
   ExternalMessageId    NUMERIC                          NOT NULL,
   YukonMessageId       NUMERIC                          NOT NULL,
   UserId               NUMERIC                          NOT NULL,
   MessageEndTime       DATETIME                         NOT NULL,
   CONSTRAINT PK_ExtToYukonMessageIdMapping PRIMARY KEY (ExternalMessageId)
);
GO

ALTER TABLE ExtToYukonMessageIdMapping
    ADD CONSTRAINT FK_EToYMIMap_User FOREIGN KEY (UserId)
        REFERENCES YukonUser (UserId)
            ON DELETE CASCADE;
GO
            
INSERT INTO YukonServices VALUES(19,'YukonMessageListener','classpath:com/cannontech/services/yukonMessageListener/yukonMessageListener.xml', 'ServiceManager');
/* End YUK-10725 */

/* Start YUK-10730 */
INSERT INTO YukonRoleProperty VALUES(-1706,-8,'Enable Captchas','true','This feature turns on and off all the captchas in the system');
/* End YUK-10730 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 