/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9213 */
UPDATE CapControlStrategy 
SET ControlMethod = 'INDIVIDUAL_FEEDER' 
WHERE ControlMethod = 'IndividualFeeder'; 
UPDATE CapControlStrategy 
SET ControlMethod = 'SUBSTATION_BUS' 
WHERE ControlMethod = 'SubstationBus'; 
UPDATE CapControlStrategy 
SET ControlMethod = 'BUSOPTIMIZED_FEEDER' 
WHERE ControlMethod = 'BusOptimizedFeeder'; 
UPDATE CapControlStrategy 
SET ControlMethod = 'MANUAL_ONLY' 
WHERE ControlMethod = 'ManualOnly'; 
UPDATE CapControlStrategy 
SET ControlMethod = 'TIME_OF_DAY' 
WHERE ControlMethod = 'TimeOfDay'; 

UPDATE CapControlStrategy 
SET ControlUnits = 'NONE' 
WHERE ControlUnits = '(none)'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'MULTI_VOLT' 
WHERE ControlUnits = 'Multi Volt'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'KVAR' 
WHERE ControlUnits = 'kVAr'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'MULTI_VOLT_VAR' 
WHERE ControlUnits = 'Multi Volt Var'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'PFACTOR_KW_KVAR' 
WHERE ControlUnits = 'P-Factor kW/kVAr'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'TIME_OF_DAY' 
WHERE ControlUnits = 'Time of Day'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'INTEGRATED_VOLT_VAR' 
WHERE ControlUnits = 'Integrated Volt/Var';
/* End YUK-9213 */ 

/* Start YUK-9160 */
ALTER TABLE CCSubstationBusToLTC DROP CONSTRAINT FK_CCSubBusToLTC_CapContSubBus;
ALTER TABLE CCSubstationBusToLTC DROP CONSTRAINT FK_CCSubBusToLTC_YukonPAO;
DROP TABLE CCSubstationBusToLTC;
/* End YUK-9160 */ 

/* Start YUK-9143 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId = -10810;
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -10810;
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -10810;
/* End YUK-9143 */ 

/* Start YUK-9233 */
UPDATE YukonPAObject 
SET Type = 'LTC', Category = 'CAPCONTROL' 
WHERE Type = 'Load Tap Changer'; 
UPDATE YukonPAObject 
SET Type = 'GO_REGULATOR', Category = 'CAPCONTROL' 
WHERE Type = 'GANGOPERATED'; 
UPDATE YukonPAObject 
SET Type = 'PO_REGULATOR', Category = 'CAPCONTROL' 
WHERE Type = 'PHASEOPERATED';
/* End YUK-9233 */

/* Start YUK-9235 */
UPDATE YukonPAObject 
SET Type = 'RFN-410fL' 
WHERE Type = 'RFN-AL'; 
UPDATE YukonPAObject 
SET Type = 'RFN-410fX' 
WHERE Type = 'RFN-AX'; 
UPDATE YukonPAObject 
SET Type = 'RFN-410fD' 
WHERE Type = 'RFN-AXSD';
/* End YUK-9235 */

/* Start YUK-9229 */
ALTER TABLE DynamicCCMonitorPointResponse 
ADD StaticDelta CHAR(1); 

UPDATE DynamicCCMonitorPointResponse 
SET StaticDelta = 'N'; 

ALTER TABLE DynamicCCMonitorPointResponse 
ALTER COLUMN StaticDelta CHAR(1) NOT NULL; 
/* End YUK-9229 */

/* Start YUK-9214 */
ALTER TABLE CapBankToZoneMapping 
ADD GraphPositionOffset FLOAT; 
ALTER TABLE CapBankToZoneMapping 
ADD Distance FLOAT; 

ALTER TABLE PointToZoneMapping 
ADD GraphPositionOffset FLOAT; 
ALTER TABLE PointToZoneMapping 
ADD Distance FLOAT; 

ALTER TABLE Zone 
ADD GraphStartPosition FLOAT; 
/* End YUK-9214 */

/* Start YUK-9244 */
IF 0 < (SELECT COUNT(*)
        FROM YukonGroup YG 
        JOIN YukonGroupRole YGR ON YG.GroupId = YGR.GroupId 
        WHERE YGR.RolePropertyId = -20161 
        AND YGR.Value NOT IN ('(none)', ' ', '0'))
            RAISERROR('The database update requires manual interaction to continue. Please refer to YUK-9051 for more information on updating the ACCOUNT_NUMBER_LENGTH role property.', 16, 1);
GO

IF 0 < (SELECT COUNT(*)
        FROM YukonGroup YG 
        JOIN YukonGroupRole YGR ON YG.GroupId = YGR.GroupId 
        WHERE YGR.RolePropertyId = -20162
        AND YGR.Value NOT IN ('(none)', ' ', '0'))
               RAISERROR('The database update requires manual interaction to continue. Please refer to YUK-9051 for more information on updating the ROTATION_DIGIT_LENGTH role property.', 16, 1);
GO 

DELETE FROM YukonGroupRole 
WHERE RolePropertyID IN (-20161, -20162); 
DELETE FROM YukonUserRole 
WHERE RolePropertyID IN (-20161, -20162); 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyID IN (-20161, -20162);

INSERT INTO YukonRoleProperty VALUES(-1116,-2,'Account Number Length',' ','Specifies the number of account number characters to consider for comparison purposes during the customer account import process.'); 
INSERT INTO YukonRoleProperty VALUES(-1117,-2,'Rotation Digit Length',' ','Specifies the number of rotation digit characters to ignore during the customer account import process.'); 
/* End YUK-9244 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.2', 'Matt K', '04-NOV-2010', 'Latest Update', 3);