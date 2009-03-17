/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7122 */
ALTER TABLE CCurtEEPricingWindow
ALTER COLUMN EnergyPrice NUMERIC(19,6) NOT NULL;
/* End YUK-7122 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
