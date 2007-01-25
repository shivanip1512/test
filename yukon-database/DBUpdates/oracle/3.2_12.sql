/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

update mspVendor set CompanyName = 'Cannon' where vendorid = 1;
update mspVendor set AppName = 'Yukon' where vendorid = 1;

/* @error ignore-begin */
insert into billingfileformats values(21, 'SIMPLE_TOU');
insert into billingfileformats values(22, 'EXTENDED_TOU');
/* @error ignore-end */

insert into billingfileformats values (-23, 'Big Rivers Elec Coop');
insert into billingfileformats values(-24, 'INCODE (Extended TOU)');

alter table DeviceMeterGroup modify MeterNumber VARCHAR2(50);

insert into stategroup (StateGroupId, Name, GroupType) select max(stategroupid) + 1 , 'TrueFalse', 'Status' from stategroup;
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 0, 'False', 0, 6, 0 from stategroup where name = 'TrueFalse';
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 1, 'True', 1, 6, 0 from stategroup where name = 'TrueFalse';

insert into stategroup (StateGroupId, Name, GroupType) select max(stategroupid) + 1 , 'RemoteLocal', 'Status' from stategroup;
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 0, 'Remote', 0, 6, 0 from stategroup where name = 'RemoteLocal';
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 1, 'Local', 1, 6, 0 from stategroup where name = 'RemoteLocal';

update point set stategroupid = (select stategroupid from stategroup where name = 'TrueFalse') where 
paobjectid in (select paobjectid from yukonpaobject where type like 'CBC 702%') and pointtype = 'Status' and (pointoffset  = 2 or pointoffset > 3);

update point set stategroupid = (select stategroupid from stategroup where name = 'RemoteLocal') where 
paobjectid in (select paobjectid from yukonpaobject where type like 'CBC 702%') and pointtype = 'Status' and pointname like 'Control Mode%';


/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Jon', '25-Jan-2007', 'Latest Update', 12 );