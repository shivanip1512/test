CREATE TABLE LMGroupVersacomSerial (
                                      DeviceID        INTEGER REFERENCES Device NOT NULL,
                                      DeviceIDofGroup INTEGER REFERENCES Device(DeviceID) NOT NULL,
                                      RouteID         INTEGER REFERENCES Route NOT NULL,
                                      SerialNumber    INTEGER NOT NULL,
                                      RelayUsage      CHAR(3) NOT NULL)
/