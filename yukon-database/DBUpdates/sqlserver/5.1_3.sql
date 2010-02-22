/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8403 */
ALTER TABLE ApplianceCategory ADD ConsumerSelectable CHAR(1);
GO
UPDATE ApplianceCategory SET ConsumerSelectable = ' ';
GO
ALTER TABLE ApplianceCategory ALTER COLUMN ConsumerSelectable CHAR(1) NOT NULL;
GO
/* End YUK-8403 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
