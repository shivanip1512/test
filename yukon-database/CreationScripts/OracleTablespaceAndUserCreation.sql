create tablespace yukon datafile 'yukon.ora' size 10M autoextend on;
create temporary tablespace yukontemp tempfile 'yukontemp.ora' size 5M autoextend on;

create user yukon identified by yukon default tablespace yukon temporary tablespace yukontemp quota unlimited on yukon quota unlimited on yukontemp;
grant dba to yukon;
