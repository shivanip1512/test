alter table CustomerAccount
   add constraint FK_CustAcc_Add foreign key (BillingAddressID)
      references Address (AddressID);
go