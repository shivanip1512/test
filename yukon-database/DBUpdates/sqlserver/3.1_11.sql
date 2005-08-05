/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table DynamicPAOInfo drop constraint AK_DYNPAO_OWNKYUQ;
go

alter table DynamicPAOInfo
   add constraint AK_DYNPAO_OWNKYUQ unique  (PAObjectID, Owner, InfoKey);
go


alter table DynamicPAOInfo add UpdateTime datetime;
go
update DynamicPAOInfo set UpdateTime = '1-JAN-1990';
go
alter table DynamicPAOInfo alter column UpdateTime datetime not null;
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
insert into CTIDatabase values('3.1', 'Ryan', '5-AUG-2005', 'Manual version insert done', 11);