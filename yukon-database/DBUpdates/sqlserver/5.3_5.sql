/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 
      
/* Start YUK-10174 */ 
DELETE FROM YukonServices 
    WHERE ServiceID = 18 AND ServiceName = 'CymDISTMessageListener';
/* End YUK-10174 */
    
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.3', 'Garrett D', '07-OCT-2011', 'Latest Update', 5 );
