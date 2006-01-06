/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/
drop table settlementConfig cascade constraints;

create table SettlementConfig (
   ConfigID			number			not null,
   FieldName		varchar2(64)	not null,
   FieldValue		varchar2(64)	not null,
   CTISettlement	varchar2(32)	not null,
   YukonDefID		number			not null,
   Description		varchar2(128)	not null,
   EntryID			number			not null,
   RefEntryID		number			not null
);

insert into SettlementConfig values (-1, 'CDI Rate', '0', 'HECO', '3651', 'Controlled Demand Incentive, Dollars per kW.', 0, 0);
insert into SettlementConfig values (-2, 'ERI Rate', '0', 'HECO', '3651', 'Energy Reduction Incentive, Dollars per kWh.', 0, 0);
insert into SettlementConfig values (-3, 'UF Delay', '0', 'HECO', '3651', 'Under frequency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-4, 'Dispatched Delay', '0', 'HECO', '3651', 'Dispatched Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-5, 'Emergency Delay', '0', 'HECO', '3651', 'Emergency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-6, 'Allowed Violations', '0', 'HECO', '3651', 'Max number of allowed violations, deviations.', 0, 0);
insert into SettlementConfig values (-7, 'Restore Duration', '0', 'HECO', '3651', 'Duration for event restoration to occur, in minutes.', 0, 0);
insert into SettlementConfig values (-8, 'Demand Charge', '0', 'HECO', '3651', 'Rate Schedule billing demand charge', 0, 0);

insert into YukonSelectionList values (1066,'A','(none)','Energy Company Settlement configuration selection','Settlement','Y');

insert into YukonRoleProperty values(-10921,-109,'Settlement Reports Group Label','Settlement','Label (header) for Settlement group reports.');
insert into YukonRoleProperty values(-10911,-109,'Settlement Reports Group','false','Access to Settlement group reports.');







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
