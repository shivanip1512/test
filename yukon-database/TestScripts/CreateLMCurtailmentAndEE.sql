/********************************** CI Customers ************************************/

/* UserID, Username, Password, LoginCount, LastLogin, Status */
insert into yukonuser values(1,'cti1','cti1',0,'01/01/1990','Enabled');
insert into yukonuser values(2,'cti2','cti2',0,'01/01/1990','Enabled');
insert into yukonuser values(3,'cti3','cti3',0,'01/01/1990','Enabled');
insert into yukonuser values(4,'cti4','cti4',0,'01/01/1990','Enabled');
insert into yukonuser values(5,'cti5','cti5',0,'01/01/1990','Enabled');
insert into yukonuser values(6,'cti6','cti6',0,'01/01/1990','Enabled');
insert into yukonuser values(7,'cti7','cti7',0,'01/01/1990','Enabled');
insert into yukonuser values(8,'cti8','cti8',0,'01/01/1990','Enabled');
insert into yukonuser values(9,'cti9','cti9',0,'01/01/1990','Enabled');
insert into yukonuser values(10,'cti10','cti10',0,'01/01/1990','Enabled');
insert into yukonuser values(11,'cti11','cti11',0,'01/01/1990','Enabled');
insert into yukonuser values(12,'cti12','cti12',0,'01/01/1990','Enabled');
insert into yukonuser values(13,'cti13','cti13',0,'01/01/1990','Enabled');
insert into yukonuser values(14,'cti14','cti14',0,'01/01/1990','Enabled');
insert into yukonuser values(15,'cti15','cti15',0,'01/01/1990','Enabled');
insert into yukonuser values(16,'cti16','cti16',0,'01/01/1990','Enabled');

insert into yukongroup values(236, 'lmcustomer', 'Custom lm user group');

/* userid, groupid  */
insert into yukonusergroup values(1,236);
insert into yukonusergroup values(2,236);
insert into yukonusergroup values(3,236);
insert into yukonusergroup values(4,236);
insert into yukonusergroup values(5,236);
insert into yukonusergroup values(6,236);
insert into yukonusergroup values(7,236);
insert into yukonusergroup values(8,236);
insert into yukonusergroup values(9,236);
insert into yukonusergroup values(10,236);
insert into yukonusergroup values(11,236);
insert into yukonusergroup values(12,236);
insert into yukonusergroup values(13,236);
insert into yukonusergroup values(14,236);
insert into yukonusergroup values(15,236);
insert into yukonusergroup values(16,236);

insert into yukonusergroup values(1,-1);
insert into yukonusergroup values(2,-1);
insert into yukonusergroup values(3,-1);
insert into yukonusergroup values(4,-1);
insert into yukonusergroup values(5,-1);
insert into yukonusergroup values(6,-1);
insert into yukonusergroup values(7,-1);
insert into yukonusergroup values(8,-1);
insert into yukonusergroup values(9,-1);
insert into yukonusergroup values(10,-1);
insert into yukonusergroup values(11,-1);
insert into yukonusergroup values(12,-1);
insert into yukonusergroup values(13,-1);
insert into yukonusergroup values(14,-1);
insert into yukonusergroup values(15,-1);
insert into yukonusergroup values(16,-1);


/* Web Client Role */
insert into yukongrouprole values(2222, 236, -108, -10800, '/user/CILC/user_trending.jsp');
insert into yukongrouprole values(2223, 236, -108, -10801, 'ALL');
insert into yukongrouprole values(2224, 236, -108, -10802, '(none)');
insert into yukongrouprole values(2225, 236, -108, -10803, '(none)');
insert into yukongrouprole values(2226, 236, -108, -10804, '(none)');
insert into yukongrouprole values(2227, 236, -108, -10805, '(none)');

/* Customer Direct Loadcontrol role */
insert into yukongrouprole values(2228, 236, -300, -30000, '(none)');
insert into yukongrouprole values(2229, 236, -300, -30001, '(none)');

