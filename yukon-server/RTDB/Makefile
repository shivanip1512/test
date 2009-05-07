include ..\common\global.inc
include ..\common\rwglobal.inc


all:
            @build -nologo -f makepch.mak
            @build -nologo -f makepntdb.mak
            @build -nologo -f makedevdb.mak
            @build -nologo -f makeprtdb.mak
            @build -nologo -f maketcpsup.mak
            @build -nologo -f makeholiday.mak
            @build -nologo -f makeseason.mak
	    @build -nologo -f maketest.mak
            @build -nologo -f makeexe.mak

copy:
            @build -nologo -f makepch.mak $@
            @build -nologo -f makepntdb.mak $@
            @build -nologo -f makedevdb.mak $@
            @build -nologo -f makeprtdb.mak $@
            @build -nologo -f maketcpsup.mak $@
	    @build -nologo -f maketest.mak $@
            @build -nologo -f makeholiday.mak
            @build -nologo -f makeseason.mak
            @build -nologo -f makeexe.mak $@

deps:
            @build -nologo -f makepntdb.mak $@
            @build -nologo -f makedevdb.mak $@
            @build -nologo -f makeprtdb.mak $@
            @build -nologo -f maketcpsup.mak $@
            @build -nologo -f makeholiday.mak $@
            @build -nologo -f makeseason.mak $@
	    @build -nologo -f maketest.mak $@
            @build -nologo -f makeexe.mak $@


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


