/* 4.1_0fc3 to 4.1_0rc1 changes.  These are changes to 4.1 that have been made since 4.1_0fc3*/
/* This script must be run manually using the SQL tool and not the DBToolsFrame tool. */

/* START SPECIAL BLOCK */

/* Start YUK-6549 */
UPDATE YukonGroupRole 
SET GroupRoleId = -2500 
WHERE GroupRoleId = -1150 
AND GroupId = -2 
AND RolePropertyId = -21300;

UPDATE YukonGroupRole 
SET GroupRoleId = -2501 
WHERE GroupRoleId = -1151 
AND GroupId = -2 
AND RolePropertyId = -21301;

UPDATE YukonGroupRole 
SET GroupRoleId = -2502 
WHERE GroupRoleId = -1152 
AND GroupId = -2 
AND RolePropertyId = -21302;

UPDATE YukonGroupRole 
SET GroupRoleId = -2503 
WHERE GroupRoleId = -1153
AND GroupId = -2 
AND RolePropertyId = -21303;

UPDATE YukonGroupRole 
SET GroupRoleId = -2504 
WHERE GroupRoleId = -1154
AND GroupId = -2 
AND RolePropertyId = -21304;

UPDATE YukonGroupRole 
SET GroupRoleId = -2505 
WHERE GroupRoleId = -1155
AND GroupId = -2 
AND RolePropertyId = -21305;

UPDATE YukonGroupRole 
SET GroupRoleId = -2506 
WHERE GroupRoleId = -1156
AND GroupId = -2 
AND RolePropertyId = -21306;

UPDATE YukonGroupRole 
SET GroupRoleId = -2507 
WHERE GroupRoleId = -1157
AND GroupId = -2 
AND RolePropertyId = -21307;
/* End YUK-6549 */

/* Start YUK-6587 */
UPDATE YukonRoleProperty 
SET Description = 'Controls access to mass change collection actions. Includes all Mass Change actions.'
WHERE rolePropertyId = -21305;
/* End YUK-6587 */

/* Start YUK-6586 */
DELETE FROM YukonUserRole WHERE rolePropertyId = -40054; 
DELETE FROM YukonGroupRole WHERE rolePropertyId = -40054;
DELETE FROM YukonRoleProperty WHERE rolePropertyId = -40054; 
/* End YUK-6586 */

/* Start YUK-6241 */
UPDATE Display
SET description = 'This display will receive current events as they happen in the system.'
WHERE displayNum = 1;

UPDATE Display
SET description = 'This display will allow the user to select a range of dates and show the events that occurred.'
WHERE displayNum = 2;

UPDATE Display
SET description = 'This display will receive current raw point updates as they happen in the system.'
WHERE displayNum = 3;

UPDATE Display
SET description = 'This display will receive all alarm events as they happen in the system.'
WHERE displayNum = 4;

UPDATE Display
SET description = 'This display will receive all priority 1 alarm events as they happen in the system.'
WHERE displayNum = 5;

UPDATE Display
SET description = 'This display will receive all priority 2 alarm events as they happen in the system.'
WHERE displayNum = 6;

UPDATE Display
SET description = 'This display will receive all priority 3 alarm events as they happen in the system.'
WHERE displayNum = 7;

UPDATE Display
SET description = 'This display will receive all priority 4 alarm events as they happen in the system.'
WHERE displayNum = 8;

UPDATE Display
SET description = 'This display will receive all priority 5 alarm events as they happen in the system.'
WHERE displayNum = 9;

UPDATE Display
SET description = 'This display will receive all priority 6 alarm events as they happen in the system.'
WHERE displayNum = 10;

UPDATE Display
SET description = 'This display will receive all priority 7 alarm events as they happen in the system.'
WHERE displayNum = 11;

UPDATE Display
SET description = 'This display will receive all priority 8 alarm events as they happen in the system.'
WHERE displayNum = 12;

UPDATE Display
SET description = 'This display will receive all priority 9 alarm events as they happen in the system.'
WHERE displayNum = 13;

UPDATE Display
SET description = 'This display will receive all priority 10 alarm events as they happen in the system.'
WHERE displayNum = 14;

UPDATE Display
SET description = 'This display will receive all priority 11 alarm events as they happen in the system.'
WHERE displayNum = 15;

UPDATE Display
SET description = 'This display will receive all priority 12 alarm events as they happen in the system.'
WHERE displayNum = 16;

UPDATE Display
SET description = 'This display will receive all priority 13 alarm events as they happen in the system.'
WHERE displayNum = 17;

UPDATE Display
SET description = 'This display will receive all priority 14 alarm events as they happen in the system.'
WHERE displayNum = 18;

UPDATE Display
SET description = 'This display will receive all priority 15 alarm events as they happen in the system.'
WHERE displayNum = 19;

UPDATE Display
SET description = 'This display will receive all priority 16 alarm events as they happen in the system.'
WHERE displayNum = 20;

UPDATE Display
SET description = 'This display will receive all priority 17 alarm events as they happen in the system.'
WHERE displayNum = 21;

UPDATE Display
SET description = 'This display will receive all priority 18 alarm events as they happen in the system.'
WHERE displayNum = 22;

UPDATE Display
SET description = 'This display will receive all priority 19 alarm events as they happen in the system.'
WHERE displayNum = 23;

UPDATE Display
SET description = 'This display will receive all priority 20 alarm events as they happen in the system.'
WHERE displayNum = 24;

UPDATE Display
SET description = 'This display will receive all priority 21 alarm events as they happen in the system.'
WHERE displayNum = 25;

UPDATE Display
SET description = 'This display will receive all priority 22 alarm events as they happen in the system.'
WHERE displayNum = 26;

UPDATE Display
SET description = 'This display will receive all priority 23 alarm events as they happen in the system.'
WHERE displayNum = 27;

UPDATE Display
SET description = 'This display will receive all priority 24 alarm events as they happen in the system.'
WHERE displayNum = 28;

UPDATE Display
SET description = 'This display will receive all priority 25 alarm events as they happen in the system.'
WHERE displayNum = 29;

UPDATE Display
SET description = 'This display will receive all priority 26 alarm events as they happen in the system.'
WHERE displayNum = 30;

UPDATE Display
SET description = 'This display will receive all priority 27 alarm events as they happen in the system.'
WHERE displayNum = 31;

UPDATE Display
SET description = 'This display will receive all priority 28 alarm events as they happen in the system.'
WHERE displayNum = 32;

UPDATE Display
SET description = 'This display will receive all priority 29 alarm events as they happen in the system.'
WHERE displayNum = 33;

UPDATE Display
SET description = 'This display will receive all priority 30 alarm events as they happen in the system.'
WHERE displayNum = 34;

UPDATE Display
SET description = 'This display will receive all priority 31 alarm events as they happen in the system.'
WHERE displayNum = 35;

UPDATE Display
SET description = 'This display is used to show what a user created display looks like. You may edit this display to fit your own needs.'
WHERE displayNum = 99;
/* End YUK-6241 */

/* Start YUK-6240 */
UPDATE Command
SET label = 'Read freeze config from meter and enable scheduled freeze processing in Yukon.'
WHERE commandId = -142;
/* End YUK-6240 */

/* Start YUK-6628 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-20010,-200,'Auto Process Batch Configs','false','Automatically process batch configs using the DailyTimerTask.');
/* @error ignore-end */
/* End YUK-6628 */