/* Customer Curtailment role */
insert into yukongrouprole values(2230, 236, -301, -30100, '(none)');
insert into yukongrouprole values(2231, 236, -301, -30101, 'Xcel Energy');

/* Customer Energy Buyback role */
insert into yukongrouprole values(2232, 236, -302, -30200, 'Peak Day Partners');
insert into yukongrouprole values(2233, 236, -302, -30201, '1-800-123-3456');

/* addressid, locationaddress1, locationAddress2, cityname, statecode, zipcode, county */
insert into address values(1, '1212 e wayzata blvd','suite 200','Wayzata','MN','55391','hennepin');

/* contactid,contfirstname,contlastname,loginid,addressid */
insert into contact values(1,'Josh','Wolberg',1,1);
insert into contact values(2,'Aaron','Lauinger',2,1);
insert into contact values(3,'Josh','Wolberg',3,1);
insert into contact values(4,'Aaron','Lauinger',4,1);
insert into contact values(5,'Josh','Wolberg',5,1);
insert into contact values(6,'Aaron','Lauinger',6,1);
insert into contact values(7,'Josh','Wolberg',7,1);
insert into contact values(8,'Aaron','Lauinger',8,1);
insert into contact values(9,'Josh','Wolberg',9,1);
insert into contact values(10,'Aaron','Lauinger',10,1);
insert into contact values(11,'Josh','Wolberg',11,1);
insert into contact values(12,'Aaron','Lauinger',12,1);
insert into contact values(13,'Josh','Wolberg',13,1);
insert into contact values(14,'Aaron','Lauinger',14,1);
insert into contact values(15,'Josh','Wolberg',15,1);
insert into contact values(16,'Aaron','Lauinger',16,1);

/* contactid,notificationcategoryid, disableflag, notification */
insert into contactnotification values(1,1,1,'N','josh@cannontech.com');
insert into contactnotification values(2,2,1,'N','aaronl@cannontech.com');
insert into contactnotification values(3,3,1,'N','josh@cannontech.com');
insert into contactnotification values(4,4,1,'N','aaronl@cannontech.com');
insert into contactnotification values(5,5,1,'N','josh@cannontech.com');
insert into contactnotification values(6,6,1,'N','aaronl@cannontech.com');
insert into contactnotification values(7,7,1,'N','josh@cannontech.com');
insert into contactnotification values(8,8,1,'N','aaronl@cannontech.com');
insert into contactnotification values(9,9,1,'N','josh@cannontech.com');
insert into contactnotification values(10,10,1,'N','aaronl@cannontech.com');
insert into contactnotification values(11,11,1,'N','josh@cannontech.com');
insert into contactnotification values(12,12,1,'N','aaronl@cannontech.com');
insert into contactnotification values(13,13,1,'N','josh@cannontech.com');
insert into contactnotification values(14,14,1,'N','aaronl@cannontech.com');
insert into contactnotification values(15,15,1,'N','josh@cannontech.com');
insert into contactnotification values(16,16,1,'N','aaronl@cannontech.com');

/* customerid, primarycontactid,customertypeid,timezone */
insert into customer values(300,1,2,'America/Chicago');
insert into customer values(301,2,2,'America/Chicago');
insert into customer values(302,3,2,'America/Chicago');
insert into customer values(303,4,2,'America/Chicago');
insert into customer values(304,5,2,'America/Chicago');
insert into customer values(305,6,2,'America/Chicago');
insert into customer values(306,7,2,'America/Chicago');
insert into customer values(307,8,2,'America/Chicago');
insert into customer values(308,9,2,'America/Chicago');
insert into customer values(310,10,2,'America/Chicago');
insert into customer values(320,11,2,'America/Chicago');
insert into customer values(330,12,2,'America/Chicago');
insert into customer values(340,13,2,'America/Chicago');
insert into customer values(350,14,2,'America/Chicago');
insert into customer values(360,15,2,'America/Chicago');
insert into customer values(370,16,2,'America/Chicago');

