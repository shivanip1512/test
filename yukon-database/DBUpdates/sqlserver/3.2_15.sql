/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

insert into YukonRoleProperty values(-1114,-2,'Inherit Parent App Cats','true','If part of a member structure, should appliance categories be inherited from the parent.');
go

insert into YukonListEntry values (1064,1005,-1,'ExpressStat Heat Pump',1313);
go

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */