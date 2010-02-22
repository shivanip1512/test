/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8403 */
ALTER TABLE ApplianceCategory ADD ConsumerSelectable CHAR(1);
UPDATE ApplianceCategory SET ConsumerSelectable = 'Y';
ALTER TABLE ApplianceCategory MODIFY ConsumerSelectable CHAR(1) NOT NULL;
/* End YUK-8403 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
