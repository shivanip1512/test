create table CalcBase (
		PointID		INTEGER	REFERENCES Point NOT NULL,
		UpdateType	VARCHAR2(10) NOT NULL,
		PeriodicRate	INTEGER NOT NULL );

create table CalcComponent (
		PointID			INTEGER	REFERENCES Point NOT NULL,
		ComponentOrder		INTEGER NOT NULL,
		ComponentType		VARCHAR2(10) NOT NULL,
		ComponentPointID	INTEGER NOT NULL,
		Operation		VARCHAR2(10),
		Constant		FLOAT NOT NULL,
		FunctionName		VARCHAR2(20) );

create table Logic (
		LogicID			INTEGER	PRIMARY KEY,
		LogicName		VARCHAR2(20) NOT NULL,
		PeriodicRate		INTEGER NOT NULL,
		StateFlag		VARCHAR2(10) NOT NULL,
		ScriptName		VARCHAR2(20) NOT NULL );
