@echo off
if "%1" == "" goto usage

java -Djava.class.path=.;%YUKON_BASE%/server/web;classes12.jar;tools.jar;common.jar;jtds.jar;SqlServer.jar;j2ee.jar;log4j-1.2.4.jar;yukonappserver.jar; com.cannontech.tools.custom.RevisionHFinder %1 %2 %3 %4
goto done

:usage
echo:
echo Only ONE of coll or logfile can(should) be used.  If both set, then logfile wins!
echo:
echo Usage:	RevisionHFinder.bat [logfile=xxx.xxx | coll="xxx" [retry=#] ] reportfile=xxx.xxx
echo:
echo Ex1:  RevisionHFinder.bat logfile=C:/yukon/server/log/macs01.log reportfile=c:/yukon/server/export/revHdata.txt
echo		- parses log file for "Revision H" return messages, writes results to reportfile
echo Ex2:  RevisionHFinder.bat coll="Cycle 1" retry=2 reportfile=C:/yukon/server/export/revHData.txt
echo		- sends "getvalue model" command for all meters in Cycle 1, write results to reportfile
echo:
:done