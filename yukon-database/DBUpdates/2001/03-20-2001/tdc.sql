update display set name = 'All Categories' where displaynum = -1;
update display set name = 'All Areas' where displaynum = -2;

update displaycolumns set title='Description' where (displaynum>=1 and displaynum<=14) and typenum=12;
update displaycolumns set title='Additional Info' where (displaynum>=1 and displaynum<=14) and typenum=10;