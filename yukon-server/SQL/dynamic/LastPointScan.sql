CREATE TABLE LastPointScan (
                           PointID        INTEGER REFERENCES Point NOT NULL,
                           Value          FLOAT NOT NULL,
                           Time           DATE NOT NULL,
                           Quality        INTEGER NOT NULL,
                           AlarmState     INTEGER NOT NULL)
/


