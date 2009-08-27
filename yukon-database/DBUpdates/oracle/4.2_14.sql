/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7750 */
ALTER TABLE CapControlSubstation ADD MapLocationId VARCHAR2(64); 
UPDATE CapControlSubstation SET MapLocationId = '0';
ALTER TABLE CapControlSubstation MODIFY MapLocationId NOT NULL; 
/* End YUK-7750 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '27-AUG-2009', 'Latest Update', 14);