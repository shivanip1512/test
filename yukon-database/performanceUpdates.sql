
/* Cascade deleting for thermostat schedule deadlock issue */
ALTER TABLE LMThermostatSeason
DROP CONSTRAINT FK_ThSc_LThSs;

ALTER TABLE LMThermostatSeason
   ADD CONSTRAINT FK_LMThermSea_LMThermSch FOREIGN KEY (ScheduleId)
      REFERENCES LMThermostatSchedule (ScheduleId)
          ON DELETE CASCADE;


ALTER TABLE LMThermostatSeasonEntry
DROP CONSTRAINT FK_LThSe_LThSEn;

ALTER TABLE LMThermostatSeasonEntry
   ADD CONSTRAINT FK_LMThermSeaEntry_LMThermoSea FOREIGN KEY (SeasonId)
      REFERENCES LMThermostatSeason (SeasonId)
         ON DELETE CASCADE;
         
         
/* index to improve LMThermostatSeasonEntry access time */         
drop index INDX_SeasonId;

/*==============================================================*/
/* Index: INDX_SeasonId                                         */
/*==============================================================*/
create index INDX_SeasonId on LMThermostatSeasonEntry (
   SeasonID ASC
);
