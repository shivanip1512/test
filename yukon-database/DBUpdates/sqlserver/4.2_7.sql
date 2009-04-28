/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7403 */
alter table DYNAMICCCSPECIALAREA
   add constraint FK_DynCCSpecA_CapContSpecA foreign key (AreaID)
      references CAPCONTROLSPECIALAREA (AreaID);
/* End YUK-7403 */

/* Start YUK-7331 */
Update YukonRoleProperty
Set Description = 'A Java Regular Expression that will be matched against the output of the URL to determine if the call was successful'
Where RolePropertyId = -80005;
/* End YUK-7331 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '28-APR-2009', 'Latest Update', 7);
