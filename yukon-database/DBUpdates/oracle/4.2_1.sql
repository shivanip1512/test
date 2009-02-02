/******************************************/
/**** Oracle DBupdates   		       ****/
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
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-20015,-200,'Manage Indexes','true','Controls access to manually build Lucene indexes.');
INSERT INTO YukonRoleProperty VALUES(-20016,-200,'View Logs','true','Controls access to view or download log files.');
/* @error ignore-end */
/* End YUK-6518 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

/* __YUKON_VERSION__ */