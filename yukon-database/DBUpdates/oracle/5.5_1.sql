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
        RAISE_APPLICATION_ERROR(-20001, 'Your system currently has a potential role conflict.  Please go to YUK-11012 to find out the correct way to resolve this issue.');
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

/* Start YUK-11013 */
INSERT INTO YukonRole VALUES(-110,'Password Policy','Application','Handles the password rules and restrictions for a given group.');

INSERT INTO YukonRoleProperty VALUES(-11001,-110,'Password History','5','The number of different passwords retained before a user can reuse a password.');
INSERT INTO YukonRoleProperty VALUES(-11002,-110,'Minimum Password Length','8','The minimum number of characters a password has to be before it passes.');
INSERT INTO YukonRoleProperty VALUES(-11003,-110,'Minimum Password Age','0','The number of hours a user has to wait before changing their password again.');
INSERT INTO YukonRoleProperty VALUES(-11004,-110,'Maximum Password Age','60','The number of days before a login is expired and needs to be changed.');
INSERT INTO YukonRoleProperty VALUES(-11005,-110,'Lockout Threshold','5','The number of login attempts before an account is locked out.');
INSERT INTO YukonRoleProperty VALUES(-11006,-110,'Lockout Duration','20','The number of minutes a login is disabled when an account is locked out.');

INSERT INTO YukonRoleProperty VALUES(-11050,-110,'Policy Quality Check','3','The number of policy rules that are required to be able to save a password.');
INSERT INTO YukonRoleProperty VALUES(-11051,-110,'Policy Rule - Uppercase Characters','true','Uppercase characters count toward the required policy quality check.  (A, B, C, ... Z)');
INSERT INTO YukonRoleProperty VALUES(-11052,-110,'Policy Rule - Lowercase Characters','true','Lowercase characters count toward the required policy quality check.  (a, b, c, ... z)');
INSERT INTO YukonRoleProperty VALUES(-11053,-110,'Policy Rule - Base 10 Digits','true','Base 10 digits count toward the required policy quality check.  (0, 1, 2, ... 9)');
INSERT INTO YukonRoleProperty VALUES(-11054,-110,'Policy Rule - Nonalphanumeric Characters','true','Nonalphanumic characters count toward the required password rules check.  (~, !, @, #, $, %, ^, &, *, _, -, +, =, `, |, (, ), {, }, [, ], :, ", '', <, >, ,, ., ?, /)');
INSERT INTO YukonRoleProperty VALUES(-11055,-110,'Policy Rule - Unicode Characters','true','Any Unicode character that is categorized as an alphabetic character but is not uppercase or lowercase count toward the policy quality check. This includes Unicode characters from Asian languages.');

ALTER TABLE YukonUser
    ADD LastChangedDate DATE;
UPDATE YukonUser
SET LastChangedDate = SYSDATE;
ALTER TABLE YukonUser
    MODIFY LastChangedDate DATE NOT NULL;

ALTER TABLE YukonUser
	ADD ForceReset CHAR(1);
UPDATE YukonUser
SET ForceReset = 'N';
ALTER TABLE YukonUser
	MODIFY ForceReset CHAR(1) NOT NULL;

CREATE TABLE PasswordHistory  (
    PasswordHistoryId    NUMBER                          NOT NULL,
    UserId               NUMBER                          NOT NULL,
    Password             VARCHAR2(64)                    NOT NULL,
    AuthType             VARCHAR2(16)                    NOT NULL,
    PasswordChangedDate  DATE                            NOT NULL,
    CONSTRAINT PK_PasswordHistory PRIMARY KEY (UserId)
);

ALTER TABLE PasswordHistory
    ADD CONSTRAINT FK_PassHist_YukonUser FOREIGN KEY (UserId)
        REFERENCES YukonUser (UserId)
           ON DELETE CASCADE;

/* Migrating all of the current passwords to the password history table. */
/* @start-block */
DECLARE
    v_UserId                      NUMBER;
    v_MaxPasswordHistoryId        NUMBER;
	
    CURSOR userId_curs IS SELECT DISTINCT YU.UserId FROM YukonUser YU;
BEGIN
    OPEN userId_curs;
    LOOP
        FETCH userId_curs into v_UserId;
        EXIT WHEN userId_curs%NOTFOUND;

            SELECT NVL(MAX(PasswordHistoryId)+1, 0) INTO v_MaxPasswordHistoryId
            FROM PasswordHistory;

            INSERT INTO PasswordHistory
	            SELECT v_MaxPasswordHistoryId, YU.UserId, YU.Password, YU.AuthType, YU.LastChangedDate
	            FROM YukonUser YU
	            WHERE YU.UserId = v_UserId;

    END LOOP;
    CLOSE userId_curs;
END;
/
/* @end-block */
/* End YUK-11013 */
    
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
