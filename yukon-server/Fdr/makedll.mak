# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(PROT)\include \
-I$(RTDB)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(FDR)\Telegyr\inc \
-I$(FDR)\OSIPI\inc \
-I$(FDR)\LiveData \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(RTDB)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(PROT)\include


FDRINTERFACES=\
fdrvalmetutil.obj

FDRINTERFACEOBJS=\
$(PRECOMPILED_OBJ) \
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
fdrscadahelper.obj \


FDRTELEGYROBJS=\
telegyrgroup.obj \
telegyrcontrolcenter.obj \
fdrtelegyr.obj

FDRPIOBJS=\
fdrpibase.obj \
fdrpipoll.obj \
fdrpinotify.obj


CTIFDRLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
wininet.lib

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
fdrvalmetmulti.dll \
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
fdrtristatesub.dll \
fdrdnpslave.dll

!IFDEF DEBUG
IGNORE_LIB = /nodefaultlib:msvcrt.lib
!ELSE
IGNORE_LIB = /nodefaultlib:msvcrtd.lib
!ENDIF


FDR_DLL_FULLBUILD = $[Filename,$(OBJ),FdrDllFullBuild,target]


PROGS_VERSION=\
$(CTIFDRDLLS)


dirs:
                @if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                @if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)


all:            dirs $(CTIFDRDLLS)


$(FDR_DLL_FULLBUILD) :
                @touch $@
                @echo Compiling cpp to obj
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRBASE -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(FDRINTERFACEOBJS)]


cti_fdr.dll: $(FDR_DLL_FULLBUILD) $(FDRINTERFACEOBJS) $(OBJ)\cti_fdr.res
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(FDRINTERFACEOBJS) $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) Ws2_32.lib /Fe..\$@ cti_fdr.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtextexport.dll: fdrtextexport.obj Makefile $(OBJ)\fdrtextexport.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtextexport.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrtextexport.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtextimport.dll: fdrtextimport.obj Makefile $(OBJ)\fdrtextimport.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtextimport.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrtextimport.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrdsm2import.dll: fdrdsm2import.obj Makefile $(OBJ)\fdrdsm2import.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrdsm2import.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrdsm2import.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrdsm2filein.dll: fdrdsm2filein.obj Makefile $(OBJ)\fdrdsm2filein.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrdsm2filein.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrdsm2filein.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrrdex.dll: fdrrdex.obj Makefile $(OBJ)\fdrrdex.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrrdex.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib Ws2_32.lib /Fe..\$@ fdrrdex.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrcygnet.dll: fdrcygnet.obj Makefile $(OBJ)\fdrcygnet.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                @if exist ..\cygnet\dclnd.lib copy ..\cygnet\dclnd.lib $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrcygnet.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\dclnd.lib /Fe..\$@ fdrcygnet.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdracs.dll:     fdracs.obj Makefile $(OBJ)\fdracs.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(<F) $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdracs.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdracsmulti.dll: fdracsmulti.obj Makefile $(OBJ)\fdracsmulti.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(<F) $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib Ws2_32.lib /Fe..\$@ fdracsmulti.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

.obj.dll :
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(<F) $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrvalmet.dll:  fdrvalmet.obj $(FDRINTERFACES)  Makefile $(OBJ)\fdrvalmet.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrvalmet.obj $(FDRINTERFACES) $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrvalmet.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrvalmetmulti.dll: fdrvalmetmulti.obj $(FDRINTERFACES)  Makefile $(OBJ)\fdrvalmetmulti.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrvalmetmulti.obj $(FDRINTERFACES) $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrvalmetmulti.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrinet.dll: fdrinet.obj Makefile $(OBJ)\fdrinet.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrinet.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib Ws2_32.lib /Fe..\$@ fdrinet.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrstec.dll: fdrstec.obj Makefile $(OBJ)\fdrstec.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrstec.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrstec.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtristate.dll: fdrtristate.obj Makefile $(OBJ)\fdrtristate.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtristate.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrtristate.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrrccs.dll: fdrrccs.obj Makefile $(OBJ)\fdrrccs.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrrccs.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\fdrinet.lib Ws2_32.lib /Fe..\$@ fdrrccs.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlivedata.dll: fdrlivedata.obj livedata_rtp_api.obj RTP_API.obj livedatatypes.obj Makefile $(OBJ)\fdrlivedata.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrlivedata.obj livedata_rtp_api.obj RTP_API.obj livedatatypes.obj $(INCLPATHS) ws2_32.lib $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrlivedata.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlodestarimport_enh.dll: fdrlodestarimport_enh.obj Makefile $(OBJ)\fdrlodestarimport_enh.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrlodestarimport_enh.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrlodestarimport_enh.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlodestarimport_std.dll: fdrlodestarimport_std.obj Makefile $(OBJ)\fdrlodestarimport_std.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrlodestarimport_std.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrlodestarimport_std.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)


