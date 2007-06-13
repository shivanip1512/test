/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/



/********** Jason - adding 3 new mct4xxx commands  ************/
insert into command values(-130, 'getvalue lp channel ?''Channel #'' ?''Enter Start Date: xx/xx/xxxx'' ?''Enter End Date: xx/xx/xxxx''', 'Read LP Channel Data', 'ALL MCT-4xx Series')
insert into command values(-131, 'getvalue lp status', 'Read LP Channel Data Status', 'ALL MCT-4xx Series')
insert into command values(-132, 'getvalue lp cancel', 'Read LP Channel Data Cancel', 'ALL MCT-4xx Series')

insert into devicetypecommand values(-640, -130, 'MCT-410 kWh Only', 21, 'N', -1)
insert into devicetypecommand values(-641, -130, 'MCT-410CL', 27, 'N', -1)
insert into devicetypecommand values(-642, -130, 'MCT-410FL', 27, 'N', -1)
insert into devicetypecommand values(-643, -130, 'MCT-410GL', 27, 'N', -1)
insert into devicetypecommand values(-644, -130, 'MCT-410IL', 27, 'N', -1)
insert into devicetypecommand values(-645, -130, 'MCT-410iLE', 21, 'N', -1)
insert into devicetypecommand values(-646, -130, 'MCT-430A', 23, 'N', -1)
insert into devicetypecommand values(-647, -130, 'MCT-430S4', 23, 'N', -1)
insert into devicetypecommand values(-648, -130, 'MCT-430SN', 23, 'N', -1)
insert into devicetypecommand values(-649, -130, 'MCT-470', 29, 'N', -1)

insert into devicetypecommand values(-650, -131, 'MCT-410 kWh Only', 22, 'N', -1)
insert into devicetypecommand values(-651, -131, 'MCT-410CL', 28, 'N', -1)
insert into devicetypecommand values(-652, -131, 'MCT-410FL', 28, 'N', -1)
insert into devicetypecommand values(-653, -131, 'MCT-410GL', 28, 'N', -1)
insert into devicetypecommand values(-654, -131, 'MCT-410IL', 28, 'N', -1)
insert into devicetypecommand values(-655, -131, 'MCT-410iLE', 22, 'N', -1)
insert into devicetypecommand values(-656, -131, 'MCT-430A', 24, 'N', -1)
insert into devicetypecommand values(-657, -131, 'MCT-430S4', 24, 'N', -1)
insert into devicetypecommand values(-658, -131, 'MCT-430SN', 24, 'N', -1)
insert into devicetypecommand values(-659, -131, 'MCT-470', 30, 'N', -1)

insert into devicetypecommand values(-660, -132, 'MCT-410 kWh Only', 23, 'N', -1)
insert into devicetypecommand values(-661, -132, 'MCT-410CL', 29, 'N', -1)
insert into devicetypecommand values(-662, -132, 'MCT-410FL', 29, 'N', -1)
insert into devicetypecommand values(-663, -132, 'MCT-410GL', 29, 'N', -1)
insert into devicetypecommand values(-664, -132, 'MCT-410IL', 29, 'N', -1)
insert into devicetypecommand values(-665, -132, 'MCT-410iLE', 23, 'N', -1)
insert into devicetypecommand values(-666, -132, 'MCT-430A', 25, 'N', -1)
insert into devicetypecommand values(-667, -132, 'MCT-430S4', 25, 'N', -1)
insert into devicetypecommand values(-668, -132, 'MCT-430SN', 25, 'N', -1)
insert into devicetypecommand values(-669, -132, 'MCT-470', 31, 'N', -1)

/********** END Jason - adding 3 new mct4xxx commands  ************/

/********** Jess's Calc Dropdown additions  ************/

insert into yukonlistentry values (139, 100, 0, 'Get Point Limit', 0)
insert into yukonlistentry values (140, 100, 0, 'Get Interval Minutes', 0)
insert into yukonlistentry values (141, 100, 0, 'Intervals To Value', 0)
insert into yukonlistentry values (142, 100, 0, 'Linear Slope', 0)

/********** Jess's Calc Dropdown additions no more  ************/


/** this is really old..., but is wrong in the creation scripts and never made it into an update **/
alter table CICUSTOMERPOINTDATA 
   DROP constraint PK_CICUSTOMERPOINTDATA;
alter table CICUSTOMERPOINTDATA
   add constraint PK_CICUSTOMERPOINTDATA primary key (CustomerID, Type);
/** end tmack **/

