/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/


alter table LMGroupMCT drop constraint FK_LMGrMC_Dev;
go
alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID);
go



/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.42', 'Ryan', '1-Aug-2003', ' ');
go