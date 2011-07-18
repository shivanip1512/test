!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc


all:
        @build -nologo -f $(PRECOMPILED)\makepch.mak
        @build -nologo -f makeexe.mak
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
        @build -nologo -f $(PRECOMPILED)\makepch.mak $@
        @build -nologo -f makeexe.mak $@
        @build -nologo -f maketest.mak $@

deps:
        @build -nologo -f makeexe.mak $@
        @build -nologo -f maketest.mak $@

