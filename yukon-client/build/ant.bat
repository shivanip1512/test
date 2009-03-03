@echo off
REM $Id: ant.bat,v 1.6 2007/02/21 20:13:45 softwarebuild Exp $
REM set ANT_HOME=ant
set ANT_OPTS=-Xmx768m
set ANT_HOME=%~dp0ant
"%ANT_HOME%"\bin\ant.bat %*
