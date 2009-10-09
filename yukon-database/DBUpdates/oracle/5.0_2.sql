/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7875 */ 
UPDATE DeviceGroup 
SET GroupName = 'CIS Substation' 
WHERE DeviceGroupId = 25 
AND GroupName = 'Substation' 
AND ParentDeviceGroupId = 1 
AND Permission = 'NOEDIT_MOD' 
AND Type = 'STATIC'; 
/* End YUK-7875 */

/* Start YUK-7882 */
INSERT INTO YukonRoleProperty VALUES (-90009,-900,'Ignore LM Pao Permissions','false','Allow access to all load management objects. Set to false to force the use of per pao permissions.');
/* End YUK-7882 */

/* Start YUK-7904 */
INSERT INTO YukonServices VALUES (7, 'ActiveMQ', 'classpath:com/cannontech/services/jms/activemq.xml', '(none)', '(none)', 'ServiceManager');
INSERT INTO YukonServices VALUES (8, 'PointInjector', 'classpath:com/cannontech/services/points/pointInjectionContext.xml', '(none)', '(none)', 'ServiceManager');
INSERT INTO YukonServices VALUES (9, 'Monitors', 'classpath:com/cannontech/services/monitors/monitorsContext.xml', '(none)', '(none)', 'ServiceManager');
/* End YUK-7904 */

/* Start YUK-7910 */
DELETE FROM DeviceTypeCommand
WHERE DeviceCommandId = -12;

UPDATE DeviceTypeCommand
SET DeviceType = 'Device Group'
WHERE DeviceType = 'CollectionGroup'
OR DeviceType = 'TestCollectionGroup';
/* End YUK-7910 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
