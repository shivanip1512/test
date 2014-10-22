create table tempA (id NUMERIC);
drop tablespace tempDatabase including contents and datafiles;
drop tablespace tempDatabase_temp including contents and datafiles;
drop user tempDatabase cascade;
BEGIN
    SYS.DBMS_SCHEDULER.RUN_JOB('CLEANTEMPFILES');
END;
/
drop table tempA;