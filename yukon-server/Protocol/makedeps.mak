!include ..\common\global.inc
!include ..\common\rwglobal.inc


INCLUDE=%INCLUDE%;\$(INCPATHADDITIONS)

all:
        scandeps -DirAbs -Output makedll.mak *.cpp


