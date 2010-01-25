/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8288 */
CREATE UNIQUE INDEX Indx_LMPWP_DevId_UNQ ON LMProgramWebPublishing (
DeviceId ASC
);
/* End YUK-8288 */

/* Start YUK-8160 */
ALTER TABLE CCurtEEParticipantSelection 
ALTER COLUMN ConnectionAudit VARCHAR(2550) NOT NULL; 
/* End YUK-8160 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
