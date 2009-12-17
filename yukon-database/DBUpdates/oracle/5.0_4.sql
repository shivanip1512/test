/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8159 */ 
UPDATE YukonServices 
SET ServiceClass = 'classpath:com/cannontech/services/optout/optOutContext.xml' 
WHERE ServiceId = 10;
/* End YUK-8159 */ 

/* Start YUK-8172 */ 
ALTER TABLE PAOProperty DROP CONSTRAINT FK_PAOProp_YukonPAO;
ALTER TABLE PAOProperty
   ADD CONSTRAINT FK_PAOProp_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId)
         ON DELETE CASCADE;
/* End YUK-8172 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
