@echo off
if "%1" == "" goto usage

call setjavapath.bat
java -Djava.class.path=.;%YUKON_BASE%/server/web;classes12.jar;tools.jar;common.jar;jtds.jar;SqlServer.jar;j2ee.jar;log4j-1.2.4.jar;yukonappserver.jar; com.cannontech.tools.custom.RevisionHFinder %1 %2 %3 %4
goto done

:usage
echo:
echo Note: Only one of coll or logfile can(should) be used.  If both set, then logfile wins!
echo		-log:<the path and log filename to parse for "Revision H" text>
echo		-coll:<the collectionGroup to send "getconfig model" command to>
echo		-retry:<the number of times to retry missed meters, iff coll is used>
echo		-report:<the path and filename to write result data to>
echo:
echo Usage:	RevisionHFinder.bat [log:xxx.xxx OR coll:"xxx" [retry:#] ] report:xxx.xxx
echo:
echo Ex1:  RevisionHFinder.bat log:C:/yukon/server/log/macs01.log report:c:/yukon/server/export/revHdata.txt
echo		- parses log file for "Revision H" return messages, writes results to reportfile
echo:
echo Ex2:  RevisionHFinder.bat coll:"Cycle 1" retry:2 report:C:/yukon/server/export/revHData.txt
echo		- sends "getconfig model" command for all meters in Cycle 1, write results to reportfile
echo:
:done