/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!*/

/*New issue, check YUK-4334 for reference */
NEED TO CHECK TO SEE WHETHER THE USERPAOWNER TO USERPAOPERMISSION TRANSITION IN THE 3.4_0.sql file
is working correctly to move the default route group mapping for STARS energy companies.

If a default route is not loading correctly in STARS, then run the following query:
insert into UserPaoPermission (UserPaoPermissionID, UserID, PaoID, Permission) select 0, ec.userID, macro.OwnerID, 'allow LM visibility'
from EnergyCompany ec, LMGroupExpressCom exc, GenericMacro macro 
where macro.MacroType = 'GROUP' and macro.ChildID = exc.LMGroupID and exc.SerialNumber = '0' and ec.EnergyCompanyID = 0;

If an ID is returned, then the mapping failed and the default route is not correctly saved.

There are several questions we need to answer on this issue, so this can stay open for awhile.  
/*I'm marking this in here for reference until we figure out the best way to deal with this.  Jon*/



/* Role Prop changes for 3.5fc4 and HEAD -- tmack */
/* I'm okay with not "moving" the old value */
delete from YukonRoleProperty where RolePropertyId = -1113;
insert into YukonRoleProperty values(-10816, -108,'Standard Page Style Sheet',' ','A comma separated list of URLs for CSS files that will be included on every Standard Page');

/*end*/


/*start ************  Change misspelled vendor name SN 22071018 **********/
/*****  change for 3.5 update script */
update BillingFileFormats, set FormatType = 'DAFFRON' where formatType = 'DAFRON';

/* Please make sure to change the insert statements for the creation scripts in 3.5 and head  
   To be 'DAFFRON' instead of 'DAFRON'  */
/*end ************  Change misspelled vendor name SN 22071018 **********/


/*start: Add to Head only -Thain*/

alter table userPaoPermission add Allow varchar(5);
update UserPaoPermission set Allow = 'ALLOW';
alter table groupPaoPermission add Allow varchar(5);
update GroupPaoPermission set Allow = 'ALLOW';

/* 
   UserPaoPermission needs a unique constraint on (userId, paoId, permission)
   GroupPaoPermission needs a unique constraint on (groupId, paoId, permission)
*/
/*end*/

/* start: YUK-4191 Long Load Profile Widget -- mike p*/
/* New metering role property to give access to the Profile Widget */
insert into YukonRoleProperty values(-20206, -202,'Enable Profile Request','true','Access to perform profile data request');
/* end */

