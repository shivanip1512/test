USE MASTER;
CREATE DATABASE tempDatabase;
USE tempDatabase;
CREATE LOGIN tempDatabase
WITH PASSWORD = 'tempDatabase', 
     CHECK_POLICY = OFF, 
     DEFAULT_DATABASE = tempDatabase;
CREATE USER tempDatabase FOR LOGIN tempDatabase;
GRANT SELECT, INSERT, DELETE, UPDATE TO tempDatabase; 
