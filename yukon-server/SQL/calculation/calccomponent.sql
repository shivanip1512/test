create table CalcComponent (
		PointID			INTEGER	REFERENCES Point NOT NULL,
		ComponentOrder		INTEGER NOT NULL,
		ComponentType		VARCHAR2(10) NOT NULL,
		ComponentPointID	INTEGER NOT NULL,
		Operation		VARCHAR2(10),
		Constant		FLOAT NOT NULL,
		FunctionName		VARCHAR2(20) )
/
