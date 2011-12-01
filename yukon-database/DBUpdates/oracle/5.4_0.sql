/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10174 */ 
INSERT INTO YukonServices 
VALUES (18, 'CymDISTMessageListener', 
        'classpath:com/cannontech/services/cymDISTService/cymDISTServiceContext.xml', 
        'ServiceManager');
/* End YUK-10174 */
        
/* Start YUK-10175 */
/***** Dynamic Cap Control Table Cascades *****/
ALTER TABLE DynamicCCArea
    DROP CONSTRAINT FK_ccarea_Dynccarea;
    
ALTER TABLE DynamicCCFeeder
    DROP CONSTRAINT FK_CCFEED_DYFEED;
    
ALTER TABLE DynamicCCCapBank
    DROP CONSTRAINT FK_CPBNK_DYNCPBNK;
    
ALTER TABLE DynamicCCSpecialArea
    DROP CONSTRAINT FK_DynCCSpecA_CapContSpecA;
    
ALTER TABLE DynamicCCSubstation
    DROP CONSTRAINT FK_DYNAMICC_REFERENCE_CAPCONTR;

ALTER TABLE DynamicCCSubstationBus
    DROP CONSTRAINT FK_CCSUBBS_DYSUBBS;

ALTER TABLE DynamicCCOperationStatistics
    DROP CONSTRAINT FK_DYNAMICC_REFERENCE_YUKONPAO;

ALTER TABLE DynamicDeviceScanData 
    DROP CONSTRAINT SYS_C0015139;
    
ALTER TABLE CCSubAreaAssignment
    DROP CONSTRAINT FK_CCSUBARE_CAPSUBAREAASSGN;
    
ALTER TABLE CCSubAreaAssignment
    DROP CONSTRAINT FK_CCSUBARE_REFERENCE_CAPCONTR;
    
ALTER TABLE CCSubstationSubBusList
    DROP CONSTRAINT FK_CCSUBSTA_CAPCONTR;
    
ALTER TABLE CCSubstationSubBusList
    DROP CONSTRAINT FK_CCSUBSTA_REFERENCE_CAPCONTR;
    
ALTER TABLE CCFeederSubAssignment
    DROP CONSTRAINT FK_CCFEED_CCFASS;
    
ALTER TABLE CCFeederSubAssignment
    DROP CONSTRAINT FK_CCSUB_CCFEED;
    
ALTER TABLE CCFeederBankList
    DROP CONSTRAINT FK_CB_CCFEEDLST;
    
ALTER TABLE CCFeederBankList
    DROP CONSTRAINT FK_CCFEED_CCBNK;
    
ALTER TABLE DynamicCCArea
   ADD CONSTRAINT FK_DynCCArea_CCArea FOREIGN KEY (AreaId)
      REFERENCES CapControlArea (AreaId)
      ON DELETE CASCADE;

ALTER TABLE DynamicCCFeeder
   ADD CONSTRAINT FK_DynCCFeeder_CCFeeder FOREIGN KEY (FeederId)
      REFERENCES CapControlFeeder (FeederId)
      ON DELETE CASCADE;
	
ALTER TABLE DynamicCCCapBank
   ADD CONSTRAINT FK_DynCCCapBank_CapBank FOREIGN KEY (CapBankId)
      REFERENCES CAPBANK (DeviceId)
      ON DELETE CASCADE;

ALTER TABLE DynamicCCSpecialArea
   ADD CONSTRAINT FK_DynCCSpecial_CCSpecialArea FOREIGN KEY (AreaId)
      REFERENCES CAPCONTROLSPECIALAREA (AreaId)
      ON DELETE CASCADE;

ALTER TABLE DynamicCCSubstation
   ADD CONSTRAINT FK_DynCCSubst_CCSubst FOREIGN KEY (SubStationId)
      REFERENCES CAPCONTROLSUBSTATION (SubstationId)
      ON DELETE CASCADE;

ALTER TABLE DynamicCCSubstationBus
   ADD CONSTRAINT FK_DynCCSubBus_CCSubBus FOREIGN KEY (SubstationBusId)
      REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusId)
      ON DELETE CASCADE;

/***** Other Cascades *****/
ALTER TABLE DynamicCCOperationStatistics
    ADD CONSTRAINT FK_DynCCOpStats_YukonPAObject FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
        ON DELETE CASCADE;
	        
ALTER TABLE DynamicDeviceScanData 
    ADD CONSTRAINT FK_DynDeviceScanData FOREIGN KEY (DeviceId) 
        REFERENCES Device (DeviceId) 
        ON DELETE CASCADE; 

/***** CCSubAreaAssignment *****/
ALTER TABLE CCSubAreaAssignment
    ADD CONSTRAINT FK_CCSubAreaAssignment_CCArea FOREIGN KEY (AreaId)
        REFERENCES CAPCONTROLAREA (AreaId)
        ON DELETE CASCADE;
	        
ALTER TABLE CCSubAreaAssignment
    ADD CONSTRAINT FK_CCSubAreaAssign_CCSubst FOREIGN KEY (SubstationBusId)
        REFERENCES CAPCONTROLSUBSTATION (SubstationId)
        ON DELETE CASCADE;

