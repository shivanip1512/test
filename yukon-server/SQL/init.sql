connect system/manager @yukon
create tablespace yukon datafile 'yukon.ora' size 10M autoextend on;
create tablespace yukontemp datafile 'yukontemp.ora' size 5M autoextend on;
create user yukon identified by yukon default tablespace yukon temporary tablespace yukontemp quota unlimited on yukon quota unlimited on yukontemp;
grant create session to yukon;
grant create table to yukon;
grant create view to yukon;
disconnect
connect yukon/yukon @yukon;
@core\CreateAll.sql
@dynamic\LastPointScan.sql
@lm\CreateAll.sql
@macs\CreateAll.sql
@core\systemlog.sql
@core\sysdevices.sql
@core\usereventlog_view.sql
@tdc\addall.sql
@calculation\createall.sql
@capcontrol\createall.sql
disconnect

