create table GraphDataSeries (
	GraphDataSeriesID	INTEGER	PRIMARY KEY,
	GraphDefinitionID	INTEGER	REFERENCES GraphDefinition NOT NULL,
	PointID			INTEGER REFERENCES Point NOT NULL )
/