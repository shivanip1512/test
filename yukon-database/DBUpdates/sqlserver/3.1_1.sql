/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table MACSchedule add template numeric null;
go
update macschedule set template = 0 where commandfile is not null;
go

delete from YukonListEntry where ListID in
(select ListID from YukonSelectionList where ListName like 'OptOutPeriod%');
delete from ECToGenericMapping where MappingCategory = 'YukonSelectionList' and ItemID in
(select ListID from YukonSelectionList where ListName like 'OptOutPeriod%' and ListID > 2000);
delete from YukonSelectionList where ListName like 'OptOutPeriod%' and ListID > 2000;

insert into YukonListEntry values (1241,1049,1,'1 Day',24);
insert into YukonListEntry values (1242,1049,2,'2 Days',48);
insert into YukonListEntry values (1243,1049,3,'3 Days',72);
insert into YukonListEntry values (1244,1049,4,'4 Days',96);
insert into YukonListEntry values (1245,1049,5,'5 Days',120);
insert into YukonListEntry values (1246,1049,6,'6 Days',144);
insert into YukonListEntry values (1247,1049,7,'7 Days',168);

insert into YukonRoleProperty values(-21002,-210,'Work Order Report','true','Controls whether to allow reporting on work orders');

insert into YukonRole values (-700,'CapControl Settings','CapBank Control','Allows the user to control access and display settings of the CapControl module.');
insert into YukonRoleProperty values(-70000,-700,'Access','false','Sets accessibility to the CapControl module.');
insert into YukonRoleProperty values(-70001,-700,'Allow Control','true','Enables or disables field and local controls for the given user');
insert into YukonRoleProperty values(-70002,-700,'Hide Reports','false','Sets the visibility of reports.');
insert into YukonRoleProperty values(-70003,-700,'Hide Graphs','false','Sets the visibility of graphs.');
insert into YukonRoleProperty values(-70004,-700,'Hide One-Lines','false','Sets the visibility of one-line displays.');

insert into YukonListEntry values (1307,1051,0,'Alt Track #',2707);

/* @error ignore */
alter table LMProgramConstraints drop column AvailableSeasons;
go

update LMProgram set constraintid = 0 where constraintid in
(select constraintid from lmprogramconstraints where
AvailableWeekDays = 'YYYYYYYN' and MaxHoursDaily = 0 and
MaxHoursMonthly = 0 and MaxHoursSeasonal = 0 and MaxHoursAnnually = 0 and
MinActivateTime = 0 and MinRestartTime = 0 and MaxDailyOps = 0 and
MaxActivateTime = 0 and HolidayScheduleID = 0 and SeasonScheduleID = 0);
go

delete from LMProgramConstraints where constraintid not in (select constraintid from LMProgram) and constraintid <> 0;
go

insert into YukonListEntry values (1137,1011,0,'Alt Track #',1607);
insert into YukonListEntry values (1063,1005,-1,'SA Simple',1312);

update ctidatabase set version = '3.0' where version = '3.00';







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