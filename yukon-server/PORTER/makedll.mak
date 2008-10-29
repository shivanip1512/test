# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PORTER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(PROT)\include \
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
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST) \
;$(RW)


DLLOBJS=\
port_shr.obj \
port_shr_ip.obj \
portglob.obj \
dllmain.obj



ALL:            portglob.dll


portglob.dll:  $(DLLOBJS) Makedll.mak
               @$(MAKE) -nologo -f $(_InputFile) id
               @echo Building  ..\$@
               @%cd $(OBJ)
               $(CC) $(RWCPPFLAGS) $(DLLFLAGS) $(DLLOBJS) id_pgdll.obj $(INCLPATHS) /Fe..\$@ -link $(LIBS) $(COMPILEBASE)\lib\tcpsup.lib $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\clrdump.lib $(COMPILEBASE)\lib\ctiprtdb.lib $(COMPILEBASE)\lib\ctidbsrc.lib $(COMPILEBASE)\lib\cticparms.lib $(RWLIBS) $(BOOSTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\portglob.dll copy bin\portglob.dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\portglob.lib copy bin\portglob.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

deps:
                scandeps -Output makedll.mak *.cpp

.cpp.obj :
                @echo:
                @echo Compiling: $< Output: $@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_PORTGLOB $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<


# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_pgdll.obj

id_pgdll.obj:    id_pgdll.cpp include\id_pgdll.h id_vinfo.h


#UPDATE#
disp_thd.obj:	portglob.h port_udp.h EncodingFilterFactory.h \
		EncodingFilter.h
dllmain.obj:	portglob.h
encodingfilterfactory.obj:	EncodingFilterFactory.h EncodingFilter.h \
		encryption_lantronix.h encryption_noop.h
encryption_lantronix.obj:	encryption_lantronix.h EncodingFilter.h
encryption_noop.obj:	encryption_noOp.h EncodingFilter.h
id_pgdll.obj:	id_pgdll.h
id_porter.obj:	id_porter.h
phlidlc.obj:	portdecl.h portglob.h
plidlc.obj:	portglob.h
portconf.obj:	portglob.h portdecl.h
portdialback.obj:	portglob.h
portentry.obj:	portdecl.h portglob.h
porter.obj:	portdecl.h portverify.h systemmsgthread.h portglob.h \
		port_shr.h port_shr_ip.h
portersu.obj:	portglob.h portdecl.h
portfield.obj:	portdecl.h tapterm.h portglob.h portverify.h port_udp.h \
		EncodingFilterFactory.h EncodingFilter.h
portfill.obj:	portdecl.h portglob.h
portglob.obj:	portglob.h
portgw.obj:	portdecl.h portglob.h
portload.obj:	portglob.h portdecl.h
portmain.obj:	portsvc.h portglob.h
portperf.obj:	portglob.h portdecl.h
portpil.obj:	portglob.h
portpool.obj:	portdecl.h portglob.h
portque.obj:	portglob.h portdecl.h port_shr.h
portsvc.obj:	portsvc.h portglob.h
porttime.obj:	portdecl.h portglob.h
portverify.obj:	portverify.h
port_shr.obj:	port_shr.h
port_shr_ip.obj:	port_shr_ip.h port_shr.h
port_udp.obj:	portglob.h port_udp.h EncodingFilterFactory.h \
		EncodingFilter.h portdecl.h
ripple.obj:	portdecl.h portglob.h
systemmsgthread.obj:	systemmsgthread.h portdecl.h
test_lantronixencryption.obj:	encryption_lantronix.h EncodingFilter.h
#ENDUPDATE#
