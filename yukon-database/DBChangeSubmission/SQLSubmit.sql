/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/
/*
changes for the progress oneline phase 2
elliot
*/
/*start oneline progress fla*/
INSERT INTO StateGroup VALUES( 4, '1LNSUBSTATE', 'Status' );
INSERT INTO StateGroup VALUES( 5, '1LNVERIFY', 'Status' );



INSERT INTO State VALUES( 5, 0, 'Verify All', 2, 6 , 0);
INSERT INTO State VALUES( 5, 1, 'Verify Stop', 6, 6 , 0);


INSERT INTO State VALUES( 4, 0, 'Enable', 5, 6 , 0);
INSERT INTO State VALUES( 4, 1, 'Disable',9, 6 , 0);
INSERT INTO State VALUES( 4, 2, 'Pending',7, 6 , 0);
INSERT INTO State VALUES( 4, 3, 'Alt - Enabled', 2, 6 , 0);
/*end oneline progress fla*/
/***********************************************************************************/


/*************** JULIE ADDITIONS: added 2 new state groups for points off of 2 way cbc's. Customer complaints about interpretting open/closed for point values.******/
insert into stategroup (StateGroupId, Name, GroupType) select max(stategroupid) + 1 , 'TrueFalse', 'Status' from stategroup;
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 0, 'False', 0, 6, 0 from stategroup where name = 'TrueFalse';
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 1, 'True', 1, 6, 0 from stategroup where name = 'TrueFalse';
go
insert into stategroup (StateGroupId, Name, GroupType) select max(stategroupid) + 1 , 'RemoteLocal', 'Status' from stategroup;
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 0, 'Remote', 0, 6, 0 from stategroup where name = 'RemoteLocal';
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 1, 'Local', 1, 6, 0 from stategroup where name = 'RemoteLocal';
go
update point set stategroupid = (select stategroupid from stategroup where name = 'TrueFalse') where 
paobjectid in (select paobjectid from yukonpaobject where type like 'CBC 702%') and pointtype = 'Status' and (pointoffset  = 2 or pointoffset > 3);
go
update point set stategroupid = (select stategroupid from stategroup where name = 'RemoteLocal') where 
paobjectid in (select paobjectid from yukonpaobject where type like 'CBC 702%') and pointtype = 'Status' and pointname like 'Control Mode%';
go
/*************** END JULIE ADDITIONS *********************/

/*************** JULIE ADDITIONS: corrected dnp point definitions for cbc uv and ov op counts **************************/
update point set pointoffset = 2 where pointname like 'UV op count%';
update point set pointoffset = 3 where pointname like 'OV op count%';
/*************** END JULIE ADDITIONS *********************/


/*************** tmack ADDITIONS: new table for MACS **************************/
create table MeterReadLog (
   MeterReadLogID       numeric              not null,
   DeviceID             numeric              not null,
   RequestID            numeric              not null,
   Timestamp            datetime             not null,
   StatusCode           numeric              not null
);
go

alter table MeterReadLog
   add constraint PK_METERREADLOG primary key  (MeterReadLogID);
go

insert into sequencenumber values (1,'MeterReadLog');
/*************** END tmack ADDITIONS *********************/


