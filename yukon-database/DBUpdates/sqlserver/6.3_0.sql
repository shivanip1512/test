/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13472 */
INSERT INTO YukonRoleProperty VALUES (-21315, -213, 'Demand Reset', 'true', 'Controls access to Demand Reset collection action.');
/* End YUK-13472 */

/* Start YUK-13709 */
INSERT INTO YukonRoleProperty VALUES (-90047, -900, 'Allow DR Enable/Disable', 'true', 'Controls access to enable or disable control areas,load programs and load groups. Requires Allow DR Control.');
INSERT INTO YukonRoleProperty VALUES (-90048, -900, 'Allow Change Gears', 'true', 'Controls access to change gears for scenarios, control areas, and load programs. Requires Allow DR Control.');
/* End YUK-13709 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.3', '30-SEP-2014', 'Latest Update', 0, GETDATE());*/