!include ..\common\global.inc
!include ..\common\rwglobal.inc


ALL:
            @if not exist obj md obj
            @if not exist bin md bin
            @build -nologo -f makepch.mak
            @build -nologo -f makedll.mak

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


copy:
            @build -nologo -f makepch.mak $@
            @build -nologo -f makedll.mak $@

deps:

