/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8159 */ 
UPDATE YukonServices 
SET ServiceClass = 'classpath:com/cannontech/services/optout/optOutContext.xml' 
WHERE ServiceId = 10;
/* End YUK-8159 */ 

/* Start YUK-8172 */ 
ALTER TABLE PAOProperty DROP CONSTRAINT FK_PAOProp_YukonPAO;
GO
ALTER TABLE PAOProperty
   ADD CONSTRAINT FK_PAOProp_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId)
         ON DELETE CASCADE;
GO
/* End YUK-8172 */ 

/* Start YUK-8158 */ 
ALTER TABLE PAOFavorites DROP CONSTRAINT FK_PAOFav_YukonPAO;
GO
ALTER TABLE PAOFavorites
   ADD CONSTRAINT FK_PAOFav_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId)
         ON DELETE CASCADE;
GO
ALTER TABLE PAOFavorites DROP CONSTRAINT FK_PAOFav_YukonUser;
GO
ALTER TABLE PAOFavorites
   ADD CONSTRAINT FK_PAOFav_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserId)
         ON DELETE CASCADE;
GO
ALTER TABLE PAORecentViews DROP CONSTRAINT FK_PAORecentViews_YukonPAO;
GO
ALTER TABLE PAORecentViews
   ADD CONSTRAINT FK_PAORecentViews_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId)
      	  ON DELETE CASCADE;
GO
/* End YUK-8158 */


/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
