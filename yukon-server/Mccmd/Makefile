!include ..\common\global.inc
!include ..\common\rwglobal.inc


all:
        @$(MAKE) -nologo -f makepch.mak
        @$(MAKE) -nologo -f makedll.mak

copy:
        @$(MAKE) -nologo -f makepch.mak $@
        @$(MAKE) -nologo -f makedll.mak $@

deps:
        @$(MAKE) -nologo -f makedll.mak $@

clean:
        -del \
*.pdb \
*.obj \
*.idb \
$(OBJ)\*.obj \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.map \
$(BIN)\*.manifest \
$(BIN)\*.exe

