update state set rawstate = (rawstate + 1) where stategroupid=-5;
update state set rawstate=0 where stategroupid=-5 and rawstate=7;

insert into display values(11, 'Priority 7 Alarms', 'Alarms and Events', 'Priority 7 Alarm Viewer', 'This display will recieve all priority 7 alarm events as they happen in the system.');
insert into display values(12, 'Priority 8 Alarms', 'Alarms and Events', 'Priority 8 Alarm Viewer', 'This display will recieve all priority 8 alarm events as they happen in the system.');
insert into display values(13, 'Priority 9 Alarms', 'Alarms and Events', 'Priority 9 Alarm Viewer', 'This display will recieve all priority 9 alarm events as they happen in the system.');
insert into display values(14, 'Priority 10 Alarms', 'Alarms and Events', 'Priority 10 Alarm Viewer', 'This display will recieve all priority 10 alarm events as they happen in the system.');


INSERT INTO State VALUES(-5, 7, 'Priority 7', 3, 6);
INSERT INTO State VALUES(-5, 8, 'Priority 8', 2, 6);
INSERT INTO State VALUES(-5, 9, 'Priority 9', 6, 6);
INSERT INTO State VALUES(-5, 10, 'Priority 10', 9, 6);

insert into displaycolumns values(11, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(11, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(11, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(11, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(11, 'User Name', 8, 5, 50 );
insert into displaycolumns values(12, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(12, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(12, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(12, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(12, 'User Name', 8, 5, 50 );
insert into displaycolumns values(13, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(13, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(13, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(13, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(13, 'User Name', 8, 5, 50 );
insert into displaycolumns values(14, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(14, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(14, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(14, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(14, 'User Name', 8, 5, 50 );


/*************/
/* 12-1-2000 */
/*************/
update display set type='Load Control Client' where displaynum=-3;
update display set type='Cap Control Client' where displaynum=-2;
update display set type='Scheduler Client' where displaynum=-1;

update display set type='Alarms and Events' where displaynum >= 1 and displaynum <=99;
update display set type='Custom Displays' where displaynum >= 100;

delete from displaycolumns where displaynum=99;
delete from display where displaynum = 99;
insert into display values(99, 'Basic User Created', 'Custom Displays', 'A Predefined Generic Display', 'This display is is used to show what a user created display looks like.');
insert into displaycolumns values(99, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(99, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(99, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(99, 'Value', 9, 4, 160 );