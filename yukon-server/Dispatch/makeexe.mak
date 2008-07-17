include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(SERVICE)\include \
-I$(MSG)\include \
-I$(RW) \
-I$(BOOST) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
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
;$(SIGNAL)\include \
;$(RW) \
;$(BOOST)


TSTEST=\
tstest.obj

TESTOBJS=\
test.obj

TEST2OBJS=\
test2.obj

SIGTESTOBJS=\
sigtest.obj

PORTERPOKE=\
porterpoker.obj

CBCOBJS=\
cbctest.obj

SIGSRCTESTOBJS=\
sigsrctest.obj

CMDTESTOBJS=\
cmdtest.obj


PCMOBJS=\
pcmtest.obj

VGOBJS= \
dispmain.obj \
dispsvc.obj \
con_mgr_vg.obj \
vgexe_factory.obj \
vangogh.obj


WINLIBS=kernel32.lib user32.lib
SOCKSLIB=wsock32.lib

CTIPROGS=\
dispatch.exe \
cmdtest.exe \
cbctest.exe \
pcmtest.exe \
poker.exe \
sigsinktest.exe \
sigsrctest.exe \
vgsrctest.exe \
vgsinktest.exe

VGLIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cmdline.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\clrdump.lib \



TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\clrdump.lib \





ALL:            $(CTIPROGS)

dispatch.exe:   $(VGOBJS) makeexe.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(VGOBJS) id_vg.obj -link $(RWLIBS) $(BOOSTLIBS) $(VGLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


pcmtest.exe:    $(PCMOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PCMOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

vgsrctest.exe:  $(TESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(TESTOBJS) -link $(BOOSTLIBS) $(RWLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

vgsinktest.exe: $(TEST2OBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(TEST2OBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


sigsinktest.exe: $(SIGTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(SIGTESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

poker.exe: $(PORTERPOKE) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PORTERPOKE) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

cbctest.exe: $(CBCOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(CBCOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


sigsrctest.exe: $(SIGSRCTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(SIGSRCTESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

cmdtest.exe: $(CMDTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(CMDTESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

tstest.exe:     $(TSTEST) makeexe.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(TSTEST) tstest.obj -link $(RWLIBS) $(BOOSTLIBS) $(VGLIBS)
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

# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_vg.obj

id_vg.obj:    id_vg.cpp include\id_vg.h id_vinfo.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################


#UPDATE#
applist.obj:	applist.h
con_mgr_vg.obj:	con_mgr_vg.h vgexe_factory.h exe_ptchg.h
ctivangogh.obj:	ctivangogh.h con_mgr_vg.h vgexe_factory.h exe_ptchg.h \
		pendingopthread.h pendable.h pending_info.h signalmanager.h \
		tagmanager.h mgr_ptclients.h ptconnect.h dllvg.h
dispmain.obj:	dispsvc.h dllvg.h
dispsvc.obj:	dispsvc.h
dllvg.obj:	dllvg.h
exe_ptchg.obj:	con_mgr_vg.h vgexe_factory.h exe_ptchg.h ctivangogh.h \
		pendingopthread.h pendable.h pending_info.h signalmanager.h \
		tagmanager.h
exe_signal.obj:	con_mgr_vg.h vgexe_factory.h exe_ptchg.h ctivangogh.h \
		pendingopthread.h pendable.h pending_info.h signalmanager.h \
		tagmanager.h exe_signal.h
id_vg.obj:	id_vg.h
id_vgdll.obj:	id_vgdll.h
mgr_ptclients.obj:	dllvg.h mgr_ptclients.h ptconnect.h con_mgr_vg.h \
		vgexe_factory.h exe_ptchg.h
pcmtest.obj:	mgr_ptclients.h ptconnect.h
pendingopthread.obj:	dllvg.h mgr_ptclients.h ptconnect.h \
		pendingopthread.h pendable.h pending_info.h signalmanager.h
pending_info.obj:	pending_info.h
ptconnect.obj:	ptconnect.h
signalmanager.obj:	signalmanager.h
tagmanager.obj:	tagmanager.h
test_signalmanager.obj:	signalmanager.h
vangogh.obj:	ctivangogh.h con_mgr_vg.h vgexe_factory.h exe_ptchg.h \
		pendingopthread.h pendable.h pending_info.h signalmanager.h \
		tagmanager.h dllvg.h
vgexe_factory.obj:	vgexe_factory.h exe_ptchg.h
#ENDUPDATE#
