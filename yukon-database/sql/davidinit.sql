connect system/manager @yukon
create tablespace david datafile 'david1.ora' size 10M autoextend on;
create tablespace davidtemp datafile 'david1temp.ora' size 5M autoextend on;
create user david identified by david default tablespace david temporary tablespace davidtemp quota unlimited on david quota unlimited on davidtemp;
grant create session to david;
grant create table to david;
grant create view to david;
disconnect
connect david/david @yukon;
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

