/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

delete from YukonServices where servicename='Notification_Server';
go

/* @error ignore */
insert into YukonRoleProperty values(-20160,-201,'Create Login With Account','false','Require that a login is created with every new customer account.');
go

/* @error ignore */
insert into YukonRoleProperty values(-20161,-201,'Account Number Length','(none)','Specifies the number of account number characters to consider for comparison purposes during the customer account import process.');
go

/* @error ignore */
insert into YukonRoleProperty values(-20162,-201,'Rotation Digit Length','(none)','Specifies the number of rotation digit characters to ignore during the customer account import process.');
go

Update graphdataseries set type = 65 where type = 64;
go

create table POINTTRIGGER  (
   PointID              numeric                          not null,
   TriggerID            numeric                          not null,
   TriggerDeadband      float                           not null,
   VerificationID       numeric                          not null,
   VerificationDeadband float                           not null,
   CommandTimeout       numeric                          not null,
   Parameters           varchar(40)                    not null
);
go

alter table POINTTRIGGER
   add constraint PK_POINTTRIGGER primary key (PointID);
go

alter table POINTTRIGGER
   add constraint FK_PTTRIGGER_PT foreign key (PointID)
      references POINT (POINTID);
go

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERTRIGGERPT_PT foreign key (TriggerID)
      references POINT (POINTID);
go

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERVERIF_PT foreign key (VerificationID)
      references POINT (POINTID);
go


INSERT INTO DEVICETYPECOMMAND VALUES (-426, -3, 'MCT-430A', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-427, -20, 'MCT-430A', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-428, -21, 'MCT-430A', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-429, -22, 'MCT-430A', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-430, -115, 'MCT-430A', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-431, -116, 'MCT-430A', 6, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-432, -117, 'MCT-430A', 7, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-433, -118, 'MCT-430A', 8, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-434, -119, 'MCT-430A', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-435, -120, 'MCT-430A', 10, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-436, -121, 'MCT-430A', 11, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-437, -122, 'MCT-430A', 12, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-448, -123, 'MCT-430A', 13, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-449, -3, 'MCT-430S', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-450, -20, 'MCT-430S', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-451, -21, 'MCT-430S', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-452, -22, 'MCT-430S', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-453, -115, 'MCT-430S', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-454, -116, 'MCT-430S', 6, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-455, -117, 'MCT-430S', 7, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-456, -118, 'MCT-430S', 8, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-457, -119, 'MCT-430S', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-458, -120, 'MCT-430S', 10, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-459, -121, 'MCT-430S', 11, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-460, -122, 'MCT-430S', 12, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-461, -123, 'MCT-430S', 13, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-462, -30, 'CBC Expresscom', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-463, -31, 'CBC Expresscom', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-464, -32, 'CBC Expresscom', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-465, -33, 'CBC Expresscom', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-466, -30, 'CBC 7010', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-467, -31, 'CBC 7010', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-468, -32, 'CBC 7010', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-469, -33, 'CBC 7010', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-470, -30, 'CBC 7011', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-471, -31, 'CBC 7011', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-472, -32, 'CBC 7011', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-473, -33, 'CBC 7011', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-474, -30, 'CBC 7011', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-475, -31, 'CBC 7011', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-476, -32, 'CBC 7011', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-477, -33, 'CBC 7011', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-478, -30, 'CBC 7012', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-479, -31, 'CBC 7012', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-480, -32, 'CBC 7012', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-481, -33, 'CBC 7012', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-482, -30, 'CBC 7020', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-483, -31, 'CBC 7020', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-484, -32, 'CBC 7020', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-485, -33, 'CBC 7020', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-486, -30, 'CBC 7022', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-487, -31, 'CBC 7022', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-488, -32, 'CBC 7022', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-489, -33, 'CBC 7022', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-490, -30, 'CBC 7023', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-491, -31, 'CBC 7023', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-492, -32, 'CBC 7023', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-493, -33, 'CBC 7023', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-494, -30, 'CBC 7024', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-495, -31, 'CBC 7024', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-496, -32, 'CBC 7024', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-497, -33, 'CBC 7024', 4, 'Y', -1);

insert into command values(-124, 'putconfig raw 38 0', 'Install Emetcon Gold 1', 'VersacomSerial');
insert into command values(-125, 'putconfig raw 38 1', 'Install Emetcon Gold 1', 'VersacomSerial');
insert into command values(-126, 'putconfig raw 38 2', 'Install Emetcon Gold 1', 'VersacomSerial');
INSERT INTO DEVICETYPECOMMAND VALUES (-498, -124, 'VersacomSerial', 19, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-499, -125, 'VersacomSerial', 20, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-500, -126, 'VersacomSerial', 21, 'N', -1);

create table MSPVendor(
   VendorID             numeric              not null,
   CompanyName          varchar(64)          not null,
   UserName             varchar(64)          not null,
   Password             varchar(64)          not null,
   UniqueKey            varchar(32)          not null,
   Timeout              numeric              not null,
   URL                  varchar(120)         not null
)
go
alter table MSPVendor
   add constraint PK_MSPVendor primary key  (VendorID)
go
create table MSPInterface(
    VendorID        numeric     not null,
    Interface       varchar(12) not null,
    Endpoint        varchar(32) not null
)
go

alter table MSPInterface
   add constraint PK_MSPInterface primary key  (VendorID, Interface, Endpoint)
go
alter table MSPInterface
   add constraint FK_Intrfc_Vend foreign key (VendorID)
      references MSPVendor (VendorID)
go


insert into MSPVendor values (1, 'cannon', '(none)', '(none)', 'meterNumber', 0, 'http://127.0.0.1:8080/soap');
insert into MSPInterface values (1, 'CB_MR', 'CB_MRSoap');
insert into MSPInterface values (1, 'MR_CB', 'MR_CBSoap');
insert into MSPInterface values (1, 'EA_MR', 'EA_MRSoap');
insert into MSPInterface values (1, 'MR_EA', 'MR_EASoap');
insert into MSPInterface values (1, 'OA_OD', 'OA_ODSoap');
insert into MSPInterface values (1, 'OD_OA', 'OD_OASoap');
insert into MSPInterface values (1, 'CB_CD', 'CB_CDSoap');
insert into MSPInterface values (1, 'CD_CB', 'CD_CBSoap');

update command set label = 'Read two outages per read.  Specify 1(1&2), 3(3&4), 5(5&6)' where commandid = -106;
update command set label = 'Read rates for LI, LP, Volt LI, and Volt Profile Demand' where commandid = -111;
update command set label = 'Reset and Write current peak demand - kW and kWh to frozen register' where commandid = -113;
update command set label = 'Reset and Write current min/max voltage to frozen register' where commandid = -114;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'DBUPdater', '01-JULY-2006', 'DB Update Script', 7 );
go
