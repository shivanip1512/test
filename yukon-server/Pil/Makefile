!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

all:
        @build -nologo -f $(PRECOMPILED)\makepch.mak
        @build -nologo -f makedll.mak

copy:
        @build -nologo -f $(PRECOMPILED)\makepch.mak $@
        @build -nologo -f makedll.mak $@

deps:
        @build -nologo -f makedll.mak $@

clean:
        -del \
*.pdb \
*.obj \
*.idb \
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
