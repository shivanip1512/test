CREATE TABLE LMGroupVersacom (
                               DeviceID         INTEGER REFERENCES Device NOT NULL,
                               RouteID          INTEGER REFERENCES Route NOT NULL,
                               UtilityAddress   INTEGER NOT NULL,
                               SectionAddress   INTEGER NOT NULL,
                               ClassAddress     INTEGER NOT NULL,
                               DivisionAddress  INTEGER NOT NULL,
                               AddressUsage     CHAR(4) NOT NULL,
                               RelayUsage       CHAR(3) NOT NULL)
/