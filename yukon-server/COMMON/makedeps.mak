!include ..\common\global.inc
!include ..\common\rwglobal.inc


INCLUDE=%INCLUDE%;\$(INCPATHADDITIONS)

all:
        scandeps -Output makebase.mak *.cpp
#        scandeps -DirAbs -Output makecmdline.mak *.cpp


