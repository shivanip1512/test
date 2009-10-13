/******************************************/
/**** SQLServer 2000 DBupdates         ****/
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

/* Start YUK-7917 */
INSERT INTO YukonRoleProperty VALUES (-90010,-900,'Control Areas','true','Controls access to view Control Areas');
INSERT INTO YukonRoleProperty VALUES (-90011,-900,'Scenarios','true','Controls access to view Scenarios');

INSERT INTO YukonRoleProperty VALUES (-90020,-900,'Control Area State','true','Controls access to view Control Area State');
INSERT INTO YukonRoleProperty VALUES (-90021,-900,'Control Area Trigger Value/Threshold','true','Controls access to view Control Area Trigger Value/Threshold');
INSERT INTO YukonRoleProperty VALUES (-90022,-900,'Control Area Trigger Peak/Projection','true','Controls access to view Control Area Trigger Peak/Projection');
INSERT INTO YukonRoleProperty VALUES (-90023,-900,'Control Area Trigger ATKU','true','Controls access to view Control Area Trigger ATKU');
INSERT INTO YukonRoleProperty VALUES (-90024,-900,'Control Area Priority','true','Controls acces to view Control Area Priority');
INSERT INTO YukonRoleProperty VALUES (-90025,-900,'Control Area Time Window','true','Controls access to view Control Area Time Window');
INSERT INTO YukonRoleProperty VALUES (-90026,-900,'Control Area Load Capacity','true','Controls access to view Control Area Load Capacity');

INSERT INTO YukonRoleProperty VALUES (-90027,-900,'Program State','true','Controls access to view Program State');
INSERT INTO YukonRoleProperty VALUES (-90028,-900,'Program Start','true','Controls access to view Program Start');
INSERT INTO YukonRoleProperty VALUES (-90029,-900,'Program Stop','true','Controls access to view Program Stop');
INSERT INTO YukonRoleProperty VALUES (-90030,-900,'Program Current Gear','true','Controls access to view Program Current Gear');
INSERT INTO YukonRoleProperty VALUES (-90031,-900,'Program Priority','true','Controls access to view Program Priority');
INSERT INTO YukonRoleProperty VALUES (-90032,-900,'Program Reduction','true','Controls access to view Program Reduction');
INSERT INTO YukonRoleProperty VALUES (-90033,-900,'Program Load Capacity','true','Controls access to view Program Load Capacity');

INSERT INTO YukonRoleProperty VALUES (-90034,-900,'Load Group State','true', 'Controls access to view Load Group State');
INSERT INTO YukonRoleProperty VALUES (-90035,-900,'Load Group Last Action','true', 'Controls access to view Load Group Last Action');
INSERT INTO YukonRoleProperty VALUES (-90036,-900,'Load Group Control Statistics','true', 'Controls access to view Load Group Control Statistics');
INSERT INTO YukonRoleProperty VALUES (-90037,-900,'Load Group Reduction','true', 'Controls access to view Load Group Reduction');
INSERT INTO YukonRoleProperty VALUES (-90038,-900,'Load Group Load Capacity','true', 'Controls access to view Load Group Load Capacity'); 
/* End YUK-7917 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
