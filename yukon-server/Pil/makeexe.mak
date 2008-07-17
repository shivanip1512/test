include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PIL)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(BOOST) \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
;$(RTDB)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST) \
;$(RW)



PARSETESTOBJS=\
parsetest.obj

TESTOBJS=\
piltest.obj

PILOBJS= \
pilhost.obj


WINLIBS=kernel32.lib user32.lib
SOCKSLIB=wsock32.lib

CTIPROGS=\
pilhost.exe \
parsetest.exe \
piltest.exe

VGLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctipil.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\cticonfig.lib


TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib

PARSETESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib


ALL:            $(CTIPROGS)

pilhost.exe:    $(PILOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PILOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(VGLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
               @%cd $(CWD)


piltest.exe:    $(TESTOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(TESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

parsetest.exe:    $(PARSETESTOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PARSETESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(PARSETESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
                scandeps -Output makeexe.mak *.cpp


clean:
        -del *.obj
        -del *.pch
        -del *.pdb
        -del *.sdb
        -del *.adb
        -del *.ilk
        -del *.exe


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################



#UPDATE#
applist.obj:	applist.h
exe_pcreq.obj:	pil_conmgr.h pilserver.h exe_pcreq.h
pilhost.obj:	pilserver.h
pilserver.obj:	pil_conmgr.h pil_exefct.h pilserver.h pilglob.h
pil_conmgr.obj:	pil_conmgr.h pil_exefct.h
pil_exefct.obj:	pil_exefct.h exe_pcreq.h
#ENDUPDATE#
