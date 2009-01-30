!include ..\common\global.inc
!include ..\common\rwglobal.inc

all:
        @$(MAKE) -nologo -f makepch.mak
        @$(MAKE) -nologo -f makedll.mak
        @$(MAKE) -nologo -f makeexe.mak

#        @$(MAKE) -nologo -f makescn.mak

copy:
        @$(MAKE) -nologo -f makepch.mak $@
        @$(MAKE) -nologo -f makedll.mak $@
        @$(MAKE) -nologo -f makeexe.mak $@

deps:
        @$(MAKE) -nologo -f makedll.mak $@
        @$(MAKE) -nologo -f makeexe.mak $@

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

allclean:   clean all


