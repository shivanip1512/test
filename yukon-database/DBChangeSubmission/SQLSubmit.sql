/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/*****added by Elliot ***********************/
/****Bug Id: 573*****************************/
/*****COS from P/Q is not supported anymore**/
/*****however still exists in DB Editor******/
/****start***********************************/
delete from yukonlistentry where entryid = 125
/****end*************************************/
/***********added by Elliot******************/
/***********DB changes related to Bugs 493, 494****/
/*************need a table between template and display****/
/****start***********************************/
create table TemplateDisplay (
DisplayNum numeric (18,0) not null,
TemplateNum numeric (18,0) not null
)

go

alter table TemplateDisplay
   add constraint PK_TemplateDisplay primary key  (DisplayNum)
go

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_DISPLAY foreign key (DisplayNum)
      references DISPLAY (DISPLAYNUM)
go

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_TEMPLATE foreign key (TemplateNum)
      references TEMPLATE (TEMPLATENUM)
go
/****end*************************************/

/***********************************************************************************/

