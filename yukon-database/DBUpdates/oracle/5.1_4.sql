/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8420 */
UPDATE YukonRoleProperty 
SET Description = 'Controls whether to perform inventory checking while creating or updating hardware information' 
WHERE RolePropertyId = -20153; 
/* End YUK-8420 */

/* Start YUK-8533 */
CREATE INDEX INDX_YukonUser_Username_FB ON YukonUser(
    LOWER(Username)
);
/* End YUK-8533 */

/* Start YUK-8577 */
/* @error ignore-begin */
DROP INDEX Indx_LMPWP_DevId_UNQ;
/* @error ignore-end */
/* End YUK-8577 */

/* Start YUK-8573 */
INSERT INTO YukonRoleProperty VALUES (-90041,-900,'Schedule Stop Checked By Default', 'true', 'Controls whether the schedule stop check box is checked by default in demand response.');
INSERT INTO YukonRoleProperty VALUES (-90042,-900,'Start Time Default',' ', 'Specifies the default start time for a control event in the format (hh:mm). It will use the current time if no time has been supplied');
/* End YUK-8573 */

/* Start YUK-8569 */
INSERT INTO YukonRoleProperty VALUES (-90039,-900,'Start Now Checked By Default','true', 'Controls whether the start now checkbox is checked by default in demand response.');
INSERT INTO YukonRoleProperty VALUES (-90040,-900,'Control Duration Default','240', 'Specifies the default duration for a control event in minutes for demand response');
/* End YUK-8569 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.1', 'Matt K', '14-APR-2010', 'Latest Update', 4);
