delete from display2waydata where displaynum < 1000;
delete from displaycolumns where displaynum < 1000;
delete from display where displaynum < 1000;
delete from templatecolumns where templatenum < 1000;
delete from columntype where typenum < 1000;
delete from template where templatenum < 1000;
delete from state where stategroupid = -5;
delete from stategroup where stategroupid = -5;

drop view display2waydata_view;

drop table displaycolumns;
drop table templatecolumns;
drop table columntype;
drop table display2waydata;
drop table display;
drop table template;