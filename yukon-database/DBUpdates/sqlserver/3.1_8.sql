/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

insert into YukonRoleProperty values(-20302,-203,'3 Tier Direct Control','false','Allows access to the 3-tier load management web interface');
insert into yukongrouprole values (-777,-301,-203,-20302,'(none)');
insert into YukonGroupRole values (-1277,-2,-203,-20302,'(none)');
insert into YukonUserRole values (-777,-1,-203,-20302,'(none)');

create table DynamicPAOInfo (
   EntryID              numeric              not null,
   PAObjectID           numeric              not null,
   Owner                varchar(64)          not null,
   Info                 varchar(128)         not null,
   Value                varchar(128)         not null
);
go
alter table DynamicPAOInfo
   add constraint PK_DYNPAOINFO primary key  (EntryID);
go
alter table DynamicPAOInfo
   add constraint AK_DYNPAO_OWNKYUQ unique  (EntryID, PAObjectID, Owner);
go
alter table DynamicPAOInfo
   add constraint FK_DynPAOInfo_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
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
insert into CTIDatabase values('3.1', 'Ryan', '5-JUN-2005', 'Manual version insert done', 8);