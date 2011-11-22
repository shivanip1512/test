/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-10442 */
/* @error warn-once */
/* @start-block */
DECLARE
    errorFlagCount INT;
BEGIN
    SELECT COUNT(*) INTO errorFlagCount FROM (
      SELECT COUNT(*)
      FROM YukonListEntry     YLE
      JOIN YukonSelectionList YSL ON YSL.ListId = YLE.ListId
      WHERE YSL.ListName = 'DeviceType'
        AND YLE.YukonDefinitionId IN (1301, 1304, 1313, 1314, 1316, 1318, 1319)
      GROUP BY YLE.YukonDefinitionId, YSL.EnergyCompanyId
      HAVING COUNT(*) > 1);
    IF (0 < errorFlagCount) THEN
        RAISE_APPLICATION_ERROR(-20001, 'There are thermostat device types that share a base device type.  While this is an acceptable configuration, it will cause default schedules for shared base device types to be invalid after this upgrade script (5.2_1.sql) is run.  This issue requires manual intervention to preserve the integrity of the system''s default schedules.  See YUK-10442 for more information and instructions.');
    END IF;
END;
/* @end-block */
/* End YUK-10442 */

/* Start YUK-8881 */
ALTER TABLE SequenceNumber MODIFY SequenceName VARCHAR2(30); 
   
CREATE TABLE AcctThermostatSchedule  (
   AcctThermostatScheduleId  NUMBER     NOT NULL,
   AccountId                 NUMBER     NOT NULL,
   ScheduleName              VARCHAR2(60)   NOT NULL,
   ThermostatType            VARCHAR2(60)   DEFAULT NULL, /* changed to not null after migration */
   ScheduleMode              VARCHAR2(60)   DEFAULT NULL,
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
                                WHERE YLE.YukonDefinitionId IN (1301, 1304, 1314, 1313, 3100));

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


UPDATE AcctThermostatSchedule
SET ThermostatType = 'HEAT_PUMP_EXPRESSSTAT'
WHERE EXISTS (SELECT * FROM LMThermostatSchedule LMTS
                WHERE LMTS.ThermostatTypeId IN (SELECT YLE.EntryId
                                                 FROM YukonListEntry YLE
                                                 WHERE YLE.YukonDefinitionId = 1313)
                AND LMTS.ScheduleId = AcctThermostatScheduleId);

UPDATE AcctThermostatSchedule
SET ThermostatType = 'ENERGY_PRO'
WHERE EXISTS (SELECT * FROM LMThermostatSchedule LMTS
                WHERE LMTS.ThermostatTypeId IN (SELECT YLE.EntryId
                                                 FROM YukonListEntry YLE
                                                 WHERE YLE.YukonDefinitionId = 3100)
                AND LMTS.ScheduleId = AcctThermostatScheduleId);
              
/* make ThermostatType column not allow nulls now */
ALTER TABLE AcctThermostatSchedule
   MODIFY ThermostatType VARCHAR2(60)   NOT NULL;

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
   EnergyCompanyId            NUMBER                          NOT NULL,
   AcctThermostatScheduleId   NUMBER                          NOT NULL,
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
AND YLE.YukonDefinitionId IN (1301, 1304, 1314, 1313, 3100);

