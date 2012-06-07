/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10982 */
UPDATE BillingFileFormats
SET FormatType = 'CMEP-MDM1'
WHERE FormatType = 'CMEP';
/* End YUK-10982 */

/* Start YUK-10978 */
UPDATE ArchiveValuesExportField 
SET FieldType = 'ADDRESS' 
WHERE FieldType = 'DLC_ADDRESS';
/* End YUK-10978 */

/* Start YUK-10963 */
/* @error ignore-begin */
ALTER TABLE MACSimpleSchedule
    DROP CONSTRAINT FK_MACSIMPLE_MACSCHED_ID;
ALTER TABLE MACSimpleSchedule
    ADD CONSTRAINT FK_MACSimpSch_MACSch FOREIGN KEY (ScheduleId)
        REFERENCES MACSchedule (ScheduleId);
UPDATE MACSimpleSchedule
SET TargetPAObjectId = 0
WHERE TargetPAObjectId IS NULL;
ALTER TABLE MACSimpleSchedule
    MODIFY TargetPAObjectId NUMBER NOT NULL;

ALTER TABLE PorterResponseMonitorErrorCode
    MODIFY RuleId NUMBER NOT NULL;

ALTER TABLE OptOutTemporaryOverride
    MODIFY OptOutValue VARCHAR2(25);

UPDATE AcctThermostatScheduleEntry
SET CoolTemp = 72
WHERE CoolTemp IS NULL;
ALTER TABLE AcctThermostatScheduleEntry
    MODIFY CoolTemp FLOAT NOT NULL;
    
UPDATE AcctThermostatScheduleEntry
SET HeatTemp = 72
WHERE HeatTemp IS NULL;
ALTER TABLE AcctThermostatScheduleEntry
    MODIFY HeatTemp FLOAT NOT NULL;
/* @error ignore-end */
/* End YUK-10963 */

/* Start YUK-10981 */
CREATE TABLE tempDynamicPaoStatistics (
   DynamicPAOStatisticsId NUMBER               not null,
   PAObjectId             NUMBER               not null,
   StatisticType          VARCHAR2(16)         not null,
   StartDateTime          DATE                 not null,
   Requests               NUMBER               not null,
   Attempts               NUMBER               not null,
   Completions            NUMBER               not null,
   CommErrors             NUMBER               not null,
   ProtocolErrors         NUMBER               not null,
   SystemErrors           NUMBER               not null
);

INSERT INTO tempDynamicPaoStatistics 
    SELECT 
        MIN(DynamicPaoStatisticsId), 
        PAObjectId, 
        'Lifetime',
        TO_DATE('2000-JAN-01 00:00', 'YYYY-MON-DD HH24::MI'),
        SUM(requests), 
        SUM(attempts), 
        SUM(completions), 
        SUM(CommErrors), 
        SUM(ProtocolErrors), 
        SUM(SystemErrors) 
    FROM DynamicPAOStatistics 
    WHERE statistictype = 'Lifetime' 
    GROUP BY PAObjectId;

DELETE FROM DynamicPAOStatistics WHERE StatisticType = 'Lifetime';

INSERT INTO DynamicPAOStatistics 
    SELECT * FROM tempDynamicPaoStatistics;

DROP TABLE tempDynamicPaoStatistics;
/* End YUK-10981 */    
    
/* Start YUK-11023 */
INSERT INTO YukonRoleProperty VALUES (-70027, -700, 'Enable Importer', 'false', 'Allows access to the Cap Control importers');
/* End YUK-11023 */

/* Start YUK-11025 */
INSERT INTO YukonListEntry VALUES (1045, 1005, 0, 'LCR-6200(RFN)', 1324);
INSERT INTO YukonListEntry VALUES (1046, 1005, 0, 'LCR-6600(RFN)', 1325);

INSERT INTO StateGroup VALUES (-18, 'LCR Service Status', 'Status');

INSERT INTO State VALUES (-18, 0, 'Unknown', 9, 6, 0);
INSERT INTO State VALUES (-18, 1, 'In Service', 0, 6, 0);
INSERT INTO State VALUES (-18, 2, 'Out of Service', 1, 6, 0);
INSERT INTO State VALUES (-18, 3, 'Temporarily Out of Serivice', 7, 6, 0);
/* End YUK-11025 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
