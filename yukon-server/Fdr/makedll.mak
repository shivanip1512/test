# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(PROCLOG)\include \
-I$(RW) \
-I$(BOOST) \
-I$(FDR)\Telegyr\inc \
-I$(FDR)\OSIPI\inc \
-I$(FDR)\LiveData


.PATH.cpp = .;$(R_FDR)

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
;$(PROT)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(TCLINC) \
;$(RW)


FDRINTERFACES=\

FDRINTERFACEOBJS=\
fdrdestination.obj  \
fdrlodestarinfo.obj  \
fdrpoint.obj \
mgr_fdrpoint.obj \
fdrinterface.obj \
fdrlodestarimport.obj \
fdrsocketlayer.obj \
fdrsocketconnection.obj \
fdrserverconnection.obj \
fdrclientconnection.obj \
fdrftpinterface.obj \
fdrpointlist.obj \
fdrsocketinterface.obj \
fdrasciiimportbase.obj \
textfileinterfaceparts.obj \
fdrtextfilebase.obj \
fdrsimplebase.obj \
fdrsinglesocket.obj \
fdrsocketserver.obj \
fdrscadaserver.obj \
fdrclientserverconnection.obj \
fdrscadahelper.obj


FDRTELEGYROBJS=\
telegyrgroup.obj \
telegyrcontrolcenter.obj \
fdrtelegyr.obj

FDRPIOBJS=\
fdrpibase.obj \
fdrpipoll.obj \
fdrpinotify.obj

# socketinterface.obj \
#fdrpointidmap.obj \
#fdrprotectedmaplist.obj \

CTIFDRLIBS=\
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\proclog.lib \
$(COMPILEBASE)\lib\cmdline.lib \
wininet.lib

TESTINTERFACEDLLOBJS=\
testinterface.obj \

FDRTELEGYRLIBS=\
$(COMPILEBASE)\lib\apiclilib.lib \
$(COMPILEBASE)\lib\pllib.lib \
$(COMPILEBASE)\lib\psapi.lib

FDRPILIBS=\
$(COMPILEBASE)\Fdr\OSIPI\lib\piapi32.lib \
$(COMPILEBASE)\Fdr\OSIPI\lib\pilog32.lib


CTIFDRDLLS=\
cti_fdr.dll \
fdrbepc.dll \
fdrtextexport.dll \
fdrtextimport.dll \
fdrdsm2import.dll \
fdrdsm2filein.dll \
fdrrdex.dll \
fdrcygnet.dll \
fdracs.dll \
fdracsmulti.dll \
fdrvalmet.dll \
fdrinet.dll \
fdrstec.dll \
fdrtristate.dll \
fdrrccs.dll \
fdrlodestarimport_enh.dll \
fdrlodestarimport_std.dll \
fdrtelegyr.dll \
fdrpi.dll \
fdrxa21lm.dll \
fdrlivedata.dll \
fdrwabash.dll \
fdrtristatesub.dll 

#  commented out because of bigtime Microsoft compiler problems.  i'm compiling
#    all of the code into one .obj, because it can't handle the code being contained
#    in more than one file.  #include "fdrinterface.cpp" is ugly, but the only way
#    around it for now.
# $(FDRINTERFACEOBJS) \

dirs:
                @if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                @if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)


all:            dirs $(CTIFDRDLLS)

cti_fdr.dll: $(FDRINTERFACEOBJS)
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(FDRINTERFACEOBJS) $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtextexport.dll: fdrtextexport.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtextexport.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtextimport.dll: fdrtextimport.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtextimport.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrdsm2import.dll: fdrdsm2import.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrdsm2import.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrdsm2filein.dll: fdrdsm2filein.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrdsm2filein.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\cparms.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrrdex.dll: fdrrdex.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrrdex.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrcygnet.dll: fdrcygnet.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                @if exist ..\cygnet\dclnd.lib copy ..\cygnet\dclnd.lib $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrcygnet.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\dclnd.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

