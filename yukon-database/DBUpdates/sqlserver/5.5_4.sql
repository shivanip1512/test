/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11429 */
ALTER TABLE Regulator
ADD VoltChangePerTap FLOAT;

UPDATE Regulator
SET VoltChangePerTap = 0.75;

ALTER TABLE Regulator
ALTER COLUMN VoltChangePerTap FLOAT NOT NULL;
/* End YUK-11429 */

/* Start YUK-11466 */
INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Low Voltage Violation', '3.0', 'BANDWIDTH'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'High Voltage Violation', '1.0', 'BANDWIDTH'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Low Voltage Violation', '-10.0', 'COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'High Voltage Violation', '70.0', 'COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Low Voltage Violation', '-150.0', 'EMERGENCY_COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'High Voltage Violation', '300.0', 'EMERGENCY_COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';
/* End YUK-11466 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
