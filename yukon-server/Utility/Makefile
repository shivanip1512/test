include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROTOCOL)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)

BASEOBJS=cid.obj

all:    wpsccfgparse.exe cid.exe

cid.exe:  $(BASEOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ $(BASEOBJS) -link $(LIBS) $(RWLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT)\yukon md $(YUKONOUTPUT)\yukon
               -@copy ..\$@ $(YUKONOUTPUT)\yukon
                @echo Done building Target ..\$@
                @%cd $(CWD)


wpsccfgparse.exe:  wpsccfgparse.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ wpsccfgparse.obj
                @echo:
               -@if not exist $(YUKONOUTPUT)\yukon md $(YUKONOUTPUT)\yukon
               -@copy ..\$@ $(YUKONOUTPUT)\yukon
                @echo Done building Target ..\$@
                @%cd $(CWD)



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


.cpp.obj:
                @echo:
                @echo Compiling: $<
                @echo C-Options: $(CFLAGS)
                @echo Output   : $@
                @echo:
                $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<




