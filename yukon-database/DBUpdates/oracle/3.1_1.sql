/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table MacSchedule add template NUMBER;
update MacSchedule set template = 0 where commandfile is not null;

delete from YukonListEntry where ListID in
(select ListID from YukonSelectionList where ListName like 'OptOutPeriod%');
delete from ECToGenericMapping where MappingCategory = 'YukonSelectionList' and ItemID in
(select ListID from YukonSelectionList where ListName like 'OptOutPeriod%' and ListID > 2000);
delete from YukonSelectionList where ListName like 'OptOutPeriod%' and ListID > 2000;

insert into YukonListEntry values (1241,1049,1,'1 Day',1);
insert into YukonListEntry values (1242,1049,2,'2 Days',2);
insert into YukonListEntry values (1243,1049,3,'3 Days',3);
insert into YukonListEntry values (1244,1049,4,'4 Days',4);
insert into YukonListEntry values (1245,1049,5,'5 Days',5);
insert into YukonListEntry values (1246,1049,6,'6 Days',6);
insert into YukonListEntry values (1247,1049,7,'7 Days',7);

insert into YukonRoleProperty values(-21002,-210,'Work Order Report','true','Controls whether to allow reporting on work orders');

insert into YukonRole values (-700,'CapControl Settings','CapBank Control','Allows the user to control access and display settings of the CapControl module.');
insert into YukonRoleProperty values(-70000,-700,'Access','false','Sets accessibility to the CapControl module.');
insert into YukonRoleProperty values(-70001,-700,'Allow Control','true','Enables or disables field and local controls for the given user');
insert into YukonRoleProperty values(-70002,-700,'Hide Reports','false','Sets the visibility of reports.');
insert into YukonRoleProperty values(-70003,-700,'Hide Graphs','false','Sets the visibility of graphs.');
insert into YukonRoleProperty values(-70004,-700,'Hide One-Lines','false','Sets the visibility of one-line displays.');

insert into YukonListEntry values (1307,1051,0,'Alt Track #',2707);









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