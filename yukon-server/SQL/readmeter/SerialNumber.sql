CREATE TABLE SerialNumber (
   Owner          INTEGER REFERENCES UserInfo NOT NULL,
   SerialNumber   VARCHAR2(15),
   Name           VARCHAR2(20) )
/

