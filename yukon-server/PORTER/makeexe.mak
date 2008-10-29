# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLUDE=%INCLUDE%;\$(INCPATHADDITIONS)

INCLPATHS+= \
-I$(PORTER)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(SCANNER)\include \
-I$(SERVICE)\include \
-I$(PIL)\include \
-I$(SERVER)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(PROT)\include \
-I$(BOOST) \
-I$(RW) \
-I$(XERCESINC) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(COMMON)\include\openssl \
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


BASEOBJS=\
disp_thd.obj \
phlidlc.obj \
plidlc.obj \
portconf.obj \
portdialback.obj \
portentry.obj \
porter.obj \
portersu.obj \
portfield.obj \
portfill.obj \
portgw.obj \
port_udp.obj \
portload.obj \
portmain.obj \
portperf.obj \
portpool.obj \
portpil.obj \
portque.obj \
portsvc.obj \
porttime.obj \
portverify.obj \
ripple.obj \
systemmsgthread.obj \
encryption_lantronix.obj \
EncodingFilterFactory.obj \
encryption_noOp.obj \

PORTERLIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\tcpsup.lib \
$(COMPILEBASE)\lib\portglob.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctipil.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctiprtdb.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\cticonfig.lib \
$(COMPILEBASE)\porter\lib\libeay32.lib \
$(COMPILEBASE)\porter\lib\ssleay32.lib \

EXECS=\
porter.exe \
traceset.exe \
contest.exe


ALL:            $(EXECS)
                -@if exist $(COMPILEBASE)\porter\lib\libeay32.dll copy $(COMPILEBASE)\porter\lib\libeay32.dll $(YUKONOUTPUT)
                -@if exist $(COMPILEBASE)\porter\lib\ssleay32.dll copy $(COMPILEBASE)\porter\lib\ssleay32.dll $(YUKONOUTPUT)

porter.exe:     $(BASEOBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(BASEOBJS) id_porter.obj $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ -link $(LIBS) $(RWLIBS) $(PORTERLIBS) $(BOOSTLIBS) $(LINKFLAGS)
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

traceset.exe:   traceset.obj
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
traceset.obj -link $(LIBS) $(COMPILEBASE)\lib\portglob.lib
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

contest.exe:    contest.obj
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
contest.obj -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) $(COMPILEBASE)\lib\ctibase.lib
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

copy:           $(EXECS)
               -if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


clean:
                -del *.obj
                -del *.dll
                -del *.ilk
                -del *.pdb
                -del *.lib
                -del *.exp

.cpp.obj:
                @echo:
                @echo Compiling: $<
                @echo C-Options: $(CFLAGS)
                @echo Output   : $@
                @echo:
                $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

deps:
                scandeps -Output makeexe.mak *.cpp



# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_porter.obj

id_porter.obj:    id_porter.cpp include\id_porter.h id_vinfo.h


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
