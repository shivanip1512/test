CREATE TABLE UserWeb (
   UserID   INTEGER   REFERENCES UserInfo NOT NULL,
   HomeURL  VARCHAR2(200),
   Logo     VARCHAR2(40),
   Logins   INTEGER )
/
                        