/* customerid,mainaddressid,customerdemandlevel,curtailmentagreement,curtailamount,companyname */
insert into cicustomerbase values(300,1,100.0,'(none)',0.0,'Curtail Cust 1');
insert into cicustomerbase values(301,1,150.0,'(none)',0.0,'Curtail Cust 2');
insert into cicustomerbase values(302,1,200.0,'(none)',0.0,'Curtail Cust 3');
insert into cicustomerbase values(303,1,250.0,'(none)',0.0,'Curtail Cust 4');
insert into cicustomerbase values(304,1,300.0,'(none)',0.0,'Curtail Cust 5');
insert into cicustomerbase values(305,1,350.0,'(none)',0.0,'Curtail Cust 6');
insert into cicustomerbase values(306,1,400.0,'(none)',0.0,'Curtail Cust 7');
insert into cicustomerbase values(307,1,450.0,'(none)',0.0,'Curtail Cust 8');
insert into cicustomerbase values(308,1,0.0,'(none)',100.0,'Exchange Cust 1');
insert into cicustomerbase values(310,1,0.0,'(none)',110.0,'Exchange Cust 2');
insert into cicustomerbase values(320,1,0.0,'(none)',120.0,'Exchange Cust 3');
insert into cicustomerbase values(330,1,0.0,'(none)',130.0,'Exchange Cust 4');
insert into cicustomerbase values(340,1,0.0,'(none)',140.0,'Exchange Cust 5');
insert into cicustomerbase values(350,1,0.0,'(none)',150.0,'Exchange Cust 6');
insert into cicustomerbase values(360,1,0.0,'(none)',160.0,'Exchange Cust 7');
insert into cicustomerbase values(370,1,0.0,'(none)',170.0,'Exchange Cust 8');

/* customerid,contactid */
insert into customeradditionalcontact values(300,1);
insert into customeradditionalcontact values(301,2);
insert into customeradditionalcontact values(302,3);
insert into customeradditionalcontact values(303,4);
insert into customeradditionalcontact values(304,5);
insert into customeradditionalcontact values(305,6);
insert into customeradditionalcontact values(306,7);
insert into customeradditionalcontact values(307,8);
insert into customeradditionalcontact values(308,9);
insert into customeradditionalcontact values(310,10);
insert into customeradditionalcontact values(320,11);
insert into customeradditionalcontact values(330,12);
insert into customeradditionalcontact values(340,13);
insert into customeradditionalcontact values(350,14);
insert into customeradditionalcontact values(360,15);
insert into customeradditionalcontact values(370,16);



insert into yukonpaobject values(100,'LOADMANAGEMENT','LOADMANAGEMENT','Curtailment 1','LM CURTAIL PROGRAM','(none)','N','-----');
insert into yukonpaobject values(110,'LOADMANAGEMENT','LOADMANAGEMENT','Curtailment 2','LM CURTAIL PROGRAM','(none)','N','-----');
insert into yukonpaobject values(120,'LOADMANAGEMENT','LOADMANAGEMENT','Exchange 1','LM ENERGY EXCHANGE','(none)','N','-----');
insert into yukonpaobject values(130,'LOADMANAGEMENT','LOADMANAGEMENT','Exchange 2','LM ENERGY EXCHANGE','(none)','N','-----');



