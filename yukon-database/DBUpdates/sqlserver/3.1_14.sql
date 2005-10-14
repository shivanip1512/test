/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table DynamicLMProgramDirect add ConstraintOverride char(1);
go
update DynamicLMProgramDirect set ConstraintOverride = 'N';
go
alter table DynamicLMProgramDirect alter column ConstraintOverride char(1) not null;
go







/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '14-OCT-2005', 'Manual version insert done', 14);