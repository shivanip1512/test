!include $(COMPILEBASE)\global.inc

all:
        @build -nologo -f $(PRECOMPILED)\makepch.mak
        @build -nologo -f makedll.mak
        @build -nologo -f maketest.mak

copy:
        @build -nologo -f $(PRECOMPILED)\makepch.mak $@
        @build -nologo -f makedll.mak $@
        @build -nologo -f maketest.mak $@

deps:
        @build -nologo -f makedll.mak $@
        @build -nologo -f maketest.mak $@

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
