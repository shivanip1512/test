!include ..\common\global.inc
!include ..\common\rwglobal.inc


all:
        @build -nologo -f makepch.mak
        @build -nologo -f makeexe.mak
	@build -nologo -f maketest.mak

clean:
        -del \
*.pdb \
*.idb \
*.obj \
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


copy:
        @build -nologo -f makepch.mak $@
        @build -nologo -f makeexe.mak $@

deps:
        @build -nologo -f makeexe.mak $@
	@build -nologo -f maketest.mak $@

