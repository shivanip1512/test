/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/

insert into CTIDatabase values('2.38', 'Ryan', '6-NOV-2002', 'Added a column to DynamicLMGroup and a Windows Service row to display');


insert into display values(-4, 'Yukon Server', 'Static Displays', 'Yukon Servers', 'com.comopt.windows.WinServicePanel');
go




/* Add a column to the DynamicLMGroup table */
delete from DynamicLMGroup;
go
alter table dynamiclmgroup drop constraint PK_DYNAMICLMGROUP
go

alter TABLE DynamicLMGroup add LMProgramID NUMERIC not null DEFAULT 0;
go
alter table DynamicLMGroup
   add constraint PK_DYNAMICLMGROUP primary key (DeviceID, LMProgramID)
go
alter table DynamicLMGroup
   add constraint FK_DyLmGr_LmPrDGr foreign key (LMProgramID)
      references LMProgramDirect (DeviceID)
go

create index Indx_SYSLG_PtId on SYSTEMLOG (
   POINTID ASC
)
go

create index Indx_SYSLG_Date on SYSTEMLOG (
   DATETIME ASC
)
go

create index Indx_SYSLG_PtIdDt on SYSTEMLOG (
   POINTID ASC,
   DATETIME ASC
)
go