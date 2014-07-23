This directory contains changes that have been made to the
Yukon database over time.  The directories and files represent the
version number and the database type.  The format is:

   \oracle\2.41.sql
   or
   \sqlserver\2.41.sql


------------------------------------------------------------------------------------------
Directions
------------------------------------------------------------------------------------------
Inside each database specific directory (example: \oracle\ or \sqlserer) you will find
a set of files used to update the database.  Every .sql file will be named by the
version of the script. Any other files that do not have 
the extension .sql will be used to as tools to assist the .sql file.  An example
of this is the file:

   \oracle\RenCol.txt

If a .sql needs to use other files, it will be noted inside the .sql file.


------------------------------------------------------------------------------------------
Important Notes
------------------------------------------------------------------------------------------
If you do not know what scripts were ran last on your database, you can either
execute the following query:

	select * from ctidatabase;

to get the current version of the database and date of the last update. Or you
can open any .sql file in the most recent directory and note any new tables or changes 
to the data.  Then, look to see if those changes are in your database.  If they are,
you have an updated database and do not need to run any script files.  If not, you
need to repeat the above process with an older script directory.  You always want to
run the updates from oldest to newest.


------------------------------------------------------------------------------------------
Suggested DBUpdate Process Steps (Added: 8-10-2003)
------------------------------------------------------------------------------------------
1) Install any new JRE (optional)
2) Update DBMS version (example: oracle 8 to oracle 9) (optional)
3) Install new Yukon software (clients, server, etc)
4) Execute DBUpdate application (DBToolsFrame.bat file in \yukon\client\bin\ )


------------------------------------------------------------------------------------------
Automatic DBUpdate process  (DBupdate application added: 8-10-2003)
------------------------------------------------------------------------------------------
For 2.40 and above updates ONLY. 
The following minimum versions of software are needed:
 JRE 1.4.0
 Oracle 9.2
 SQLServer 7 (prefer 2000)

Here is what the DBUpdater tool does:
1) Searches the directory that you specify for all valid .sql files (ex: 2.41.sql | 2.40-2.1.2003.sql)
2) Reads in all the lines of the files found
    - only reads the .sql files that have a version number greater than the database version
3) Attempts to write the lines out to an intermediate file in the \Yukon\Client\Log\ directory 
    called X.XXvalids.sql where X.XX is the version (ex: 2.41valids.sql). If an intermediate
    file already exists, no file is written.
4) Commands in the intermediate files are then executed, commiting each successful command.
    - If all the commands in an entire file are executed successfully, 
    the file is written with embedded descriptive tags and renamed by adding the following text
    to the extension _MM-dd-yyyy_HH-mm-ss (ex: 2.40valids.sql_06-24-2003_10-40-08 )
    - If a command fails, the file is written with embedded descriptive tags and the
    name of the file is left unchanged.

Embedded descriptive tags:
All tags apply to the line below the tag.
@error [ignore | autofix | verbose] - what action to take in case of an error 
@success [true | false]  - did the line commit successfully to the DB
  
NOTES: 
-Try to fix any unexpected errors on your own or find someone else who
 can fix it. Do NOT ignore errors.
-Any changes made to fix script errors should be made in the generated
 intermediate file. 
-During an error, the application can be rerun and and it will pick up
 command execution on the last command that did not execute successfully (only commands that 
 do not have a @success true tag will run).  

Format of DBUpdate files:
1) All comments should begin and end on 1 line.
2) a ; (semi colon) should follow every command
3) File name must have the version number as the first 4 chars (ex: 2.40, 2.41)
4) All meta data tags (the @ sign) should be nested in a comment