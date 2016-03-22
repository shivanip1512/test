/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-15182 */
/* @error ignore-begin */
INSERT INTO State VALUES(-20, 14, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 15, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 16, 'N/A', 9, 6, 0);
/* @error ignore-end */
/* End YUK-15182 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '31-JAN-2016', 'Latest Update', 0, GETDATE());*/