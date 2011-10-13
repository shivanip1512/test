/******************************************/ 
/**** SQL Server DBupdates             ****/ 
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
    DROP CONSTRAINT FK_ccarea_Dynccarea
GO
ALTER TABLE DynamicCCFeeder
    DROP CONSTRAINT FK_CCFeed_DyFeed
GO
ALTER TABLE DynamicCCCapBank
    DROP CONSTRAINT FK_CpBnk_DynCpBnk
GO
ALTER TABLE DYNAMICCCSPECIALAREA
    DROP CONSTRAINT FK_DynCCSpecA_CapContSpecA
GO
ALTER TABLE DYNAMICCCSUBSTATION
    DROP CONSTRAINT FK_DYNAMICC_REFERENCE_CAPCONTR
GO
ALTER TABLE DynamicCCSubstationBus
    DROP CONSTRAINT FK_CCSubBs_DySubBs
GO
ALTER TABLE DynamicCCOperationStatistics
    DROP CONSTRAINT FK_DYNAMICC_REFERENCE_YUKONPAO
GO
ALTER TABLE DynamicDeviceScanData 
    DROP CONSTRAINT SYS_C0015139
GO
ALTER TABLE CCSUBAREAASSIGNMENT
    DROP CONSTRAINT FK_CCSUBARE_REFERENCE_CAPCONTR
GO
ALTER TABLE CCSUBAREAASSIGNMENT
    DROP CONSTRAINT FK_CCSUBARE_CAPSUBAREAASSGN
GO
ALTER TABLE CCSUBSTATIONSUBBUSLIST
    DROP CONSTRAINT FK_CCSUBSTA_REFERENCE_CAPCONTR
GO
ALTER TABLE CCSUBSTATIONSUBBUSLIST
    DROP CONSTRAINT FK_CCSUBSTA_CAPCONTR
GO
ALTER TABLE CCFeederSubAssignment
    DROP CONSTRAINT FK_CCFeed_CCFass
GO
ALTER TABLE CCFeederSubAssignment
    DROP CONSTRAINT FK_CCSub_CCFeed
GO
ALTER TABLE CCFeederBankList
    DROP CONSTRAINT FK_CCFeed_CCBnk
GO
ALTER TABLE CCFeederBankList
    DROP CONSTRAINT FK_CB_CCFeedLst
GO

ALTER TABLE DynamicCCArea
    ADD CONSTRAINT FK_DynCCArea_CCArea foreign key (AreaID)
        REFERENCES CapControlArea (AreaID)
        ON DELETE CASCADE
GO
ALTER TABLE DynamicCCFeeder
    ADD CONSTRAINT FK_DynCCFeeder_CCFeeder foreign key (FeederID)
        REFERENCES CapControlFeeder (FeederID)
        ON DELETE CASCADE
GO
ALTER TABLE DynamicCCCapBank
    ADD CONSTRAINT FK_DynCCCapBank_CapBank foreign key (CapBankID)
        REFERENCES CAPBANK (DeviceId)
        ON DELETE CASCADE
GO
ALTER TABLE DYNAMICCCSPECIALAREA
    ADD CONSTRAINT FK_DynCCSpecial_CCSpecialArea foreign key (AreaID)
        REFERENCES CAPCONTROLSPECIALAREA (AreaId)
        ON DELETE CASCADE
GO
ALTER TABLE DYNAMICCCSUBSTATION
    ADD CONSTRAINT FK_DynCCSubst_CCSubst foreign key (SubStationID)
        REFERENCES CAPCONTROLSUBSTATION (SubstationID)
        ON DELETE CASCADE
GO
ALTER TABLE DynamicCCSubstationBus
    ADD CONSTRAINT FK_DynCCSubBus_CCSubBus foreign key (SubstationBusID)
        REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusID)
        ON DELETE CASCADE
GO
/***** Other Cascades *****/
ALTER TABLE DynamicCCOperationStatistics
    ADD CONSTRAINT FK_DynCCOpStats_YukonPAObject foreign key (PAObjectID)
        REFERENCES YukonPAObject (PAObjectID)
        ON DELETE CASCADE
GO
ALTER TABLE DynamicDeviceScanData 
    ADD CONSTRAINT FK_DynDeviceScanData FOREIGN KEY (DeviceID) 
        REFERENCES Device (DeviceID) 
        ON DELETE CASCADE 
GO
/***** CCSUBAREAASSIGNMENT *****/
ALTER TABLE CCSUBAREAASSIGNMENT
    ADD CONSTRAINT FK_CCSubAreaAssignment_CCArea foreign key (AreaID)
        REFERENCES CAPCONTROLAREA (AreaID)
        ON DELETE CASCADE
GO
ALTER TABLE CCSUBAREAASSIGNMENT
    ADD CONSTRAINT FK_CCSubAreaAssign_CCSubst foreign key (SubstationBusID)
        REFERENCES CAPCONTROLSUBSTATION (SubstationID)
        ON DELETE CASCADE
GO
/***** CCSUBSTATIONSUBBUSLIST *****/
ALTER TABLE CCSUBSTATIONSUBBUSLIST
    ADD CONSTRAINT FK_CCSubstSubBusList_CCSub foreign key (SubstationID)
        REFERENCES CAPCONTROLSUBSTATION (SubstationID)
        ON DELETE CASCADE
GO
ALTER TABLE CCSUBSTATIONSUBBUSLIST
    ADD CONSTRAINT FK_CCSubstSubBusList_CCSubBus foreign key (SubstationBusID)
        REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusID)
        ON DELETE CASCADE
GO
/***** CCFeederSubAssignment *****/
ALTER TABLE CCFeederSubAssignment
    ADD CONSTRAINT FK_CCFeederSubAssign_CCSubBus foreign key (SubStationBusID)
        REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusID)
        ON DELETE CASCADE
GO
ALTER TABLE CCFeederSubAssignment
    ADD CONSTRAINT FK_CCFeederSubAssign_CCFeeder foreign key (FeederID)
        REFERENCES CapControlFeeder (FeederID)
        ON DELETE CASCADE
GO
/***** CCFeederBankList *****/
ALTER TABLE CCFeederBankList
    ADD CONSTRAINT FK_CCFeederBankList_CCFeeder foreign key (FeederID)
        REFERENCES CapControlFeeder (FeederID)
        ON DELETE CASCADE
GO
ALTER TABLE CCFeederBankList
    ADD CONSTRAINT FK_CCFeederBankList_CapBank foreign key (DeviceID)
        REFERENCES CAPBANK (DeviceID)
        ON DELETE CASCADE
GO
/*  End YUK-10175  */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
