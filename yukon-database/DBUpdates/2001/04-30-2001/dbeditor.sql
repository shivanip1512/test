INSERT INTO UnitMeasure VALUES ('$', 0);
INSERT INTO UnitMeasure VALUES ('Dollars', 0);

alter table device modify name VARCHAR2(60);
/* SQLServer */
/* alter table device alter column name VARCHAR(60); */

alter table point modify pointname VARCHAR2(60);
/* SQLServer */
/* alter table point alter column pointname VARCHAR(60); */

update grouprecipient set locationname='(none)',emailaddress='(none)',pagernumber='(none)' where locationid=0
