alter session set NLS_DATE_FORMAT='MM/DD/YYYY HH24:MI:SS';

/******************* Insert TDC Cap Control View ***************************/
insert into display values(-2, 'All Areas', 'Cap Control Client', 'Cap Control', 'com.cannontech.cbc.gui.StrategyReceiver');

/******************* Insert a port, tap term, and route ***************************/

/********* Port *********/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(7,'PORT','PORT','CC Modem Port','Local Dialup','Description','N','-----');

/* portid, alarminhibit, commonprotocol, performthreshold, performancealarm, sharedporttype, sharedsocketnumber */
insert into commport values(7,'N','None',90,'Y','(none)',1025);

/* portid, pretxwait, rtstotxwait, posttxwait, receiveddatawait, extratimeout */
insert into porttiming values(7,25,0,0,0,0);

/* portid, physicalport */
insert into portlocalserial values(7,'com2');

/* portid, baudrate, cdwait, linesettings */
insert into portsettings values(7,1200,0,'8N1');

/* portid, modemtype, initializationstring, prefixnumber, suffixnumber */
insert into portdialupmodem values(7,'U.S. Robotics Courier','ATH0',' ',' ');

/********* Tap Term *********/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(200000,'DEVICE','TRANSMITTER','CC Tap Term','TAP TERMINAL','Description','N','-----');

/* deviceid(paoid), alarminhibit, controlinhibit */
insert into device values(200000,'N','N');

/* deviceid, pagernumber */
insert into deviceTapPagingSettings values(200000,'885522456');

/* deviceid, password, slaveaddress */
insert into deviceIED values(200000,'None','Master');

/* deviceid, portid */
insert into deviceDirectCommSettings values(200000,7);

/* deviceid, phonenumber, minconnecttime, maxconnecttime, linesttings, baudrate */
insert into deviceDialupSettings values(200000,'918008070616',0,30,'7E2',1200);

/* deviceid, montlystats, twentryfourhourstats, hourlystats, failurealarm, performancethresold, 
   performancealarm, performancetwentyfourhouralarm */
insert into device2WayFlags values(200000,'N','N','N','N',90,'N','N');

/********* Route *********/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(300000,'ROUTE','ROUTE','CC Tap Route','Tap Paging','Description','N','-----');

/* routeid, deviceid, defaultroute */
insert into route values(300000,200000,'Y');


/******************* Insert Virtual PAO (Device) and the Var/KW Load Points ***************************/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(400000,'DEVICE','Virtual','CC Virtual','Virtual System','Description','N','-----');

/* pointid, pointtype, pointname, paobjectid, logicalgroup, stategroupid, serviceflag, alarminhibit,
   pseudoflag, pointoffset, achivetype, archiveinterval */
