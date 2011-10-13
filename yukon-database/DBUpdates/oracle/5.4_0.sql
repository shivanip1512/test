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
    
ALTER TABLE DYNAMICCCSPECIALAREA
    DROP CONSTRAINT FK_DynCCSpecA_CapContSpecA;
    
ALTER TABLE DYNAMICCCSUBSTATION
    DROP CONSTRAINT FK_DYNAMICC_REFERENCE_CAPCONTR;

ALTER TABLE DynamicCCSubstationBus
    DROP CONSTRAINT FK_CCSUBBS_DYSUBBS;

ALTER TABLE DynamicCCOperationStatistics
    DROP CONSTRAINT FK_DYNAMICC_REFERENCE_YUKONPAO;

ALTER TABLE DynamicDeviceScanData 
    DROP CONSTRAINT SYS_C0015139;
    
ALTER TABLE CCSUBAREAASSIGNMENT
    DROP CONSTRAINT FK_CCSUBARE_CAPSUBAREAASSGN;
    
ALTER TABLE CCSUBAREAASSIGNMENT
    DROP CONSTRAINT FK_CCSUBARE_REFERENCE_CAPCONTR;
    
ALTER TABLE CCSUBSTATIONSUBBUSLIST
    DROP CONSTRAINT FK_CCSUBSTA_CAPCONTR;
    
ALTER TABLE CCSUBSTATIONSUBBUSLIST
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
   ADD CONSTRAINT FK_DynCCArea_CCArea FOREIGN KEY (AreaID)
      REFERENCES CapControlArea (AreaID)
      ON DELETE CASCADE;

ALTER TABLE DynamicCCFeeder
   ADD CONSTRAINT FK_DynCCFeeder_CCFeeder FOREIGN KEY (FeederID)
      REFERENCES CapControlFeeder (FeederID)
      ON DELETE CASCADE;
	
ALTER TABLE DynamicCCCapBank
   ADD CONSTRAINT FK_DynCCCapBank_CapBank FOREIGN KEY (CapBankID)
      REFERENCES CAPBANK (DeviceId)
      ON DELETE CASCADE;

ALTER TABLE DYNAMICCCSPECIALAREA
   ADD CONSTRAINT FK_DynCCSpecial_CCSpecialArea FOREIGN KEY (AreaID)
      REFERENCES CAPCONTROLSPECIALAREA (AreaId)
      ON DELETE CASCADE;

ALTER TABLE DYNAMICCCSUBSTATION
   ADD CONSTRAINT FK_DynCCSubst_CCSubst FOREIGN KEY (SubStationID)
      REFERENCES CAPCONTROLSUBSTATION (SubstationID)
      ON DELETE CASCADE;

ALTER TABLE DynamicCCSubstationBus
   ADD CONSTRAINT FK_DynCCSubBus_CCSubBus FOREIGN KEY (SubstationBusID)
      REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusID)
      ON DELETE CASCADE;

/***** Other Cascades *****/
ALTER TABLE DynamicCCOperationStatistics
    ADD CONSTRAINT FK_DynCCOpStats_YukonPAObject FOREIGN KEY (PAObjectID)
        REFERENCES YukonPAObject (PAObjectID)
        ON DELETE CASCADE;
	        
ALTER TABLE DynamicDeviceScanData 
    ADD CONSTRAINT FK_DynDeviceScanData FOREIGN KEY (DeviceID) 
        REFERENCES Device (DeviceID) 
        ON DELETE CASCADE; 

/***** CCSUBAREAASSIGNMENT *****/
ALTER TABLE CCSUBAREAASSIGNMENT
    ADD CONSTRAINT FK_CCSubAreaAssignment_CCArea FOREIGN KEY (AreaID)
        REFERENCES CAPCONTROLAREA (AreaID)
        ON DELETE CASCADE;
	        
ALTER TABLE CCSUBAREAASSIGNMENT
    ADD CONSTRAINT FK_CCSubAreaAssign_CCSubst FOREIGN KEY (SubstationBusID)
        REFERENCES CAPCONTROLSUBSTATION (SubstationID)
        ON DELETE CASCADE;

/***** CCSUBSTATIONSUBBUSLIST *****/
ALTER TABLE CCSUBSTATIONSUBBUSLIST
    ADD CONSTRAINT FK_CCSubstSubBusList_CCSub FOREIGN KEY (SubstationID)
        REFERENCES CAPCONTROLSUBSTATION (SubstationID)
        ON DELETE CASCADE;
	
ALTER TABLE CCSUBSTATIONSUBBUSLIST
    ADD CONSTRAINT FK_CCSubstSubBusList_CCSubBus FOREIGN KEY (SubstationBusID)
        REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusID)
        ON DELETE CASCADE;

/***** CCFeederSubAssignment *****/
ALTER TABLE CCFeederSubAssignment
    ADD CONSTRAINT FK_CCFeederSubAssign_CCSubBus FOREIGN KEY (SubStationBusID)
        REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusID)
        ON DELETE CASCADE;

ALTER TABLE CCFeederSubAssignment
    ADD CONSTRAINT FK_CCFeederSubAssign_CCFeeder FOREIGN KEY (FeederID)
        REFERENCES CapControlFeeder (FeederID)
        ON DELETE CASCADE;

/***** CCFeederBankList *****/
ALTER TABLE CCFeederBankList
    ADD CONSTRAINT FK_CCFeederBankList_CCFeeder FOREIGN KEY (FeederID)
        REFERENCES CapControlFeeder (FeederID)
        ON DELETE CASCADE;

ALTER TABLE CCFeederBankList
    ADD CONSTRAINT FK_CCFeederBankList_CapBank FOREIGN KEY (DeviceID)
        REFERENCES CAPBANK (DeviceID)
        ON DELETE CASCADE;
/*  End YUK-10175  */
        
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
