create table Display2WayData (
                DisplayNum      INTEGER REFERENCES Display(DisplayNum) NOT NULL,
                PointID         INTEGER NOT NULL,
                Ordering        INTEGER NOT NULL )
/                
