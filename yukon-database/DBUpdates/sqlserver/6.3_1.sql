/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13797 */
DELETE FROM YukonGroupRole 
WHERE RolePropertyId IN (-10100, -10102, -10300, -10807, -10808, -20010, -20800, -20801, -20900, 
                         -20908, -40051, -40102, -40200, -70005, -70006, -100000, -100001, -100002, 
                        -100003, -100004, -100005, -100006, -100011, -100012, -100013, -100100, 
                        -100101, -100102, -100103, -100104, -100105, -100106, -100107, -100108, 
                        -100201, -100202, -100203, -100206);
 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId IN (-10100, -10102, -10300, -10807, -10808, -20010, -20800, -20801, -20900, 
                         -20908, -40051, -40102, -40200, -70005, -70006, -100000, -100001, -100002, 
                        -100003, -100004, -100005, -100006, -100011, -100012, -100013, -100100, 
                        -100101, -100102, -100103, -100104, -100105, -100106, -100107, -100108, 
                        -100201, -100202, -100203, -100206);
 
DELETE FROM YukonGroupRole
WHERE RoleId IN (-1001, -1000);
 
DELETE FROM YukonRoleProperty
WHERE RoleId IN (-1001, -1000);
 
DELETE FROM YukonRole
WHERE RoleId IN (-1001, -1000);
 
UPDATE YukonRoleProperty SET KeyName = 'Odds For Control Exists', DefaultValue = 'true'
WHERE RolePropertyId = -20700;
 
UPDATE YukonGroupRole SET Value = 'true'
WHERE RolePropertyId = -20700
AND VALUE != ' ';
 
UPDATE YukonRoleProperty SET KeyName = 'CI Curtailment Exists', DefaultValue = 'true'
WHERE RolePropertyId = -21100;
 
UPDATE YukonGroupRole SET Value = 'true'
WHERE RolePropertyId = -21100
AND VALUE != ' ';
/* End YUK-13797 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.3', '30-NOV-2014', 'Latest Update', 1, GETDATE());*/