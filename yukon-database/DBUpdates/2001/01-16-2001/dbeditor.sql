/* CapBank db's only */
update capbank set operationalstate='Switched' where operationalstate<>'Fixed';
INSERT into point  values (-5,  'System', 'Cap Control', 0, 'Default', 0, 'N', 'N', 0, 'S', 5  ,'None', 0);

/* SQLServer  *****/
/* alter table point alter column pointtype varchar(20) not null; */
alter table point modify pointtype varchar2(20);

update point set pointtype='CalcAnalog' where pointtype='Calculated';
update point set pointtype='PulseAccumulator' where pointtype='Accumulator';

/* SQLServer  *****/
/* alter table calcbase alter column updatetype varchar(16) not null; */
alter table calcbase modify updatetype varchar2(16);