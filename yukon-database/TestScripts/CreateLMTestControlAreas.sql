/**alter session set NLS_DATE_FORMAT='MM/DD/YYYY HH24:MI:SS';*/

/******************* Insert TDC Load Managment View ***************************/
insert into display values(-3, 'All Control Areas', 'Load Management Client', 'Load Management', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel');

/******************* Insert the port, tap term, and route ***************************/

/********* Port *********/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(1,'PORT','PORT','LM Modem Port','Local Dialup','Description','N','-----');

/* portid, alarminhibit, commonprotocol, performthreshold, performancealarm, sharedporttype, sharedsocketnumber */
insert into commport values(1,'N','None',90,'Y','(none)',1025);

/* portid, pretxwait, rtstotxwait, posttxwait, receiveddatawait, extratimeout */
insert into porttiming values(1,25,0,0,0,0);

/* portid, physicalport */
insert into portlocalserial values(1,'com2');

/* portid, baudrate, cdwait, linesettings */
insert into portsettings values(1,1200,0,'8N1');

/* portid, modemtype, initializationstring, prefixnumber, suffixnumber */
insert into portdialupmodem values(1,'U.S. Robotics Courier','ATH0',' ',' ');


/********* Tap Term *********/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(2,'DEVICE','TRANSMITTER','LM Tap Term','TAP TERMINAL','Description','N','-----');

/* deviceid(paoid), alarminhibit, controlinhibit */
insert into device values(2,'N','N');

/* deviceid, pagernumber */
insert into deviceTapPagingSettings values(2,'885522456');

/* deviceid, password, slaveaddress */
insert into deviceIED values(2,'None','Master');

/* deviceid, portid */
insert into deviceDirectCommSettings values(2,1);

/* deviceid, phonenumber, minconnecttime, maxconnecttime, linesttings, baudrate */
insert into deviceDialupSettings values(2,'918008070616',0,30,'7E2',1200);

/* deviceid, montlystats, twentryfourhourstats, hourlystats, failurealarm, performancethresold, 
   performancealarm, performancetwentyfourhouralarm */
insert into device2WayFlags values(2,'N','N','N','N',90,'N','N');

/********* Route *********/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(3,'ROUTE','ROUTE','LM Tap Route','Tap Paging','Description','N','-----');

/* routeid, deviceid, defaultroute */
insert into route values(3,2,'Y');

/******** RTU with Controllable Status Point **********/

/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(5,'DEVICE','RTU','Pseudo WELCO RTU','RTU-WELCO','(none)','N','-----');

/* deviceid(paoid), alarminhibit, controlinhibit */
insert into device values(5,'N','N');

/* deviceid, montlystats, twentryfourhourstats, hourlystats, failurealarm, performancethresold, 
   performancealarm, performancetwentyfourhouralarm */
insert into device2wayflags values(5,'N','N','N','N',90,'N','N');

/* deviceid, portid */
insert into devicedirectcommsettings values(5,1);

/* deviceid, address, postcommwait, ccuampusetype */
insert into deviceidlcremote values(5,123,0,'Amp 1');

/* deviceid, phonenumber, minconnecttime, maxconnecttime, linesttings, baudrate */
insert into devicedialupsettings values(5,'9529999999',0,30,'8N1',1200);

/* pointid, pointtype, pointname, paobjectid, logicalgroup, stategroupid, serviceflag, alarminhibit,
   pseudoflag, pointoffset, achivetype, archiveinterval */
insert into point values(3,'Status','contol point',5,'Default',1,'N','N','P',0,'None',0);

/* pointid, alarmstates, excludenotifystates, notifyacknowledge, notificationgroupid, recipientid */
insert into pointalarming values(3,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);

/* pointid, initialstate, controltype, controlinhibit, controloffset, closetime1, closetime2,
   statezerocontrol, stateonecontrol, commandtimeout */
insert into pointstatus values(3,0,'Normal','N',1,0,0,'control open','control close',0);


/******************* Insert Virtual PAO (Device) and the Trigger Points ***************************/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(4,'DEVICE','Virtual','LM Virtual','Virtual System','Description','N','-----');

/* pointid, pointtype, pointname, paobjectid, logicalgroup, stategroupid, serviceflag, alarminhibit,
   pseudoflag, pointoffset, achivetype, archiveinterval */
insert into point values(1,'Analog','Analog Point',4,'Default',-1,'N','N','R',1,'None',0);
insert into point values(2,'Status','Status Point',4,'Default',1,'N','N','R',1,'None',0);

/* pointid, alarmstates, excludenotifystates, notifyacknowledge, notificationgroupid, recipientid */
insert into pointalarming values(1,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(2,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);

/* pointid, uofmid, decimalplaces, highreasonabilitylimit, lowreasonabilitylimit */
insert into pointunit values(1,0,3,1.0E30,-1.0E30);

/* pointid, deadband, transducertype, multiplier, dataoffset */
insert into pointanalog values(1,-1,'None',1,0);

/* pointid, initialstate, controltype, controlinhibit, controloffset, closetime1, closetime2,
   statezerocontrol, stateonecontrol, commandtimeout */
insert into pointstatus values(2,0,'None','N',0, 0,0,'control open','control close',0);



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

/* LM Groups */
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(10,'DEVICE','GROUP','Irr 1','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(11,'DEVICE','GROUP','Irr 2','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(12,'DEVICE','GROUP','Irr 3','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(13,'DEVICE','GROUP','Irr 4','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(14,'DEVICE','GROUP','Irr 5','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(15,'DEVICE','GROUP','Irr 6','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(16,'DEVICE','GROUP','Irr 7','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(17,'DEVICE','GROUP','Irr 8','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(18,'DEVICE','GROUP','AC 1','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(19,'DEVICE','GROUP','AC 2','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(20,'DEVICE','GROUP','AC 3','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(21,'DEVICE','GROUP','AC 4','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(22,'DEVICE','GROUP','Res AC Odd','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(23,'DEVICE','GROUP','Res AC Even','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(24,'DEVICE','GROUP','Comm AC 1','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(25,'DEVICE','GROUP','Comm AC 2','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(26,'DEVICE','GROUP','WH 1','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(27,'DEVICE','GROUP','WH 21','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(28,'DEVICE','GROUP','Macro Irr 3-6','MACRO GROUP','(none)','N','-----');
insert into yukonpaobject values(29,'DEVICE','GROUP','Versa Group Latching','VERSACOM GROUP','(none)','N','-----');
insert into yukonpaobject values(30,'DEVICE','GROUP','Point Group Latching','POINT GROUP','(none)','N','-----');

/* deviceid(paoid), alarminhibit, controlinhibit */
insert into device values(10,'N','N');
insert into device values(11,'N','N');
insert into device values(12,'N','N');
insert into device values(13,'N','N');
insert into device values(14,'N','N');
insert into device values(15,'N','N');
insert into device values(16,'N','N');
insert into device values(17,'N','N');
insert into device values(18,'N','N');
insert into device values(19,'N','N');
insert into device values(20,'N','N');
insert into device values(21,'N','N');
insert into device values(22,'N','N');
insert into device values(23,'N','N');
insert into device values(24,'N','N');
insert into device values(25,'N','N');
insert into device values(26,'N','N');
insert into device values(27,'N','N');
insert into device values(28,'N','N');
insert into device values(29,'N','N');
insert into device values(30,'N','N');

/* deviceid(paoid), kwcapacity, recordcontrolhistoryflag */
insert into lmgroup values(10,100.0);
insert into lmgroup values(11,100.0);
insert into lmgroup values(12,100.0);
insert into lmgroup values(13,100.0);
insert into lmgroup values(14,100.0);
insert into lmgroup values(15,100.0);
insert into lmgroup values(16,100.0);
insert into lmgroup values(17,100.0);
insert into lmgroup values(18,100.0);
insert into lmgroup values(19,100.0);
insert into lmgroup values(20,100.0);
insert into lmgroup values(21,100.0);
insert into lmgroup values(22,10000.0);
insert into lmgroup values(23,10000.0);
insert into lmgroup values(24,1000.0);
insert into lmgroup values(25,1000.0);
insert into lmgroup values(26,500.0);
insert into lmgroup values(27,500.0);
insert into lmgroup values(28,400.0);
insert into lmgroup values(29,200.0);
insert into lmgroup values(30,200.0);

/* deviceid, routeid, utilityaddress, sectionaddress, classaddress, divisionaddress, addressusage,
   relayusage, serialaddress */
insert into lmgroupversacom values(10,3,30,1,1,1,'USCD','1  ','0');
insert into lmgroupversacom values(11,3,30,1,1,1,'USCD',' 2 ','0');
insert into lmgroupversacom values(12,3,30,1,1,1,'USCD','  3','0');
insert into lmgroupversacom values(13,3,30,1,1,1,'USCD','1  ','0');
insert into lmgroupversacom values(14,3,30,1,1,1,'USCD','1  ','0');
insert into lmgroupversacom values(15,3,30,1,1,1,'USCD',' 2 ','0');
insert into lmgroupversacom values(16,3,30,1,1,1,'USCD','  3','0');
insert into lmgroupversacom values(17,3,30,1,1,1,'USCD','1  ','0');
insert into lmgroupversacom values(18,3,30,1,1,1,'USCD',' 2 ','0');
insert into lmgroupversacom values(19,3,30,1,1,1,'USCD','  3','0');
insert into lmgroupversacom values(20,3,30,1,1,1,'USCD','1  ','0');
insert into lmgroupversacom values(21,3,30,1,1,1,'USCD',' 2 ','0');
insert into lmgroupversacom values(22,3,30,1,1,1,'USCD','1  ','0');
insert into lmgroupversacom values(23,3,30,1,1,1,'USCD','1  ','0');
insert into lmgroupversacom values(24,3,30,1,1,1,'USCD','1  ','0');
insert into lmgroupversacom values(25,3,30,1,1,1,'USCD',' 2 ','0');
insert into lmgroupversacom values(26,3,30,1,1,1,'USCD','1  ','0');
insert into lmgroupversacom values(27,3,30,1,1,1,'USCD','  3','0');
insert into lmgroupversacom values(29,3,30,1,1,1,'USCD','1  ','0');

/* ownerid, childid, childorder, macrotype */
insert into genericmacro values(28,12,1,'GROUP');
insert into genericmacro values(28,13,2,'GROUP');
insert into genericmacro values(28,14,3,'GROUP');
insert into genericmacro values(28,15,4,'GROUP');

/* deviceid, deviceidusage, pointidusage, startcontrolrawstate */
insert into lmgrouppoint values(30,5,3,0);

/* pointid, pointtype, pointname, paobjectid, logicalgroup, stategroupid, serviceflag, alarminhibit,
   pseudoflag, pointoffset, achivetype, archiveinterval */
insert into point values(10,'Status','controllable status',10,'Default',1,'N','N','P',0,'None',0);
insert into point values(11,'Status','controllable status',11,'Default',1,'N','N','P',0,'None',0);
insert into point values(12,'Status','controllable status',12,'Default',1,'N','N','P',0,'None',0);
insert into point values(13,'Status','controllable status',13,'Default',1,'N','N','P',0,'None',0);
insert into point values(14,'Status','controllable status',14,'Default',1,'N','N','P',0,'None',0);
insert into point values(15,'Status','controllable status',15,'Default',1,'N','N','P',0,'None',0);
insert into point values(16,'Status','controllable status',16,'Default',1,'N','N','P',0,'None',0);
insert into point values(17,'Status','controllable status',17,'Default',1,'N','N','P',0,'None',0);
insert into point values(18,'Status','controllable status',18,'Default',1,'N','N','P',0,'None',0);
insert into point values(19,'Status','controllable status',19,'Default',1,'N','N','P',0,'None',0);
insert into point values(20,'Status','controllable status',20,'Default',1,'N','N','P',0,'None',0);
insert into point values(21,'Status','controllable status',21,'Default',1,'N','N','P',0,'None',0);
insert into point values(22,'Status','controllable status',22,'Default',1,'N','N','P',0,'None',0);
insert into point values(23,'Status','controllable status',23,'Default',1,'N','N','P',0,'None',0);
insert into point values(24,'Status','controllable status',24,'Default',1,'N','N','P',0,'None',0);
insert into point values(25,'Status','controllable status',25,'Default',1,'N','N','P',0,'None',0);
insert into point values(26,'Status','controllable status',26,'Default',1,'N','N','P',0,'None',0);
insert into point values(27,'Status','controllable status',27,'Default',1,'N','N','P',0,'None',0);
insert into point values(29,'Status','controllable status',29,'Default',1,'N','N','P',0,'None',0);
insert into point values(30,'Status','controllable status',30,'Default',1,'N','N','P',0,'None',0);

/* pointid, alarmstates, excludenotifystates, notifyacknowledge, notificationgroupid, recipientid */
insert into pointalarming values(10,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(11,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(12,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(13,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(14,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(15,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(16,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(17,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(18,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(19,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(20,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(21,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(22,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(23,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(24,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(25,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(26,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(27,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(29,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);
insert into pointalarming values(30,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0);

/* pointid, initialstate, controltype, controlinhibit, controloffset, closetime1, closetime2,
   statezerocontrol, stateonecontrol, commandtimeout */
insert into pointstatus values(10,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(13,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(14,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(15,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(16,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(17,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(18,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(19,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(20,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(21,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(22,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(23,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(24,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(25,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(26,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(27,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(29,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(30,0,'Normal','N',1,0,0,'control open','control close',0);

/* pointid, pointtype, pointname, paobjectid, logicalgroup, stategroupid, serviceflag, alarminhibit,
   pseudoflag, pointoffset, achivetype, archiveinterval */
INSERT INTO Point VALUES(100,'Analog','ANNUAL HISTORY',10,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(101,'Analog','DAILY HISTORY', 10,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(102,'Analog','SEASON HISTORY',10,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(103,'Analog','MONTH HISTORY', 10,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(104,'Analog','ANNUAL HISTORY',11,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(105,'Analog','DAILY HISTORY', 11,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(106,'Analog','SEASON HISTORY',11,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(107,'Analog','MONTH HISTORY', 11,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(108,'Analog','ANNUAL HISTORY',12,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(109,'Analog','DAILY HISTORY', 12,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(110,'Analog','SEASON HISTORY',12,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(111,'Analog','MONTH HISTORY', 12,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(112,'Analog','ANNUAL HISTORY',13,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(113,'Analog','DAILY HISTORY', 13,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(114,'Analog','SEASON HISTORY',13,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(115,'Analog','MONTH HISTORY', 13,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(116,'Analog','ANNUAL HISTORY',14,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(117,'Analog','DAILY HISTORY', 14,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(118,'Analog','SEASON HISTORY',14,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(119,'Analog','MONTH HISTORY', 14,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(120,'Analog','ANNUAL HISTORY',15,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(121,'Analog','DAILY HISTORY', 15,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(122,'Analog','SEASON HISTORY',15,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(123,'Analog','MONTH HISTORY', 15,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(124,'Analog','ANNUAL HISTORY',16,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(125,'Analog','DAILY HISTORY', 16,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(126,'Analog','SEASON HISTORY',16,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(127,'Analog','MONTH HISTORY', 16,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(128,'Analog','ANNUAL HISTORY',17,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(129,'Analog','DAILY HISTORY', 17,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(130,'Analog','SEASON HISTORY',17,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(131,'Analog','MONTH HISTORY', 17,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(132,'Analog','ANNUAL HISTORY',18,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(133,'Analog','DAILY HISTORY', 18,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(134,'Analog','SEASON HISTORY',18,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(135,'Analog','MONTH HISTORY', 18,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(136,'Analog','ANNUAL HISTORY',19,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(137,'Analog','DAILY HISTORY', 19,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(138,'Analog','SEASON HISTORY',19,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(139,'Analog','MONTH HISTORY', 19,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(140,'Analog','ANNUAL HISTORY',20,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(141,'Analog','DAILY HISTORY', 20,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(142,'Analog','SEASON HISTORY',20,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(143,'Analog','MONTH HISTORY', 20,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(144,'Analog','ANNUAL HISTORY',21,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(145,'Analog','DAILY HISTORY', 21,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(146,'Analog','SEASON HISTORY',21,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(147,'Analog','MONTH HISTORY', 21,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(148,'Analog','ANNUAL HISTORY',22,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(149,'Analog','DAILY HISTORY', 22,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(150,'Analog','SEASON HISTORY',22,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(151,'Analog','MONTH HISTORY', 22,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(152,'Analog','ANNUAL HISTORY',23,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(153,'Analog','DAILY HISTORY', 23,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(154,'Analog','SEASON HISTORY',23,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(155,'Analog','MONTH HISTORY', 23,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(156,'Analog','ANNUAL HISTORY',24,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(157,'Analog','DAILY HISTORY', 24,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(158,'Analog','SEASON HISTORY',24,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(159,'Analog','MONTH HISTORY', 24,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(164,'Analog','ANNUAL HISTORY',26,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(165,'Analog','DAILY HISTORY', 26,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(166,'Analog','SEASON HISTORY',26,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(167,'Analog','MONTH HISTORY', 26,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(168,'Analog','ANNUAL HISTORY',27,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(169,'Analog','DAILY HISTORY', 27,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(170,'Analog','SEASON HISTORY',27,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(171,'Analog','MONTH HISTORY', 27,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(172,'Analog','ANNUAL HISTORY',29,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(173,'Analog','DAILY HISTORY', 29,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(174,'Analog','SEASON HISTORY',29,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(175,'Analog','MONTH HISTORY', 29,'Default',-1,'N','N','R',2501,'None',0); 
INSERT INTO Point VALUES(176,'Analog','ANNUAL HISTORY',30,'Default',-1,'N','N','R',2503,'None',0); 
INSERT INTO Point VALUES(177,'Analog','DAILY HISTORY', 30,'Default',-1,'N','N','R',2500,'None',0); 
INSERT INTO Point VALUES(178,'Analog','SEASON HISTORY',30,'Default',-1,'N','N','R',2502,'None',0); 
INSERT INTO Point VALUES(179,'Analog','MONTH HISTORY', 30,'Default',-1,'N','N','R',2501,'None',0); 

/* pointid, alarmstates, excludenotifystates, notifyacknowledge, notificationgroupid, recipientid */
INSERT INTO PointAlarming VALUES(100,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(101,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(102,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(103,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(104,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(105,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(106,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(107,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(108,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(109,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(110,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(111,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(112,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(113,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(114,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(115,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(116,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(117,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(118,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(119,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(120,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(121,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(122,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(123,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(124,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(125,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(126,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(127,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(128,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(129,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(130,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(131,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(132,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(133,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(134,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(135,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(136,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(137,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(138,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(139,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(140,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(141,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(142,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(143,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(144,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(145,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(146,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(147,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(148,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(149,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(150,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(151,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(152,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(153,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(154,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(155,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(156,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(157,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(158,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(159,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(164,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(165,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(166,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(167,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(168,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(169,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(170,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(171,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(172,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(173,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(174,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(175,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(176,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(177,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(178,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 
INSERT INTO PointAlarming VALUES(179,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N',1,0); 

/* pointid, uofmid, decimalplaces, highreasonabilitylimit, lowreasonabilitylimit */
INSERT INTO PointUnit VALUES(100,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(101,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(102,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(103,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(104,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(105,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(106,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(107,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(108,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(109,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(110,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(111,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(112,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(113,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(114,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(115,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(116,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(117,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(118,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(119,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(120,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(121,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(122,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(123,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(124,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(125,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(126,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(127,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(128,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(129,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(130,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(131,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(132,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(133,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(134,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(135,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(136,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(137,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(138,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(139,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(140,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(141,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(142,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(143,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(144,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(145,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(146,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(147,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(148,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(149,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(150,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(151,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(152,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(153,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(154,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(155,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(156,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(157,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(158,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(159,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(164,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(165,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(166,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(167,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(168,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(169,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(170,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(171,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(172,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(173,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(174,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(175,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(176,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(177,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(178,9,3,1.0E30,-1.0E30); 
INSERT INTO PointUnit VALUES(179,9,3,1.0E30,-1.0E30); 

/* pointid, deadband, transducertype, multiplier, dataoffset */
INSERT INTO PointAnalog VALUES(100,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(101,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(102,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(103,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(104,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(105,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(106,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(107,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(108,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(109,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(110,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(111,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(112,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(113,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(114,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(115,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(116,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(117,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(118,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(119,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(120,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(121,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(122,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(123,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(124,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(125,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(126,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(127,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(128,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(129,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(130,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(131,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(132,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(133,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(134,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(135,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(136,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(137,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(138,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(139,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(140,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(141,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(142,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(143,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(144,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(145,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(146,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(147,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(148,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(149,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(150,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(151,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(152,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(153,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(154,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(155,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(156,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(157,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(158,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(159,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(164,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(165,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(166,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(167,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(168,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(169,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(170,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(171,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(172,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(173,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(174,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(175,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(176,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(177,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(178,-1.0,'None',1.0,0.0); 
INSERT INTO PointAnalog VALUES(179,-1.0,'None',1.0,0.0); 


/* LM Programs */
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(40,'LOADMANAGEMENT','LOADMANAGEMENT','Irrigation','LM DIRECT PROGRAM','(none)','N','-----');
insert into yukonpaobject values(50,'LOADMANAGEMENT','LOADMANAGEMENT','Air Conditioning','LM DIRECT PROGRAM','(none)','N','-----');
insert into yukonpaobject values(60,'LOADMANAGEMENT','LOADMANAGEMENT','Residential AC Odd','LM DIRECT PROGRAM','(none)','N','-----');
insert into yukonpaobject values(70,'LOADMANAGEMENT','LOADMANAGEMENT','Residential AC Even','LM DIRECT PROGRAM','(none)','N','-----');
insert into yukonpaobject values(80,'LOADMANAGEMENT','LOADMANAGEMENT','Commercial AC','LM DIRECT PROGRAM','(none)','N','-----');
insert into yukonpaobject values(90,'LOADMANAGEMENT','LOADMANAGEMENT','Water Heaters','LM DIRECT PROGRAM','(none)','N','-----');
insert into yukonpaobject values(100,'LOADMANAGEMENT','LOADMANAGEMENT','Curtailment 1','LM CURTAIL PROGRAM','(none)','N','-----');
insert into yukonpaobject values(110,'LOADMANAGEMENT','LOADMANAGEMENT','Curtailment 2','LM CURTAIL PROGRAM','(none)','N','-----');
insert into yukonpaobject values(120,'LOADMANAGEMENT','LOADMANAGEMENT','Exchange 1','LM ENERGY EXCHANGE','(none)','N','-----');
insert into yukonpaobject values(130,'LOADMANAGEMENT','LOADMANAGEMENT','Exchange 2','LM ENERGY EXCHANGE','(none)','N','-----');
insert into yukonpaobject values(140,'LOADMANAGEMENT','LOADMANAGEMENT','Latching Program','LM DIRECT PROGRAM','(none)','N','-----');

/* paobjectid, controltype, availableseasons, availableweekdays, maxhoursdaily,
   maxhoursmonthly, maxhoursseasonal, maxhoursannually, minactivatetime, minrestarttime,
   holidaysheduleid, seasonscheduleid */
insert into lmprogram values(40,'Automatic','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(50,'Automatic','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(60,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(70,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(80,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(90,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(100,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(110,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(120,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(130,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);
insert into lmprogram values(140,'ManualOnly','YYYY','YYYYYYYN',0,0,0,0,0,0,0,0);

/* paobjectid, groupselectionmethod */
insert into lmprogramdirect values(40);
insert into lmprogramdirect values(50);
insert into lmprogramdirect values(60);
insert into lmprogramdirect values(70);
insert into lmprogramdirect values(80);
insert into lmprogramdirect values(90);
insert into lmprogramdirect values(140);

/* paobjectid, gearname, gearnumber, controlmethod, methodrate, methodperiod, methodratecount,
   cyclerefreshrate, methodstoptype, changecondition, changeduration,
   changepriority, changetriggernumber, changetriggeroffset, percentreduction, methodoptiontype,
   methodoptionmax, gearid */
insert into lmprogramdirectgear values(40,'TimeRefresh',1,'TimeRefresh',600,1800,2,0,'Restore','None',0,0,0.0,0,20,'LastControlled','FixedCount',0,1);
insert into lmprogramdirectgear values(50,'SmartCycle',1,'SmartCycle',25,1800,8,3600,'Restore','None',0,0,0.0,0,25,'LastControlled','CountDown',0,2);
insert into lmprogramdirectgear values(60,'SmartCycle',1,'SmartCycle',25,1800,8,3600,'Restore','None',0,0,0.0,0,25,'LastControlled','CountDown',8,3);
insert into lmprogramdirectgear values(70,'SmartCycle',1,'SmartCycle',25,1800,8,3600,'Restore','None',0,0,0.0,0,25,'LastControlled','LimitedCountDown',0,4);
insert into lmprogramdirectgear values(80,'SmartCycle',1,'SmartCycle',50,1800,8,3600,'StopCycle','None',0,0,0.0,0,50,'LastControlled','FixedCount',0,5);
insert into lmprogramdirectgear values(90,'TimeRefresh',1,'TimeRefresh',1800,3600,2,0,'Restore','None',0,0,0.0,0,25,'LastControlled','FixedCount',0,6);
insert into lmprogramdirectgear values(140,'Latching',1,'Latching',0,0,0,0,'Restore','None',0,0,0.0,0,50,'LastControlled','FixedCount',0,7);

/* paobjectid, lmgroupdeviceid, grouporder */
insert into lmprogramdirectgroup values(40,10,1);
insert into lmprogramdirectgroup values(40,11,2);
insert into lmprogramdirectgroup values(40,28,3);
insert into lmprogramdirectgroup values(40,16,7);
insert into lmprogramdirectgroup values(40,17,8);
insert into lmprogramdirectgroup values(50,18,1);
insert into lmprogramdirectgroup values(50,19,2);
insert into lmprogramdirectgroup values(50,20,3);
insert into lmprogramdirectgroup values(50,21,4);
insert into lmprogramdirectgroup values(60,22,1);
insert into lmprogramdirectgroup values(70,23,1);
insert into lmprogramdirectgroup values(80,24,1);
insert into lmprogramdirectgroup values(80,25,2);
insert into lmprogramdirectgroup values(90,26,1);
insert into lmprogramdirectgroup values(90,27,2);
insert into lmprogramdirectgroup values(140,29,1);
insert into lmprogramdirectgroup values(140,30,2);

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

/* LM Control Areas */
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(2000,'LOADMANAGEMENT','LOADMANAGEMENT','North','LM CONTROL AREA','(none)','N','-----');
insert into yukonpaobject values(3000,'LOADMANAGEMENT','LOADMANAGEMENT','South','LM CONTROL AREA','(none)','N','-----');
insert into yukonpaobject values(10000,'LOADMANAGEMENT','LOADMANAGEMENT','Energy Exchange','LM CONTROL AREA','(none)','N','-----');
insert into yukonpaobject values(11000,'LOADMANAGEMENT','LOADMANAGEMENT','Latching Area','LM CONTROL AREA','(none)','N','-----');

/* paobjectid, defoperationstate, controlinterval, minresponsetime, defdailystarttime,
   defdailystoptime, requirealltriggersactiveflag */
/* times are 9AM-4PM for control area 1 and 8AM-6PM for control area 2 */
insert into LMControlArea values(2000,'Enabled',0,0,32400,64800,'F');
insert into LMControlArea values(3000,'None',0,0,28800,68400,'F');
insert into LMControlArea values(10000,'None',0,0,0,86400,'F');
insert into LMControlArea values(11000,'None',0,0,-1,-1,'F');

/* Xcel has no triggers all push-button control */
/* paobjectid, triggernumber, triggertype, pointid, normalstate, threshold, projectiontype,
   projectionpoints, projectaheadduration, thresholdkickpercent, minrestoreoffset,
   peakpointid */
insert into LMControlAreaTrigger values(2000,1,'Threshold',1,0,90000,'None',0,0,0,900.0,0);
insert into LMControlAreaTrigger values(2000,2,'Status',2,0,0,'None',0,0,0,0.0,0);

/* paobjectid, lmprogrampaobjectid, userorder, stoporder, defaultpriority */
insert into LMControlAreaProgram values(2000,40,1,2,1);
insert into LMControlAreaProgram values(2000,50,2,1,2);
insert into LMControlAreaProgram values(3000,60,1,6,1);
insert into LMControlAreaProgram values(3000,70,2,5,1);
insert into LMControlAreaProgram values(3000,80,3,4,2);
insert into LMControlAreaProgram values(3000,90,4,3,4);
insert into LMControlAreaProgram values(3000,100,5,2,1);
insert into LMControlAreaProgram values(3000,110,6,1,1);
insert into LMControlAreaProgram values(10000,120,1,1,1);
insert into LMControlAreaProgram values(10000,130,2,1,1);
insert into LMControlAreaProgram values(11000,140,1,1,1);


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


