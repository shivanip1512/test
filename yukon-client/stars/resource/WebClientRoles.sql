insert into YukonRole values(-2,'Energy Company','Yukon','Energy company role');
insert into YukonRole values(-207,'Odds For Control','Operator','Operator access to odds for control');
insert into YukonRole values(-400,'Residential Customer','Consumer','Access to residential customer information');

/* Energy Company Role Properties */
insert into YukonRoleProperty values(-1100,-2,'admin_email_address','info@cannontech.com','Sender address of the emails sent by the STARS server');
insert into YukonRoleProperty values(-1101,-2,'optout_notification_recipients','info@cannontech.com','Recipients of the opt out notification email');
insert into YukonRoleProperty values(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
insert into YukonRoleProperty values(-1103,-2,'switch_command_file','c:/yukon/switch_command/default_switch.txt','Location of the file to temporarily store the switch commands');
insert into YukonRoleProperty values(-1104,-2,'optout_command_file','c:/yukon/switch_command/default_optout.txt','Location of the file to temporarily store the opt out commands');
insert into YukonRoleProperty values(-1105,-2,'customer_group_name','Residential Customers','Group name of all the customer logins');
insert into YukonRoleProperty values(-1106,-2,'opt_out_noun','opt out','Noun form of the customized term for opt out');
insert into YukonRoleProperty values(-1107,-2,'opt_out_verb','opt out of','Verbical form of the customized term for opt out');
insert into YukonRoleProperty values(-1108,-2,'opt_out_past','opted out','Past form of the customized term for opt out');
insert into YukonRoleProperty values(-1109,-2,'term_reenable','reenable','Customized term for reenable');

/* Web Client Role Properties */
insert into YukonRoleProperty values(-10806,-108,'log_off_url','(none)','The url to take the user after logging off the Yukon web application');

/* Operator Administrator Role Properties */
insert into YukonRoleProperty values(-20000,-200,'Config Energy Company','false','Controls whether to allow configuring the energy company');
insert into YukonRoleProperty values(-20001,-200,'Create Energy Company','false','Controls whether to allow creating a new energy company');
insert into YukonRoleProperty values(-20002,-200,'Delete Energy Company','false','Controls whether to allow deleting the energy company');

/* Operator Consumer Info Role Properties */
insert into YukonRoleProperty values(-20100,-201,'Not Implemented','false','Controls whether to show the features not implemented yet (not recommended)');
insert into YukonRoleProperty values(-20101,-201,'Account General','true','Controls whether to show the general account information');
insert into YukonRoleProperty values(-20102,-201,'Account Residence','false','Controls whether to show the customer residence information');
insert into YukonRoleProperty values(-20103,-201,'Account Call Tracking','false','Controls whether to enable the call tracking feature');
insert into YukonRoleProperty values(-20104,-201,'Metering Interval Data','false','Controls whether to show the metering interval data');
insert into YukonRoleProperty values(-20105,-201,'Metering Usage','false','Controls whether to show the metering time of use');
insert into YukonRoleProperty values(-20106,-201,'Programs Control History','true','Controls whether to show the program control history');
insert into YukonRoleProperty values(-20107,-201,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
insert into YukonRoleProperty values(-20108,-201,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
insert into YukonRoleProperty values(-20109,-201,'Appliances','true','Controls whether to show the appliance information');
insert into YukonRoleProperty values(-20110,-201,'Appliances Create','true','Controls whether to enable the appliance creation feature');
insert into YukonRoleProperty values(-20111,-201,'Hardwares','true','Controls whether to show the hardware information');
insert into YukonRoleProperty values(-20112,-201,'Hardwares Create','true','Controls whether to enable the hardware creation feature');
insert into YukonRoleProperty values(-20113,-201,'Hardwares Thermostat','true','Controls whether to enable the thermostat programming feature');
insert into YukonRoleProperty values(-20114,-201,'Work Orders','false','Controls whether to enable the service request feature');
insert into YukonRoleProperty values(-20115,-201,'Admin Change Login','true','Controls whether to enable the changing customer login feature');
insert into YukonRoleProperty values(-20116,-201,'Admin FAQ','false','Controls whether to show customer FAQs');
insert into YukonRoleProperty values(-20150,-201,'Super Operator','false','Used for some testing functions (not recommended)');
insert into YukonRoleProperty values(-20151,-201,'New Account Wizard','true','Controls whether to enable the new account wizard');
insert into YukonRoleProperty values(-20152,-201,'Customized FAQ Link','false','Controls whether the FAQ link links to a customized location provided by the energy company');
insert into YukonRoleProperty values(-20800,-201,'Link FAQ','FAQ.jsp','The customized FAQ link');
insert into YukonRoleProperty values(-20810,-201,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-20813,-201,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-20814,-201,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-20815,-201,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-20816,-201,'Text Reenable','reenable','Term for reenable');
insert into YukonRoleProperty values(-20819,-201,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-20820,-201,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-20830,-201,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-20831,-201,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-20832,-201,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-20833,-201,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-20834,-201,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-20850,-201,'Title Programs Control History','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-20851,-201,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-20852,-201,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-20853,-201,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-20854,-201,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-20855,-201,'Title Thermostat Schedule','Schedule','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-20856,-201,'Title Thermostat Manual','Manual','Title of the thermostat manual page');
insert into YukonRoleProperty values(-20870,-201,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');

/* Odds For Control Role Properties */
insert into YukonRoleProperty values(-20700,-207,'Odds For Control Label','Odds for Control','The operator specific name for odds for control');

/* Residential Customer Role Properties */
insert into YukonRoleProperty values(-40000,-400,'Not Implemented','false','Controls whether to show the features not implemented yet (not recommended)');
insert into YukonRoleProperty values(-40001,-400,'Account General','true','Controls whether to show the general account information');
insert into YukonRoleProperty values(-40002,-400,'Metering Usage','false','Controls whether to show the metering time of use');
insert into YukonRoleProperty values(-40003,-400,'Programs Control History','true','Controls whether to show the program control history');
insert into YukonRoleProperty values(-40004,-400,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
insert into YukonRoleProperty values(-40005,-400,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
insert into YukonRoleProperty values(-40006,-400,'Hardwares Thermostat','true','Controls whether to enable the thermostat programming feature');
insert into YukonRoleProperty values(-40007,-400,'Questions Utility','true','Controls whether to show the contact information of the energy company');
insert into YukonRoleProperty values(-40008,-400,'Questions FAQ','true','Controls whether to show customer FAQs');
insert into YukonRoleProperty values(-40009,-400,'Admin Change Login','true','Controls whether to allow customers to change their own login');
insert into YukonRoleProperty values(-40050,-400,'Notification on General Page','false','Controls whether to show the notification email box on the general page (useful only when the programs enrollment feature is not selected)');
insert into YukonRoleProperty values(-40051,-400,'Hide Opt Out Box','false','Controls whether to show the opt out box on the programs opt out page');
insert into YukonRoleProperty values(-40052,-400,'Customized FAQ Link','false','Controls whether the FAQ link links to a customized location provided by the energy company');
insert into YukonRoleProperty values(-40053,-400,'Customized Utility Email Link','false','Controls whether the utility email links to a customized location provided by the energy company');
insert into YukonRoleProperty values(-40054,-400,'Disable Program Signup','false','Controls whether to prevent the customers from enrolling in or out of the programs');
insert into YukonRoleProperty values(-40100,-400,'Link FAQ','FAQ.jsp','The customized FAQ link');
insert into YukonRoleProperty values(-40101,-400,'Link Utility Email','FAQ.jsp','The customized utility email');
insert into YukonRoleProperty values(-40110,-400,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-40111,-400,'Text Controlled','controlled','Past form of the term for control');
insert into YukonRoleProperty values(-40112,-400,'Text Controlling','controlling','Present form of the term for control');
insert into YukonRoleProperty values(-40113,-400,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-40114,-400,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-40115,-400,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-40116,-400,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-40117,-400,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-40130,-400,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-40131,-400,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-40132,-400,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-40133,-400,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-40134,-400,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-40150,-400,'Title General','WELCOME TO ENERGY COMPANY SERVICES!','Title of the general page');
insert into YukonRoleProperty values(-40151,-400,'Title Programs Control History','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-40152,-400,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-40153,-400,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-40154,-400,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-40155,-400,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-40156,-400,'Title Utility','QUESTIONS - UTILITY','Title of the utility page');
insert into YukonRoleProperty values(-40157,-400,'Title Thermostat Schedule','Schedule','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-40158,-400,'Title Thermostat Manual','Manual','Title of the thermostat manual page');
insert into YukonRoleProperty values(-40170,-400,'Description General','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','Description on the general page');
insert into YukonRoleProperty values(-40171,-400,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');
insert into YukonRoleProperty values(-40180,-400,'Image Corner','Mom.jpg','Image at the upper-left corner');
insert into YukonRoleProperty values(-40181,-400,'Image General','Family.jpg','Image on the general page');

/* Web Client Groups */
insert into yukongroup values (-301,'Web Client Operators','The default group of web client operators');
insert into yukongroup values (-300,'Residential Customers','The default group of residential customers');

insert into yukongrouprole values (500,-300,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (501,-300,-108,-10801,'(none)');
insert into yukongrouprole values (502,-300,-108,-10802,'(none)');
insert into yukongrouprole values (503,-300,-108,-10803,'(none)');
insert into yukongrouprole values (504,-300,-108,-10804,'(none)');
insert into yukongrouprole values (505,-300,-108,-10805,'DemoHeaderCES.gif');
insert into yukongrouprole values (506,-300,-108,-10806,'(none)');
insert into yukongrouprole values (520,-300,-400,-40000,'true');
insert into yukongrouprole values (521,-300,-400,-40001,'true');
insert into yukongrouprole values (522,-300,-400,-40002,'false');
insert into yukongrouprole values (523,-300,-400,-40003,'true');
insert into yukongrouprole values (524,-300,-400,-40004,'true');
insert into yukongrouprole values (525,-300,-400,-40005,'true');
insert into yukongrouprole values (526,-300,-400,-40006,'true');
insert into yukongrouprole values (527,-300,-400,-40007,'true');
insert into yukongrouprole values (528,-300,-400,-40008,'true');
insert into yukongrouprole values (529,-300,-400,-40009,'true');
insert into yukongrouprole values (550,-300,-400,-40050,'false');
insert into yukongrouprole values (551,-300,-400,-40051,'false');
insert into yukongrouprole values (552,-300,-400,-40052,'false');
insert into yukongrouprole values (553,-300,-400,-40053,'false');
insert into yukongrouprole values (554,-300,-400,-40054,'false');
insert into yukongrouprole values (600,-300,-400,-40100,'(none)');
insert into yukongrouprole values (601,-300,-400,-40101,'(none)');
insert into yukongrouprole values (602,-300,-400,-40102,'(none)');
insert into yukongrouprole values (610,-300,-400,-40110,'(none)');
insert into yukongrouprole values (611,-300,-400,-40111,'(none)');
insert into yukongrouprole values (612,-300,-400,-40112,'(none)');
insert into yukongrouprole values (613,-300,-400,-40113,'(none)');
insert into yukongrouprole values (614,-300,-400,-40114,'(none)');
insert into yukongrouprole values (615,-300,-400,-40115,'(none)');
insert into yukongrouprole values (616,-300,-400,-40116,'(none)');
insert into yukongrouprole values (617,-300,-400,-40117,'(none)');
insert into yukongrouprole values (630,-300,-400,-40130,'(none)');
insert into yukongrouprole values (631,-300,-400,-40131,'(none)');
insert into yukongrouprole values (632,-300,-400,-40132,'(none)');
insert into yukongrouprole values (633,-300,-400,-40133,'(none)');
insert into yukongrouprole values (634,-300,-400,-40134,'(none)');
insert into yukongrouprole values (650,-300,-400,-40150,'(none)');
insert into yukongrouprole values (651,-300,-400,-40151,'(none)');
insert into yukongrouprole values (652,-300,-400,-40152,'(none)');
insert into yukongrouprole values (653,-300,-400,-40153,'(none)');
insert into yukongrouprole values (654,-300,-400,-40154,'(none)');
insert into yukongrouprole values (655,-300,-400,-40155,'(none)');
insert into yukongrouprole values (656,-300,-400,-40156,'(none)');
insert into yukongrouprole values (657,-300,-400,-40157,'(none)');
insert into yukongrouprole values (658,-300,-400,-40158,'(none)');
insert into yukongrouprole values (670,-300,-400,-40170,'(none)');
insert into yukongrouprole values (671,-300,-400,-40171,'(none)');
insert into yukongrouprole values (680,-300,-400,-40180,'(none)');
insert into yukongrouprole values (681,-300,-400,-40181,'(none)');

insert into yukongrouprole values (700,-301,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (701,-301,-108,-10801,'(none)');
insert into yukongrouprole values (702,-301,-108,-10802,'(none)');
insert into yukongrouprole values (703,-301,-108,-10803,'(none)');
insert into yukongrouprole values (704,-301,-108,-10804,'(none)');
insert into yukongrouprole values (705,-301,-108,-10805,'(none)');
insert into yukongrouprole values (706,-301,-108,-10806,'(none)');
insert into yukongrouprole values (720,-301,-201,-20100,'true');
insert into yukongrouprole values (721,-301,-201,-20101,'true');
insert into yukongrouprole values (722,-301,-201,-20102,'true');
insert into yukongrouprole values (723,-301,-201,-20103,'true');
insert into yukongrouprole values (724,-301,-201,-20104,'false');
insert into yukongrouprole values (725,-301,-201,-20105,'false');
insert into yukongrouprole values (726,-301,-201,-20106,'true');
insert into yukongrouprole values (727,-301,-201,-20107,'true');
insert into yukongrouprole values (728,-301,-201,-20108,'true');
insert into yukongrouprole values (729,-301,-201,-20109,'true');
insert into yukongrouprole values (730,-301,-201,-20110,'true');
insert into yukongrouprole values (731,-301,-201,-20111,'true');
insert into yukongrouprole values (732,-301,-201,-20112,'true');
insert into yukongrouprole values (733,-301,-201,-20113,'true');
insert into yukongrouprole values (734,-301,-201,-20114,'true');
insert into yukongrouprole values (735,-301,-201,-20115,'true');
insert into yukongrouprole values (736,-301,-201,-20116,'true');
insert into yukongrouprole values (750,-301,-201,-20150,'true');
insert into yukongrouprole values (751,-301,-201,-20151,'true');
insert into yukongrouprole values (752,-301,-201,-20152,'false');
insert into yukongrouprole values (770,-301,-202,-20200,'(none)');
insert into yukongrouprole values (775,-301,-203,-20300,'(none)');
insert into yukongrouprole values (776,-301,-203,-20301,'(none)');
insert into yukongrouprole values (780,-301,-204,-20400,'(none)');
insert into yukongrouprole values (785,-301,-205,-20500,'(none)');
insert into yukongrouprole values (790,-301,-207,-20700,'(none)');
insert into yukongrouprole values (800,-301,-201,-20800,'(none)');
insert into yukongrouprole values (810,-301,-201,-20810,'(none)');
insert into yukongrouprole values (813,-301,-201,-20813,'(none)');
insert into yukongrouprole values (814,-301,-201,-20814,'(none)');
insert into yukongrouprole values (815,-301,-201,-20815,'(none)');
insert into yukongrouprole values (816,-301,-201,-20816,'(none)');
insert into yukongrouprole values (819,-301,-201,-20819,'(none)');
insert into yukongrouprole values (820,-301,-201,-20820,'(none)');
insert into yukongrouprole values (830,-301,-201,-20830,'(none)');
insert into yukongrouprole values (831,-301,-201,-20831,'(none)');
insert into yukongrouprole values (832,-301,-201,-20832,'(none)');
insert into yukongrouprole values (833,-301,-201,-20833,'(none)');
insert into yukongrouprole values (834,-301,-201,-20834,'(none)');
insert into yukongrouprole values (850,-301,-201,-20850,'(none)');
insert into yukongrouprole values (851,-301,-201,-20851,'(none)');
insert into yukongrouprole values (852,-301,-201,-20852,'(none)');
insert into yukongrouprole values (853,-301,-201,-20853,'(none)');
insert into yukongrouprole values (854,-301,-201,-20854,'(none)');
insert into yukongrouprole values (855,-301,-201,-20855,'(none)');
insert into yukongrouprole values (856,-301,-201,-20856,'(none)');
insert into yukongrouprole values (870,-301,-201,-20870,'(none)');
