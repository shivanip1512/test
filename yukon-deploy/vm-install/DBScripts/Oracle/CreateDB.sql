create tablespace tempDatabase datafile 'tempDatabase.ora' size 10M autoextend on;
create temporary tablespace tempDatabase_temp tempfile 'tempDatabase_temp.ora' size 5M autoextend on;
create user tempDatabase identified by tempDatabase default tablespace tempDatabase temporary tablespace tempDatabase_temp quota unlimited on tempDatabase;
grant dba to tempDatabase;