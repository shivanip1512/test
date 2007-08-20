/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* @error ignore-begin */
create table dynamicccarea ( AreaID numeric not null, additionalflags varchar(20) not null );
go
alter table dynamicccarea
   add constraint FK_ccarea_Dynccarea foreign key (areaID)
      references Capcontrolarea (areaID);
go
insert into dynamicccarea (areaid, additionalflags) select areaid, 'NNNNNNNNNNNNNNNNNNNN' from capcontrolarea; 
/* @error ignore-end */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */