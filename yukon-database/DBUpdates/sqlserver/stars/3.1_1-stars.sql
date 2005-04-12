delete from ECToGenericMapping where MappingCategory = 'YukonSelectionList' and ItemID in
(select ListID from YukonSelectionList where ListName like 'OptOutPeriod%' and ListID > 2000);
go

delete from YukonListEntry where ListID in
(select ListID from YukonSelectionList where ListName like 'OptOutPeriod%');
go

delete from YukonSelectionList where ListName like 'OptOutPeriod%' and ListID > 2000;
go

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
