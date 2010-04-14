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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
