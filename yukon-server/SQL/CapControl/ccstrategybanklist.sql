create table CCStrategyBankList (
		CapStrategyID		INTEGER	REFERENCES CapControlStrategy NOT NULL,
		DeviceID		INTEGER REFERENCES Device NOT NULL,
		ControlOrder		INTEGER NOT NULL )
/
