!include ..\common\global.inc
!include ..\common\rwglobal.inc

all:
        @$(MAKE) -nologo -f makedll.mak
        @$(MAKE) -nologo -f fdrdsm2import.mak
        @$(MAKE) -nologo -f fdrrdex.mak
        @$(MAKE) -nologo -f fdrcygnet.mak
        @$(MAKE) -nologo -f fdracs.mak
        @$(MAKE) -nologo -f fdrvalmet.mak
        @$(MAKE) -nologo -f fdrinet.mak
        @$(MAKE) -nologo -f fdrstec.mak
        @$(MAKE) -nologo -f fdrtristate.mak
        @$(MAKE) -nologo -f fdrrccs.mak
        @$(MAKE) -nologo -f makeexe.mak

copy:
        @$(MAKE) -nologo -f makedll.mak $@
        @$(MAKE) -nologo -f fdrdsm2import.mak
        @$(MAKE) -nologo -f fdrrdex.mak
        @$(MAKE) -nologo -f fdrrccs.mak
        @$(MAKE) -nologo -f fdrcygnet.mak $@
        @$(MAKE) -nologo -f fdracs.mak
        @$(MAKE) -nologo -f fdrvalmet.mak
        @$(MAKE) -nologo -f fdrinet.mak
        @$(MAKE) -nologo -f fdrstec.mak
        @$(MAKE) -nologo -f fdrtristate.mak
        @$(MAKE) -nologo -f makeexe.mak $@


deps:
        @$(MAKE) -nologo -f makedll.mak $@
        @$(MAKE) -nologo -f fdrdsm2import.mak
        @$(MAKE) -nologo -f fdrrdex.mak
        @$(MAKE) -nologo -f fdrrccs.mak
        @$(MAKE) -nologo -f fdrvalmet.mak $@
        @$(MAKE) -nologo -f fdrinet.mak $@
        @$(MAKE) -nologo -f fdrstec.mak $@
        @$(MAKE) -nologo -f fdrtristate.mak $@
        @$(MAKE) -nologo -f fdracs.mak $@
        @$(MAKE) -nologo -f fdrcygnet.mak $@
        @$(MAKE) -nologo -f makeexe.mak $@


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



allclean:       clean all
