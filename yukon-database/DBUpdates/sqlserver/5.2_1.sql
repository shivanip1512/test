/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-8881 */
ALTER TABLE SequenceNumber ALTER COLUMN SequenceName VARCHAR(30) NOT NULL;

ALTER TABLE SequenceNumber
   DROP CONSTRAINT SequenceName VARCHAR(30); 

ALTER TABLE SequenceNumber
   ALTER COLUMN SequenceName VARCHAR(30); 
   
CREATE TABLE AcctThermostatSchedule  (
   AcctThermostatScheduleId  NUMERIC     NOT NULL,
   AccountId                 NUMERIC     NOT NULL,
   ScheduleName              VARCHAR(60)   NOT NULL,
   ThermostatType            VARCHAR(60)   DEFAULT NULL, /* changed to not null after migration */
   ScheduleMode              VARCHAR(60)   DEFAULT NULL,
   CONSTRAINT PK_AcctThermSch PRIMARY KEY (AcctThermostatScheduleId)
);

ALTER TABLE AcctThermostatSchedule
   ADD CONSTRAINT FK_AcctThermSch_CustAcct FOREIGN KEY (AccountId)
      REFERENCES CustomerAccount (AccountId);

/* transfer from LMThermostatSchdule to AcctThermostatSchedule, only keep real thermostat schedules */
INSERT INTO AcctThermostatSchedule (AcctThermostatScheduleId, AccountId, ScheduleName, ThermostatType, ScheduleMode)
SELECT LMTS.ScheduleId, LMTS.AccountId, LMTS.ScheduleName, null, null 
FROM LMThermostatSchedule LMTS
WHERE LMTS.ThermostatTypeId IN (SELECT YLE.EntryId
                                FROM YukonListEntry YLE
                                WHERE YLE.YukonDefinitionId IN (1301, 1304, 1314));

/* update ThermostatTypes */
UPDATE AcctThermostatSchedule
SET ThermostatType = 'RESIDENTIAL_EXPRESSSTAT'
WHERE EXISTS (SELECT * FROM LMThermostatSchedule LMTS 
              WHERE LMTS.ThermostatTypeId IN (SELECT YLE.EntryId
                                              FROM YukonListEntry YLE
                                              WHERE YLE.YukonDefinitionId = 1301) 
              AND LMTS.ScheduleId = AcctThermostatScheduleId);

UPDATE AcctThermostatSchedule
SET ThermostatType = 'COMMERCIAL_EXPRESSSTAT'
WHERE EXISTS (SELECT * FROM LMThermostatSchedule LMTS 
              WHERE LMTS.ThermostatTypeId IN (SELECT YLE.EntryId
                                              FROM YukonListEntry YLE
                                              WHERE YLE.YukonDefinitionId = 1304)
              AND LMTS.ScheduleId = AcctThermostatScheduleId);

UPDATE AcctThermostatSchedule
SET ThermostatType = 'UTILITY_PRO'
WHERE EXISTS (SELECT * FROM LMThermostatSchedule LMTS 
              WHERE LMTS.ThermostatTypeId IN (SELECT YLE.EntryId
                                              FROM YukonListEntry YLE
                                              WHERE YLE.YukonDefinitionId = 1314)
              AND LMTS.ScheduleId = AcctThermostatScheduleId);

/* make ThermostatType column not allow nulls now */
ALTER TABLE AcctThermostatSchedule
   ALTER COLUMN ThermostatType VARCHAR(60)   NOT NULL;

/* update ScheduleName, use inventory DeviceLabel if it exists and the Schedulename is currently (none) */
UPDATE AcctThermostatSchedule
SET ScheduleName = (SELECT ib.DeviceLabel 
                        FROM InventoryBase ib
                        JOIN  LMThermostatSchedule LMTS ON (LMTS.InventoryId = ib.InventoryId)
                        WHERE LMTS.ScheduleId = AcctThermostatScheduleId)
WHERE ScheduleName = '(none)'
AND EXISTS (SELECT * 
            FROM LMThermostatSchedule LMTS 
        JOIN InventoryBase ib ON (LMTS.InventoryId = ib.InventoryId)
            WHERE LMTS.ScheduleId = AcctThermostatScheduleId
            AND LMTS.InventoryId > 0
            AND ib.DeviceLabel IS NOT NULL);

CREATE TABLE ECToAcctThermostatSchedule  (
   EnergyCompanyId            NUMERIC                          NOT NULL,
   AcctThermostatScheduleId   NUMERIC                          NOT NULL,
   CONSTRAINT PK_ECToAcctThermSch PRIMARY KEY (EnergyCompanyId, AcctThermostatScheduleId)
);

ALTER TABLE ECToAcctThermostatSchedule
   ADD CONSTRAINT FK_ECToAccThermSch_AccThermSch FOREIGN KEY (AcctThermostatScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId);

ALTER TABLE ECToAcctThermostatSchedule
   ADD CONSTRAINT FK_ECToAccThermSch_EC FOREIGN KEY (EnergyCompanyId)
      REFERENCES EnergyCompany (EnergyCompanyId);

