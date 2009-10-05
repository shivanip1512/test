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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
