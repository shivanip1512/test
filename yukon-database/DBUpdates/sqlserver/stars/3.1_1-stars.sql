alter table CustomerAccount
   add constraint FK_CustAcc_Add foreign key (BillingAddressID)
      references Address (AddressID);
go

create table LMConfigurationSASimple (
ConfigurationID      numeric              not null,
OperationalAddress   varchar(8)           not null,
);
go
alter table LMConfigurationSASimple
   add constraint PK_LMCONFIGURATIONSASIMPLE primary key (ConfigurationID);
go
alter table LMConfigurationSASimple
   add constraint FK_LMCfgSmp_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);
go
