create tablespace yukon datafile 'yukon.ora' size 10M autoextend on;
create temporary tablespace yukontemp tempfile 'yukontemp.ora' size 5M autoextend on;

create user yukon identified by yukon default tablespace yukon temporary tablespace yukontemp quota unlimited on yukon quota unlimited on yukontemp;
grant create session to yukon;
grant create table to yukon;
grant create view to yukon;
grant create trigger to yukon;
