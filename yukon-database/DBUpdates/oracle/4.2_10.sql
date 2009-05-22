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
UPDATE FDRInteface 
SET PossibleDirections = 'Receive,Send' 
WHERE InterfaceId = 26;
/* End YUK-7503 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
