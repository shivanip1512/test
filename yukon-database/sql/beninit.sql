connect system/manager @yukon
create tablespace ben datafile 'ben1.ora' size 10M autoextend on;
create tablespace bentemp datafile 'ben1temp.ora' size 5M autoextend on;
create user ben identified by ben default tablespace ben temporary tablespace bentemp quota unlimited on ben quota unlimited on bentemp;
grant create session to ben;
grant create table to ben;
grant create view to ben;
disconnect
connect ben/ben @yukon;
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

