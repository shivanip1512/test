/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/

insert into CTIDatabase values('2.38', 'Ryan', '2002-NOV-6', 'Added a column to DynamicLMGroup and a Windows Service row to display');

insert into display values(-4, 'Yukon Server', 'Static Displays', 'Yukon Servers', 'com.comopt.windows.WinServicePanel');
/




/* Add a column to the DynamicLMGroup table */
delete from DynamicLMGroup;
/
alter table dynamiclmgroup drop constraint PK_DYNAMICLMGROUP
/

alter TABLE DynamicLMGroup add LMProgramID NUMBER;
update DynamicLMGroup set LMProgramID = 0;
alter TABLE DynamicLMGroup MODIFY LMProgramID NOT NULL;
/
alter table DynamicLMGroup
   add constraint PK_DYNAMICLMGROUP primary key (DeviceID, LMProgramID);
/
alter table DynamicLMGroup
   add constraint FK_DyLmGr_LmPrDGr foreign key (LMProgramID)
      references LMProgramDirect (DeviceID);
/

create index Indx_SYSLG_PtId on SYSTEMLOG (
   POINTID ASC
)
/

create index Indx_SYSLG_Date on SYSTEMLOG (
   DATETIME ASC
)
/

create index Indx_SYSLG_PtIdDt on SYSTEMLOG (
   POINTID ASC,
   DATETIME ASC
)