/* NOT SURE!!!*/
/*INSERT into point  values (-1000, 'System', 'Dummy' , 0, 'Default', 0, 'N', 'N', 0, 'S', 11 ,'None', 0);*/

update displaycolumns set typenum=12 where typenum=9 and displaynum>=1 and displaynum<=14;

/* RAWPOINTHISTORY UPDATE BEGIN */
update displaycolumns set title='Value' where title='Description' and displaynum=3;
update displaycolumns set title='Quality' where title='Action' and displaynum=3;
delete from displaycolumns where displaynum=3 and ordering=6;
update displaycolumns set typenum=9 where typenum=12 and displaynum=3;
/* RAWPOINTHISTORY UPDATE END */