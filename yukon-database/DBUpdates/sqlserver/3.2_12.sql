/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

update mspVendor set CompanyName = 'Cannon' where vendorid = 1;
update mspVendor set AppName = 'Yukon' where vendorid = 1;

/* @error ignore-begin */
insert into billingfileformats values(21, 'SIMPLE_TOU');
insert into billingfileformats values(22, 'EXTENDED_TOU');
/* @error ignore-end */

insert into billingfileformats values (-23, 'Big Rivers Elec Coop');


/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Jon', '15-Jan-2007', 'Latest Update', 12 );