fdrtelegyr.dll: $(FDRTELEGYROBJS) Makefile $(OBJ)\fdrtelegyr.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                @if exist ..\telegyr\lib\apiclilib.lib copy ..\telegyr\lib\apiclilib.lib $(COMPILEBASE)\lib
                @if exist ..\telegyr\lib\pllib.lib copy ..\telegyr\lib\pllib.lib $(COMPILEBASE)\lib
                @if exist ..\telegyr\lib\psapi.lib copy ..\telegyr\lib\psapi.lib $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) $(FDRTELEGYROBJS) $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(FDRTELEGYRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib Ws2_32.lib /Fe..\$@ /link $(IGNORE_LIB) fdrtelegyr.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrpi.dll: $(FDRPIOBJS) Makefile $(OBJ)\fdrpi.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(FDRPIOBJS) $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(FDRPILIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrpi.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrxa21lm.dll: fdrxa21lm.obj Makefile $(OBJ)\fdrxa21lm.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrxa21lm.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib Ws2_32.lib /Fe..\$@ fdrxa21lm.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrbepc.dll: fdrbepc.obj Makefile $(OBJ)\fdrbepc.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrbepc.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrbepc.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrwabash.dll: fdrwabash.obj Makefile $(OBJ)\fdrwabash.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrwabash.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrwabash.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtristatesub.dll: fdrtristatesub.obj Makefile $(OBJ)\fdrtristatesub.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtristatesub.obj $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrtristatesub.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrdnpslave.dll: fdrdnpslave.obj $(PRECOMPILED_OBJ) Makefile $(OBJ)\fdrdnpslave.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrdnpslave.obj $(PRECOMPILED_OBJ) $(INCLPATHS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\ctidevdb.lib $(COMPILEBASE)\lib\cti_fdr.lib Ws2_32.lib /Fe..\$@ fdrdnpslave.res
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
#                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRBASE -D_DLL_ENH_FDRLODESTARIMPORT -D_DLL_STD_FDRLODESTARIMPORT -D_DLL_FDRLODESTARIMPORT -D_DLL_FDRCYGNET -D_DLL_FDRACS -D_DLL_FDRVALMET -D_DLL_FDRINET -D_DLL_FDRRCCS -D_DLL_FDRTRISTATE -D_DLL_FDRSTEC -D_DLL_FDRRDEX -D_DLL_FDRDSM2IMPORT -D_DLL_FDRTELEGYRAPI -D_DLL_FDRTEXTIMPORT -D_DLL_FDRTEXTEXPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrtextexport.obj : fdrtextexport.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTEXTEXPORT -DWINDOWS -Fo$(OBJ)\ -c $<

fdrtextimport.obj : fdrtextimport.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTEXTIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrdsm2import.obj : fdrdsm2import.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRDSM2IMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrdsm2filein.obj : fdrdsm2filein.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRDSM2FILEIN -DWINDOWS -Fo$(OBJ)\ -c $<
fdrrdex.obj : fdrrdex.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRRDEX -DWINDOWS -Fo$(OBJ)\ -c $<
fdrcygnet.obj : fdrcygnet.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRCYGNET -DWINDOWS -Fo$(OBJ)\ -c $<
fdracs.obj : fdracs.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRACS -DWINDOWS -Fo$(OBJ)\ -c $<
fdracsmulti.obj : fdracsmulti.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRACSMULTI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrvalmetutil.obj : fdrvalmetutil.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
fdrvalmet.obj : fdrvalmet.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRVALMET -DWINDOWS -Fo$(OBJ)\ -c $<
fdrvalmetmulti.obj : fdrvalmetmulti.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRVALMETMULTI -DWINDOWS -Fo$(OBJ)\ -c $<
fdrinet.obj : fdrinet.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRINET -DWINDOWS -Fo$(OBJ)\ -c $<
fdrstec.obj : fdrstec.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRSTEC -DWINDOWS -Fo$(OBJ)\ -c $<
fdrtristate.obj : fdrtristate.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTRISTATE -DWINDOWS -Fo$(OBJ)\ -c $<
fdrrccs.obj : fdrrccs.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRRCCS -DWINDOWS -Fo$(OBJ)\ -c $<
fdrlodestarimport_enh.obj : fdrlodestarimport_enh.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_ENH_FDRLODESTARIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrlodestarimport_std.obj : fdrlodestarimport_std.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_STD_FDRLODESTARIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<

telegyrgroup.obj : telegyrgroup.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTELEGYRAPI -DWINDOWS -Fo$(OBJ)\ -c $<

telegyrcontrolcenter.obj : telegyrcontrolcenter.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTELEGYRAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrtelegyr.obj : fdrtelegyr.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTELEGYRAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrpibase.obj : fdrpibase.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRPIBASEAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrpipoll.obj : fdrpipoll.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRPIBASEAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrpinotify.obj : fdrpinotify.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRPIBASEAPI -DWINDOWS -Fo$(OBJ)\ -c $<

fdrlivedata.obj : fdrlivedata.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRLIVEDATAAPI -DWINDOWS -Fo$(OBJ)\  -c $<

fdrdnpslave.obj : fdrdnpslave.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRDNPSLAVE -Fo$(OBJ)\  -c $<

fdrxa21lm.obj : fdrxa21lm.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRXA21LM -DWINDOWS -Fo$(OBJ)\ -c $<

fdrbepc.obj : fdrbepc.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRBEPC -DWINDOWS -Fo$(OBJ)\ -c $<

.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRBASE -DWINDOWS -Fo$(OBJ)\ -c $<

RTP_API.obj : RTP_API.C
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS)  -D_DLL_FDRBASE -DWINDOWS -Fo$(OBJ)\ -c $<




#UPDATE#
fdr.obj:	precompiled.h ctitime.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logManager.h module_util.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h guard.h cparms.h configkey.h \
		configval.h CServiceConfig.h fdrservice.h cservice.h id_fdr.h \
		connection_base.h
fdracs.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		socket_helper.h win_helper.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdracs.h
fdracsmulti.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h cparms.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h mutex.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h queue.h \
		connection_base.h worker_thread.h timing_util.h \
		fdrdebuglevel.h socket_helper.h win_helper.h fdrscadahelper.h \
		fdracsmulti.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h std_helper.h
fdrasciiimportbase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h loggable.h dllbase.h os2_2w32.h \
		cticalls.h critical_section.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h guard.h fdrasciiimportbase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h
fdrbepc.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		configkey.h configval.h msg_cmd.h message.h ctidbgmem.h \
		collectable.h loggable.h pointtypes.h numstr.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h guard.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrbepc.h ctitokenizer.h
fdrclientconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h guard.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h dllbase.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrsocketlayer.h fdrsocketconnection.h \
		socket_helper.h win_helper.h fdrclientconnection.h
fdrclientserverconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h guard.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h dllbase.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h \
		socket_helper.h win_helper.h prot_dnp.h prot_base.h xfer.h \
		packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h
fdrcygnet.obj:	precompiled.h ctitime.h dlldefs.h cparms.h configkey.h \
		configval.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		loggable.h msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		dllbase.h os2_2w32.h cticalls.h critical_section.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h guard.h fdrcygnet.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h
fdrdestination.obj:	precompiled.h fdrpoint.h dlldefs.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h loggable.h types.h \
		ctitime.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h dllbase.h guard.h
fdrdnpslave.obj:	precompiled.h fdrdnpslave.h dlldefs.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h boostutil.h \
		utility.h ctitime.h numstr.h dllbase.h critical_section.h \
		readers_writer_lock.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h mutex.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h \
		worker_thread.h timing_util.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_base.h \
		fdrdebuglevel.h msg_cmd.h socket_helper.h win_helper.h \
		dnp_object_analoginput.h dnp_objects.h prot_base.h xfer.h \
		dnp_object_time.h prot_dnpSlave.h dnp_application.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h prot_dnp.h \
		packet_finder.h dnp_object_analogoutput.h amq_constants.h \
		resolvers.h db_entry_defines.h slctdev.h std_helper.h
fdrdsm2filein.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h loggable.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h guard.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrdsm2filein.h ctitokenizer.h
fdrdsm2import.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h loggable.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrdsm2import.h fdrasciiimportbase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h
fdrftpinterface.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h loggable.h dllbase.h os2_2w32.h \
		cticalls.h critical_section.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h guard.h fdrftpinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h
fdrinet.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h cparms.h configkey.h configval.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h dllbase.h critical_section.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		guard.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		socket_helper.h win_helper.h fdrserverconnection.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrclientconnection.h \
		dsm2.h streamConnection.h netports.h immutable.h dsm2err.h \
		words.h optional.h macro_offset.h fdrinet.h
fdrinterface.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		cparms.h configkey.h configval.h dbaccess.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h loggable.h msg_ptreg.h msg_cmd.h \
		msg_dbchg.h msg_signal.h fdrinterface.h connection_client.h \
		connection.h msg_reg.h mutex.h queue.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h readers_writer_lock.h \
		guard.h connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h database_reader.h fdrdebuglevel.h \
		fdrpointlist.h amq_constants.h
fdrlivedata.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h dbaccess.h resolvers.h db_entry_defines.h \
		fdr.h fdrdebuglevel.h livedatatypes.h fdrlivedata.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h database_connection.h \
		database_reader.h row_reader.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h livedata_rtp_api.h RTP_APIW.H RTP.H
fdrlodestarimport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h loggable.h msg_signal.h yukon.h \
		types.h pointtypes.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h
fdrlodestarimport_enh.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h loggable.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h guard.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h fdrlodestarimport_enh.h
fdrlodestarimport_std.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h loggable.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h guard.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h fdrlodestarimport_std.h
fdrlodestarinfo.obj:	precompiled.h fdrlodestarinfo.h dlldefs.h fdr.h \
		pointdefs.h
fdrpibase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h dbaccess.h resolvers.h db_entry_defines.h \
		fdr.h fdrdebuglevel.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h database_connection.h database_reader.h \
		row_reader.h fdrpointlist.h fdrpipoll.h fdrpibase.h \
		fdrsimplebase.h fdrasciiimportbase.h fdrpinotify.h
fdrpinotify.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		fdrpinotify.h fdrpibase.h fdrinterface.h message.h \
		ctidbgmem.h collectable.h loggable.h msg_dbchg.h yukon.h \
		types.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h configkey.h configval.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h dllbase.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrsimplebase.h fdrasciiimportbase.h
fdrpipoll.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h fdrpipoll.h fdrpibase.h fdrinterface.h \
		message.h collectable.h loggable.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h configkey.h configval.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h dllbase.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrsimplebase.h fdrasciiimportbase.h
fdrpoint.obj:	precompiled.h ctitime.h dlldefs.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h loggable.h \
		types.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h
fdrpointidmap.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h fdrpointidmap.h ctitime.h pointtypes.h \
		fdr.h pointdefs.h resolvers.h db_entry_defines.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h guard.h
fdrpointlist.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h constants.h numstr.h \
		guard.h fdrpoint.h pointtypes.h fdrdestination.h fdr.h \
		pointdefs.h fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h mutex.h
fdrrccs.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h cparms.h configkey.h configval.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h dllbase.h critical_section.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		guard.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		socket_helper.h win_helper.h fdrserverconnection.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrclientconnection.h \
		fdrinet.h dsm2.h streamConnection.h netports.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h fdrrccs.h
fdrrdex.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		socket_helper.h win_helper.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h \
		fdrrdex.h
fdrscadahelper.obj:	precompiled.h fdrscadahelper.h dlldefs.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h loggable.h \
		ctitime.h msg_cmd.h message.h ctidbgmem.h collectable.h \
		fdrdebuglevel.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h os2_2w32.h constants.h numstr.h \
		critical_section.h fdracsmulti.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h dllbase.h readers_writer_lock.h \
		guard.h fdrpoint.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h mutex.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h worker_thread.h timing_util.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_base.h \
		socket_helper.h win_helper.h fdrdnpslave.h \
		dnp_object_analoginput.h dnp_objects.h prot_base.h xfer.h \
		dnp_object_time.h prot_dnpSlave.h dnp_application.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		fdrvalmetmulti.h
fdrscadaserver.obj:	precompiled.h fdrscadaserver.h dlldefs.h \
		fdrsocketserver.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h \
		fdrclientserverconnection.h serverconnection.h loggable.h \
		string_util.h streamBuffer.h worker_thread.h timing_util.h \
		fdrinterface.h message.h ctitime.h collectable.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h configkey.h configval.h logger.h \
		exception_helper.h boostutil.h utility.h numstr.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h mgr_fdrpoint.h smartmap.h dllbase.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h socket_helper.h win_helper.h
fdrserverconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h guard.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h dllbase.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrsocketlayer.h fdrsocketconnection.h \
		socket_helper.h win_helper.h fdrserverconnection.h
fdrservice.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h guard.h fdrservice.h cservice.h \
		thread_monitor.h smartmap.h dllbase.h readers_writer_lock.h \
		queue.h thread.h mutex.h thread_register_data.h boost_time.h \
		connection_client.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h connection_base.h worker_thread.h timing_util.h \
		msg_cmd.h amq_constants.h module_util.h
fdrsimplebase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h loggable.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h dbaccess.h resolvers.h db_entry_defines.h \
		fdr.h fdrdebuglevel.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h database_connection.h database_reader.h \
		row_reader.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h
fdrsinglesocket.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h loggable.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		socket_helper.h win_helper.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrsinglesocket.h
fdrsocketconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h guard.h fdrsocketlayer.h \
		fdrsocketconnection.h socket_helper.h timing_util.h \
		win_helper.h worker_thread.h
fdrsocketinterface.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h guard.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h mutex.h fdrsocketconnection.h socket_helper.h \
		timing_util.h win_helper.h fdrsocketinterface.h \
		fdrinterface.h message.h collectable.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h connection_base.h worker_thread.h fdrdebuglevel.h \
		msg_cmd.h
fdrsocketlayer.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h guard.h socket_helper.h timing_util.h \
		win_helper.h fdrinterface.h message.h collectable.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h dllbase.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrsocketinterface.h fdrclientconnection.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h
fdrsocketserver.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		ctitime.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		socket_helper.h win_helper.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h ctidate.h
fdrstec.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrstec.h fdrftpinterface.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h
fdrtelegyr.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h dbaccess.h resolvers.h db_entry_defines.h \
		fdr.h fdrdebuglevel.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h database_connection.h database_reader.h \
		row_reader.h fdrpointlist.h fdrtelegyr.h fdrasciiimportbase.h \
		telegyrgroup.h telegyrcontrolcenter.h rtdb.h
fdrtextexport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h loggable.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h guard.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrtextexport.h
fdrtextfilebase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h loggable.h dllbase.h os2_2w32.h \
		cticalls.h critical_section.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h guard.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		TextFileInterfaceParts.h
fdrtextimport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h configkey.h configval.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h loggable.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h guard.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrtextimport.h
fdrtristate.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrtristate.h fdrftpinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h
fdrtristatesub.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		fdrTriStateSub.h fdrftpinterface.h fdrinterface.h message.h \
		ctitime.h collectable.h loggable.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h configkey.h configval.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h readers_writer_lock.h \
		guard.h connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		ctidate.h
fdrvalmet.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h cparms.h configkey.h configval.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h dllbase.h critical_section.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		guard.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		socket_helper.h win_helper.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrvalmet.h \
		fdrvalmetutil.h
fdrvalmetmulti.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h cparms.h configkey.h \
		configval.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h dllbase.h critical_section.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		guard.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		socket_helper.h win_helper.h fdrscadahelper.h \
		fdrvalmetmulti.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h \
		fdrvalmetutil.h
fdrvalmetutil.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		fdrvalmetutil.h pointdefs.h fdrpointlist.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		mgr_fdrpoint.h smartmap.h boostutil.h utility.h numstr.h \
		dllbase.h critical_section.h readers_writer_lock.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h mutex.h cparms.h configkey.h configval.h
fdrwabash.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h fdrwabash.h \
		fdrinterface.h message.h ctitime.h collectable.h loggable.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h configkey.h configval.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h readers_writer_lock.h \
		guard.h connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h
fdrxa21lm.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h loggable.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h guard.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		socket_helper.h win_helper.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h \
		fdrxa21lm.h
livedatatypes.obj:	precompiled.h livedatatypes.h pointdefs.h cparms.h \
		dlldefs.h configkey.h configval.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h guard.h
livedata_rtp_api.obj:	precompiled.h livedata_rtp_api.h logger.h \
		dlldefs.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h guard.h RTP_APIW.H RTP.H
mgr_fdrpoint.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h resolvers.h pointtypes.h \
		db_entry_defines.h fdr.h pointdefs.h fdrdebuglevel.h \
		mgr_fdrpoint.h smartmap.h boostutil.h utility.h ctitime.h \
		queues.h constants.h numstr.h readers_writer_lock.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h fdrpoint.h fdrdestination.h \
		database_connection.h database_reader.h row_reader.h \
		FdrException.h
telegyrcontrolcenter.obj:	precompiled.h telegyrcontrolcenter.h \
		dlldefs.h fdr.h pointdefs.h telegyrgroup.h ctitime.h \
		fdrpoint.h pointtypes.h fdrdestination.h loggable.h types.h \
		mutex.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h
telegyrgroup.obj:	precompiled.h telegyrgroup.h ctitime.h dlldefs.h \
		fdr.h pointdefs.h fdrpoint.h pointtypes.h fdrdestination.h \
		loggable.h types.h
test_fdrdnpslave.obj:	fdrdnpslave.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h boostutil.h \
		utility.h ctitime.h numstr.h dllbase.h critical_section.h \
		readers_writer_lock.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h mutex.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h \
		worker_thread.h timing_util.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_base.h \
		fdrdebuglevel.h msg_cmd.h socket_helper.h win_helper.h \
		dnp_object_analoginput.h dnp_objects.h prot_base.h xfer.h \
		dnp_object_time.h prot_dnpSlave.h dnp_application.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h desolvers.h \
		boost_test_helpers.h millisecond_timer.h
test_fdrtelegyr.obj:	fdrtelegyr.h dlldefs.h fdrinterface.h message.h \
		ctitime.h ctidbgmem.h collectable.h loggable.h msg_dbchg.h \
		yukon.h types.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h configkey.h configval.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h dllbase.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrasciiimportbase.h telegyrgroup.h \
		telegyrcontrolcenter.h rtdb.h
test_fdrtextimport.obj:	fdrtextimport.h dlldefs.h fdrtextfilebase.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h msg_dbchg.h yukon.h types.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h configkey.h \
		configval.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h mgr_fdrpoint.h smartmap.h \
		dllbase.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		TextFileInterfaceParts.h
test_fdrtristatesub.obj:	fdrTriStateSub.h dlldefs.h fdrftpinterface.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h msg_dbchg.h yukon.h types.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h configkey.h \
		configval.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h mgr_fdrpoint.h smartmap.h \
		dllbase.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		ctidate.h
textfileinterfaceparts.obj:	precompiled.h cparms.h dlldefs.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h ctitime.h loggable.h dllbase.h os2_2w32.h \
		cticalls.h critical_section.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h guard.h \
		TextFileInterfaceParts.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
