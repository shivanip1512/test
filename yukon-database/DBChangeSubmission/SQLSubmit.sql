/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/********** Jason - Update commands - please put into 3.4 and head ************/

insert into devicetypecommand values (-581, -83, 'MCT-470', 23, 'Y', -1)
insert into devicetypecommand values (-582, -6, 'MCT-470', 24, 'Y', -1)
insert into devicetypecommand values (-583, -34, 'MCT-470', 25, 'Y', -1)

insert into devicetypecommand values (-584, -15, 'LMT-2', 11, 'Y', -1)
insert into devicetypecommand values (-585, -18, 'LMT-2', 12, 'Y', -1)
insert into devicetypecommand values (-586, -19, 'LMT-2', 13, 'Y', -1)

insert into devicetypecommand values (-587, -15, 'DCT-501', 1, 'Y', -1)
insert into devicetypecommand values (-588, -18, 'DCT-501', 2, 'Y', -1)
insert into devicetypecommand values (-589, -19, 'DCT-501', 3, 'Y', -1)

insert into devicetypecommand values (-590, -15, 'MCT-310IDL', 11, 'Y', -1)
insert into devicetypecommand values (-591, -18, 'MCT-310IDL', 12, 'Y', -1)
insert into devicetypecommand values (-592, -19, 'MCT-310IDL', 13, 'Y', -1)

insert into devicetypecommand values (-593, -15, 'MCT-310CT', 11, 'Y', -1)
insert into devicetypecommand values (-594, -18, 'MCT-310CT', 12, 'Y', -1)
insert into devicetypecommand values (-595, -19, 'MCT-310CT', 13, 'Y', -1)

insert into devicetypecommand values (-596, -15, 'MCT-310IM', 1, 'Y', -1)
insert into devicetypecommand values (-597, -18, 'MCT-310IM', 2, 'Y', -1)
insert into devicetypecommand values (-598, -19, 'MCT-310IM', 3, 'Y', -1)

insert into devicetypecommand values (-599, -15, 'MCT-318L', 12, 'Y', -1)
insert into devicetypecommand values (-600, -18, 'MCT-318L', 13, 'Y', -1)
insert into devicetypecommand values (-601, -19, 'MCT-318L', 14, 'Y', -1)

insert into devicetypecommand values (-602, -15, 'MCT-410CL', 26, 'Y', -1)

insert into devicetypecommand values (-603, -15, 'MCT-410FL', 26, 'Y', -1)

insert into devicetypecommand values (-604, -15, 'MCT-410GL', 26, 'Y', -1)

insert into devicetypecommand values (-605, -15, 'MCT-410IL', 26, 'Y', -1)

insert into devicetypecommand values (-606, -15, 'MCT-430EL', 14, 'Y', -1)
insert into devicetypecommand values (-607, -18, 'MCT-430EL', 15, 'Y', -1)
insert into devicetypecommand values (-608, -19, 'MCT-430EL', 16, 'Y', -1)

insert into devicetypecommand values (-609, -15, 'MCT-430IN', 14, 'Y', -1)
insert into devicetypecommand values (-610, -18, 'MCT-430IN', 15, 'Y', -1)
insert into devicetypecommand values (-611, -19, 'MCT-430IN', 16, 'Y', -1)

insert into devicetypecommand values (-612, -15, 'MCT-430LG', 14, 'Y', -1)
insert into devicetypecommand values (-613, -18, 'MCT-430LG', 15, 'Y', -1)
insert into devicetypecommand values (-614, -19, 'MCT-430LG', 16, 'Y', -1)

insert into devicetypecommand values (-615, -15, 'MCT-470', 21, 'Y', -1)
insert into devicetypecommand values (-616, -18, 'MCT-470', 22, 'Y', -1)
insert into devicetypecommand values (-617, -19, 'MCT-470', 23, 'Y', -1)


update command set category='All MCT-4xx Series' where commandid = -98 or commandid = -99


insert into devicetypecommand values (-618, -6, 'MCT-430EL', 17, 'Y', -1)
insert into devicetypecommand values (-619, -34, 'MCT-430EL', 18, 'Y', -1)

insert into devicetypecommand values (-620, -6, 'MCT-430IN', 17, 'Y', -1)
insert into devicetypecommand values (-621, -34, 'MCT-430IN', 18, 'Y', -1)

insert into devicetypecommand values (-622, -6, 'MCT-430LG', 17, 'Y', -1)
insert into devicetypecommand values (-623, -34, 'MCT-430LG', 18, 'Y', -1)

update command set category = 'ALL MCT-4xx Series' where commandid = -105 or commandid = -109 or commandid = -112 or commandid = -113


insert into devicetypecommand values (-624, -105, 'MCT-430EL', 19, 'Y', -1)
insert into devicetypecommand values (-625, -109, 'MCT-430EL', 20, 'Y', -1)
insert into devicetypecommand values (-626, -112, 'MCT-430EL', 21, 'Y', -1)
insert into devicetypecommand values (-627, -113, 'MCT-430EL', 22, 'Y', -1)

insert into devicetypecommand values (-628, -105, 'MCT-430IN', 19, 'Y', -1)
insert into devicetypecommand values (-629, -109, 'MCT-430IN', 20, 'Y', -1)
insert into devicetypecommand values (-630, -112, 'MCT-430IN', 21, 'Y', -1)
insert into devicetypecommand values (-631, -113, 'MCT-430IN', 22, 'Y', -1)

insert into devicetypecommand values (-632, -105, 'MCT-430LG', 19, 'Y', -1)
insert into devicetypecommand values (-633, -109, 'MCT-430LG', 20, 'Y', -1)
insert into devicetypecommand values (-634, -112, 'MCT-430LG', 21, 'Y', -1)
insert into devicetypecommand values (-635, -113, 'MCT-430LG', 22, 'Y', -1)

insert into devicetypecommand values (-636, -105, 'MCT-470', 26, 'Y', -1)
insert into devicetypecommand values (-637, -109, 'MCT-470', 27, 'Y', -1)
insert into devicetypecommand values (-638, -112, 'MCT-470', 28, 'Y', -1)
insert into devicetypecommand values (-639, -113, 'MCT-470', 29, 'Y', -1)

/***************************************************/


/* device formatter */
insert into YukonRole values(-8,'Configuration','Yukon','Miscellaneous Yukon configuration settings');
insert into YukonRoleProperty values(-1700,-8,'Device Display Template','{name}','Defines the format for displaying devices. Available placeholders: {name},{description},{meterNumber},{id},{address}');
insert into YukonGroupRole values(-280,-1,-8,-1700,'{name}');

/* end device formatter */
