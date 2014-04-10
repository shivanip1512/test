/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13225 */
ALTER TABLE EstimatedLoadFormula
    ADD CONSTRAINT AK_EstimatedLoadFormula_Name UNIQUE (Name);
/* End YUK-13225 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '31-APR-2014', 'Latest Update', 10, SYSDATE);*/