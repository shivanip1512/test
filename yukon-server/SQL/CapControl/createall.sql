create table CapBank (
		DeviceID		INTEGER	REFERENCES Device NOT NULL,
		BankAddress		VARCHAR2(40) NOT NULL UNIQUE,
		OperationalState	VARCHAR2(8) NOT NULL,
		ControlPointID		INTEGER REFERENCES Point(PointID) NOT NULL,
		BankSize		FLOAT NOT NULL,
		ControlDeviceID		INTEGER REFERENCES Device(DeviceID) NOT NULL );

create table DeviceCBC (
		DeviceID		INTEGER	REFERENCES Device NOT NULL,
		SerialNumber		INTEGER NOT NULL,
		RouteID			INTEGER REFERENCES Route NOT NULL );

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
		Status				VARCHAR2(12) NOT NULL );

create table CCStrategyBankList (
		CapStrategyID		INTEGER	REFERENCES CapControlStrategy NOT NULL,
		DeviceID		INTEGER REFERENCES Device NOT NULL,
		ControlOrder		INTEGER NOT NULL );