insert into point values(100000,'Analog','Bus 1 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(200000,'Analog','Bus 2 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(300000,'Analog','Bus 3 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(400000,'Analog','Bus 4 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(500000,'Analog','Feeder 1-1 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(600000,'Analog','Feeder 1-2 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(700000,'Analog','Feeder 2-1 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(800000,'Analog','Feeder 2-2 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(900000,'Analog','Feeder 2-3 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(1000000,'Analog','Feeder 3-1 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(1100000,'Analog','Feeder 3-2 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(1200000,'Analog','Feeder 3-3 Vars',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(1300000,'Analog','Feeder 4-1 Vars',400000,'Default',-1,'N','N','P',0,'None',0);


insert into point values(1400000,'Analog','Bus 1 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(1500000,'Analog','Bus 2 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(1600000,'Analog','Bus 3 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(1700000,'Analog','Bus 4 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(1800000,'Analog','Feeder 1-1 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(1900000,'Analog','Feeder 1-2 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(2000000,'Analog','Feeder 2-1 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(2100000,'Analog','Feeder 2-2 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(2200000,'Analog','Feeder 2-3 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(2300000,'Analog','Feeder 3-1 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(2400000,'Analog','Feeder 3-2 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(2500000,'Analog','Feeder 3-3 KW',400000,'Default',-1,'N','N','P',0,'None',0);
insert into point values(2600000,'Analog','Feeder 4-1 KW',400000,'Default',-1,'N','N','P',0,'None',0);


/* pointid, alarmstates, excludenotifystates, notifyacknowledge, notificationgroupid, recipientid */
insert into pointalarming values(100000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(200000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(300000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(400000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(500000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(600000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(700000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(800000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(900000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(1000000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(1100000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(1200000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(1300000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);


insert into pointalarming values(1400000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(1500000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(1600000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(1700000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(1800000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(1900000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(2000000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(2100000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(2200000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(2300000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(2400000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(2500000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);
insert into pointalarming values(2600000,'','NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN','N', 1,0);


/* pointid, uofmid, decimalplaces, highreasonabilitylimit, lowreasonabilitylimit */
insert into pointunit values(100000,3,3,0,0);
insert into pointunit values(200000,3,3,0,0);
insert into pointunit values(300000,3,3,0,0);
insert into pointunit values(400000,3,3,0,0);
insert into pointunit values(500000,3,3,0,0);
insert into pointunit values(600000,3,3,0,0);
insert into pointunit values(700000,3,3,0,0);
insert into pointunit values(800000,3,3,0,0);
insert into pointunit values(900000,3,3,0,0);
insert into pointunit values(1000000,3,3,0,0);
insert into pointunit values(1100000,3,3,0,0);
insert into pointunit values(1200000,3,3,0,0);
insert into pointunit values(1300000,3,3,0,0);


insert into pointunit values(1400000,0,3,0,0);
insert into pointunit values(1500000,0,3,0,0);
insert into pointunit values(1600000,0,3,0,0);
insert into pointunit values(1700000,0,3,0,0);
insert into pointunit values(1800000,0,3,0,0);
insert into pointunit values(1900000,0,3,0,0);
insert into pointunit values(2000000,0,3,0,0);
insert into pointunit values(2100000,0,3,0,0);
insert into pointunit values(2200000,0,3,0,0);
insert into pointunit values(2300000,0,3,0,0);
insert into pointunit values(2400000,0,3,0,0);
insert into pointunit values(2500000,0,3,0,0);
insert into pointunit values(2600000,0,3,0,0);


/* pointid, deadband, transducertype, multiplier, dataoffset */
insert into pointanalog values(100000,-1,'None',1,0);
insert into pointanalog values(200000,-1,'None',1,0);
insert into pointanalog values(300000,-1,'None',1,0);
insert into pointanalog values(400000,-1,'None',1,0);
insert into pointanalog values(500000,-1,'None',1,0);
insert into pointanalog values(600000,-1,'None',1,0);
insert into pointanalog values(700000,-1,'None',1,0);
insert into pointanalog values(800000,-1,'None',1,0);
insert into pointanalog values(900000,-1,'None',1,0);
insert into pointanalog values(1000000,-1,'None',1,0);
insert into pointanalog values(1100000,-1,'None',1,0);
insert into pointanalog values(1200000,-1,'None',1,0);
insert into pointanalog values(1300000,-1,'None',1,0);


insert into pointanalog values(1400000,-1,'None',1,0);
insert into pointanalog values(1500000,-1,'None',1,0);
insert into pointanalog values(1600000,-1,'None',1,0);
insert into pointanalog values(1700000,-1,'None',1,0);
insert into pointanalog values(1800000,-1,'None',1,0);
insert into pointanalog values(1900000,-1,'None',1,0);
insert into pointanalog values(2000000,-1,'None',1,0);
insert into pointanalog values(2100000,-1,'None',1,0);
insert into pointanalog values(2200000,-1,'None',1,0);
insert into pointanalog values(2300000,-1,'None',1,0);
insert into pointanalog values(2400000,-1,'None',1,0);
insert into pointanalog values(2500000,-1,'None',1,0);
insert into pointanalog values(2600000,-1,'None',1,0);



/******************* Insert CBCs with control points ***************************/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(10100000,'DEVICE','CAPCONTROL','CBC 1-1-1','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(10200000,'DEVICE','CAPCONTROL','CBC 1-1-2','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(10300000,'DEVICE','CAPCONTROL','CBC 1-1-3','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(10400000,'DEVICE','CAPCONTROL','CBC 1-1-4','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(10500000,'DEVICE','CAPCONTROL','CBC 1-1-5','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(10600000,'DEVICE','CAPCONTROL','CBC 1-2-1','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(10700000,'DEVICE','CAPCONTROL','CBC 1-2-2','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(10800000,'DEVICE','CAPCONTROL','CBC 1-2-3','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(10900000,'DEVICE','CAPCONTROL','CBC 2-1-1','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11000000,'DEVICE','CAPCONTROL','CBC 2-1-2','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11100000,'DEVICE','CAPCONTROL','CBC 2-1-3','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11200000,'DEVICE','CAPCONTROL','CBC 2-2-1','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11300000,'DEVICE','CAPCONTROL','CBC 2-2-2','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11400000,'DEVICE','CAPCONTROL','CBC 2-2-3','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11500000,'DEVICE','CAPCONTROL','CBC 2-3-1','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11600000,'DEVICE','CAPCONTROL','CBC 2-3-2','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11700000,'DEVICE','CAPCONTROL','CBC 2-3-3','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11800000,'DEVICE','CAPCONTROL','CBC 3-1-1','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(11900000,'DEVICE','CAPCONTROL','CBC 3-1-2','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12000000,'DEVICE','CAPCONTROL','CBC 3-1-3','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12100000,'DEVICE','CAPCONTROL','CBC 3-1-4','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12200000,'DEVICE','CAPCONTROL','CBC 3-1-5','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12300000,'DEVICE','CAPCONTROL','CBC 3-2-1','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12400000,'DEVICE','CAPCONTROL','CBC 3-2-2','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12500000,'DEVICE','CAPCONTROL','CBC 3-2-3','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12600000,'DEVICE','CAPCONTROL','CBC 3-2-4','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12700000,'DEVICE','CAPCONTROL','CBC 3-2-5','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12800000,'DEVICE','CAPCONTROL','CBC 3-3-1','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(12900000,'DEVICE','CAPCONTROL','CBC 3-3-2','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(13000000,'DEVICE','CAPCONTROL','CBC 3-3-3','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(13100000,'DEVICE','CAPCONTROL','CBC 4-1-1','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(13200000,'DEVICE','CAPCONTROL','CBC 4-1-2','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(13300000,'DEVICE','CAPCONTROL','CBC 4-1-3','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(13400000,'DEVICE','CAPCONTROL','CBC 4-1-4','CBC Versacom','Description','N','-----');
insert into yukonpaobject values(13500000,'DEVICE','CAPCONTROL','CBC 4-1-5','CBC Versacom','Description','N','-----');


/* deviceid(paoid), alarminhibit, controlinhibit */
insert into device values(10100000,'N','N');
insert into device values(10200000,'N','N');
insert into device values(10300000,'N','N');
insert into device values(10400000,'N','N');
insert into device values(10500000,'N','N');
insert into device values(10600000,'N','N');
insert into device values(10700000,'N','N');
insert into device values(10800000,'N','N');
insert into device values(10900000,'N','N');
insert into device values(11000000,'N','N');
insert into device values(11100000,'N','N');
insert into device values(11200000,'N','N');
insert into device values(11300000,'N','N');
insert into device values(11400000,'N','N');
insert into device values(11500000,'N','N');
insert into device values(11600000,'N','N');
insert into device values(11700000,'N','N');
insert into device values(11800000,'N','N');
insert into device values(11900000,'N','N');
insert into device values(12000000,'N','N');
insert into device values(12100000,'N','N');
insert into device values(12200000,'N','N');
insert into device values(12300000,'N','N');
insert into device values(12400000,'N','N');
insert into device values(12500000,'N','N');
insert into device values(12600000,'N','N');
insert into device values(12700000,'N','N');
insert into device values(12800000,'N','N');
insert into device values(12900000,'N','N');
insert into device values(13000000,'N','N');
insert into device values(13100000,'N','N');
insert into device values(13200000,'N','N');
insert into device values(13300000,'N','N');
insert into device values(13400000,'N','N');
insert into device values(13500000,'N','N');


/* deviceid, serialnumber, routeid */
insert into deviceCBC values(10100000,101,300000);
insert into deviceCBC values(10200000,102,300000);
insert into deviceCBC values(10300000,103,300000);
insert into deviceCBC values(10400000,104,300000);
insert into deviceCBC values(10500000,105,300000);
insert into deviceCBC values(10600000,106,300000);
insert into deviceCBC values(10700000,107,300000);
insert into deviceCBC values(10800000,108,300000);
insert into deviceCBC values(10900000,109,300000);
insert into deviceCBC values(11000000,110,300000);
insert into deviceCBC values(11100000,111,300000);
insert into deviceCBC values(11200000,112,300000);
insert into deviceCBC values(11300000,113,300000);
insert into deviceCBC values(11400000,114,300000);
insert into deviceCBC values(11500000,115,300000);
insert into deviceCBC values(11600000,116,300000);
insert into deviceCBC values(11700000,117,300000);
insert into deviceCBC values(11800000,118,300000);
insert into deviceCBC values(11900000,119,300000);
insert into deviceCBC values(12000000,120,300000);
insert into deviceCBC values(12100000,121,300000);
insert into deviceCBC values(12200000,122,300000);
insert into deviceCBC values(12300000,123,300000);
insert into deviceCBC values(12400000,124,300000);
insert into deviceCBC values(12500000,125,300000);
insert into deviceCBC values(12600000,126,300000);
insert into deviceCBC values(12700000,127,300000);
insert into deviceCBC values(12800000,128,300000);
insert into deviceCBC values(12900000,129,300000);
insert into deviceCBC values(13000000,130,300000);
insert into deviceCBC values(13100000,131,300000);
insert into deviceCBC values(13200000,132,300000);
insert into deviceCBC values(13300000,133,300000);
insert into deviceCBC values(13400000,134,300000);
insert into deviceCBC values(13500000,135,300000);


/* pointid, pointtype, pointname, paobjectid, logicalgroup, stategroupid, serviceflag, alarminhibit,
   pseudoflag, pointoffset, achivetype, archiveinterval */
insert into point values(10100000,'Status','CBCControlStatus 1-1-1',10100000,'Default',1,'N','N','R',1,'None',0);
insert into point values(10200000,'Status','CBCControlStatus 1-1-2',10200000,'Default',1,'N','N','R',1,'None',0);
insert into point values(10300000,'Status','CBCControlStatus 1-1-3',10300000,'Default',1,'N','N','R',1,'None',0);
insert into point values(10400000,'Status','CBCControlStatus 1-1-4',10400000,'Default',1,'N','N','R',1,'None',0);
insert into point values(10500000,'Status','CBCControlStatus 1-1-5',10500000,'Default',1,'N','N','R',1,'None',0);
insert into point values(10600000,'Status','CBCControlStatus 1-2-1',10600000,'Default',1,'N','N','R',1,'None',0);
insert into point values(10700000,'Status','CBCControlStatus 1-2-2',10700000,'Default',1,'N','N','R',1,'None',0);
insert into point values(10800000,'Status','CBCControlStatus 1-2-3',10800000,'Default',1,'N','N','R',1,'None',0);
insert into point values(10900000,'Status','CBCControlStatus 2-1-1',10900000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11000000,'Status','CBCControlStatus 2-1-2',11000000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11100000,'Status','CBCControlStatus 2-1-3',11100000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11200000,'Status','CBCControlStatus 2-2-1',11200000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11300000,'Status','CBCControlStatus 2-2-2',11300000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11400000,'Status','CBCControlStatus 2-2-3',11400000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11500000,'Status','CBCControlStatus 2-3-1',11500000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11600000,'Status','CBCControlStatus 2-3-2',11600000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11700000,'Status','CBCControlStatus 2-3-3',11700000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11800000,'Status','CBCControlStatus 3-1-1',11800000,'Default',1,'N','N','R',1,'None',0);
insert into point values(11900000,'Status','CBCControlStatus 3-1-2',11900000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12000000,'Status','CBCControlStatus 3-1-3',12000000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12100000,'Status','CBCControlStatus 3-1-4',12100000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12200000,'Status','CBCControlStatus 3-1-5',12200000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12300000,'Status','CBCControlStatus 3-2-1',12300000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12400000,'Status','CBCControlStatus 3-2-2',12400000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12500000,'Status','CBCControlStatus 3-2-3',12500000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12600000,'Status','CBCControlStatus 3-2-4',12600000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12700000,'Status','CBCControlStatus 3-2-5',12700000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12800000,'Status','CBCControlStatus 3-3-1',12800000,'Default',1,'N','N','R',1,'None',0);
insert into point values(12900000,'Status','CBCControlStatus 3-3-2',12900000,'Default',1,'N','N','R',1,'None',0);
insert into point values(13000000,'Status','CBCControlStatus 3-3-3',13000000,'Default',1,'N','N','R',1,'None',0);
insert into point values(13100000,'Status','CBCControlStatus 4-1-1',13100000,'Default',1,'N','N','R',1,'None',0);
insert into point values(13200000,'Status','CBCControlStatus 4-1-2',13200000,'Default',1,'N','N','R',1,'None',0);
insert into point values(13300000,'Status','CBCControlStatus 4-1-3',13300000,'Default',1,'N','N','R',1,'None',0);
insert into point values(13400000,'Status','CBCControlStatus 4-1-4',13400000,'Default',1,'N','N','R',1,'None',0);
insert into point values(13500000,'Status','CBCControlStatus 4-1-5',13500000,'Default',1,'N','N','R',1,'None',0);


/* pointid, initialstate, controltype, controlinhibit, controloffset, closetime1, closetime2,
   statezerocontrol, stateonecontrol */
insert into pointstatus values(10100000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(10200000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(10300000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(10400000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(10500000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(10600000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(10700000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(10800000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(10900000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11000000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11100000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11200000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11300000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11400000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11500000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11600000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11700000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11800000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(11900000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12000000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12100000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12200000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12300000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12400000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12500000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12600000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12700000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12800000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(12900000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(13000000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(13100000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(13200000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(13300000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(13400000,0,'Normal','N',1,0,0,'control open','control close',0);
insert into pointstatus values(13500000,0,'Normal','N',1,0,0,'control open','control close',0);





/******************* Insert Cap Banks with status points ***************************/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(20100000,'DEVICE','CAPCONTROL','Cap Bank 1-1-1','CAP BANK','Location','N','-----');
insert into yukonpaobject values(20200000,'DEVICE','CAPCONTROL','Cap Bank 1-1-2','CAP BANK','Location','N','-----');
insert into yukonpaobject values(20300000,'DEVICE','CAPCONTROL','Cap Bank 1-1-3','CAP BANK','Location','N','-----');
insert into yukonpaobject values(20400000,'DEVICE','CAPCONTROL','Cap Bank 1-1-4','CAP BANK','Location','N','-----');
insert into yukonpaobject values(20500000,'DEVICE','CAPCONTROL','Cap Bank 1-1-5','CAP BANK','Location','N','-----');
insert into yukonpaobject values(20600000,'DEVICE','CAPCONTROL','Cap Bank 1-2-1','CAP BANK','Location','N','-----');
insert into yukonpaobject values(20700000,'DEVICE','CAPCONTROL','Cap Bank 1-2-2','CAP BANK','Location','N','-----');
insert into yukonpaobject values(20800000,'DEVICE','CAPCONTROL','Cap Bank 1-2-3','CAP BANK','Location','N','-----');
insert into yukonpaobject values(20900000,'DEVICE','CAPCONTROL','Cap Bank 2-1-1','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21000000,'DEVICE','CAPCONTROL','Cap Bank 2-1-2','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21100000,'DEVICE','CAPCONTROL','Cap Bank 2-1-3','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21200000,'DEVICE','CAPCONTROL','Cap Bank 2-2-1','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21300000,'DEVICE','CAPCONTROL','Cap Bank 2-2-2','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21400000,'DEVICE','CAPCONTROL','Cap Bank 2-2-3','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21500000,'DEVICE','CAPCONTROL','Cap Bank 2-3-1','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21600000,'DEVICE','CAPCONTROL','Cap Bank 2-3-2','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21700000,'DEVICE','CAPCONTROL','Cap Bank 2-3-3','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21800000,'DEVICE','CAPCONTROL','Cap Bank 3-1-1','CAP BANK','Location','N','-----');
insert into yukonpaobject values(21900000,'DEVICE','CAPCONTROL','Cap Bank 3-1-2','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22000000,'DEVICE','CAPCONTROL','Cap Bank 3-1-3','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22100000,'DEVICE','CAPCONTROL','Cap Bank 3-1-4','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22200000,'DEVICE','CAPCONTROL','Cap Bank 3-1-5','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22300000,'DEVICE','CAPCONTROL','Cap Bank 3-2-1','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22400000,'DEVICE','CAPCONTROL','Cap Bank 3-2-2','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22500000,'DEVICE','CAPCONTROL','Cap Bank 3-2-3','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22600000,'DEVICE','CAPCONTROL','Cap Bank 3-2-4','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22700000,'DEVICE','CAPCONTROL','Cap Bank 3-2-5','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22800000,'DEVICE','CAPCONTROL','Cap Bank 3-3-1','CAP BANK','Location','N','-----');
insert into yukonpaobject values(22900000,'DEVICE','CAPCONTROL','Cap Bank 3-3-2','CAP BANK','Location','N','-----');
insert into yukonpaobject values(23000000,'DEVICE','CAPCONTROL','Cap Bank 3-3-3','CAP BANK','Location','N','-----');
insert into yukonpaobject values(23100000,'DEVICE','CAPCONTROL','Cap Bank 4-1-1','CAP BANK','Location','N','-----');
insert into yukonpaobject values(23200000,'DEVICE','CAPCONTROL','Cap Bank 4-1-2','CAP BANK','Location','N','-----');
insert into yukonpaobject values(23300000,'DEVICE','CAPCONTROL','Cap Bank 4-1-3','CAP BANK','Location','N','-----');
insert into yukonpaobject values(23400000,'DEVICE','CAPCONTROL','Cap Bank 4-1-4','CAP BANK','Location','N','-----');
insert into yukonpaobject values(23500000,'DEVICE','CAPCONTROL','Cap Bank 4-1-5','CAP BANK','Location','N','-----');


/* deviceid(paoid), alarminhibit, controlinhibit */
insert into device values(20100000,'N','N');
insert into device values(20200000,'N','N');
insert into device values(20300000,'N','N');
insert into device values(20400000,'N','N');
insert into device values(20500000,'N','N');
insert into device values(20600000,'N','N');
insert into device values(20700000,'N','N');
insert into device values(20800000,'N','N');
insert into device values(20900000,'N','N');
insert into device values(21000000,'N','N');
insert into device values(21100000,'N','N');
insert into device values(21200000,'N','N');
insert into device values(21300000,'N','N');
insert into device values(21400000,'N','N');
insert into device values(21500000,'N','N');
insert into device values(21600000,'N','N');
insert into device values(21700000,'N','N');
insert into device values(21800000,'N','N');
insert into device values(21900000,'N','N');
insert into device values(22000000,'N','N');
insert into device values(22100000,'N','N');
insert into device values(22200000,'N','N');
insert into device values(22300000,'N','N');
insert into device values(22400000,'N','N');
insert into device values(22500000,'N','N');
insert into device values(22600000,'N','N');
insert into device values(22700000,'N','N');
insert into device values(22800000,'N','N');
insert into device values(22900000,'N','N');
insert into device values(23000000,'N','N');
insert into device values(23100000,'N','N');
insert into device values(23200000,'N','N');
insert into device values(23300000,'N','N');
insert into device values(23400000,'N','N');
insert into device values(23500000,'N','N');


/* deviceid, operationalstate, controllertype, controldeviceid, controlpointid, banksize,
   typeofswitch, switchmanufacture, maplocationid */
insert into capbank values(20100000,'Switched','Default',10100000,10100000,600,'Default','Default',20100000);
insert into capbank values(20200000,'Switched','Default',10200000,10200000,600,'Default','Default',20200000);
insert into capbank values(20300000,'Switched','Default',10300000,10300000,600,'Default','Default',20300000);
insert into capbank values(20400000,'Switched','Default',10400000,10400000,600,'Default','Default',20400000);
insert into capbank values(20500000,'Switched','Default',10500000,10500000,600,'Default','Default',20500000);
insert into capbank values(20600000,'Switched','Default',10600000,10600000,600,'Default','Default',20600000);
insert into capbank values(20700000,'Switched','Default',10700000,10700000,600,'Default','Default',20700000);
insert into capbank values(20800000,'Switched','Default',10800000,10800000,600,'Default','Default',20800000);
insert into capbank values(20900000,'Switched','Default',10900000,10900000,600,'Default','Default',20900000);
insert into capbank values(21000000,'Switched','Default',11000000,11000000,600,'Default','Default',21000000);
insert into capbank values(21100000,'Switched','Default',11100000,11100000,600,'Default','Default',21100000);
insert into capbank values(21200000,'Switched','Default',11200000,11200000,600,'Default','Default',21200000);
insert into capbank values(21300000,'Switched','Default',11300000,11300000,600,'Default','Default',21300000);
insert into capbank values(21400000,'Switched','Default',11400000,11400000,600,'Default','Default',21400000);
insert into capbank values(21500000,'Switched','Default',11500000,11500000,600,'Default','Default',21500000);
insert into capbank values(21600000,'Switched','Default',11600000,11600000,600,'Default','Default',21600000);
insert into capbank values(21700000,'Switched','Default',11700000,11700000,600,'Default','Default',21700000);
insert into capbank values(21800000,'Switched','Default',11800000,11800000,600,'Default','Default',21800000);
insert into capbank values(21900000,'Switched','Default',11900000,11900000,600,'Default','Default',21900000);
insert into capbank values(22000000,'Switched','Default',12000000,12000000,600,'Default','Default',22000000);
insert into capbank values(22100000,'Switched','Default',12100000,12100000,600,'Default','Default',22100000);
insert into capbank values(22200000,'Switched','Default',12200000,12200000,600,'Default','Default',22200000);
insert into capbank values(22300000,'Switched','Default',12300000,12300000,600,'Default','Default',22300000);
insert into capbank values(22400000,'Switched','Default',12400000,12400000,600,'Default','Default',22400000);
insert into capbank values(22500000,'Switched','Default',12500000,12500000,600,'Default','Default',22500000);
insert into capbank values(22600000,'Switched','Default',12600000,12600000,600,'Default','Default',22600000);
insert into capbank values(22700000,'Switched','Default',12700000,12700000,600,'Default','Default',22700000);
insert into capbank values(22800000,'Switched','Default',12800000,12800000,600,'Default','Default',22800000);
insert into capbank values(22900000,'Switched','Default',12900000,12900000,600,'Default','Default',22900000);
insert into capbank values(23000000,'Switched','Default',13000000,13000000,600,'Default','Default',23000000);
insert into capbank values(23100000,'Switched','Default',13100000,13100000,600,'Default','Default',23100000);
insert into capbank values(23200000,'Switched','Default',13200000,13200000,600,'Default','Default',23200000);
insert into capbank values(23300000,'Switched','Default',13300000,13300000,600,'Default','Default',23300000);
insert into capbank values(23400000,'Switched','Default',13400000,13400000,600,'Default','Default',23400000);
insert into capbank values(23500000,'Switched','Default',13500000,13500000,600,'Default','Default',23500000);

/*'Fixed'*/

/* pointid, pointtype, pointname, paobjectid, logicalgroup, stategroupid, serviceflag, alarminhibit,
   pseudoflag, pointoffset, achivetype, archiveinterval */
insert into point values(20100000,'Status','Cap Bank Status 1-1-1',20100000,'Default',3,'N','N','R',1,'None',0);
insert into point values(20200000,'Status','Cap Bank Status 1-1-2',20200000,'Default',3,'N','N','R',1,'None',0);
insert into point values(20300000,'Status','Cap Bank Status 1-1-3',20300000,'Default',3,'N','N','R',1,'None',0);
insert into point values(20400000,'Status','Cap Bank Status 1-1-4',20400000,'Default',3,'N','N','R',1,'None',0);
insert into point values(20500000,'Status','Cap Bank Status 1-1-5',20500000,'Default',3,'N','N','R',1,'None',0);
insert into point values(20600000,'Status','Cap Bank Status 1-2-1',20600000,'Default',3,'N','N','R',1,'None',0);
insert into point values(20700000,'Status','Cap Bank Status 1-2-2',20700000,'Default',3,'N','N','R',1,'None',0);
insert into point values(20800000,'Status','Cap Bank Status 1-2-3',20800000,'Default',3,'N','N','R',1,'None',0);
insert into point values(20900000,'Status','Cap Bank Status 2-1-1',20900000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21000000,'Status','Cap Bank Status 2-1-2',21000000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21100000,'Status','Cap Bank Status 2-1-3',21100000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21200000,'Status','Cap Bank Status 2-2-1',21200000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21300000,'Status','Cap Bank Status 2-2-2',21300000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21400000,'Status','Cap Bank Status 2-2-3',21400000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21500000,'Status','Cap Bank Status 2-3-1',21500000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21600000,'Status','Cap Bank Status 2-3-2',21600000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21700000,'Status','Cap Bank Status 2-3-3',21700000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21800000,'Status','Cap Bank Status 3-1-1',21800000,'Default',3,'N','N','R',1,'None',0);
insert into point values(21900000,'Status','Cap Bank Status 3-1-2',21900000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22000000,'Status','Cap Bank Status 3-1-3',22000000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22100000,'Status','Cap Bank Status 3-1-4',22100000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22200000,'Status','Cap Bank Status 3-1-5',22200000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22300000,'Status','Cap Bank Status 3-2-1',22300000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22400000,'Status','Cap Bank Status 3-2-2',22400000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22500000,'Status','Cap Bank Status 3-2-3',22500000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22600000,'Status','Cap Bank Status 3-2-4',22600000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22700000,'Status','Cap Bank Status 3-2-5',22700000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22800000,'Status','Cap Bank Status 3-3-1',22800000,'Default',3,'N','N','R',1,'None',0);
insert into point values(22900000,'Status','Cap Bank Status 3-3-2',22900000,'Default',3,'N','N','R',1,'None',0);
insert into point values(23000000,'Status','Cap Bank Status 3-3-3',23000000,'Default',3,'N','N','R',1,'None',0);
insert into point values(23100000,'Status','Cap Bank Status 4-1-1',23100000,'Default',3,'N','N','R',1,'None',0);
insert into point values(23200000,'Status','Cap Bank Status 4-1-2',23200000,'Default',3,'N','N','R',1,'None',0);
insert into point values(23300000,'Status','Cap Bank Status 4-1-3',23300000,'Default',3,'N','N','R',1,'None',0);
insert into point values(23400000,'Status','Cap Bank Status 4-1-4',23400000,'Default',3,'N','N','R',1,'None',0);
insert into point values(23500000,'Status','Cap Bank Status 4-1-5',23500000,'Default',3,'N','N','R',1,'None',0);


/* pointid, initialstate, controltype, controlinhibit, controloffset, closetime1, closetime2,
   statezerocontrol, stateonecontrol */
insert into pointstatus values(20100000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(20200000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(20300000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(20400000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(20500000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(20600000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(20700000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(20800000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(20900000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21000000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21100000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21200000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21300000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21400000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21500000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21600000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21700000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21800000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(21900000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22000000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22100000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22200000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22300000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22400000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22500000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22600000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22700000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22800000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(22900000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(23000000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(23100000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(23200000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(23300000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(23400000,0,'None','N',0,0,0,'control open','control close',0);
insert into pointstatus values(23500000,0,'None','N',0,0,0,'control open','control close',0);




/******************* Insert Feeders and associate Cap Banks ***************************/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(30100000,'CAPCONTROL','CAPCONTROL','Feeder 1-1','CCFEEDER','Location','N','-----');
insert into yukonpaobject values(30200000,'CAPCONTROL','CAPCONTROL','Feeder 1-2','CCFEEDER','Location','N','-----');
insert into yukonpaobject values(30300000,'CAPCONTROL','CAPCONTROL','Feeder 2-1','CCFEEDER','Location','N','-----');
insert into yukonpaobject values(30400000,'CAPCONTROL','CAPCONTROL','Feeder 2-2','CCFEEDER','Location','N','-----');
insert into yukonpaobject values(30500000,'CAPCONTROL','CAPCONTROL','Feeder 2-3','CCFEEDER','Location','N','-----');
insert into yukonpaobject values(30600000,'CAPCONTROL','CAPCONTROL','Feeder 3-1','CCFEEDER','Location','N','-----');
insert into yukonpaobject values(30700000,'CAPCONTROL','CAPCONTROL','Feeder 3-2','CCFEEDER','Location','N','-----');
insert into yukonpaobject values(30800000,'CAPCONTROL','CAPCONTROL','Feeder 3-3','CCFEEDER','Location','N','-----');
insert into yukonpaobject values(30900000,'CAPCONTROL','CAPCONTROL','Feeder 4-1','CCFEEDER','Location','N','-----');


/* feederid, peaksetpoint, offpeaksetpoint, upperbandwidth, currentvarloadpointid,
   currentwattloadpointid, maplocationid, lowerbandwidth */
insert into capcontrolfeeder values(30100000,150.0,0.0,450,500000,1800000,30100000,450);
insert into capcontrolfeeder values(30200000,150.0,0.0,450,600000,1900000,30200000,450);
insert into capcontrolfeeder values(30300000,150.0,0.0,450,700000,2000000,30300000,450);
insert into capcontrolfeeder values(30400000,150.0,0.0,450,800000,2100000,30400000,450);
insert into capcontrolfeeder values(30500000,150.0,0.0,450,900000,2200000,30500000,450);
insert into capcontrolfeeder values(30600000,150.0,0.0,450,1000000,2300000 ,30600000,450);
insert into capcontrolfeeder values(30700000,150.0,0.0,450,1100000,2400000 ,30700000,450);
insert into capcontrolfeeder values(30800000,150.0,0.0,450,1200000,2500000 ,30800000,450);
insert into capcontrolfeeder values(30900000,98.2,98.2,67,1300000,2600000 ,30900000,50);


/* feederid, deviceid(of cap bank), displayorder */
insert into ccfeederbanklist values(30100000,20100000,1);
insert into ccfeederbanklist values(30100000,20200000,2);
insert into ccfeederbanklist values(30100000,20300000,3);
insert into ccfeederbanklist values(30100000,20400000,4);
insert into ccfeederbanklist values(30100000,20500000,5);
insert into ccfeederbanklist values(30200000,20600000,1);
insert into ccfeederbanklist values(30200000,20700000,2);
insert into ccfeederbanklist values(30200000,20800000,3);
insert into ccfeederbanklist values(30300000,20900000,1);
insert into ccfeederbanklist values(30300000,21000000,2);
insert into ccfeederbanklist values(30300000,21100000,3);
insert into ccfeederbanklist values(30400000,21200000,1);
insert into ccfeederbanklist values(30400000,21300000,2);
insert into ccfeederbanklist values(30400000,21400000,3);
insert into ccfeederbanklist values(30500000,21500000,1);
insert into ccfeederbanklist values(30500000,21600000,2);
insert into ccfeederbanklist values(30500000,21700000,3);
insert into ccfeederbanklist values(30600000,21800000,1);
insert into ccfeederbanklist values(30600000,21900000,2);
insert into ccfeederbanklist values(30600000,22000000,3);
insert into ccfeederbanklist values(30600000,22100000,4);
insert into ccfeederbanklist values(30600000,22200000,5);
insert into ccfeederbanklist values(30700000,22300000,1);
insert into ccfeederbanklist values(30700000,22400000,2);
insert into ccfeederbanklist values(30700000,22500000,3);
insert into ccfeederbanklist values(30700000,22600000,4);
insert into ccfeederbanklist values(30700000,22700000,5);
insert into ccfeederbanklist values(30800000,22800000,1);
insert into ccfeederbanklist values(30800000,22900000,2);
insert into ccfeederbanklist values(30800000,23000000,3);
insert into ccfeederbanklist values(30900000,23100000,1);
insert into ccfeederbanklist values(30900000,23200000,2);
insert into ccfeederbanklist values(30900000,23300000,3);
insert into ccfeederbanklist values(30900000,23400000,4);
insert into ccfeederbanklist values(30900000,23500000,5);




/******************* Insert Sub Buses and associate Feeders ***************************/
/* paobjectid, paocategory, paoclass, paoname, type, description, disableflag */
insert into yukonpaobject values(40100000,'CAPCONTROL','CAPCONTROL','1 IndFeed','CCSUBBUS','GeoAreaName1','N','-----');
insert into yukonpaobject values(40200000,'CAPCONTROL','CAPCONTROL','2 SubBus','CCSUBBUS','GeoAreaName1','N','-----');
insert into yukonpaobject values(40300000,'CAPCONTROL','CAPCONTROL','3 BusOpt','CCSUBBUS','GeoAreaName2','N','-----');
insert into yukonpaobject values(40400000,'CAPCONTROL','CAPCONTROL','4 IndFeedPFbyKVAR','CCSUBBUS','GeoAreaName2','N','-----');

/* substationbusid, controlmethod, maxdailyoperation, maxoperationdisableflag, peaksetpoint,
   offpeaksetpoint, peakstarttime, peakstoptime, currentvarloadpointid, currentwattloadpointid,
   upperbandwidth, controlinterval, minresponsetime, minconfirmpercent, failurepercent, daysofweek,
   maplocationid, lowerbandwidth, controlunits */
insert into capcontrolsubstationbus values(40100000,'IndividualFeeder',0,'N',150.0,0.0,3600,72000,100000,1400000,450,0,60,50,25,'YYYYYYYN',40100000,450,'KVAR');
insert into capcontrolsubstationbus values(40200000,'SubstationBus',0,'N',150.0,0.0,3600,72000,200000,1500000,450,0,60,50,25,'YYYYYYYN',40200000,450,'KVAR');
insert into capcontrolsubstationbus values(40300000,'BusOptimizedFeeder',0,'N',150.0,0.0,3600,72000,300000,1600000,450,0,60,50,25,'YYYYYYYN',40300000,450,'KVAR');
insert into capcontrolsubstationbus values(40400000,'IndividualFeeder',0,'N',0.0,0.0,3600,72000,400000,1700000,0,0,60,50,25,'YYYYYYYN',40400000,0,'P-Factor KW/KVar');

/* substationbusid, feederid, displayorder */
insert into ccfeedersubassignment values(40100000,30100000,1);
insert into ccfeedersubassignment values(40100000,30200000,2);
insert into ccfeedersubassignment values(40200000,30300000,1);
insert into ccfeedersubassignment values(40200000,30400000,2);
insert into ccfeedersubassignment values(40200000,30500000,3);
insert into ccfeedersubassignment values(40300000,30600000,1);
insert into ccfeedersubassignment values(40300000,30700000,2);
insert into ccfeedersubassignment values(40300000,30800000,3);
insert into ccfeedersubassignment values(40400000,30900000,1);


