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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
