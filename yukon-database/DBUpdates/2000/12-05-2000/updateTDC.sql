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