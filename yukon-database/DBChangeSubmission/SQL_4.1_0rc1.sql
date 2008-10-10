/* 4.1_0fc3 to 4.1_0rc1 changes.  These are changes to 4.1 that have been made since 4.1_0fc3*/
/* This script must be run manually using the SQL tool and not the DBToolsFrame tool. */

/* START SPECIAL BLOCK */

UPDATE YukonGroupRole 
SET GroupRoleId = -2500 
WHERE GroupRoleId = -1150 
AND GroupId = -2 
AND RolePropertyId = -21300;

UPDATE YukonGroupRole 
SET GroupRoleId = -2501 
WHERE GroupRoleId = -1151 
AND GroupId = -2 
AND RolePropertyId = -21301;

UPDATE YukonGroupRole 
SET GroupRoleId = -2502 
WHERE GroupRoleId = -1152 
AND GroupId = -2 
AND RolePropertyId = -21302;

UPDATE YukonGroupRole 
SET GroupRoleId = -2503 
WHERE GroupRoleId = -1153
AND GroupId = -2 
AND RolePropertyId = -21303;

UPDATE YukonGroupRole 
SET GroupRoleId = -2504 
WHERE GroupRoleId = -1154
AND GroupId = -2 
AND RolePropertyId = -21304;

UPDATE YukonGroupRole 
SET GroupRoleId = -2505 
WHERE GroupRoleId = -1155
AND GroupId = -2 
AND RolePropertyId = -21305;

UPDATE YukonGroupRole 
SET GroupRoleId = -2506 
WHERE GroupRoleId = -1156
AND GroupId = -2 
AND RolePropertyId = -21306;

UPDATE YukonGroupRole 
SET GroupRoleId = -2507 
WHERE GroupRoleId = -1157
AND GroupId = -2 
AND RolePropertyId = -21307;
