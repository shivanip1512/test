/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

Update graphdataseries set type = 65 where type = 64;

insert into billingfileformats values(21, 'SIMPLE_TOU');
insert into billingfileformats values(22, 'EXTENDED_TOU');
update billingfileformats set formatid = -20 where formattype = 'IVUE_BI_T65';

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
insert into CTIDatabase values('3.1', 'DBAdmin', '21-JUNE-2006', 'Manual version insert done', 25);