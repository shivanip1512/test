!include ..\common\global.inc
!include ..\common\rwglobal.inc


ALL:
            @$(MAKE) -nologo -f makepch.mak
            @$(MAKE) -nologo -f makedll.mak
            @$(MAKE) -nologo -f makeexe.mak

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


copy:
            @$(MAKE) -nologo -f makepch.mak $@
            @$(MAKE) -nologo -f makedll.mak $@
            @$(MAKE) -nologo -f makeexe.mak $@




