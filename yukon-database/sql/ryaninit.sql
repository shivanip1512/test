create tablespace ryantest datafile 'ryantest1.ora' size 10M autoextend on;
create tablespace ryantemptest datafile 'ryantemptest1.ora' size 5M autoextend on;
create user ryantest identified by ryantest default tablespace ryantest temporary tablespace ryantest quota unlimited on ryantest quota unlimited on ryantemptest;
grant create session to ryantest;
grant create table to ryantest;
grant create view to ryantest;