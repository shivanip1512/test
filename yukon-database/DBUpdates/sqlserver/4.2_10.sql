/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7257 */
/* @error ignore-begin */
DROP INDEX LMThermostatSeasonEntry.INDX_LMThermSeaEnt_SeaId;
DROP INDEX LMThermostatSeasonEntry.INDX_LMThermSeaEntry_SeaId;
/* @error ignore-end */
CREATE INDEX INDX_LMThermSeaEntry_SeaId ON LMThermostatSeasonEntry (
   SeasonId ASC
);
/* End YUK-7257 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
