create table GraphDefinition (
	GraphDefinitionID	INTEGER PRIMARY KEY,
	UserID			INTEGER REFERENCES UserInfo NOT NULL,
	Name			VARCHAR2(40) NOT NULL,
	StartDate		DATE,
	StopDate		DATE )
/
