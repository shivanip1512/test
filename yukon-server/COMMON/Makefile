include global.inc
include rwglobal.inc

all:
         @$(MAKE) -nologo -f makepch.mak	
         @$(MAKE) -nologo -f makebase.mak
         @$(MAKE) -nologo -f makecmdline.mak


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
        @$(MAKE) -nologo -f makebase.mak $@
        @$(MAKE) -nologo -f makecmdline.mak $@


deps:
        @$(MAKE) -nologo -f makebase.mak $@
        @$(MAKE) -nologo -f makecmdline.mak $@

