!include $(COMPILEBASE)\global.inc


ALL:
            @if not exist obj md obj
            @if not exist bin md bin
            @build -nologo -f $(PRECOMPILED)\makepch.mak
            @build -nologo -f makedll.mak

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

deps:

