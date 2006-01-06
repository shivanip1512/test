/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

/* @error ignore */
create table CommandGroup  (
   CommandGroupID       NUMBER                          not null,
   CommandGroupName     VARCHAR2(60)                    not null
);

/* @error ignore */
insert into CommandGroup values (-1, 'Default Commands');

/* @error ignore */
alter table CommandGroup
   add constraint PK_COMMANDGROUP primary key (CommandGroupID);

/* @error ignore */
alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID);

create table SettlementConfig  (
   ConfigID             NUMBER                          not null,
   FieldName            VARCHAR2(64)                    not null,
   FieldValue           VARCHAR2(64)                    not null,
   CTISettlement        VARCHAR2(32)                    not null,
   YukonDefID           NUMBER                          not null,
   Description          VARCHAR2(128)                   not null,
   EntryID              NUMBER                          not null,
   RefEntryID           NUMBER                          not null
);

insert into SettlementConfig values (-1, 'CDI Rate', '0', 'HECO', '3651', 'Controlled Demand Incentive, Dollars per kW.', 0, 0);
insert into SettlementConfig values (-2, 'ERI Rate', '0', 'HECO', '3651', 'Energy Reduction Incentive, Dollars per kWh.', 0, 0);
insert into SettlementConfig values (-3, 'UF Delay', '0', 'HECO', '3651', 'Under frequency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-4, 'Dispatched Delay', '0', 'HECO', '3651', 'Dispatched Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-5, 'Emergency Delay', '0', 'HECO', '3651', 'Emergency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-6, 'Allowed Violations', '0', 'HECO', '3651', 'Max number of allowed violations, deviations.', 0, 0);
insert into SettlementConfig values (-7, 'Restore Duration', '0', 'HECO', '3651', 'Duration for event restoration to occur, in minutes.', 0, 0);
insert into SettlementConfig values (-8, 'Demand Charge', '0', 'HECO', '3651', 'Rate Schedule billing demand charge', 0, 0);

alter table SettlementConfig
   add constraint PK_SETTLEMENTCONFIG primary key (ConfigID);

insert into YukonSelectionList values (1066,'A','(none)','Energy Company Settlement Types','Settlement','Y');



/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.3', 'Ryan', '06-Jan-2005', 'Manual version insert done', 0 );
