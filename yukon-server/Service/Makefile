!include ..\common\global.inc
!include ..\common\rwglobal.inc


all:
        @$(MAKE) -nologo -f makedll.mak

copy:
        @$(MAKE) -nologo -f makedll.mak $@

deps:
        @$(MAKE) -nologo -f makedll.mak $@

clean:
        -del \
*.pdb \
$(OBJ)\*.obj \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe


