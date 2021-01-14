create tablespace tempDBCreation_7_5_1 datafile 'tempDBCreation_7_5_1.ora' size 10M autoextend on;
create temporary tablespace tempDBCreation_7_5_1_temp tempfile 'tempDBCreation_7_5_1_temp.ora' size 5M autoextend on;
create user tempDBCreation_7_5_1 identified by tempDBCreation_7_5_1 default tablespace tempDBCreation_7_5_1 temporary tablespace tempDBCreation_7_5_1_temp quota unlimited on tempDBCreation_7_5_1;
grant dba to tempDBCreation_7_5_1;