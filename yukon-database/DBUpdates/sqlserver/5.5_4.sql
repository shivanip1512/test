/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11429 */
ALTER TABLE Regulator
ADD vTapChange FLOAT;

UPDATE Regulator
SET vTapChange = 0.75;

ALTER TABLE Regulator
ALTER COLUMN vTapChange FLOAT NOT NULL;
/* End YUK-11429 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
