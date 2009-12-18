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

/* Start YUK-8117 */
UPDATE LMThermostatManualEvent 
SET OperationStateID = 1214
WHERE OperationStateID = 1211;

UPDATE LMThermostatManualEvent 
SET FanOperationID = 1222
WHERE FanOperationID = 1221;
/* End YUK-8117 */

/* Start YUK-8174 */
CREATE UNIQUE INDEX Indx_LMPDG_DevId_LMGrpDev_UNQ ON LMProgramDirectGroup(
DeviceId ASC,
LMGroupDeviceId ASC
);
/* End YUK-8174 */ 

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
