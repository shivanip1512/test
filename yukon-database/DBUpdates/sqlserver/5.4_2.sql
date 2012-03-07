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
    ADD CONSTRAINT FK_DynCCMonBankHistory_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
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
                              JOIN CCSUBSTATIONSUBBUSLIST ssb ON ssb.SubStationBusID = fsa.SubStationBusID
                              JOIN CCSUBAREAASSIGNMENT sa ON sa.SubstationBusID = ssb.SubStationID
                              LEFT JOIN CCSEASONSTRATEGYASSIGNMENT seasStrat1 ON sa.AreaID = seasStrat1.paobjectid
                              LEFT JOIN CCSEASONSTRATEGYASSIGNMENT seasStrat2 ON ssb.SubStationBusID = seasStrat2.paobjectid
                              LEFT JOIN CCSEASONSTRATEGYASSIGNMENT seasStrat3 ON fsa.FeederID = seasStrat3.paobjectid
                              LEFT JOIN CCStrategyTargetSettings strat1u ON strat1u.StrategyId = seasStrat1.strategyid AND strat1u.SettingName = 'Upper Volt Limit' AND strat1u.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat1l ON strat1l.StrategyId = seasStrat1.strategyid AND strat1l.SettingName = 'Lower Volt Limit' AND strat1l.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat2u ON strat2u.StrategyId = seasStrat2.strategyid AND strat2u.SettingName = 'Upper Volt Limit' AND strat2u.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat2l ON strat2l.StrategyId = seasStrat2.strategyid AND strat2l.SettingName = 'Lower Volt Limit' AND strat2l.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat3u ON strat3u.StrategyId = seasStrat3.strategyid AND strat3u.SettingName = 'Upper Volt Limit' AND strat3u.SettingType = 'PEAK' 
                              LEFT JOIN CCStrategyTargetSettings strat3l ON strat3l.StrategyId = seasStrat3.strategyid AND strat3l.SettingName = 'Lower Volt Limit' AND strat3l.SettingType = 'PEAK' 
                             ) x 
                        WHERE x.BankId = CCMonitorBankList.BankId AND x.PointId = CCMonitorBankList.PointId
                       );

ALTER TABLE CCMonitorBankList 
ALTER COLUMN OverrideStrategy CHAR(1) NOT NULL;

ALTER TABLE CCMonitorBankList 
DROP CONSTRAINT FK_CCMonBankList_CapBank;

INSERT INTO CCMonitorBankList 
SELECT p.paobjectid, ptz.pointid, 1, 'Y', 3, 
CASE
    WHEN (seasStratBus.strategyid > 0) THEN stratBusUpper.SettingValue
    ELSE stratAreaUpper.SettingValue
END upperLimit,
CASE
    WHEN (seasStratBus.strategyid > 0) THEN stratBusLower.SettingValue
    ELSE stratAreaLower.SettingValue
END lowerLimit,
ptz.phase, 
'N'
FROM PointToZoneMapping ptz
JOIN POINT p ON p.POINTID = ptz.PointId
JOIN zone z ON z.ZoneId = ptz.ZoneId
JOIN CCSUBSTATIONSUBBUSLIST ssb ON ssb.SubStationBusID = z.SubStationBusID
JOIN CCSUBAREAASSIGNMENT sa ON sa.SubstationBusID = ssb.SubStationID
LEFT JOIN CCSEASONSTRATEGYASSIGNMENT seasStratArea ON sa.AreaID = seasStratArea.paobjectid
LEFT JOIN CCSEASONSTRATEGYASSIGNMENT seasStratBus ON ssb.SubStationBusID = seasStratBus.paobjectid
LEFT JOIN CCStrategyTargetSettings stratAreaUpper ON stratAreaUpper.StrategyId = seasStratArea.strategyid AND stratAreaUpper.SettingName = 'Upper Volt Limit' AND stratAreaUpper.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratAreaLower ON stratAreaLower.StrategyId = seasStratArea.strategyid AND stratAreaLower.SettingName = 'Lower Volt Limit' AND stratAreaLower.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratBusUpper ON stratBusUpper.StrategyId = seasStratBus.strategyid AND stratBusUpper.SettingName = 'Upper Volt Limit' AND stratBusUpper.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratBusLower ON stratBusLower.StrategyId = seasStratBus.strategyid AND stratBusLower.SettingName = 'Lower Volt Limit' AND stratBusLower.SettingType = 'PEAK';

INSERT INTO CCMonitorBankList 
SELECT rtz.RegulatorId, epp.pointid, 1, 'Y', 3, 
CASE
    WHEN (seasStratBus.strategyid > 0) THEN stratBusUpper.SettingValue
    ELSE stratAreaUpper.SettingValue
END upperLimit,
CASE
    WHEN (seasStratBus.strategyid > 0) THEN stratBusLower.SettingValue
    ELSE stratAreaLower.SettingValue
END lowerLimit,
rtz.phase, 
'N'
FROM RegulatorToZoneMapping rtz
JOIN ExtraPaoPointAssignment epp ON epp.PAObjectId = rtz.RegulatorId AND epp.Attribute = 'VOLTAGE_Y'
JOIN zone z ON z.ZoneId = rtz.ZoneId
JOIN CCSUBSTATIONSUBBUSLIST ssb ON ssb.SubStationBusID = z.SubStationBusID
JOIN CCSUBAREAASSIGNMENT sa ON sa.SubstationBusID = ssb.SubStationID
LEFT JOIN CCSEASONSTRATEGYASSIGNMENT seasStratArea ON sa.AreaID = seasStratArea.paobjectid
LEFT JOIN CCSEASONSTRATEGYASSIGNMENT seasStratBus ON ssb.SubStationBusID = seasStratBus.paobjectid
LEFT JOIN CCStrategyTargetSettings stratAreaUpper ON stratAreaUpper.StrategyId = seasStratArea.strategyid AND stratAreaUpper.SettingName = 'Upper Volt Limit' AND stratAreaUpper.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratAreaLower ON stratAreaLower.StrategyId = seasStratArea.strategyid AND stratAreaLower.SettingName = 'Lower Volt Limit' AND stratAreaLower.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratBusUpper ON stratBusUpper.StrategyId = seasStratBus.strategyid AND stratBusUpper.SettingName = 'Upper Volt Limit' AND stratBusUpper.SettingType = 'PEAK' 
LEFT JOIN CCStrategyTargetSettings stratBusLower ON stratBusLower.StrategyId = seasStratBus.strategyid AND stratBusLower.SettingName = 'Lower Volt Limit' AND stratBusLower.SettingType = 'PEAK';

sp_rename 'CcMonitorBankList.BankId ', 'DeviceId', 'COLUMN';
/* End YUK-10707 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.4', 'Garrett D', '05-MAR-2012', 'Latest Update', 2 );