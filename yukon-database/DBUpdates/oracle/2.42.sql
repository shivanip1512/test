/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/


alter table LMGroupMCT drop constraint FK_LMGrMC_Dev;

alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID);



/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.42', 'Ryan', '1-Aug-2003', ' ');