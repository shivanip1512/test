/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8145 */
INSERT INTO YukonRoleProperty VALUES(-1705,-8,'Database Migration File Location','/Server/Export/','File location of the database migration export process.'); 
/* End YUK-8145 */

/* Start YUK-8119 */
ALTER TABLE OptOutTemporaryOverride
ADD ProgramId NUMBER;
/* End YUK-8119 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
