CREATE TABLE LMGroupEmetcon (
                              DeviceID       INTEGER  REFERENCES Device NOT NULL,
                              GoldAddress    INTEGER NOT NULL,
                              SilverAddress  INTEGER NOT NULL,
                              AddressUsage   CHAR(1) NOT NULL,
                              RelayUsage     CHAR(1) NOT NULL,
                              RouteID        INTEGER  REFERENCES Route NOT NULL )
/