/***** CCSubstationSubBusList *****/
ALTER TABLE CCSubstationSubBusList
    ADD CONSTRAINT FK_CCSubstSubBusList_CCSub FOREIGN KEY (SubstationId)
        REFERENCES CAPCONTROLSUBSTATION (SubstationId)
        ON DELETE CASCADE;
	
ALTER TABLE CCSubstationSubBusList
    ADD CONSTRAINT FK_CCSubstSubBusList_CCSubBus FOREIGN KEY (SubstationBusId)
        REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusId)
        ON DELETE CASCADE;

/***** CCFeederSubAssignment *****/
ALTER TABLE CCFeederSubAssignment
    ADD CONSTRAINT FK_CCFeederSubAssign_CCSubBus FOREIGN KEY (SubStationBusId)
        REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusId)
        ON DELETE CASCADE;

ALTER TABLE CCFeederSubAssignment
    ADD CONSTRAINT FK_CCFeederSubAssign_CCFeeder FOREIGN KEY (FeederId)
        REFERENCES CapControlFeeder (FeederId)
        ON DELETE CASCADE;

/***** CCFeederBankList *****/
ALTER TABLE CCFeederBankList
    ADD CONSTRAINT FK_CCFeederBankList_CCFeeder FOREIGN KEY (FeederId)
        REFERENCES CapControlFeeder (FeederId)
        ON DELETE CASCADE;

ALTER TABLE CCFeederBankList
    ADD CONSTRAINT FK_CCFeederBankList_CapBank FOREIGN KEY (DeviceId)
        REFERENCES CAPBANK (DeviceId)
        ON DELETE CASCADE;
/*  End YUK-10175  */
        
/* Start YUK-10249 */
DELETE FROM DeviceTypeCommand WHERE DeviceType = 'MCT-410iLE';
DELETE FROM DeviceTypeCommand WHERE DeviceType = 'MCT-410 kWh Only';
DELETE FROM DeviceTypeCommand WHERE (DeviceCommandId = -393 
                                     AND CommandId   = -108 
                                     AND DeviceType  = 'MCT-410IL');
/*  End YUK-10249  */

/* Start YUK-10463 */
INSERT INTO FDRInterface       VALUES (29, 'VALMETMULTI', 'Send,Send for control,Receive,Receive for control,Receive for Analog Output', 't' );
INSERT INTO FDRInterfaceOption VALUES (29, 'Point', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES (29, 'Destination/Source', 2, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES (29, 'Port', 3, 'Text', '(none)');
/*  End YUK-10463  */

/* Start YUK-10254 */
create index Indx_RwPtHisTstPtId on RAWPOINTHISTORY (
   TIMESTAMP ASC,
   POINTID ASC
);
/*  End YUK-10254  */

/* Start YUK-10433 */
INSERT INTO StateGroup VALUES( -16, 'Event Status', 'Status');
INSERT INTO State VALUES( -16, 0, 'Cleared', 0, 6, 0);
INSERT INTO State VALUES( -16, 1, 'Active',  1, 6, 0);
/*  End YUK-10433  */

/* Start YUK-10479 */
INSERT INTO YukonRoleProperty VALUES (-21312,-213,'Manage FDR Translations','false','Controls access to FDR Translation Manager bulk operation.');
/*  End YUK-10479  */

/* Start YUK-10466 */
INSERT INTO StateGroup VALUES(-17, 'Last Control', 'Status');
INSERT INTO State VALUES(-17, 0, 'Manual', 0, 6, 0);
INSERT INTO State VALUES(-17, 1, 'SCADA Override', 1, 6, 0);
INSERT INTO State VALUES(-17, 2, 'Fault Current', 2, 6, 0);
INSERT INTO State VALUES(-17, 3, 'Emergency Voltage', 3, 6, 0);
INSERT INTO State VALUES(-17, 4, 'Time ONOFF', 4, 6, 0);
INSERT INTO State VALUES(-17, 5, 'OVUV Control', 5, 6, 0);
INSERT INTO State VALUES(-17, 6, 'VAR', 7, 6, 0);
INSERT INTO State VALUES(-17, 7, 'Analog 1', 8, 6, 0);
INSERT INTO State VALUES(-17, 8, 'Analog 2', 1, 6, 0);
INSERT INTO State VALUES(-17, 9, 'Analog 3', 2, 6, 0);
INSERT INTO State VALUES(-17, 10, 'Analog 4', 3, 6, 0);
INSERT INTO State VALUES(-17, 11, 'Analog 5', 4, 6, 0);
INSERT INTO State VALUES(-17, 12, 'Analog 6', 5, 6, 0);
INSERT INTO State VALUES(-17, 13, 'Temp', 7, 6, 0);
INSERT INTO State VALUES(-17, 14, 'Remote', 8, 6, 0);
INSERT INTO State VALUES(-17, 15, 'Time', 1, 6, 0);
INSERT INTO State VALUES(-17, 16, 'N/A', 2, 6, 0);
/*  End YUK-10466  */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 