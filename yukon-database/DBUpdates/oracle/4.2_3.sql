/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7122 */
ALTER TABLE CCurtEEPricingWindow
MODIFY EnergyPrice NUMBER(19,6);
/* End YUK-7122 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
