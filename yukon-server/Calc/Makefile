!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

all:
        @build -nologo -f $(PRECOMPILED)\makepch.mak
        @build -nologo -f makeexe.mak
	@build -nologo -f maketest.mak

copy:
        @build -nologo -f $(PRECOMPILED)\makepch.mak $@
        @build -nologo -f makeexe.mak $@


deps:
        @build -nologo -f makeexe.mak $@
	@build -nologo -f maketest.mak $@


clean:
        -del \
*.pdb \
*.idb \
*.obj \
$(OBJ)\*.obj \
$(OBJ)\*.target \
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

allclean:       clean all