CREATE TABLE AcctThermostatScheduleEntry  (
   AcctThermostatScheduleEntryId  NUMBER                          NOT NULL,
   AcctThermostatScheduleId       NUMBER                          NOT NULL,
   StartTime                      NUMBER                          NOT NULL,
   TimeOfWeek                     VARCHAR2(60)                    NULL,
   CoolTemp                       NUMBER                          NULL,
   HeatTemp                       NUMBER                          NULL,
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
   MODIFY TimeOfWeek VARCHAR2(60) NOT NULL;

/* set any null temps */
UPDATE AcctThermostatScheduleEntry
SET CoolTemp = 72
WHERE CoolTemp IS NULL;

UPDATE AcctThermostatScheduleEntry
SET HeatTemp = 72
WHERE HeatTemp IS NULL;

/* make temps not allow null snow */
ALTER TABLE AcctThermostatScheduleEntry
   MODIFY CoolTemp NUMBER NOT NULL;

ALTER TABLE AcctThermostatScheduleEntry
   MODIFY HeatTemp NUMBER NOT NULL;

CREATE TABLE InventoryToAcctThermostatSch  (
   InventoryId                NUMBER                          NOT NULL,
   AcctThermostatScheduleId   NUMBER                          NOT NULL,
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

ALTER TABLE LMThermostatSchedule DROP CONSTRAINT FK_LMThSc_CsAc;
ALTER TABLE LMThermostatSchedule DROP CONSTRAINT FK_LMThSc_InvB;
ALTER TABLE LMThermostatSchedule DROP CONSTRAINT FK_LMThSc_YkLs;
ALTER TABLE LMThermostatSeason DROP CONSTRAINT FK_YkWbC_LThSs;
ALTER TABLE LMThermostatSeasonEntry DROP CONSTRAINT FK_CsLsE_LThSE;
/* End YUK-8881 */

/* Start YUK-8773 */
/* Remove Constraints */
/* @error ignore-begin */
ALTER TABLE MACSimpleSchedule DROP CONSTRAINT FK_MACSIMPLE_MACSCHED_ID;
ALTER TABLE MACSimpleSchedule DROP CONSTRAINT PK_MACSIMPLESCHEDULE;
ALTER TABLE MACSimpleSchedule DROP CONSTRAINT PK_MACSimpSch;
/* @error ignore-end */

/* Rename the current table to a temp table */
ALTER TABLE MACSimpleSchedule RENAME TO MACSimpleScheduleTemp;

/* Create the new table */
CREATE TABLE MACSimpleSchedule (
   ScheduleId NUMBER not null,
   TargetPAObjectId NUMBER,
   StartCommand VARCHAR2(120),
   StopCommand VARCHAR2(120),
   RepeatInterval NUMBER,
   CONSTRAINT PK_MACSimpSch PRIMARY KEY (ScheduleId)
);

/* Migrate the old information into the new table */
INSERT INTO MACSimpleSchedule
SELECT MSST.ScheduleId, PAO.PAObjectId, MSST.StartCommand,
       MSST.StopCommand, MSST.RepeatInterval
FROM MACSimpleScheduleTemp MSST
LEFT JOIN YukonPAObject PAO ON (MSST.TargetSelect = PAO.PAOName
                                /* Load Groups */
                                AND (PAO.PAOClass = 'GROUP' AND
                                     PAO.Category = 'DEVICE'));

INSERT INTO MACSimpleSchedule
SELECT MSST.ScheduleId, PAO.PAObjectId, MSST.StartCommand,
       MSST.StopCommand, MSST.RepeatInterval
FROM MACSimpleScheduleTemp MSST
LEFT JOIN YukonPAObject PAO ON (MSST.TargetSelect = PAO.PAOName
                                 /* Devices */
                                 AND ( PAO.PAOClass = 'CARRIER' OR
                                       PAO.PAOClass = 'IED' OR
                                       PAO.PAOClass = 'METER' OR
                                       PAO.PAOClass = 'RFMESH' OR
                                       PAO.PAOClass = 'RTU' OR
                                       PAO.PAOClass = 'TRANSMITTER' OR
                                       PAO.PAOClass = 'VIRTUAL' OR
                                       PAO.PAOClass = 'GRID')
                                 AND PAO.Category = 'DEVICE'
                                 AND PAO.Type != 'MCT Broadcast')
WHERE MSST.ScheduleId NOT IN (SELECT MACSS.ScheduleId
                               FROM MACSimpleSchedule MACSS);

UPDATE MACSimpleSchedule
SET TargetPaobjectId = 0
WHERE TargetPaobjectId IS NULL;

ALTER TABLE MACSimpleSchedule
MODIFY TargetPaobjectId NUMBER NOT NULL;

/* Restore the removed constraints */
ALTER TABLE MACSimpleSchedule
    ADD CONSTRAINT FK_MACSimpSch_PAO FOREIGN KEY (TargetPAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
			ON DELETE CASCADE;

ALTER TABLE MACSimpleSchedule
    ADD CONSTRAINT FK_MACSimpSch_MACSch FOREIGN KEY (ScheduleId)
        REFERENCES MACSchedule (ScheduleId);

DROP TABLE MACSimpleScheduleTemp;
/* End YUK-8773 */

/* Start YUK-8880 */
DELETE FROM JobProperty
WHERE JobId IN (SELECT JobId 
                 FROM Job 
                 WHERE BeanName = 'importCustAccountsSchedulerJob');
DELETE FROM JobScheduledOneTime
WHERE JobId IN (SELECT JobId 
                 FROM Job 
                 WHERE BeanName = 'importCustAccountsSchedulerJob');
DELETE FROM JobScheduledRepeating
WHERE JobId IN (SELECT JobId 
                 FROM Job 
                 WHERE BeanName = 'importCustAccountsSchedulerJob');
DELETE FROM JobStatus 
WHERE JobId IN (SELECT JobId 
                 FROM Job 
                 WHERE BeanName = 'importCustAccountsSchedulerJob');
DELETE FROM Job 
WHERE BeanName = 'importCustAccountsSchedulerJob';
/* End YUK-8880 */

/* Start YUK-8724 */
INSERT INTO YukonRoleProperty VALUES(-20120,-201,'Web Service LM Data Access','true','Controls access to web services that retrieve LM data.');
INSERT INTO YukonRoleProperty VALUES(-20121,-201,'Web Service LM Control Access','true','Controls access to web services that perform LM control.');
/* End YUK-8724 */

/* Start YUK-8849 */
ALTER TABLE CCurtEEParticipantSelection
   DROP CONSTRAINT FK_CCURTEEPARTSEL_CCURTPART;
ALTER TABLE CCurtEEParticipantWindow
   DROP CONSTRAINT FK_CCRTEEPRTWIN_CCRTEEPRTSEL;
ALTER TABLE CCurtEEPricing
   DROP CONSTRAINT FK_CCURTEEPR_CCURTECONEVT;
ALTER TABLE CCurtEEPricingWindow
   DROP CONSTRAINT FK_CCURTEEPRWIN_CCURTEEPR;

ALTER TABLE CCurtEEParticipantSelection
   ADD CONSTRAINT FK_CCURTEEPARTSEL_CCURTPART FOREIGN KEY (CCurtEEParticipantID)
      REFERENCES CCurtEEParticipant (CCurtEEParticipantID)
         ON DELETE CASCADE;
ALTER TABLE CCurtEEParticipantWindow
   ADD CONSTRAINT FK_CCRTEEPRTWIN_CCRTEEPRTSEL FOREIGN KEY (CCurtEEParticipantSelectionID)
      REFERENCES CCurtEEParticipantSelection (CCurtEEParticipantSelectionID)
         ON DELETE CASCADE;
ALTER TABLE CCurtEEPricing
   ADD CONSTRAINT FK_CCURTEEPR_CCURTECONEVT FOREIGN KEY (CCurtEconomicEventID)
      REFERENCES CCurtEconomicEvent (CCurtEconomicEventID)
         ON DELETE CASCADE;
ALTER TABLE CCurtEEPricingWindow
   ADD CONSTRAINT FK_CCURTEEPRWIN_CCURTEEPR FOREIGN KEY (CCurtEEPricingID)
      REFERENCES CCurtEEPricing (CCurtEEPricingID)
         ON DELETE CASCADE;
/* End YUK-8849 */

/* Start YUK-8833 */
/* @error ignore-begin */
CREATE INDEX Indx_LMContHist_SOE_Tag ON LMControlHistory (
   SOE_Tag ASC
);
/* @error ignore-end */
/* End YUK-8833 */

/* Start YUK-8932 */
CREATE TABLE OptOutSurvey  (
   OptOutSurveyId       NUMBER                          NOT NULL,
   SurveyId             NUMBER                          NOT NULL,
   StartDate            DATE                            NOT NULL,
   StopDate             DATE                            NULL,
   CONSTRAINT PK_OptOutSurvey PRIMARY KEY (OptOutSurveyId)
);

CREATE TABLE OptOutSurveyProgram (
   OptOutSurveyId       NUMBER                          NOT NULL,
   ProgramId            NUMBER                          NOT NULL,
   CONSTRAINT PK_OptOutSurveyProgram PRIMARY KEY (OptOutSurveyId, ProgramId)
);

CREATE TABLE OptOutSurveyResult  (
   SurveyResultId       NUMBER                          NOT NULL,
   OptOutEventLogId     NUMBER                          NOT NULL,
   CONSTRAINT PK_OptOutSurvRes PRIMARY KEY (SurveyResultId, OptOutEventLogId)
);

CREATE TABLE Survey  (
   SurveyId             NUMBER                          NOT NULL,
   EnergyCompanyId      NUMBER                          NOT NULL,
   SurveyName           VARCHAR2(64)                    NOT NULL,
   SurveyKey            VARCHAR2(64)                    NOT NULL,
   CONSTRAINT PK_Surv PRIMARY KEY (SurveyId)
);

CREATE TABLE SurveyQuestion  (
   SurveyQuestionId     NUMBER                          NOT NULL,
   SurveyId             NUMBER                          NOT NULL,
   QuestionKey          VARCHAR2(64)                    NOT NULL,
   AnswerRequired       CHAR(1)                         NOT NULL,
   QuestionType         VARCHAR2(32)                    NOT NULL,
   TextAnswerAllowed    CHAR(1)                         NOT NULL,
   DisplayOrder         NUMBER                          NOT NULL,
   CONSTRAINT PK_SurvQuest PRIMARY KEY (SurveyQuestionId)
);

CREATE UNIQUE INDEX Indx_SurvId_DispOrder_UNQ ON SurveyQuestion (
   SurveyId ASC,
   DisplayOrder ASC
);

CREATE TABLE SurveyQuestionAnswer  (
   SurveyQuestionAnswerId NUMBER                          NOT NULL,
   SurveyQuestionId     NUMBER                          NOT NULL,
   AnswerKey            VARCHAR2(64)                    NOT NULL,
   DisplayOrder         NUMBER                          NOT NULL,
   CONSTRAINT PK_SurvQuestAns PRIMARY KEY (SurveyQuestionAnswerId)
);

CREATE UNIQUE INDEX Indx_SurvQuestId_dispOrder_UNQ ON SurveyQuestionAnswer (
   SurveyQuestionId ASC,
   DisplayOrder ASC
);

CREATE TABLE SurveyResult  (
   SurveyResultId       NUMBER                          NOT NULL,
   SurveyId             NUMBER                          NOT NULL,
   AccountId            NUMBER                          NOT NULL,
   AccountNumber        VARCHAR2(40)                    NULL,
   WhenTaken            DATE                            NOT NULL,
   CONSTRAINT PK_SurvRes PRIMARY KEY (SurveyResultId)
);

CREATE TABLE SurveyResultAnswer  (
   SurveyResultAnswerId NUMBER                          NOT NULL,
   SurveyResultId       NUMBER                          NOT NULL,
   SurveyQuestionId     NUMBER                          NOT NULL,
   SurveyQuestionAnswerId NUMBER                        NULL,
   TextAnswer           VARCHAR2(255)                   NULL,
   CONSTRAINT PK_SurvResAns PRIMARY KEY (SurveyResultAnswerId)
);

ALTER TABLE OptOutSurvey
    ADD CONSTRAINT FK_OptOutSurv_Surv FOREIGN KEY (SurveyId)
        REFERENCES Survey (SurveyId)
            ON DELETE CASCADE;

ALTER TABLE OptOutSurveyProgram
    ADD CONSTRAINT FK_OptOutSurvProg_OptOutSurv FOREIGN KEY (OptOutSurveyId)
        REFERENCES OptOutSurvey (OptOutSurveyId)
            ON DELETE CASCADE;

ALTER TABLE OptOutSurveyProgram
    ADD CONSTRAINT FK_OptOutSurvProg_LMProgWebPub FOREIGN KEY (ProgramId)
        REFERENCES LMProgramWebPublishing (ProgramId)
            ON DELETE CASCADE;

ALTER TABLE OptOutSurveyResult
    ADD CONSTRAINT FK_OptOutSurvRes_OptOutEventLo FOREIGN KEY (OptOutEventLogId)
        REFERENCES OptOutEventLog (OptOutEventLogId)
            ON DELETE CASCADE;

ALTER TABLE OptOutSurveyResult
    ADD CONSTRAINT FK_OptOutSurvRes_SurvRes FOREIGN KEY (SurveyResultId)
        REFERENCES SurveyResult (SurveyResultId)
            ON DELETE CASCADE;

ALTER TABLE Survey
    ADD CONSTRAINT FK_Surv_EC FOREIGN KEY (EnergyCompanyId)
        REFERENCES EnergyCompany (EnergyCompanyID)
            ON DELETE CASCADE;

ALTER TABLE SurveyQuestion
    ADD CONSTRAINT FK_SurvQuest_Surv FOREIGN KEY (SurveyId)
        REFERENCES Survey (SurveyId)
            ON DELETE CASCADE;

ALTER TABLE SurveyQuestionAnswer
    ADD CONSTRAINT FK_SurvQuestAns_SurvQuest FOREIGN KEY (SurveyQuestionId)
        REFERENCES SurveyQuestion (SurveyQuestionId)
            ON DELETE CASCADE;

ALTER TABLE SurveyResult
    ADD CONSTRAINT FK_SurvRes_Surv FOREIGN KEY (SurveyId)
        REFERENCES Survey (SurveyId)
            ON DELETE CASCADE;

ALTER TABLE SurveyResult
    ADD CONSTRAINT FK_SurvRes_CustAcct FOREIGN KEY (AccountId)
        REFERENCES CustomerAccount (AccountId);

ALTER TABLE SurveyResultAnswer
    ADD CONSTRAINT FK_SurvResAns_SurvRes FOREIGN KEY (SurveyResultId)
        REFERENCES SurveyResult (SurveyResultId)
            ON DELETE CASCADE;

ALTER TABLE SurveyResultAnswer
    ADD CONSTRAINT FK_SurvResAns_SurvQues FOREIGN KEY (SurveyQuestionId)
        REFERENCES SurveyQuestion (SurveyQuestionId);

ALTER TABLE SurveyResultAnswer
    ADD CONSTRAINT FK_SurvResAns_SurvQuesAnsw FOREIGN KEY (SurveyQuestionAnswerId)
        REFERENCES SurveyQuestionAnswer (SurveyQuestionAnswerId);
/* End YUK-8932 */

/* Start YUK-8976 */
INSERT INTO YukonRoleProperty VALUES(-10820, -108, 'Session Timeout (minutes)','30','The amount of idle time (in minutes) before a user''s session will expire.');
/* End YUK-8976 */

/* Start YUK-8960 */
DELETE FROM DeviceTypeCommand WHERE CommandId = -123;
DELETE FROM Command WHERE CommandId = -123;
/* End YUK-8960 */

/* Start YUK-6576 */
DELETE FROM Display WHERE DisplayNum = -4;

UPDATE YukonRoleProperty
SET Description = 'The following settings are valid: HIDE_MACS(0x00001000), HIDE_CAPCONTROL(0x00002000), HIDE_LOADCONTROL(0x00004000), HIDE_ALL_DISPLAYS(0x0000F000), HIDE_ALARM_COLORS(0x80000000)'
WHERE RolePropertyId = -10104;
/* End YUK-6576 */

/* Start YUK-8994 */
CREATE TABLE RDSTransmitter  (
   PAObjectId           NUMBER                          NOT NULL,
   SiteAddress          NUMBER                          NOT NULL,
   EncoderAddress       NUMBER                          NOT NULL,
   TransmitSpeed        FLOAT                           NOT NULL,
   GroupType            VARCHAR2(3)                     NOT NULL,
   CONSTRAINT PK_RDSTran PRIMARY KEY (PAObjectId)
);

ALTER TABLE RDSTransmitter
    ADD CONSTRAINT FK_RDSTran_PAO FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
/* End YUK-8994 */

/* Start YUK-8991 */
CREATE TABLE StaticPAOInfo  (
   StaticPAOInfoId      NUMBER                          NOT NULL,
   PAObjectId           NUMBER                          NOT NULL,
   InfoKey              VARCHAR2(128)                   NOT NULL,
   Value                VARCHAR2(128)                   NOT NULL,
   CONSTRAINT PK_StatPAOInfo PRIMARY KEY (StaticPAOInfoId)
);

CREATE UNIQUE INDEX Indx_PAObjId_InfoKey_UNQ ON StaticPAOInfo (
    PAObjectId ASC,
    InfoKey ASC
);

ALTER TABLE StaticPAOInfo
    ADD CONSTRAINT FK_StatPAOInfo FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
/* End YUK-8991 */

/* Start YUK-9005 */
INSERT INTO YukonRoleProperty VALUES(-20018,-200,'Event Logs','false','Controls access to event logs feature.');
/* End YUK-9005 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.2', 'Matt K', '20-AUG-2010', 'Latest Update', 1);