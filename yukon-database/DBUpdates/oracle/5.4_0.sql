/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10174 */ 
INSERT INTO YukonServices 
VALUES (18, 'CymDISTMessageListener', 
        'classpath:com/cannontech/services/cymDISTService/cymDISTServiceContext.xml', 
        'ServiceManager');
/* End YUK-10174 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.4', 'Garrett D', '07-OCT-2011', 'Latest Update', 0 );
