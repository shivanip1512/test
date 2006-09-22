include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(BOOST) \
-I$(RW) \
-I$(COMMON)\include \



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
;$(BOOST) \
;$(RW)

BASEOBJS=cid.obj

all:            rid.exe wpsccfgparse.exe cid.exe nextrev.exe

cid.exe:  $(BASEOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ $(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOSTLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT)\yukon md $(YUKONOUTPUT)\yukon
               -@copy ..\$@ $(YUKONOUTPUT)\yukon
                @echo Done building Target ..\$@
                @%cd $(CWD)

rid.exe:        rid.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ rid.obj -link $(LIBS) $(RWLIBS) $(BOOSTLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT)\yukon md $(YUKONOUTPUT)\yukon
               -@copy ..\$@ $(YUKONOUTPUT)\yukon
                @echo Done building Target ..\$@
                @%cd $(CWD)


nextrev.exe:    nextrev.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ nextrev.obj -link $(LIBS) $(RWLIBS) $(BOOSTLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT)\yukon md $(YUKONOUTPUT)\yukon
               -@copy ..\$@ $(YUKONOUTPUT)\yukon
                @echo Done building Target ..\$@
                @%cd $(CWD)


wpsccfgparse.exe:  wpsccfgparse.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ wpsccfgparse.obj -link $(LIBS) $(RWLIBS)
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




