/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

select awesome from nothing

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
    ADD CONSTRAINT FK_DynCCMonBankHistory_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
ALTER TABLE DynamicCCMonitorPointResponse
    ADD CONSTRAINT FK_DynCCMonPointResp_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
ALTER TABLE DynamicCCTwoWayCbc
    ADD CONSTRAINT FK_DynCCTwoWayCbc_DeviceCbc FOREIGN KEY (DeviceId)
        REFERENCES DeviceCbc (DeviceId)
            ON DELETE CASCADE;
/* End YUK-10659 */
            
/* Start YUK-10674 */
UPDATE Point 
SET PointName = 'Capacitor Bank State'
WHERE PointId IN (SELECT p.PointId 
                  FROM Point p, YukonPaobject yp
                  WHERE p.PAObjectID = yp.PAObjectID 
                    AND p.PointName = 'Bank Status'
                    AND yp.Type LIKE 'CBC%');
/* End YUK-10674 */

/* Start YUK-10709 */
INSERT INTO BillingFileFormats VALUES (-27, 'CMEP', 1);
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
   ExternalMessageId    NUMBER                          NOT NULL,
   YukonMessageId       NUMBER                          NOT NULL,
   UserId               NUMBER                          NOT NULL,
   MessageEndTime       DATE                            NOT NULL,
   CONSTRAINT PK_ExtToYukonMessageIdMapping PRIMARY KEY (ExternalMessageId)
);

ALTER TABLE ExtToYukonMessageIdMapping
    ADD CONSTRAINT FK_ExtToYukMessIdMap_YukonUser FOREIGN KEY (UserId)
        REFERENCES YukonUser (UserId)
            ON DELETE CASCADE;
            
INSERT INTO YukonServices VALUES(19,'YukonMessageListener','classpath:com/cannontech/services/yukonMessageListener/yukonMessageListener.xml', 'ServiceManager');
/* End YUK-10725 */

/* Start YUK-10730 */
INSERT INTO YukonRoleProperty VALUES(-1706,-8,'Enable CAPTCHAs','true','Enable CAPTCHAs for password management.');
/* End YUK-10730 */

/* Start YUK-10707 */
ALTER TABLE CCMonitorBankList ADD OverrideStrategy CHAR(1);

UPDATE CCMonitorBankList 
SET OverrideStrategy = (SELECT StrategyOverrideSetting 
                        FROM (SELECT 
                              CASE
                                  WHEN (strat3u.SettingValue = ccmbl.UpperBandwidth AND strat3l.SettingValue = ccmbl.LowerBandwidth ) 
                                      THEN 'N'
                                  WHEN ( strat2u.SettingValue = ccmbl.UpperBandwidth AND strat2l.SettingValue = ccmbl.LowerBandwidth ) 
                                      THEN 'N'
                                  WHEN ( strat1u.SettingValue = ccmbl.UpperBandwidth AND strat1l.SettingValue = ccmbl.LowerBandwidth ) 
                                      THEN 'N'
                                  ELSE 'Y'
                              END StrategyOverrideSetting,
                              sa.areaId, 
                              ssb.substationBusId, 
                              fsa.feederId, 
                              strat1u.SettingValue AS AreaUpperLimit,
                              strat1l.SettingValue AS AreaLowerLimit,
                              strat2u.SettingValue AS SubBusUpperLimit,
                              strat2l.SettingValue AS SubBusLowerLimit,
                              strat3u.SettingValue AS FeederUpperLimit,
                              strat3l.SettingValue AS FeederLowerLimit,
                              ccmbl.* 
                              FROM CCMonitorBankList ccmbl
                              JOIN CCFeederBankList fb ON fb.DeviceID = ccmbl.BankId
                              JOIN CCFeederSubAssignment fsa ON fsa.FeederID = fb.FeederID
                              JOIN CCSubstationSubBusList ssb ON ssb.SubStationBusID = fsa.SubStationBusID
                              JOIN CCSubAreaAssignment sa ON sa.SubstationBusID = ssb.SubStationID
                              LEFT JOIN CCSeasonStrategyAssignment seasStrat1 ON sa.AreaID = seasStrat1.PAObjectId
                              LEFT JOIN CCSeasonStrategyAssignment seasStrat2 ON ssb.SubStationBusID = seasStrat2.PAObjectId
                              LEFT JOIN CCSeasonStrategyAssignment seasStrat3 ON fsa.FeederID = seasStrat3.PAObjectId
                              LEFT JOIN CCStrategyTargetSettings strat1u ON strat1u.StrategyId = seasStrat1.StrategyId AND strat1u.SettingName = 'Upper Volt Limit' AND strat1u.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat1l ON strat1l.StrategyId = seasStrat1.StrategyId AND strat1l.SettingName = 'Lower Volt Limit' AND strat1l.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat2u ON strat2u.StrategyId = seasStrat2.StrategyId AND strat2u.SettingName = 'Upper Volt Limit' AND strat2u.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat2l ON strat2l.StrategyId = seasStrat2.StrategyId AND strat2l.SettingName = 'Lower Volt Limit' AND strat2l.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat3u ON strat3u.StrategyId = seasStrat3.StrategyId AND strat3u.SettingName = 'Upper Volt Limit' AND strat3u.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat3l ON strat3l.StrategyId = seasStrat3.StrategyId AND strat3l.SettingName = 'Lower Volt Limit' AND strat3l.SettingType = 'PEAK' 
                             ) x 
                        WHERE x.BankId = CCMonitorBankList.BankId AND x.PointId = CCMonitorBankList.PointId
                       );

