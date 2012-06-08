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
    errorFlagCount INT;
BEGIN
    SELECT COUNT(*) INTO errorFlagCount
    FROM YukonUserRole YUR
    JOIN YukonUserGroup YUG ON YUR.UserId = YUG.UserId
    JOIN YukonGroupRole YGR ON YGR.GroupId = YUG.GroupId
    WHERE YUR.RoleId = YGR.RoleId
      AND YUR.RolePropertyId = YGR.RolePropertyId;

    IF 0 < errorFlagCount THEN
        RAISE_APPLICATION_ERROR(-20001, 'Your system currently has a potential role conflict.  Please go to YUK-00000 to find out the correct way to resolve this issue.');
    END IF;
END;
/
/* @end-block */

/* Taking care of the system role properties first.  Lets Migrate the Admin user role settings to the Admin login group and the DefaultCTI user to its own login group */
INSERT INTO YukonGroupRole 
	SELECT (UserRoleId-2800), -1, RoleId, RolePropertyId, Value 
	FROM YukonUserRole
	WHERE UserId = -1;

INSERT INTO YukonGroup VALUES (-3, 'DefaultCTI Login Grp', 'The defaultCTI login group settings');
INSERT INTO YukonGroupRole 
	SELECT (UserRoleId-1600), -3, RoleId, RolePropertyId, Value 
	FROM YukonUserRole
	WHERE UserId = -100;

/* Migrating the rest of the YukonUserRole data */
/* @start-block */
DECLARE
    v_UserId              NUMBER;
    v_Username            VARCHAR2(64);
    v_MaxGroupId          NUMBER;
    
    CURSOR userId_curs IS SELECT DISTINCT YUR.UserId, YU.Username 
                          FROM YukonUserRole YUR 
                          JOIN YukonUser YU ON YU.UserId = YUR.UserId 
                          WHERE YU.UserId NOT IN (-100, -1);
BEGIN
    OPEN userId_curs;
    LOOP
        FETCH userId_curs into v_UserId, v_Username;
        EXIT WHEN userId_curs%NOTFOUND;

        SELECT MAX(GroupId)+1 INTO v_MaxGroupId
        FROM YukonGroup;
        INSERT INTO YukonGroup VALUES (v_MaxGroupId, CONCAT(v_Username,' login group'), 'Generated login group');

        INSERT INTO YukonGroupRole
            SELECT (SELECT MAX(GroupRoleId) FROM YukonGroupRole)
                   ,v_MaxGroupId
                   ,RoleId
                   ,RolePropertyId
                   ,Value
                   FROM YukonUserRole YUR
                   WHERE YUR.UserId = v_UserId;
    END LOOP;
    CLOSE userId_curs;
END;
/
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
