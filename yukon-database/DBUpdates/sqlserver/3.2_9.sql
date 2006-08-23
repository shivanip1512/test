/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table DCItemType add Description varchar(320);
go
update DCItemType set Description='(none)';
go
alter table DCItemType alter column Description varchar(320) not null;
go

insert into YukonListEntry values( 11, 1, 0, 'IVR Login', 3 );
go

alter table pointunit add DecimalDigits numeric;
go
update pointunit set DecimalDigits = 0;
go
alter table pointunit alter column DecimalDigits numeric not null;
go

/* @error ignore */
alter table DynamicPointAlarming drop constraint FKf_DynPtAl_SysL;
go

alter table DeviceMeterGroup alter column CollectionGroup varchar(50) not null;
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
