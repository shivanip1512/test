alter table GraphDataSeries add Type VARCHAR2(12);
update GraphDataSeries set Type='graph';
alter table GraphDataSeries modify Type not null;
/* SQL SERVER */
/* alter table GraphDataSeries add Type VARCHAR(12) not null default 'graph';*/
