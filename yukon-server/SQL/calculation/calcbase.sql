create table CalcBase (
		PointID		INTEGER	REFERENCES Point NOT NULL,
		UpdateType	VARCHAR2(16) NOT NULL,
		PeriodicRate	INTEGER NOT NULL )
/
