create tablespace tempDBCreation_9_0_0 datafile 'tempDBCreation_9_0_0.ora' size 10M autoextend on;
create temporary tablespace tempDBCreation_9_0_0_temp tempfile 'tempDBCreation_9_0_0_temp.ora' size 5M autoextend on;
create user tempDBCreation_9_0_0 identified by tempDBCreation_9_0_0 default tablespace tempDBCreation_9_0_0 temporary tablespace tempDBCreation_9_0_0_temp quota unlimited on tempDBCreation_9_0_0;
grant dba to tempDBCreation_9_0_0;