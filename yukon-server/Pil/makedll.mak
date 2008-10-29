# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(PIL)\include \
-I$(PROT)\include \
-I$(SERVER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
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
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST) \
;$(RW)


DLLLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cmdline.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib \


DLLOBJS=\
exe_pcreq.obj \
pil_conmgr.obj \
pil_exefct.obj \
pilglob.obj \
pilserver.obj



ALL:            ctipil.dll

ctipil.dll:     $(DLLOBJS) makedll.mak
                @echo Building  $@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(DLLOBJS) $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(DLLLIBS) -Fe..\$@ $(LINKFLAGS)
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctipil.dll copy bin\ctipil.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctipil.lib copy bin\ctipil.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

deps:
                scandeps -Output makedll.mak *.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: $@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_CTIPIL $(INCLPATHS) \
-DWINDOWS -Fo$(OBJ)\ -c $<


#UPDATE#
applist.obj:	applist.h
exe_pcreq.obj:	pil_conmgr.h pilserver.h exe_pcreq.h
pilserver.obj:	pil_conmgr.h pil_exefct.h pilserver.h
pil_conmgr.obj:	pil_conmgr.h pil_exefct.h
pil_exefct.obj:	pil_exefct.h exe_pcreq.h
#ENDUPDATE#
