/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10919 */
INSERT INTO YukonServices
VALUES (20,'OpcService','classpath:com/cannontech/services/opc/opcService.xml','ServiceManager');
/* End YUK-10919 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.4', 'Garrett D', '24-APR-2012', 'Latest Update', 4 );