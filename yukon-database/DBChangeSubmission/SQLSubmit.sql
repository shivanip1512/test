/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!*/

/*Fixes YUK-4109, needs to be in creation and update scripts for 3.5 and HEAD*/
insert into YukonGroupRole values(-20,-1,-1,-1019,'(none)');
insert into YukonGroupRole values(-21,-1,-1,-1020,'(none)');

/* Needed in 3.4.6, 3.5, and Head  TS*/
insert into yukonroleproperty values (-100011,-1000, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
insert into yukonroleproperty values (-100012,-1000, 'Substation Last Update Timestamp', 'true', 'is last update timstamp shown for substations');
insert into yukonroleproperty values (-100106,-1001, 'Feeder Last Update Timestamp', 'true', 'is last update timstamp shown for feeders');
insert into yukonroleproperty values (-100203,-1002, 'CapBank Last Update Timestamp', 'true', 'is last update timstamp shown for capbanks');
update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100008;
update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100007;


/* Needed in 3.4.6, 3.5, and Head  8-6-7*/
insert into yukonroleproperty values (-100105,-1001, 'Target', 'true', 'is target stat displayed');

/********* Needed for 3.5 and Head    SN 8-8-07 */
/* Scheduler Role */
insert into YukonRole values(-212,'Scheduler','Operator','Operator access to Scheduler'); 
/* Scheduler Role properties */
insert into YukonRoleProperty values(-21200,-212,'Enable/Disable Schedule','true','Right to enable or disable a schedule'); 
/**********************************/



/* MACS YUK-4118 fix 8-13-2007 in  3.4.6  3.5 and head*/
ALTER TABLE DeviceReadJobLog DROP CONSTRAINT FK_DEVICERE_FK_DRJOBL_MACSCHED
/************/