This directory contains changes that have been made to the
Yukon database over time.  The directories and files represent the date the
script files were released, the version number and the database type.  The format is:

   \oracle\1.02-06.07.2001.sql
   or
   \sqlserver\1.02-06.07.2001.sql

------------------------------------------------------------------------------------------
DIRECTOINS
------------------------------------------------------------------------------------------
Inside each database specific directory (example: \oracle\ or \sqlserer) you will find
a set of files used to update the database.  Every .sql file will be named by the
date the script was created and version of the script. Any other files that do not have 
the extension .sql will be used to as tools to assist the .sql file.  An example
of this is the file:

   \oracle\RenCol.txt

If a .sql needs to use other files, it will be noted inside the .sql file.


------------------------------------------------------------------------------------------
IMPORTANT NOTES
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