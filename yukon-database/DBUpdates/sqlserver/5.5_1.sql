/******************************************/ 
/**** SQL Server DBupdates             ****/ 
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
        REFERENCES MACSchedule (ScheduleID);
GO
sp_rename @objname = 'MACSchedule.ScheduleID',
          @newname = 'ScheduleId',
          @objtype = 'COLUMN';
UPDATE MACSimpleSchedule
SET TargetPAObjectId = 0
WHERE TargetPAObjectId IS NULL;
ALTER TABLE MACSimpleSchedule
    ALTER COLUMN TargetPAObjectId NUMERIC NOT NULL;

ALTER TABLE PorterResponseMonitorErrorCode
    ALTER COLUMN RuleId NUMERIC NOT NULL;

ALTER TABLE OptOutTemporaryOverride
    ALTER COLUMN OptOutValue VARCHAR(25);
    
DECLARE constraint_cursor CURSOR FOR
        SELECT name from sys.default_constraints
        WHERE OBJECT_NAME(parent_object_id) = 'AcctThermostatSchedule'
          AND name like 'DF__AcctTherm__%';
OPEN constraint_cursor;
DECLARE @constraintName VARCHAR(100);
FETCH NEXT FROM constraint_cursor INTO @constraintName;
WHILE (@@FETCH_STATUS <> -1)
BEGIN
    EXECUTE('ALTER TABLE AcctThermostatSchedule DROP CONSTRAINT ' + @constraintName);
    FETCH NEXT FROM constraint_cursor INTO @constraintName;
END

UPDATE AcctThermostatScheduleEntry
SET CoolTemp = 72
WHERE CoolTemp IS NULL;
ALTER TABLE AcctThermostatScheduleEntry
    ALTER COLUMN CoolTemp NUMERIC NOT NULL;

UPDATE AcctThermostatScheduleEntry
SET HeatTemp = 72
WHERE HeatTemp IS NULL;
ALTER TABLE AcctThermostatScheduleEntry
    ALTER COLUMN HeatTemp NUMERIC NOT NULL;
/* @error ignore-end */
/* End YUK-10963 */
    
/* Start YUK-10981 */
CREATE TABLE tempDynamicPaoStatistics (
   DynamicPAOStatisticsId NUMERIC              not null,
   PAObjectId             NUMERIC              not null,
   StatisticType          VARCHAR(16)          not null,
   StartDateTime          DATETIME             not null,
   Requests               NUMERIC              not null,
   Attempts               NUMERIC              not null,
   Completions            NUMERIC              not null,
   CommErrors             NUMERIC              not null,
   ProtocolErrors         NUMERIC              not null,
   SystemErrors           NUMERIC              not null,
);
GO
INSERT INTO tempDynamicPaoStatistics 
    SELECT 
        MIN(DynamicPaoStatisticsId), 
        PAObjectId, 
        'Lifetime',
        '2000-01-01 00:00',
        SUM(requests), 
        SUM(attempts), 
        SUM(completions), 
        SUM(CommErrors), 
        SUM(ProtocolErrors), 
        SUM(SystemErrors) 
    FROM DynamicPAOStatistics 
    WHERE statistictype = 'Lifetime' 
    GROUP BY PAObjectId;
GO
DELETE FROM DynamicPAOStatistics WHERE StatisticType = 'Lifetime';
GO
INSERT INTO DynamicPAOStatistics 
    SELECT * FROM tempDynamicPaoStatistics;
GO
DROP TABLE tempDynamicPaoStatistics;
GO
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

/* Start YUK-11031 */
INSERT INTO StateGroup VALUES(-19, 'RF Demand Reset', 'Status');

INSERT INTO State VALUES(-19, 0, 'Success', 0, 6, 0);
INSERT INTO State VALUES(-19, 1, 'Not Applicable', 9, 6, 0);
INSERT INTO State VALUES(-19, 2, 'Failure', 1, 6, 0);
INSERT INTO State VALUES(-19, 3, 'Unsupported', 7, 6, 0);
/* End YUK-11031 */

/* Start YUK-11012 */
/* @start-block */
DECLARE
    @errorFlagCount NUMERIC;
BEGIN
    SET @errorFlagCount = (SELECT COUNT(*) 
                           FROM YukonUserRole YUR
                           JOIN YukonUserGroup YUG ON YUR.UserId = YUG.UserId
                           JOIN YukonGroupRole YGR ON YGR.GroupId = YUG.GroupId
                           WHERE YUR.RoleId = YGR.RoleId
                             AND YUR.RolePropertyId = YGR.RolePropertyId);

    IF 0 < @errorFlagCount BEGIN
        RAISERROR('Your thermostat default schedules contain errors. To correct this, please go to your EC default schedules & double-check them.  There will be at least one default schedule that has invalid data (too many entries).  For any schedules that have incorrect entries, choose a different mode and save.  Then edit the schedule again to restore the original mode (the entries will then be gone).  For more information see YUK-10441.', 16, 1);
    END
END;
/* @end-block */

/* Taking care of the system role properties first.  Lets Migrate the Admin user role settings to the Admin login group and the DefaultCTI user to its own login group */
INSERT INTO YukonGroupRole 
SELECT (UserRoleId-2800), -1, RoleId, RolePropertyId, Value 
FROM YukonUserRole
WHERE UserId = -1;

INSERT INTO YukonGroup VALUES(-3, 'DefaultCTI Login Grp', 'The defaultCTI login group settings');
INSERT INTO YukonGroupRole 
SELECT (UserRoleId-1600), -3, RoleId, RolePropertyId, Value 
FROM YukonUserRole
WHERE UserId = -100;

/* Migrating the rest of the YukonUserRole data */
/* @start-block */
DECLARE @v_UserId            NUMERIC;
DECLARE @v_Username          VARCHAR(64);
DECLARE @v_MaxGroupId        NUMERIC;
DECLARE userId_curs CURSOR FOR (SELECT DISTINCT YUR.UserId, YU.Username 
                                FROM YukonUserRole YUR 
                                JOIN YukonUser YU ON YU.UserId = YUR.UserId 
                                WHERE YU.UserId NOT IN (-100, -1));

OPEN userId_curs;
FETCH NEXT FROM userId_curs INTO @v_UserId, @v_Username;
WHILE @@FETCH_STATUS = 0
BEGIN
    SET @v_MaxGroupId = (SELECT MAX(GroupId)+1
                         FROM YukonGroup);
    INSERT INTO YukonGroup VALUES (@v_MaxGroupId, @v_Username + ' login group', 'Generated login group');

    INSERT INTO YukonGroupRole
        SELECT (SELECT MAX(GroupRoleId) FROM YukonGroupRole)
               ,@v_MaxGroupId
               ,RoleId
               ,RolePropertyId
               ,Value
               FROM YukonUserRole YUR
               WHERE YUR.UserId = @v_UserId;
    
    FETCH NEXT FROM userId_curs INTO @v_UserId, @v_Username;
END    
CLOSE userId_curs;
DEALLOCATE userId_curs;
/* @end-block */

ALTER TABLE YukonUserRole
    DROP CONSTRAINT FK_YkUsRl_RlPrp;

ALTER TABLE YukonUserRole
    DROP CONSTRAINT FK_YkUsRl_YkRol;

ALTER TABLE YukonUserRole
    DROP CONSTRAINT FK_YkUsRlr_YkUsr;
/* End YUK-11012 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
