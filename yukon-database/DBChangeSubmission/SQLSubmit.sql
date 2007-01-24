/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

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
