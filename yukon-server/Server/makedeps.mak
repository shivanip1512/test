!include ..\common\global.inc
!include ..\common\rwglobal.inc

all:
        scandeps -DirAbs -Output makemgr.mak *.cpp
        scandeps -DirAbs -Output makemsg.mak *.cpp
        scandeps -DirAbs -Output makeque.mak *.cpp
        scandeps -DirAbs -Output makesvr.mak *.cpp

deps:
        scandeps -DirAbs -Output makemgr.mak *.cpp


