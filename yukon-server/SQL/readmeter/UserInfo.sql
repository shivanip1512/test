create table UserInfo (
	UserID		INTEGER PRIMARY KEY,
	Username	VARCHAR2(20) NOT NULL,
	Password	VARCHAR2(20) NOT NULL,
	DatabaseAlias   VARCHAR2(20) NOT NULL)
/