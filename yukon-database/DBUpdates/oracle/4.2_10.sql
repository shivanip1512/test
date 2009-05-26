/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7257 */
/* @error ignore-begin */
DROP INDEX INDX_LMThermSeaEnt_SeaId;
DROP INDEX INDX_LMThermSeaEntry_SeaId;
/* @error ignore-end */
CREATE INDEX INDX_LMThermSeaEntry_SeaId ON LMThermostatSeasonEntry (
   SeasonId ASC
);
/* End YUK-7257 */

/* Start YUK-7503 */
UPDATE FDRInterface 
SET PossibleDirections = 'Receive,Send' 
WHERE InterfaceId = 26;
/* End YUK-7503 */

/* Start YUK-7518 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRoleProperty VALUES(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');
/* @error ignore-end */
/* End YUK-7518 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
