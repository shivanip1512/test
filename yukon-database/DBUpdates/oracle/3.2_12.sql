/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

update mspVendor set CompanyName = 'Cannon' where vendorid = 1;
update mspVendor set AppName = 'Yukon' where vendorid = 1;

/* @error ignore-begin */
insert into billingfileformats values(21, 'SIMPLE_TOU');
insert into billingfileformats values(22, 'EXTENDED_TOU');
/* @error ignore-end */

insert into billingfileformats values (-23, 'Big Rivers Elec Coop');
insert into billingfileformats values(-24, 'INCODE (Extended TOU)');

alter table DeviceMeterGroup modify MeterNumber VARCHAR2(50);

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
