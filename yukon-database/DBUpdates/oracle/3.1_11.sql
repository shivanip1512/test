/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table DynamicPAOInfo drop constraint AK_DYNPAO_OWNKYUQ;

alter table DynamicPAOInfo
   add constraint AK_DYNPAO_OWNKYUQ unique  (PAObjectID, Owner, InfoKey);

alter table DynamicPAOInfo add UpdateTime date;
update DynamicPAOInfo set UpdateTime = '01-JAN-1990';
alter table DynamicPAOInfo modify UpdateTime not null;







/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '5-AUG-2005', 'Manual version insert done', 11);