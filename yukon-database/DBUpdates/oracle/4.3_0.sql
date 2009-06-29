/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7455 */
UPDATE YukonRoleProperty 
SET KeyName='Opt Out Period', Description='Contains information on Opt Out Period.'
WHERE RolePropertyId IN (-20157, -40055);

DELETE FROM YukonListEntry 
WHERE ListId IN (SELECT ListId 
                 FROM YukonSelectionList 
                 WHERE ListName = 'OptOutPeriod'
                 AND ListId IN (SELECT ItemId 
                                FROM ECToGenericMapping 
                                WHERE MappingCategory LIKE 'YukonSelectionList'));

DELETE FROM ECToGenericMapping 
WHERE ItemId IN (SELECT ListId 
                 FROM YukonSelectionList 
                 WHERE ListName = 'OptOutPeriod' 
                 AND ListId IN (SELECT ItemId 
                                FROM ECToGenericMapping 
                                WHERE MappingCategory LIKE 'YukonSelectionList')); 

DELETE FROM YukonSelectionList 
WHERE ListName = 'OptOutPeriod'; 
/* End YUK-7455 */

/* Start YUK-7618 */
UPDATE YukonRoleProperty 
SET Description = 'Defines a Yukon Pao (Device) Name field alias. Valid values(0-5): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer, 4=EA Location, 5=Grid Location, 6=Service Location With Register]' 
WHERE RolePropertyId = -1600;
/* End YUK-7618 */

/* Start YUK-7461 */
INSERT INTO YukonRoleProperty VALUES (-21308,-213,'Add/Remove Points','false','Controls access to Add/Remove Points mass change action.');
/* End YUK-7461 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
