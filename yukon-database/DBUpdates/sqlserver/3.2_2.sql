/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/


if exists (select 1
            from  sysobjects
           where  id = object_id('SettlementConfig')
            and   type = 'U')
   drop table SettlementConfig;
go


create table SettlementConfig (
   ConfigID			numeric				not null,
   FieldName		varchar(64)		not null,
   FieldValue		varchar(64)		not null,
   CTISettlement	varchar(32)		not null,
   YukonDefID		numeric				not null,
   Description		varchar(128)		not null,
   EntryID			numeric				not null,
   RefEntryID		numeric				not null
);
go

insert into SettlementConfig values (-1, 'CDI Rate', '0', 'HECO', '3651', 'Controlled Demand Incentive, Dollars per kW.', 0, 0);
insert into SettlementConfig values (-2, 'ERI Rate', '0', 'HECO', '3651', 'Energy Reduction Incentive, Dollars per kWh.', 0, 0);
insert into SettlementConfig values (-3, 'UF Delay', '0', 'HECO', '3651', 'Under frequency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-4, 'Dispatched Delay', '0', 'HECO', '3651', 'Dispatched Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-5, 'Emergency Delay', '0', 'HECO', '3651', 'Emergency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-6, 'Allowed Violations', '0', 'HECO', '3651', 'Max number of allowed violations, deviations.', 0, 0);
insert into SettlementConfig values (-7, 'Restore Duration', '0', 'HECO', '3651', 'Duration for event restoration to occur, in minutes.', 0, 0);
Insert into SettlementConfig values (-8, 'Demand Charge', '0', 'HECO', '3651', 'Rate Schedule billing demand charge', 0, 0);
go

insert into YukonSelectionList values (1066,'A','(none)','Energy Company Settlement configuration selection','Settlement','Y');
go

insert into YukonRoleProperty values(-10921,-109,'Settlement Reports Group Label','Settlement','Label (header) for Settlement group reports.');
insert into YukonRoleProperty values(-10911,-109,'Settlement Reports Group','false','Access to Settlement group reports.');
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
insert into CTIDatabase values('3.2', 'Ryan', '06-JAN-2005', 'Manual version insert done', 2 );
