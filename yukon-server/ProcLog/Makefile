include ..\common\global.inc
include ..\common\rwglobal.inc

# nmake file YUKON 1.0

all:
                @$(MAKE) -nologo -f makepch.mak
                @$(MAKE) -nologo -f makedll.mak
                @$(MAKE) -nologo -f makeexe.mak

copy:
                @$(MAKE) -nologo -f makepch.mak $@
                @$(MAKE) -nologo -f makedll.mak $@
                @$(MAKE) -nologo -f makeexe.mak $@

deps:
                @$(MAKE) -nologo -f makeexe.mak $@

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

allclean:   clean all