.obj.dll :
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(<F) $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrvalmet.dll: fdrvalmet.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrvalmet.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrinet.dll: fdrinet.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrinet.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrstec.dll: fdrstec.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrstec.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtristate.dll: fdrtristate.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtristate.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrrccs.dll: fdrrccs.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrrccs.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\fdrinet.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlivedata.dll: fdrlivedata.obj livedata_rtp_api.obj RTP_API.obj livedatatypes.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrlivedata.obj livedata_rtp_api.obj RTP_API.obj livedatatypes.obj $(INCLPATHS) ws2_32.lib $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlodestarimport_enh.dll: fdrlodestarimport_enh.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrlodestarimport_enh.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlodestarimport_std.dll: fdrlodestarimport_std.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrlodestarimport_std.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)


fdrtelegyr.dll: $(FDRTELEGYROBJS) Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                @if exist ..\telegyr\lib\apiclilib.lib copy ..\telegyr\lib\apiclilib.lib $(COMPILEBASE)\lib
                @if exist ..\telegyr\lib\pllib.lib copy ..\telegyr\lib\pllib.lib $(COMPILEBASE)\lib
                @if exist ..\telegyr\lib\psapi.lib copy ..\telegyr\lib\psapi.lib $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) $(FDRTELEGYROBJS) $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(FDRTELEGYRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrpi.dll: $(FDRPIOBJS) Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(FDRPIOBJS) $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(FDRPILIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrxa21lm.dll: fdrxa21lm.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrxa21lm.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrbepc.dll: fdrbepc.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrbepc.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrwabash.dll: fdrwabash.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrwabash.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtristatesub.dll: fdrtristatesub.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtristatesub.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

apiclilib.lib:
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist telegyr\lib\apiclilib.lib copy telegyr\lib\apiclilib.lib $(COMPILEBASE)\lib
                -if exist ..\telegyr\lib\pllib.lib copy ..\telegyr\lib\pllib.lib $(COMPILEBASE)\lib
                -if exist ..\telegyr\lib\psapi.lib copy ..\telegyr\lib\psapi.lib $(COMPILEBASE)\lib


copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\*.lib copy bin\*.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp *.map


deps:
                scandeps -Output makedll.mak *.cpp



#.cpp.obj :
#                @echo:
#                @echo Compiling: $< Output: ..\$@
#                @echo:
#                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRBASE -D_DLL_ENH_FDRLODESTARIMPORT -D_DLL_STD_FDRLODESTARIMPORT -D_DLL_FDRLODESTARIMPORT -D_DLL_FDRCYGNET -D_DLL_FDRACS -D_DLL_FDRVALMET -D_DLL_FDRINET -D_DLL_FDRRCCS -D_DLL_FDRTRISTATE -D_DLL_FDRSTEC -D_DLL_FDRRDEX -D_DLL_FDRDSM2IMPORT -D_DLL_FDRTELEGYRAPI -D_DLL_FDRTEXTIMPORT -D_DLL_FDRTEXTEXPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrtextexport.obj : fdrtextexport.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRTEXTEXPORT -DWINDOWS -Fo$(OBJ)\ -c $<

fdrtextimport.obj : fdrtextimport.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRTEXTIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrdsm2import.obj : fdrdsm2import.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRDSM2IMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrdsm2filein.obj : fdrdsm2filein.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRDSM2FILEIN -DWINDOWS -Fo$(OBJ)\ -c $<
fdrrdex.obj : fdrrdex.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRRDEX -DWINDOWS -Fo$(OBJ)\ -c $<
fdrcygnet.obj : fdrcygnet.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRCYGNET -DWINDOWS -Fo$(OBJ)\ -c $<
fdracs.obj : fdracs.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRACS -DWINDOWS -Fo$(OBJ)\ -c $<
fdracsmulti.obj : fdracsmulti.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRACSMULTI -DWINDOWS -Fo$(OBJ)\ -c $<
fdrvalmet.obj : fdrvalmet.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRVALMET -DWINDOWS -Fo$(OBJ)\ -c $<
fdrinet.obj : fdrinet.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRINET -DWINDOWS -Fo$(OBJ)\ -c $<
fdrstec.obj : fdrstec.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRSTEC -DWINDOWS -Fo$(OBJ)\ -c $<
fdrtristate.obj : fdrtristate.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRTRISTATE -DWINDOWS -Fo$(OBJ)\ -c $<
fdrrccs.obj : fdrrccs.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRRCCS -DWINDOWS -Fo$(OBJ)\ -c $<
fdrlodestarimport_enh.obj : fdrlodestarimport_enh.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_ENH_FDRLODESTARIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrlodestarimport_std.obj : fdrlodestarimport_std.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_STD_FDRLODESTARIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<

telegyrgroup.obj : telegyrgroup.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRTELEGYRAPI -DWINDOWS -Fo$(OBJ)\ -c $<

telegyrcontrolcenter.obj : telegyrcontrolcenter.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRTELEGYRAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrtelegyr.obj : fdrtelegyr.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRTELEGYRAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrpibase.obj : fdrpibase.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRPIBASEAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrpipoll.obj : fdrpipoll.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRPIBASEAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrpinotify.obj : fdrpinotify.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRPIBASEAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrlivedata.obj : fdrlivedata.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRLIVEDATAAPI -DWINDOWS -Fo$(OBJ)\  -c $<

fdrxa21lm.obj : fdrxa21lm.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRXA21LM -DWINDOWS -Fo$(OBJ)\ -c $<

fdrbepc.obj : fdrbepc.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRBEPC -DWINDOWS -Fo$(OBJ)\ -c $<

.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRBASE -DWINDOWS -Fo$(OBJ)\ -c $<

RTP_API.obj : RTP_API.C
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS)  -D_DLL_FDRBASE -DWINDOWS -Fo$(OBJ)\ -c $<




#UPDATE#
fdr.obj:	fdrservice.h id_fdr.h
fdracs.obj:	fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrsinglesocket.h fdrsocketlayer.h \
		fdrsocketconnection.h fdracs.h
fdracsmulti.obj:	fdrpointlist.h mgr_fdrpoint.h fdrpoint.h \
		fdrdestination.h fdr.h fdrsocketinterface.h fdrinterface.h \
		fdrdebuglevel.h fdrscadahelper.h fdracsmulti.h \
		fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h
fdrasciiimportbase.obj:	fdrasciiimportbase.h fdrinterface.h \
		mgr_fdrpoint.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrbepc.obj:	fdrtextfilebase.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h fdrbepc.h
fdrclientconnection.obj:	fdrinterface.h mgr_fdrpoint.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrclientconnection.h
fdrclientserverconnection.obj:	fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h
fdrcygnet.obj:	fdrcygnet.h fdrinterface.h mgr_fdrpoint.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrdestination.obj:	fdrpoint.h fdrdestination.h fdr.h
fdrdsm2filein.obj:	fdrtextfilebase.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h fdrdsm2filein.h
fdrdsm2import.obj:	fdrdsm2import.h fdrasciiimportbase.h fdrinterface.h \
		mgr_fdrpoint.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrftpinterface.obj:	fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h
fdrinet.obj:	fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrserverconnection.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrclientconnection.h fdrinet.h
fdrinterface.obj:	fdrinterface.h mgr_fdrpoint.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrio.obj:	fdrio.h
fdriosocket.obj:	fdriosocket.h fdrio.h
fdrlivedata.obj:	fdr.h fdrdebuglevel.h livedatatypes.h fdrlivedata.h \
		fdrinterface.h mgr_fdrpoint.h fdrpoint.h fdrdestination.h \
		fdrpointlist.h fdrsimplebase.h fdrasciiimportbase.h \
		livedata_rtp_api.h RTP_APIW.H RTP.H
fdrlodestarimport.obj:	fdrtextfilebase.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h
fdrlodestarimport_enh.obj:	fdrtextfilebase.h fdrinterface.h \
		mgr_fdrpoint.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h fdrlodestarimport_enh.h
fdrlodestarimport_std.obj:	fdrtextfilebase.h fdrinterface.h \
		mgr_fdrpoint.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h fdrlodestarimport_std.h
fdrlodestarinfo.obj:	fdrlodestarinfo.h fdr.h
fdrpibase.obj:	fdr.h fdrdebuglevel.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdrpointlist.h fdrpipoll.h \
		fdrpibase.h fdrsimplebase.h fdrasciiimportbase.h \
		fdrpinotify.h
fdrpinotify.obj:	fdrpinotify.h fdrpibase.h fdrinterface.h \
		mgr_fdrpoint.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h
fdrpipoll.obj:	fdrpipoll.h fdrpibase.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrsimplebase.h fdrasciiimportbase.h
fdrpoint.obj:	fdrpoint.h fdrdestination.h fdr.h
fdrpointidmap.obj:	fdrpointidmap.h fdr.h
fdrpointlist.obj:	fdrpoint.h fdrdestination.h fdr.h fdrpointlist.h \
		mgr_fdrpoint.h
fdrprotectedmaplist.obj:	fdrpoint.h fdrdestination.h fdr.h \
		fdrprotectedmaplist.h
fdrrccs.obj:	fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrserverconnection.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrclientconnection.h fdrinet.h \
		fdrrccs.h
fdrrdex.obj:	fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrsinglesocket.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrrdex.h
fdrscadahelper.obj:	fdrdebuglevel.h fdrscadahelper.h fdrdestination.h \
		fdr.h fdracsmulti.h fdrpointlist.h mgr_fdrpoint.h fdrpoint.h \
		fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h fdrinterface.h
fdrscadaserver.obj:	fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h
fdrserverconnection.obj:	fdrinterface.h mgr_fdrpoint.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h
fdrservice.obj:	fdrservice.h
fdrsimplebase.obj:	fdr.h fdrdebuglevel.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h
fdrsinglesocket.obj:	fdrsocketinterface.h fdrinterface.h \
		mgr_fdrpoint.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrsinglesocket.h
fdrsocketconnection.obj:	fdrsocketlayer.h fdrsocketconnection.h
fdrsocketinterface.obj:	fdrpointlist.h mgr_fdrpoint.h fdrpoint.h \
		fdrdestination.h fdr.h fdrsocketconnection.h \
		fdrsocketinterface.h fdrinterface.h fdrdebuglevel.h
fdrsocketlayer.obj:	fdrinterface.h mgr_fdrpoint.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrsocketinterface.h fdrclientconnection.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h
fdrsocketserver.obj:	fdrsocketinterface.h fdrinterface.h \
		mgr_fdrpoint.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrsocketserver.h \
		fdrclientserverconnection.h
fdrstec.obj:	fdrstec.h fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h
fdrtelegyr.obj:	fdr.h fdrdebuglevel.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdrpointlist.h fdrtelegyr.h \
		fdrasciiimportbase.h telegyrgroup.h telegyrcontrolcenter.h
fdrtextexport.obj:	fdrtextfilebase.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h fdrtextexport.h
fdrtextfilebase.obj:	fdrtextfilebase.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h
fdrtextimport.obj:	fdrtextfilebase.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h fdrtextimport.h
fdrtristate.obj:	fdrtristate.h fdrftpinterface.h fdrinterface.h \
		mgr_fdrpoint.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrtristatesub.obj:	fdrTriStateSub.h fdrftpinterface.h fdrinterface.h \
		mgr_fdrpoint.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrvalmet.obj:	fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrsinglesocket.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrvalmet.h
fdrwabash.obj:	fdrwabash.h fdrinterface.h mgr_fdrpoint.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrxa21lm.obj:	fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrsinglesocket.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrxa21lm.h
livedatatypes.obj:	livedatatypes.h
livedata_rtp_api.obj:	livedata_rtp_api.h RTP_APIW.H RTP.H
mgr_fdrpoint.obj:	fdr.h fdrdebuglevel.h mgr_fdrpoint.h fdrpoint.h \
		fdrdestination.h
telegyrcontrolcenter.obj:	telegyrcontrolcenter.h fdr.h telegyrgroup.h \
		fdrpoint.h fdrdestination.h
telegyrgroup.obj:	telegyrgroup.h fdr.h fdrpoint.h fdrdestination.h
test_fdrtextimport.obj:	fdrtextimport.h fdrtextfilebase.h \
		fdrinterface.h mgr_fdrpoint.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h
test_fdrtristatesub.obj:	fdrTriStateSub.h fdrftpinterface.h \
		fdrinterface.h mgr_fdrpoint.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h
textfileinterfaceparts.obj:	TextFileInterfaceParts.h
#ENDUPDATE#
