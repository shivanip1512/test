!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc


ALL:
            @build -nologo -f $(PRECOMPILED)\makepch.mak
            @build -nologo -f makedll.mak
            @build -nologo -f makeexe.mak

clean:
        -del \
*.pdb \
*.obj \
*.idb \
$(OBJ)\*.obj \
$(OBJ)\*.res \
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
            @build -nologo -f $(PRECOMPILED)\makepch.mak $@
            @build -nologo -f makedll.mak $@
            @build -nologo -f makeexe.mak $@

deps:
