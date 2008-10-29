# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SCANNER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(PROCLOG)\include \
-I$(PROT)\include \
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
;$(BOOST) \
;$(RW)

DLLOBJS=\
scanglob.obj \


#scan_dlc.obj


ALL:            scansup.dll


scansup.dll:   $(DLLOBJS) Makedll.mak
               @$(MAKE) -nologo -f $(_InputFile) id
               @echo Building  ..\$@
               @%cd $(OBJ)
               $(CC) $(DLLFLAGS) $(DLLOBJS) id_sgdll.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\clrdump.lib /Fe..\$@
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
               @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\scansup.dll copy bin\scansup.dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\scansup.lib copy bin\scansup.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

deps:
                scandeps -Output makedll.mak *.cpp


.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_SCANSUP $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_sgdll.obj

id_sgdll.obj:    id_sgdll.cpp include\id_sgdll.h id_vinfo.h


#UPDATE#
id_scanner.obj:	id_scanner.h
id_sgdll.obj:	id_sgdll.h
mgr_device_scannable.obj:	mgr_device_scannable.h
scanglob.obj:	scanner.h scanglob.h
scanmain.obj:	scansvc.h
scanner.obj:	scanner.h scanglob.h scansup.h mgr_device_scannable.h
scansup.obj:	scanglob.h scansup.h
scansvc.obj:	scanglob.h scansvc.h
#ENDUPDATE#
