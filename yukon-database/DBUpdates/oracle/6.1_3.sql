/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13225 */
/* @error ignore-begin */
ALTER TABLE EstimatedLoadFormula
    ADD CONSTRAINT AK_EstimatedLoadFormula_Name UNIQUE (Name);
/* @error ignore-end */
/* End YUK-13225 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.1', '14-APR-2014', 'Latest Update', 3, SYSDATE);