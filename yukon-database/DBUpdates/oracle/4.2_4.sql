/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7257 */
/* Changes the foreign keys for LMThermostatSeason and LMThermostatSeasonEntry to cascade on delete */
ALTER TABLE LMThermostatSeason
DROP CONSTRAINT FK_ThSc_LThSs;

ALTER TABLE LMThermostatSeason
   ADD CONSTRAINT FK_LMThermSea_LMThermSch FOREIGN KEY (ScheduleId)
      REFERENCES LMThermostatSchedule (ScheduleId)
          ON DELETE CASCADE;

ALTER TABLE LMThermostatSeasonEntry
DROP CONSTRAINT FK_LThSe_LThSEn;

ALTER TABLE LMThermostatSeasonEntry
   ADD CONSTRAINT FK_LMThermSeaEntry_LMThermSea FOREIGN KEY (SeasonId)
      REFERENCES LMThermostatSeason (SeasonId)
         ON DELETE CASCADE;
         
/* Index added to LMThermostatSeasonEntry (SeasonId) */
CREATE INDEX INDX_LMThermSeaEnt_SeaId ON LMThermostatSeasonEntry (
   SeasonId ASC
);

/* Index added to LMThermostatSeason (ScheduleId) */
CREATE INDEX INDX_LMThermSea_SchId ON LMThermostatSeason (
   ScheduleId ASC
);

/* Indexes added to LMHardwareControlGroup (AccountId) (InventoryId) */
CREATE INDEX INDX_LMHardContGroup_AcctId ON LMHardwareControlGroup (
   AccountId ASC
);

CREATE INDEX INDX_LMHardContGroup_InvId ON LMHardwareControlGroup (
   InventoryId ASC
);

/* Functional index added to CustomerAccount */
CREATE INDEX INDX_CustAcct_AcctNum_FB ON CustomerAccount(
    UPPER(AccountNumber)
);

/* Index added to ECToLMCustomerEventMapping (EventId) */
CREATE INDEX INDX_ECToCustEventMap_EventId ON ECToLMCustomerEventMapping(
    EventId ASC
);

/* Index added to CustomerAccount (AccountNumber) */
CREATE INDEX INDX_CustAcct_AcctNum ON CustomerAccount(
    AccountNumber ASC
);

/* Index added to LMThermostatSchedule (InventoryId) (AccountId) */
CREATE INDEX INDX_LMThermSch_InvId ON LMThermostatSchedule (
   InventoryId ASC
);

CREATE INDEX INDX_LMThermSch_AcctId on LMThermostatSchedule (
   AccountId ASC
);

/* Index added to ECToInventoryMapping (InventoryId) */
CREATE INDEX INDX_ECToInvMap_InvId ON ECToInventoryMapping (
    InventoryId ASC
)
/* End YUK-7257 */

/* Start YUK-7178 */
INSERT INTO YukonRoleProperty VALUES(-20211,-202,'CIS Info Widget Enabled','true','Controls access to view the CIS Information widget.');
INSERT INTO YukonRoleProperty VALUES(-20212,-202,'CIS Info Type','NONE','Defines the type of CIS Information widget to display. Available placeholders: NONE, MULTISPEAK, CAYENTA');
/* End YUK-7178 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
