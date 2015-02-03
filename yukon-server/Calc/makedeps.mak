!include $(COMPILEBASE)\global.inc


INCLUDE=%INCLUDE%;\$(INCPATHADDITIONS)

all:
        scandeps -DirAbs -Output makeexe.mak *.cpp
#        scandeps -DirAbs -Output makedll.mak *.cpp



