create table DisplayColumns (
          DisplayNum    INTEGER  REFERENCES Display(DisplayNum) NOT NULL,
          Title         VARCHAR2(50),
	  TypeNum	INTEGER NOT NULL REFERENCES ColumnType(TypeNum) NOT NULL,
          Ordering      INTEGER NOT NULL,
	  Width		INTEGER NOT NULL )
/                
