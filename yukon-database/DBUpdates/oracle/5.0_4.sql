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

/* Start YUK-8158 */ 
ALTER TABLE PAOFavorites DROP CONSTRAINT FK_PAOFav_YukonPAO;
ALTER TABLE PAOFavorites
   ADD CONSTRAINT FK_PAOFav_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId)
      	ON DELETE CASCADE;

ALTER TABLE PAOFavorites DROP CONSTRAINT FK_PAOFav_YukonUser;
ALTER TABLE PAOFavorites
   ADD CONSTRAINT FK_PAOFav_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserId)
      	ON DELETE CASCADE;

ALTER TABLE PAORecentViews DROP CONSTRAINT FK_PAORecentViews_YukonPAO;
ALTER TABLE PAORecentViews
   ADD CONSTRAINT FK_PAORecentViews_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId)
      	ON DELETE CASCADE;
/* End YUK-8158 */

/* Start YUK-8104 */
UPDATE YukonRoleProperty
SET DefaultValue = 'false'
WHERE RolePropertyId = -20216;
/* End YUK-8104 */

/* Start YUK-8140 */
DELETE FROM PersistedSystemValue 
WHERE Name = 'VALIDATION_ENGINE_LAST_CHANGE_ID';

UPDATE ValidationMonitor 
SET EvaluatorStatus = 'DISABLED' 
WHERE ValidationMonitorName = 'Default All Meters';
/* End YUK-8140 */

/* Start YUK-8401 */
DELETE FROM DeviceConfigurationDeviceMap;
DELETE FROM DeviceConfigurationItem; 
DELETE FROM DeviceConfiguration;
/* End YUK-8401 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.0', 'Matt K', '13-JAN-2010', 'Latest Update', 4);