insert into lmprogram values(100,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(110,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(120,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(130,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);


/* paobjectid, minnotifytime, heading, messageHeader, messageFooter, ackTimeLimit,
   canceledMsg, stoppedEarlyMsg */
insert into lmprogramcurtailment values(100,3600,'Mandatory Curtailment Notification',
'In accordance with your agreement with your energy provider, you are hereby notified that a call for Energy Interruption has been issued.',
'Please login to www.readmeter.com to acknowledge this Curtailment notice.',1800,
'THIS CURTAILMENT HAS BEEN CANCELED, PLEASE DISREGARD.','THIS CURTAILMENT HAS STOPPED EARLY, YOU MAY RESUME NORMAL OPERATIONS.');
insert into lmprogramcurtailment values(110,3600,'Mandatory Curtailment Notification',
'In accordance with your agreement with your energy provider, you are hereby notified that a call for Energy Interruption has been issued.',
'Please login to www.readmeter.com to acknowledge this Curtailment notice.',1800,
'THIS CURTAILMENT HAS BEEN CANCELED, PLEASE DISREGARD.','THIS CURTAILMENT HAS STOPPED EARLY, YOU MAY RESUME NORMAL OPERATIONS.');

/* paobjectid, lmcustomerpaobjectId, customerorder, requireack */
insert into lmprogramcurtailcustomerlist values(100,300,1,'Y');
insert into lmprogramcurtailcustomerlist values(100,301,2,'Y');
insert into lmprogramcurtailcustomerlist values(100,302,3,'N');
insert into lmprogramcurtailcustomerlist values(100,303,4,'N');
insert into lmprogramcurtailcustomerlist values(110,304,1,'Y');
insert into lmprogramcurtailcustomerlist values(110,305,2,'Y');
insert into lmprogramcurtailcustomerlist values(110,306,3,'N');
insert into lmprogramcurtailcustomerlist values(110,307,4,'N');

/* paobjectid, minnotifytime, heading, messageHeader, messageFooter,
   canceledMsg, stoppedEarlyMsg */
insert into lmprogramenergyexchange values(120,3600,'Energy Exchange Notification',
'A new Energy Exchange opportunity has been offered.',
'Please login to www.readmeter.com to view this Energy Exchange offer.',
'THIS ENERGY EXCHANGE OFFER HAS BEEN CANCELED, PLEASE DISREGARD.','THIS ENERGY EXCHANGE OFFER HAS STOPPED EARLY, YOU MAY RESUME NORMAL OPERATIONS.');
insert into lmprogramenergyexchange values(130,3600,'Energy Exchange Notification',
'A new Energy Exchange opportunity has been offered.',
'Please login to www.readmeter.com to view this Energy Exchange offer.',
'THIS ENERGY EXCHANGE OFFER HAS BEEN CANCELED, PLEASE DISREGARD.','THIS ENERGY EXCHANGE OFFER HAS STOPPED EARLY, YOU MAY RESUME NORMAL OPERATIONS.');

/* paobjectid, lmcustomerpaobjectid, CustomerOrder */
insert into lmenergyexchangecustomerlist values(120,308,1);
insert into lmenergyexchangecustomerlist values(120,310,2);
insert into lmenergyexchangecustomerlist values(120,320,3);
insert into lmenergyexchangecustomerlist values(120,330,4);
insert into lmenergyexchangecustomerlist values(130,340,1);
insert into lmenergyexchangecustomerlist values(130,350,2);
insert into lmenergyexchangecustomerlist values(130,360,3);
insert into lmenergyexchangecustomerlist values(130,370,4);



insert into yukonpaobject values(10000,'LOADMANAGEMENT','LOADMANAGEMENT','Energy Exchange','LM CONTROL AREA','(none)','N','-----');

insert into LMControlArea values(10000,'None',0,0,0,86400,'F');

insert into LMControlAreaProgram values(3000,100,5,2,1);
insert into LMControlAreaProgram values(3000,110,6,1,1);
insert into LMControlAreaProgram values(10000,120,1,1,1);
insert into LMControlAreaProgram values(10000,130,2,1,1);


insert into yukonuser values(17,'op1','op1',0,'01/01/1990','Enabled');
insert into yukonuser values(18,'op2','op2',0,'01/01/1990','Enabled');
insert into yukonuser values(19,'op3','op3',0,'01/01/1990','Enabled');
insert into yukonuser values(20,'op4','op4',0,'01/01/1990','Enabled');

insert into contact values(17,'Harry','Polter',13,1);
insert into contact values(18,'MIke','Tiger',14,1);
insert into contact values(19,'Joey','Ramone',15,1);
insert into contact values(20,'Matt','Rumsfield',16,1);


insert into yukongroup values(235, 'lmoperator', 'Custom lm operator group');

insert into yukonusergroup values(17,235);
insert into yukonusergroup values(18,235);
insert into yukonusergroup values(19,235);
insert into yukonusergroup values(20,235);

insert into yukonusergroup values(17, -1);
insert into yukonusergroup values(18, -1);
insert into yukonusergroup values(19, -1);
insert into yukonusergroup values(20, -1);

/* adds loadmanagement roles to the web operators */

/* Energy Company Role */
insert into yukongrouprole values(3111, 235, -2, -1100, 'josh@cannontech.com');
insert into yukongrouprole values(3112, 235, -2, -1101, 'josh@cannontech.com');
insert into yukongrouprole values(3113, 235, -2, -1102, '(none)');
insert into yukongrouprole values(3114, 235, -2, -1103, '(none)');
insert into yukongrouprole values(3115, 235, -2, -1104, '(none)');
insert into yukongrouprole values(3116, 235, -2, -1105, '(none)');

/* Web Client Role */
insert into yukongrouprole values(3333, 235, -108, -10800, '/operator/Operations.jsp');
insert into yukongrouprole values(3334, 235, -108, -10801, 'ALL');
insert into yukongrouprole values(2235, 235, -108, -10802, '(none)');
insert into yukongrouprole values(2236, 235, -108, -10803, '(none)');
insert into yukongrouprole values(2237, 235, -108, -10804, '(none)');
insert into yukongrouprole values(2238, 235, -108, -10805, '(none)');

/* Operator Direct Loadcontrol Role */
insert into yukongrouprole values(3339, 235, -203, -20300, '(none)');
insert into yukongrouprole values(3340, 235, -203, -20301, '(none)');

/* Operator Direct Curtailment Role */
insert into yukongrouprole values(3341, 235, -204, -20400, 'Notification');

/* Operator Energy Buyback Role */
insert into yukongrouprole values(3342, 235, -205, -20500, 'Peak Day Partners');

/* Give the operators some direct programs */
insert into lmdirectoperatorlist values(40, 17);
insert into lmdirectoperatorlist values(50, 17);
insert into lmdirectoperatorlist values(50, 18);
insert into lmdirectoperatorlist values(60, 18);
insert into lmdirectoperatorlist values(70, 19);
insert into lmdirectoperatorlist values(80, 19);
insert into lmdirectoperatorlist values(80, 20);
insert into lmdirectoperatorlist values(90, 20);
 
/* energycompanyid, name, routeid (routeid should be removed)*/
insert into energycompany values(1, 'Acme Energy Corp',17, -1);
insert into energycompany values(2, 'Brooks Energy', 19, -1);

/* energycompanyid, operatorloginid */
insert into energycompanyoperatorloginlist values(1,17);
insert into energycompanyoperatorloginlist values(1,18);
insert into energycompanyoperatorloginlist values(2,19);
insert into energycompanyoperatorloginlist values(2,20);

/* energycompanyid, customerid */
insert into energycompanycustomerlist values(1,300);
insert into energycompanycustomerlist values(1,301);
insert into energycompanycustomerlist values(1,302);
insert into energycompanycustomerlist values(1,303);
insert into energycompanycustomerlist values(2,304);
insert into energycompanycustomerlist values(2,305);
insert into energycompanycustomerlist values(2,306);
insert into energycompanycustomerlist values(2,307);
insert into energycompanycustomerlist values(1,308);
insert into energycompanycustomerlist values(1,310);
insert into energycompanycustomerlist values(1,320);
insert into energycompanycustomerlist values(1,330);
insert into energycompanycustomerlist values(2,340);
insert into energycompanycustomerlist values(2,350);
insert into energycompanycustomerlist values(2,360);
insert into energycompanycustomerlist values(2,370);


/******************* Clean up points in the DB, i.e. make sure they all have "pointalarming" entry ***************************/
insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point where pointid not in (select pointid from pointalarming);


