create table DeviceCBC (
		DeviceID		INTEGER	REFERENCES Device NOT NULL,
		SerialNumber		INTEGER NOT NULL,
		RouteID			INTEGER REFERENCES Route NOT NULL )
/
