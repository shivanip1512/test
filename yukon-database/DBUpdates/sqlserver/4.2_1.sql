/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-6863 */
DELETE FROM YukonGroupRole
WHERE GroupId IN (-1, -2, -303, -100)
AND RoleId = -400;
/* End YUK-6863 */

/* Start YUK-6878 */
INSERT INTO YukonRoleProperty 
VALUES(-1603,-7,'Msp LM Interface Mapping Setup','false','Controls access to setup the MultiSpeak LM interface mappings.');
/* End YUK-6878 */

/* Start YUK-6518 */
UPDATE YukonRoleProperty
SET DefaultValue = 'true'
WHERE RolePropertyId = -20015
OR RolePropertyId = -20016;
/* End YUK-6518 */

/* Start YUK-6897 */
INSERT INTO Job
SELECT MAX(JobId)+1, 'optOutSchedulerJob', 'N', -1, 'en_US', ' ', ' '
FROM Job;
go
INSERT INTO JobScheduledRepeating
SELECT MAX(JobId), '0 0/5 * * * ?'
FROM Job;
/* End YUK-6897 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

/* __YUKON_VERSION__ */