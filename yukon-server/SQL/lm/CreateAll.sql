CREATE TABLE LMGroupEmetcon (
                              DeviceID       INTEGER  REFERENCES Device NOT NULL,
                              GoldAddress    INTEGER NOT NULL,
                              SilverAddress  INTEGER NOT NULL,
                              AddressUsage   CHAR(1) NOT NULL,
                              RelayUsage     CHAR(1) NOT NULL,
                              RouteID        INTEGER  REFERENCES Route NOT NULL );

CREATE TABLE LMGroupVersacom (
                               DeviceID         INTEGER REFERENCES Device NOT NULL,
                               RouteID          INTEGER REFERENCES Route NOT NULL,
                               UtilityAddress   INTEGER NOT NULL,
                               SectionAddress   INTEGER NOT NULL,
                               ClassAddress     INTEGER NOT NULL,
                               DivisionAddress  INTEGER NOT NULL,
                               AddressUsage     CHAR(4) NOT NULL,
                               RelayUsage       CHAR(3) NOT NULL);

CREATE TABLE LMGroupVersacomSerial (
                                      DeviceID        INTEGER REFERENCES Device NOT NULL,
                                      DeviceIDofGroup INTEGER REFERENCES Device(DeviceID) NOT NULL,
                                      RouteID         INTEGER REFERENCES Route NOT NULL,
                                      SerialNumber    INTEGER NOT NULL,
                                      RelayUsage      CHAR(3) NOT NULL);

