drop tablespace tempDatabase including contents and datafiles;
drop tablespace tempDatabase_temp including contents and datafiles;
drop user tempDatabase cascade;
BEGIN
    SYS.DBMS_SCHEDULER.RUN_JOB('CLEANTEMPFILES');
END
select 1 from dual;