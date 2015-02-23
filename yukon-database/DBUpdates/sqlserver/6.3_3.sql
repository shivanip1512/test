/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14083 */
INSERT INTO YukonRoleProperty VALUES(-21403, -214, 'Infrastructure View', 'false', 'Controls the ability to view infrastructure devices. i.e. RF Gateways.');
/* End YUK-14083 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.3', '20-FEB-2015', 'Latest Update', 3, GETDATE());