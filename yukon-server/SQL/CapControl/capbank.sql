create table CapBank (
		DeviceID		INTEGER	REFERENCES Device NOT NULL,
		BankAddress		VARCHAR2(40) NOT NULL UNIQUE,
		OperationalState	VARCHAR2(8) NOT NULL,
		ControlPointID		INTEGER REFERENCES Point(PointID) NOT NULL,
		BankSize		FLOAT NOT NULL,
		ControlDeviceID		INTEGER REFERENCES Device(DeviceID) NOT NULL )
/
