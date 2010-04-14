/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8420 */
UPDATE YukonRoleProperty 
SET Description = 'Controls whether to perform inventory checking while creating or updating hardware information' 
WHERE RolePropertyId = -20153; 
/* End YUK-8420 */

/* Start YUK-8577 */
/* @error ignore-begin */
DROP INDEX LMProgramWebPublishing.Indx_LMPWP_DevId_UNQ;
/* @error ignore-end */
/* End YUK-8577 */

/* Start YUK-8573 */
INSERT INTO YukonRoleProperty VALUES (-90041,-900,'Schedule Stop Checked By Default', 'true', 'Controls whether the schedule stop check box is checked by default in demand response.');
INSERT INTO YukonRoleProperty VALUES (-90042,-900,'Start Time Default',' ', 'Specifies the default start time for a control event in the format (hh:mm). It will use the current time if no time has been supplied');
/* End YUK-8573 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
