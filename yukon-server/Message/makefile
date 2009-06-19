include ..\common\global.inc
include ..\common\rwglobal.inc

all:
         @build -nologo -f makepch.mak
         @build -nologo -f makedll.mak
         @build -nologo -f maketest.mak

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

copy:
        @build -nologo -f makepch.mak $@
        @build -nologo -f makedll.mak $@
        @build -nologo -f maketest.mak $@


deps:
        @build -nologo -f makedll.mak $@
        @build -nologo -f maketest.mak $@

