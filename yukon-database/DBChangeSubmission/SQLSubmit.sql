/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/********** New role properties for commands ************/
insert into yukonroleproperty values (-10306, -103, 'Read device', 'true', 'Allow the ability to read values from a device')
insert into yukonroleproperty values (-10307, -103, 'Write to device', 'true', 'Allow the ability to write values to a device')
insert into yukonroleproperty values (-10308, -103, 'Read disconnect', 'true', 'Allow the ability to read disconnect from a device')
insert into yukonroleproperty values (-10309, -103, 'Write disconnect', 'true', 'Allow the ability to write a disconnect to a device')

insert into yukonroleproperty values (-10310, -103, 'Read LM device', 'true', 'Allow the ability to read values from an LM device')
insert into yukonroleproperty values (-10311, -103, 'Write to LM device', 'true', 'Allow the ability to write values to an LM device')
insert into yukonroleproperty values (-10312, -103, 'Control LM device', 'true', 'Allow the ability to control an LM device')

insert into yukonroleproperty values (-10313, -103, 'Read Cap Control device', 'true', 'Allow the ability to read values from a Cap Control device')
insert into yukonroleproperty values (-10314, -103, 'Write to Cap Control device', 'true', 'Allow the ability to write values to a Cap Control device')
insert into yukonroleproperty values (-10315, -103, 'Control Cap Control device', 'true', 'Allow the ability to control a Cap Control device')

insert into yukonroleproperty values (-10316, -103, 'Execute Unknown Command', 'true', 'Allow the ability to execute commands which do not fall under another role property.')
insert into yukonroleproperty values (-10317, -103, 'Execute Manual Command', 'true', 'Allow the ability to execute manual commands')
/**********************/