ALTER TABLE CCMonitorBankList 
MODIFY OverrideStrategy CHAR(1) NOT NULL;

ALTER TABLE CCMonitorBankList
DROP CONSTRAINT FK_CCMonBankList_CapBank;

INSERT INTO CCMonitorBankList
SELECT p.PAObjectId, ptz.PointId, 1, 'Y', 3, 
CASE
    WHEN (seasStratBus.StrategyId > 0) THEN stratBusUpper.SettingValue
    ELSE stratAreaUpper.SettingValue
END upperLimit,
CASE
    WHEN (seasStratBus.StrategyId > 0) THEN stratBusLower.SettingValue
    ELSE stratAreaLower.SettingValue
END lowerLimit,
ptz.phase,
'N'
FROM PointToZoneMapping ptz
JOIN Point p ON p.PointId = ptz.PointId
JOIN Zone z ON z.ZoneId = ptz.ZoneId
JOIN CCSubstationSubBusList ssb ON ssb.SubStationBusID = z.SubStationBusID
JOIN CCSubAreaAssignment sa ON sa.SubstationBusID = ssb.SubStationID
LEFT JOIN CCSeasonStrategyAssignment seasStratArea ON sa.AreaID = seasStratArea.PAObjectId
LEFT JOIN CCSeasonStrategyAssignment seasStratBus ON ssb.SubStationBusID = seasStratBus.PAObjectId
LEFT JOIN CCStrategyTargetSettings stratAreaUpper ON stratAreaUpper.StrategyId = seasStratArea.StrategyId AND stratAreaUpper.SettingName = 'Upper Volt Limit' AND stratAreaUpper.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratAreaLower ON stratAreaLower.StrategyId = seasStratArea.StrategyId AND stratAreaLower.SettingName = 'Lower Volt Limit' AND stratAreaLower.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratBusUpper ON stratBusUpper.StrategyId = seasStratBus.StrategyId AND stratBusUpper.SettingName = 'Upper Volt Limit' AND stratBusUpper.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratBusLower ON stratBusLower.StrategyId = seasStratBus.StrategyId AND stratBusLower.SettingName = 'Lower Volt Limit' AND stratBusLower.SettingType = 'PEAK';

INSERT INTO CCMonitorBankList 
SELECT rtz.RegulatorId, epp.PointId, 1, 'Y', 3, 
CASE
    WHEN (seasStratBus.StrategyId > 0) THEN stratBusUpper.SettingValue
    ELSE stratAreaUpper.SettingValue
END upperLimit,
CASE
    WHEN (seasStratBus.StrategyId > 0) THEN stratBusLower.SettingValue
    ELSE stratAreaLower.SettingValue
END lowerLimit,
rtz.phase, 
'N'
FROM RegulatorToZoneMapping rtz
JOIN ExtraPaoPointAssignment epp ON epp.PAObjectId = rtz.RegulatorId AND epp.Attribute = 'VOLTAGE_Y'
JOIN Zone z ON z.ZoneId = rtz.ZoneId
JOIN CCSubstationSubBusList ssb ON ssb.SubStationBusID = z.SubStationBusID
JOIN CCSubAreaAssignment sa ON sa.SubstationBusID = ssb.SubStationID
LEFT JOIN CCSeasonStrategyAssignment seasStratArea ON sa.AreaID = seasStratArea.PAObjectId
LEFT JOIN CCSeasonStrategyAssignment seasStratBus ON ssb.SubStationBusID = seasStratBus.PAObjectId
LEFT JOIN CCStrategyTargetSettings stratAreaUpper ON stratAreaUpper.StrategyId = seasStratArea.StrategyId AND stratAreaUpper.SettingName = 'Upper Volt Limit' AND stratAreaUpper.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratAreaLower ON stratAreaLower.StrategyId = seasStratArea.StrategyId AND stratAreaLower.SettingName = 'Lower Volt Limit' AND stratAreaLower.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratBusUpper ON stratBusUpper.StrategyId = seasStratBus.StrategyId AND stratBusUpper.SettingName = 'Upper Volt Limit' AND stratBusUpper.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratBusLower ON stratBusLower.StrategyId = seasStratBus.StrategyId AND stratBusLower.SettingName = 'Lower Volt Limit' AND stratBusLower.SettingType = 'PEAK';

ALTER TABLE CcMonitorBankList RENAME COLUMN BankId TO DeviceId;
ALTER TABLE DynamicCCMonitorBankHistory RENAME COLUMN BankId TO DeviceId;
ALTER TABLE DynamicCCMonitorPointResponse RENAME COLUMN BankId TO DeviceId;
/* End YUK-10707 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.4', 'Garrett D', '05-MAR-2012', 'Latest Update', 2 );