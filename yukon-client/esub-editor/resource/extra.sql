/* testing specific */

/* esub default */

insert into yukonrole values(-200,'ESUBVIEW','Esub','true');
insert into yukonrole values(-201,'ESUBEDIT','Esub','true');
insert into yukonrole values(-202,'ESUBCONTROL','Esub','true');

insert into yukongroup values(-200,'Esub Users');
insert into yukongroup values(-201,'Esub Operators');

insert into yukongrouprole values(-200,-200,'(none)');
insert into yukongrouprole values(-201,-200,'(none)');
insert into yukongrouprole values(-201,-201,'(none)');
insert into yukongrouprole values(-201,-202,'(none)');

/* esub */

/* create esub demo customer 1 */
insert into yukonrole values(200,'DEMOGROUP1','EsubGroup','(none)');

insert into yukongroup values(200,'Demo Group 1');

insert into yukongrouprole values(200,200,'(default)');
insert into yukongrouprole values(200,-100,'/demo1/sublist.html');

insert into yukonuser values(200,'esub1','esub1',0,'01-JAN-02','Enabled');
insert into yukonuser values(201,'esuboper1','esuboper1',0,'01-JAN-02','Enabled');

insert into yukonusergroup values(200,200); 
insert into yukonusergroup values(200,-200);
insert into yukonusergroup values(201,200);
insert into yukonusergroup values(201,-201);

/* create esub demo customer 2 */
insert into yukonrole values(201,'DEMOGROUP2','EsubGroup','(none)');

insert into yukongroup values(201,'Demo Group 2');

insert into yukongrouprole values(201,201,'(default)');
insert into yukongrouprole values(201,-100,'/demo2/sublist.html');

insert into yukonuser values(202,'esub2','esub2',0,'01-JAN-02','Enabled');
insert into yukonuser values(203,'esuboper2','esuboper2',0,'01-JAN-02','Enabled');

insert into yukonusergroup values(202,201);
insert into yukonusergroup values(202,-200);
insert into yukonusergroup values(203,201);
insert into yukonusergroup values(203,-201);
