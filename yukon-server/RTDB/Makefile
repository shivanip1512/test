include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc


all:
            @build -nologo -f $(PRECOMPILED)\makepch.mak
            @build -nologo -f makepntdb.mak
            @build -nologo -f makedevdb.mak
            @build -nologo -f makeprtdb.mak
            @build -nologo -f makeholiday.mak
            @build -nologo -f makeseason.mak
	    @build -nologo -f maketest.mak
            @build -nologo -f makeexe.mak

copy:
            @build -nologo -f $(PRECOMPILED)\makepch.mak $@
            @build -nologo -f makepntdb.mak $@
            @build -nologo -f makedevdb.mak $@
            @build -nologo -f makeprtdb.mak $@
	    @build -nologo -f maketest.mak $@
            @build -nologo -f makeholiday.mak
            @build -nologo -f makeseason.mak
            @build -nologo -f makeexe.mak $@

deps:
            @build -nologo -f makepntdb.mak $@
            @build -nologo -f makedevdb.mak $@
            @build -nologo -f makeprtdb.mak $@
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
$(OBJ)\*.target \
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


