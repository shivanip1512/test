include ..\common\global.inc
include ..\common\rwglobal.inc


all:
            @$(MAKE) -nologo -f makepch.mak
            @$(MAKE) -nologo -f makepntdb.mak
            @$(MAKE) -nologo -f makedevdb.mak
            @$(MAKE) -nologo -f makeprtdb.mak
            @$(MAKE) -nologo -f maketcpsup.mak
            @$(MAKE) -nologo -f makeholiday.mak
            @$(MAKE) -nologo -f makeseason.mak
            @$(MAKE) -nologo -f makeexe.mak

copy:
            @$(MAKE) -nologo -f makepch.mak $@
            @$(MAKE) -nologo -f makepntdb.mak $@
            @$(MAKE) -nologo -f makedevdb.mak $@
            @$(MAKE) -nologo -f makeprtdb.mak $@
            @$(MAKE) -nologo -f maketcpsup.mak $@
            @$(MAKE) -nologo -f makeholiday.mak
            @$(MAKE) -nologo -f makeseason.mak
            @$(MAKE) -nologo -f makeexe.mak $@

deps:
            @$(MAKE) -nologo -f makepntdb.mak $@
            @$(MAKE) -nologo -f makedevdb.mak $@
            @$(MAKE) -nologo -f makeprtdb.mak $@
            @$(MAKE) -nologo -f maketcpsup.mak $@
            @$(MAKE) -nologo -f makeholiday.mak
            @$(MAKE) -nologo -f makeseason.mak
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


