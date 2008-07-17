# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(SIGNAL)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(BOOST) \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
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
;$(SIGNAL)\include \
;$(BOOST) \
;$(RW)



CTIVGLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\cmdline.lib \
$(COMPILEBASE)\lib\ctidbres.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\service.lib


DLLOBJS=\
ctivangogh.obj \
con_mgr_vg.obj \
dllvg.obj \
exe_ptchg.obj \
pending_info.obj \
pendingopthread.obj \
ptconnect.obj \
mgr_ptclients.obj \
signalmanager.obj \
tagmanager.obj \
vgexe_factory.obj \



ALL:            ctivg.dll

ctivg.dll:      $(DLLOBJS) Makedll.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(DLLOBJS) id_vgdll.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIVGLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctivg.dll copy bin\ctivg.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctivg.lib copy bin\ctivg.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp


deps:
                scandeps -Output makedll.mak *.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /DCTIVANGOGH $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<


# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_vgdll.obj

id_vgdll.obj:    id_vgdll.cpp include\id_vgdll.h id_vinfo.h


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
