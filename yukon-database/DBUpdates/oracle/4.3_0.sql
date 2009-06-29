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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
