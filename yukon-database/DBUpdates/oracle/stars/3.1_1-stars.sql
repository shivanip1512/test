delete from ECToGenericMapping where MappingCategory = 'YukonSelectionList' and ItemID in
(select ListID from YukonSelectionList where ListName like 'OptOutPeriod%' and ListID > 2000);

alter table CustomerAccount
   add constraint FK_CustAcc_Add foreign key (BillingAddressID)
      references Address (AddressID);

create table LMConfigurationSASimple (
ConfigurationID      NUMBER               not null,
OperationalAddress   VARCHAR2(8)          not null,
);

alter table LMConfigurationSASimple
   add constraint PK_LMCONFIGURATIONSASIMPLE primary key (ConfigurationID);
alter table LMConfigurationSASimple
   add constraint FK_LMCfgSmp_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);
      