create table UserProgram (
   UserID      INTEGER     REFERENCES UserInfo,
   ProgramID   INTEGER     REFERENCES Program,
   Attrib      INTEGER )
/
