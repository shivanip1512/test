create table CapControlStrategy (
		CapStrategyID			INTEGER	PRIMARY KEY,
		StrategyName			VARCHAR2(30) NOT NULL UNIQUE,
		DistrictName			VARCHAR2(30) NOT NULL,
		ActualVarPointID		INTEGER REFERENCES Point(PointID) NOT NULL,
		MaxDailyOperation		INTEGER NOT NULL,
		PeakSetPoint			FLOAT NOT NULL,
		OffPeakSetPoint			FLOAT NOT NULL,
		PeakStartTime			DATE NOT NULL,
		PeakStopTime			DATE NOT NULL,
		CalculatedVarLoadPointID	INTEGER REFERENCES Point(PointID) NOT NULL,
		Bandwidth			FLOAT NOT NULL,
		ControlInterval			INTEGER NOT NULL,
		MinResponseTime			INTEGER NOT NULL,
		MinConfirmPercent		INTEGER NOT NULL,
		FailurePercent			INTEGER NOT NULL,
		Status				VARCHAR2(12) NOT NULL,
		DaysOfWeek			CHAR(8) NOT NULL )
/
