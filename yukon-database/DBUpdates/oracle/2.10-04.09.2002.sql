/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.10', 'Ryan', '05-APR-2002', 'Added 2 columns to the LMProgramDirectGear table');



/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/

alter table LMProgramDirectGear ADD MethodOptionType VARCHAR2(30);
alter table LMProgramDirectGear ADD MethodOptionMax NUMBER;

update LMProgramDirectGear set MethodOptionType = 'FixedCount';
update LMProgramDirectGear set MethodOptionMax = 0;

alter table LMProgramDirectGear MODIFY MethodOptionType NOT NULL;
alter table LMProgramDirectGear MODIFY MethodOptionMax NOT NULL;

/******************************************************************************/
/* END DATABASEEDITOR UPDATES                                                 */
/******************************************************************************/



/******************************************************************************/
/* START TDC UPDATES                                                 */
/******************************************************************************/
/******************************************************************************/
/* END TDC UPDATES                                                 */
/******************************************************************************/




/******************************************************************************/
/* START GRAPH UPDATES                                                 */
/******************************************************************************/

/******************************************************************************/
/* END GRAPH UPDATES                                                 */
/******************************************************************************/
