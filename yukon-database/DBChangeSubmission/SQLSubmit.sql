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

/** Please update the 4.0 update and creation scripts with this new version of DYNAMICBILLINGFIELD -Jason **/
create table DYNAMICBILLINGFIELD (
   id                   numeric              not null,
   FormatID             numeric              not null,
   FieldName            varchar(50)          not null,
   FieldOrder           numeric              not null,
   FieldFormat          varchar(50)          null,
   MaxLength            numeric              not null
)


/************************************************************************************/
/*  YUK-4529 "Reset CopCount" Expresscom Serial common command needs to be renamed  */
/*  Matt Fisher 10/15/2007                                                          */
/*  Please update the 3.5 and head update and creation scripts to use these lines:  */

insert into command values(-65, 'putconfig xcom raw 0x05 0x00', 'Turn Off Test Light', 'ExpresscomSerial');
insert into command values(-67, 'putconfig xcom main 0x01 0x80', 'Clear Comm Loss Counter', 'ExpresscomSerial');

/*  instead of these:  */

insert into command values(-65, 'putconfig xcom raw 0x05 0x00', 'Reset CopCount', 'ExpresscomSerial');
insert into command values(-67, 'putconfig xcom main 0x01 0x80', 'Clear Comm Loss COunter', 'ExpresscomSerial');

/************************************************************************************/

