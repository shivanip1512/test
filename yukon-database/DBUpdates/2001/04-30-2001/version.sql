/*==============================================================*/
/* Table : CTIDatabase                                          */
/*==============================================================*/

create table CTIDatabase (
   Version              VARCHAR2(6)                      not null,
   CTIEmployeeName      VARCHAR2(30)                     not null,
   DateApplied          DATE,
   Notes                VARCHAR2(300),
   constraint PK_CTIDATABASE primary key (Version)
)
/
insert into CTIDatabase values('1.01', 'Ryan Neuharth', '30-APR-01', 'Added all the curtail tables along with new loadmanagement tables. Also, enlarged the device name and point name column sizes to 60.');