INSERT INTO ECToAcctThermostatSchedule(EnergyCompanyId, AcctThermostatScheduleId)
SELECT ysl.EnergyCompanyId, LMTS.ScheduleId 
FROM LMThermostatSchedule LMTS
JOIN YukonListEntry YLE ON (LMTS.ThermostatTypeId = YLE.EntryId)
JOIN YukonSelectionList ysl ON (YLE.ListId = ysl.ListId)
JOIN AcctThermostatSchedule ats ON (LMTS.ScheduleId = ats.AcctThermostatScheduleId)
JOIN ECToGenericMapping ectgm ON (ectgm.ItemId = ats.AcctThermostatScheduleId)
WHERE ectgm.MappingCategory = 'LMThermostatSchedule'
AND YLE.YukonDefinitionId IN (1301, 1304, 1314);

CREATE TABLE AcctThermostatScheduleEntry  (
   AcctThermostatScheduleEntryId  NUMERIC                          NOT NULL,
   AcctThermostatScheduleId       NUMERIC                          NOT NULL,
   StartTime                      NUMERIC                          NOT NULL,
   TimeOfWeek                     VARCHAR(60)                    NOT NULL,
   CoolTemp                       NUMERIC                          NULL,
   HeatTemp                       NUMERIC                          NULL,
   CONSTRAINT PK_AcctThermSchEntry PRIMARY KEY (AcctThermostatScheduleEntryId)
);

ALTER TABLE AcctThermostatScheduleEntry
   ADD CONSTRAINT FK_AccThermSchEnt_AccThermSch FOREIGN KEY (AcctThermostatScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId);

/* transfer from LMThermostatSeasonEntry to AcctThermostatScheduleEntry, 
   only keep data for schedules migrated into AcctThermostatSchedule */
INSERT INTO AcctThermostatScheduleEntry (AcctThermostatScheduleEntryId, AcctThermostatScheduleId, 
                                         StartTime, TimeOfWeek, CoolTemp, HeatTemp)
SELECT seasonEntry.EntryId, ats.AcctThermostatScheduleId, seasonEntry.StartTime, 
       null, seasonEntry.CoolTemperature, seasonEntry.HeatTemperature 
FROM LMThermostatSeasonEntry seasonEntry
JOIN LMThermostatSeason season ON (seasonEntry.SeasonId = season.SeasonId)
JOIN AcctThermostatSchedule ats ON (season.ScheduleId = ats.AcctThermostatScheduleId);

/* set TimeOfWeek value */
UPDATE AcctThermostatScheduleEntry
SET TimeOfWeek = UPPER((SELECT YLE.EntryText 
                       FROM YukonListEntry YLE
                       JOIN  LMThermostatSeasonEntry LMTSE ON (YLE.EntryId = LMTSE.TimeOfWeekId)
                       WHERE LMTSE.EntryId = AcctThermostatScheduleEntryId));

/* make TimeOfWeek column not allow nulls now */
ALTER TABLE AcctThermostatScheduleEntry
   ALTER COLUMN TimeOfWeek VARCHAR(60) NOT NULL;

/* set any null temps */
UPDATE AcctThermostatScheduleEntry
SET CoolTemp = 72
WHERE CoolTemp IS NULL;

UPDATE AcctThermostatScheduleEntry
SET HeatTemp = 72
WHERE HeatTemp IS NULL;

/* make temps not allow null snow */
ALTER TABLE AcctThermostatScheduleEntry
   ALTER COLUMN CoolTemp NUMERIC NOT NULL;

ALTER TABLE AcctThermostatScheduleEntry
   ALTER COLUMN HeatTemp NUMERIC NOT NULL;

CREATE TABLE InventoryToAcctThermostatSch  (
   InventoryId                NUMERIC                          NOT NULL,
   AcctThermostatScheduleId   NUMERIC                          NOT NULL,
   CONSTRAINT PK_InvToAcctThermSch PRIMARY KEY (InventoryId)
);

ALTER TABLE InventoryToAcctThermostatSch
   ADD CONSTRAINT FK_InvToAccThermSch_AccThermSc FOREIGN KEY (AcctThermostatScheduleId)
      REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId);

ALTER TABLE InventoryToAcctThermostatSch
   ADD CONSTRAINT FK_InvToAcctThermSch_InvBase FOREIGN KEY (InventoryId)
      REFERENCES InventoryBase (InventoryId);

/* transfer from LMThermostatSchedule to AcctThermostatScheduleToInv */
INSERT INTO InventoryToAcctThermostatSch (InventoryId, AcctThermostatScheduleId)
SELECT LMTS.InventoryId, ats.AcctThermostatScheduleId
FROM LMThermostatSchedule LMTS
JOIN AcctThermostatSchedule ats ON (LMTS.ScheduleId = ats.AcctThermostatScheduleId)
WHERE LMTS.InventoryId > 0;
/* End YUK-8881 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
