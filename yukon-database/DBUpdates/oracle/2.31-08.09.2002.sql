/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.31', 'Ryan', '9-AUG-2002', 'Added DeviceDNP table.');




create table DeviceDNP  (
   DeviceID             NUMBER                           not null,
   MasterAddress        NUMBER                           not null,
   SlaveAddress         NUMBER                           not null,
   PostCommWait         NUMBER                           not null,
   constraint PK_DEVICEDNP primary key (DeviceID),
   constraint FK_Dev_DevDNP foreign key (DeviceID)
         references DEVICE (DEVICEID)
)
/

