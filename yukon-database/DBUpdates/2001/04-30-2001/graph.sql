/*==============================================================*/
/* Table : LMMacsScheduleCustomerList                           */
/*==============================================================*/

create table LMMacsScheduleCustomerList (
   ScheduleID           NUMBER                           not null,
   LMCustomerDeviceID   NUMBER                           not null,
   CustomerOrder        NUMBER                           not null,
   constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key (ScheduleID, LMCustomerDeviceID),
   constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
         references CICustomerBase (DeviceID),
   constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
         references MACSchedule (ScheduleID)
)
/



/*==============================================================*/
/* Table : GraphCustomerList                                    */
/*==============================================================*/

create table GraphCustomerList (
   GraphDefinitionID    NUMBER                           not null,
   CustomerID           NUMBER                           not null,
   CustomerOrder        NUMBER                           not null,
   constraint PK_GRAPHCUSTOMERLIST primary key (GraphDefinitionID, CustomerID),
   constraint FK_GRAPHCUS_REFGRPHCS_CICUSTOM foreign key (CustomerID)
         references CICustomerBase (DeviceID),
   constraint FK_GRAPHCUS_REFGRPHCU_GRAPHDEF foreign key (GraphDefinitionID)
         references GRAPHDEFINITION (GRAPHDEFINITIONID)
)
/


/*==============================================================*/
/* Table : CustomerWebSettings                                  */
/*==============================================================*/

create table CustomerWebSettings (
   CustomerID           NUMBER                           not null,
   DatabaseAlias        VARCHAR2(40)                     not null,
   Logo                 VARCHAR2(60)                     not null,
   HomeURL              VARCHAR2(60)                     not null,
   constraint PK_CUSTOMERWEBSETTINGS primary key (CustomerID),
   constraint FK_CustWebSet_CICstBse foreign key (CustomerID)
         references CICustomerBase (DeviceID)
)
/
