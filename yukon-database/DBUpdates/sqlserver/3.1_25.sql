/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

Update graphdataseries set type = 65 where type = 64;
go

insert into billingfileformats values(21, 'SIMPLE_TOU');
go
insert into billingfileformats values(22, 'EXTENDED_TOU');
go
update billingfileformats set formatid = -20 where formattype = 'IVUE_BI_T65';
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
