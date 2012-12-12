# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(PROT)\include \
-I$(CPARMS)\include \
-I$(RW) \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(FDR)\Telegyr\inc \
-I$(FDR)\OSIPI\inc \
-I$(FDR)\LiveData


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
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(PROT)\include \
;$(RW)


FDRINTERFACES=\
fdrvalmetutil.obj

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
fdrscadahelper.obj \
fdrdnphelper.obj


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

#  commented out because of bigtime Microsoft compiler problems.  i'm compiling
#    all of the code into one .obj, because it can't handle the code being contained
#    in more than one file.  #include "fdrinterface.cpp" is ugly, but the only way
#    around it for now.
# $(FDRINTERFACEOBJS) \

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
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRBASE -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(FDRINTERFACEOBJS)]


cti_fdr.dll: $(FDR_DLL_FULLBUILD) $(FDRINTERFACEOBJS) $(OBJ)\cti_fdr.res
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(FDRINTERFACEOBJS) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) /Fe..\$@ $(LINKFLAGS) cti_fdr.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtextexport.dll: fdrtextexport.obj Makefile $(OBJ)\fdrtextexport.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtextexport.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrtextexport.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtextimport.dll: fdrtextimport.obj Makefile $(OBJ)\fdrtextimport.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtextimport.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrtextimport.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrdsm2import.dll: fdrdsm2import.obj Makefile $(OBJ)\fdrdsm2import.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrdsm2import.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrdsm2import.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrdsm2filein.dll: fdrdsm2filein.obj Makefile $(OBJ)\fdrdsm2filein.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrdsm2filein.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrdsm2filein.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrrdex.dll: fdrrdex.obj Makefile $(OBJ)\fdrrdex.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrrdex.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrrdex.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrcygnet.dll: fdrcygnet.obj Makefile $(OBJ)\fdrcygnet.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                @if exist ..\cygnet\dclnd.lib copy ..\cygnet\dclnd.lib $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrcygnet.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\dclnd.lib /Fe..\$@ $(LINKFLAGS) fdrcygnet.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdracs.dll:     fdracs.obj Makefile $(OBJ)\fdracs.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(<F) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdracs.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdracsmulti.dll: fdracsmulti.obj Makefile $(OBJ)\fdracsmulti.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(<F) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdracsmulti.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

.obj.dll :
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(<F) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS)
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrvalmet.dll:  fdrvalmet.obj $(FDRINTERFACES)  Makefile $(OBJ)\fdrvalmet.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrvalmet.obj $(FDRINTERFACES) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrvalmet.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrvalmetmulti.dll: fdrvalmetmulti.obj $(FDRINTERFACES)  Makefile $(OBJ)\fdrvalmetmulti.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrvalmetmulti.obj $(FDRINTERFACES) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrvalmetmulti.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrinet.dll: fdrinet.obj Makefile $(OBJ)\fdrinet.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrinet.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrinet.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrstec.dll: fdrstec.obj Makefile $(OBJ)\fdrstec.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrstec.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrstec.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtristate.dll: fdrtristate.obj Makefile $(OBJ)\fdrtristate.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtristate.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrtristate.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrrccs.dll: fdrrccs.obj Makefile $(OBJ)\fdrrccs.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrrccs.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\fdrinet.lib /Fe..\$@ $(LINKFLAGS) fdrrccs.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlivedata.dll: fdrlivedata.obj livedata_rtp_api.obj RTP_API.obj livedatatypes.obj Makefile $(OBJ)\fdrlivedata.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrlivedata.obj livedata_rtp_api.obj RTP_API.obj livedatatypes.obj $(INCLPATHS) ws2_32.lib $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrlivedata.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlodestarimport_enh.dll: fdrlodestarimport_enh.obj Makefile $(OBJ)\fdrlodestarimport_enh.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrlodestarimport_enh.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrlodestarimport_enh.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlodestarimport_std.dll: fdrlodestarimport_std.obj Makefile $(OBJ)\fdrlodestarimport_std.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrlodestarimport_std.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrlodestarimport_std.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)


fdrtelegyr.dll: $(FDRTELEGYROBJS) Makefile $(OBJ)\fdrtelegyr.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                @if exist ..\telegyr\lib\apiclilib.lib copy ..\telegyr\lib\apiclilib.lib $(COMPILEBASE)\lib
                @if exist ..\telegyr\lib\pllib.lib copy ..\telegyr\lib\pllib.lib $(COMPILEBASE)\lib
                @if exist ..\telegyr\lib\psapi.lib copy ..\telegyr\lib\psapi.lib $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) $(FDRTELEGYROBJS) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(FDRTELEGYRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) /link $(IGNORE_LIB) fdrtelegyr.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrpi.dll: $(FDRPIOBJS) Makefile $(OBJ)\fdrpi.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) $(FDRPIOBJS) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(FDRPILIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrpi.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrxa21lm.dll: fdrxa21lm.obj Makefile $(OBJ)\fdrxa21lm.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrxa21lm.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrxa21lm.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrbepc.dll: fdrbepc.obj Makefile $(OBJ)\fdrbepc.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrbepc.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ fdrbepc.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrwabash.dll: fdrwabash.obj Makefile $(OBJ)\fdrwabash.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrwabash.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrwabash.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtristatesub.dll: fdrtristatesub.obj Makefile $(OBJ)\fdrtristatesub.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrtristatesub.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrtristatesub.res
                @if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrdnpslave.dll: fdrdnpslave.obj Makefile $(OBJ)\fdrdnpslave.res
                @%cd $(OBJ)
                @echo Building  ..\$@
                $(CC) $(DLLFLAGS) fdrdnpslave.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@ $(LINKFLAGS) fdrdnpslave.res
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

fdrvalmetutil.obj : fdrvalmetutil.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
fdrvalmet.obj : fdrvalmet.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRVALMET -DWINDOWS -Fo$(OBJ)\ -c $<
fdrvalmetmulti.obj : fdrvalmetmulti.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRVALMETMULTI -DWINDOWS -Fo$(OBJ)\ -c $<
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

fdrdnpslave.obj : fdrdnpslave.cpp
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -D_DLL_FDRDNPSLAVE -DWINDOWS -Fo$(OBJ)\  -c $<

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
a.obj:	precompiled.h
fdr.obj:	precompiled.h ctitime.h dlldefs.h ctibase.h ctinexus.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h dllbase.h \
		dsm2.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		CServiceConfig.h fdrservice.h cservice.h id_fdr.h
fdracs.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdracs.h
fdracsmulti.obj:	precompiled.h ctidate.h dlldefs.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h fdrpointlist.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h fdrdebuglevel.h fdrscadahelper.h \
		fdracsmulti.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h
fdrasciiimportbase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		fdrasciiimportbase.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h string_utility.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h
fdrbepc.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_cmd.h message.h \
		collectable.h pointtypes.h fdrtextfilebase.h fdrinterface.h \
		msg_dbchg.h connection.h exchange.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrbepc.h ctitokenizer.h
fdrclientconnection.obj:	precompiled.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h fdrinterface.h \
		message.h ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_dbchg.h connection.h exchange.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrclientconnection.h
fdrclientserverconnection.obj:	precompiled.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		fdrinterface.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_dbchg.h connection.h exchange.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h prot_dnp.h prot_base.h xfer.h \
		packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h
fdrcygnet.obj:	precompiled.h ctitime.h dlldefs.h cparms.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		queue.h fdrcygnet.h fdrinterface.h msg_dbchg.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h
fdrdestination.obj:	precompiled.h fdrpoint.h dlldefs.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h logger.h thread.h CtiPCPtrQueue.h
fdrdnphelper.obj:	precompiled.h msg_cmd.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		fdrdebuglevel.h fdrdnphelper.h pointtypes.h fdrdestination.h \
		fdr.h pointdefs.h fdrdnpslave.h fdrpointlist.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h logger.h thread.h CtiPCPtrQueue.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h dnp_object_analoginput.h dnp_objects.h \
		prot_base.h xfer.h dnp_object_time.h prot_dnp.h \
		packet_finder.h dnp_application.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h
fdrdnpslave.obj:	precompiled.h ctidate.h dlldefs.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h ctistring.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h string_utility.h queue.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		fdrdebuglevel.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h fdrdnphelper.h \
		fdrdnpslave.h dnp_object_analoginput.h dnp_objects.h \
		prot_base.h xfer.h dnp_object_time.h prot_dnp.h \
		packet_finder.h dnp_application.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h dnp_object_binaryinput.h \
		dnp_object_counter.h
fdrdsm2filein.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h pointtypes.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrdsm2filein.h ctitokenizer.h
fdrdsm2import.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h string_utility.h queue.h fdrdsm2import.h \
		fdrasciiimportbase.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrftpinterface.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		fdrftpinterface.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h string_utility.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h
fdrinet.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrclientconnection.h \
		fdrinet.h
fdrinterface.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h boost_time.h boostutil.h configkey.h \
		configval.h ctinexus.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_dbchg.h \
		msg_signal.h fdrinterface.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_reg.h queue.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrio.obj:	precompiled.h fdrio.h
fdriosocket.obj:	precompiled.h fdriosocket.h ctitime.h dlldefs.h \
		fdrio.h
fdrlivedata.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h hashkey.h hash_functions.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		livedatatypes.h fdrlivedata.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdrpointlist.h \
		fdrsimplebase.h fdrasciiimportbase.h livedata_rtp_api.h \
		RTP_APIW.H RTP.H
fdrlodestarimport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h msg_signal.h pointtypes.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h hashkey.h hash_functions.h
fdrlodestarimport_enh.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h pointtypes.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h hashkey.h hash_functions.h \
		fdrlodestarimport_enh.h
fdrlodestarimport_std.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h pointtypes.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h hashkey.h hash_functions.h \
		fdrlodestarimport_std.h
fdrlodestarinfo.obj:	precompiled.h fdrlodestarinfo.h dlldefs.h fdr.h \
		pointdefs.h
fdrpibase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h hashkey.h hash_functions.h \
		resolvers.h db_entry_defines.h ctistring.h fdr.h \
		fdrdebuglevel.h fdrinterface.h msg_dbchg.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdrpointlist.h fdrpipoll.h \
		fdrpibase.h fdrsimplebase.h fdrasciiimportbase.h \
		fdrpinotify.h
fdrpinotify.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h fdrpinotify.h \
		fdrpibase.h fdrinterface.h message.h ctidbgmem.h \
		collectable.h rwutil.h yukon.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_dbchg.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrsimplebase.h fdrasciiimportbase.h
fdrpipoll.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h fdrpipoll.h \
		fdrpibase.h fdrinterface.h message.h ctidbgmem.h \
		collectable.h rwutil.h yukon.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_dbchg.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrsimplebase.h fdrasciiimportbase.h
fdrpoint.obj:	precompiled.h ctitime.h dlldefs.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h logger.h thread.h CtiPCPtrQueue.h
fdrpointidmap.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		fdrpointidmap.h pointtypes.h fdr.h pointdefs.h resolvers.h \
		db_entry_defines.h logger.h thread.h CtiPCPtrQueue.h
fdrpointlist.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h pointdefs.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h
fdrprotectedmaplist.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h pointdefs.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		fdrprotectedmaplist.h
fdrrccs.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrclientconnection.h \
		fdrinet.h fdrrccs.h
fdrrdex.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h \
		fdrrdex.h
fdrscadahelper.obj:	precompiled.h msg_cmd.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		fdrdebuglevel.h fdrscadahelper.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdracsmulti.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h logger.h \
		thread.h CtiPCPtrQueue.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h fdrinterface.h \
		msg_dbchg.h connection.h exchange.h string_utility.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h fdrdnpslave.h fdrdnphelper.h \
		dnp_object_analoginput.h dnp_objects.h prot_base.h xfer.h \
		dnp_object_time.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h fdrvalmetmulti.h
fdrscadaserver.obj:	precompiled.h fdrscadaserver.h dlldefs.h \
		fdrsocketserver.h queues.h cticalls.h os2_2w32.h types.h \
		fdrclientserverconnection.h serverconnection.h fdrinterface.h \
		message.h ctitime.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_dbchg.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h
fdrserverconnection.obj:	precompiled.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h fdrinterface.h \
		message.h ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_dbchg.h connection.h exchange.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h
fdrservice.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h fdrservice.h cservice.h \
		thread_monitor.h smartmap.h readers_writer_lock.h \
		critical_section.h queue.h string_utility.h \
		thread_register_data.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h msg_cmd.h
fdrsimplebase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h string_utility.h queue.h hashkey.h \
		hash_functions.h resolvers.h db_entry_defines.h fdr.h \
		fdrdebuglevel.h fdrinterface.h msg_dbchg.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h
fdrsinglesocket.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h string_utility.h queue.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h \
		fdrsinglesocket.h
fdrsocketconnection.obj:	precompiled.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h fdrsocketlayer.h \
		fdrsocketconnection.h
fdrsocketinterface.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h fdrpointlist.h mgr_fdrpoint.h \
		smartmap.h boostutil.h readers_writer_lock.h \
		critical_section.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h pointdefs.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h \
		fdrsocketconnection.h fdrsocketinterface.h fdrinterface.h \
		message.h collectable.h msg_dbchg.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		fdrdebuglevel.h msg_cmd.h
fdrsocketlayer.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h fdrinterface.h message.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_dbchg.h connection.h exchange.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h fdrsocketinterface.h \
		fdrclientconnection.h fdrsocketlayer.h fdrsocketconnection.h \
		fdrserverconnection.h
fdrsocketserver.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		queue.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h ctidate.h
fdrstec.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h fdrstec.h fdrftpinterface.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrtelegyr.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h hashkey.h hash_functions.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdrpointlist.h fdrtelegyr.h \
		fdrasciiimportbase.h telegyrgroup.h telegyrcontrolcenter.h \
		rtdb.h
fdrtextexport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h pointtypes.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrtextexport.h
fdrtextfilebase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h string_utility.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		TextFileInterfaceParts.h
fdrtextimport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		ctistring.h cparms.h rwutil.h yukon.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h msg_cmd.h message.h collectable.h \
		pointtypes.h fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrtextimport.h
fdrtristate.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h fdrtristate.h fdrftpinterface.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrtristatesub.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h fdrTriStateSub.h \
		fdrftpinterface.h fdrinterface.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_dbchg.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h ctidate.h
fdrvalmet.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrvalmet.h \
		fdrvalmetutil.h
fdrvalmetmulti.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h string_utility.h queue.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrscadahelper.h fdrvalmetmulti.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h fdrvalmetutil.h
fdrvalmetutil.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		fdrvalmetutil.h pointdefs.h fdrpointlist.h mgr_fdrpoint.h \
		smartmap.h boostutil.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h cparms.h configkey.h configval.h
fdrwabash.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h fdrwabash.h fdrinterface.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_dbchg.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h
fdrxa21lm.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		string_utility.h queue.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h \
		fdrxa21lm.h string_util.h
livedatatypes.obj:	precompiled.h livedatatypes.h pointdefs.h cparms.h \
		dlldefs.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h thread.h \
		CtiPCPtrQueue.h
livedata_rtp_api.obj:	precompiled.h livedata_rtp_api.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h RTP_APIW.H RTP.H
mgr_fdrpoint.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h hashkey.h \
		hash_functions.h resolvers.h pointtypes.h db_entry_defines.h \
		fdr.h pointdefs.h fdrdebuglevel.h mgr_fdrpoint.h smartmap.h \
		boostutil.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h logger.h thread.h \
		CtiPCPtrQueue.h FdrException.h
telegyrcontrolcenter.obj:	precompiled.h telegyrcontrolcenter.h \
		dlldefs.h fdr.h pointdefs.h telegyrgroup.h ctitime.h \
		fdrpoint.h pointtypes.h fdrdestination.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h logger.h thread.h CtiPCPtrQueue.h
telegyrgroup.obj:	precompiled.h telegyrgroup.h ctitime.h dlldefs.h \
		fdr.h pointdefs.h fdrpoint.h pointtypes.h fdrdestination.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h
test_fdrdnpslave.obj:	fdrdnpslave.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		boostutil.h utility.h ctitime.h numstr.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h guard.h \
		dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h pointdefs.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h logger.h thread.h \
		CtiPCPtrQueue.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h fdrinterface.h \
		message.h collectable.h msg_dbchg.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		fdrdebuglevel.h msg_cmd.h fdrdnphelper.h \
		dnp_object_analoginput.h dnp_objects.h prot_base.h xfer.h \
		dnp_object_time.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h boost_test_helpers.h
test_fdrtelegyr.obj:	fdrtelegyr.h dlldefs.h fdrinterface.h message.h \
		ctitime.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_dbchg.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrasciiimportbase.h telegyrgroup.h \
		telegyrcontrolcenter.h rtdb.h hashkey.h hash_functions.h
test_fdrtextimport.obj:	fdrtextimport.h dlldefs.h fdrtextfilebase.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h types.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_dbchg.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		TextFileInterfaceParts.h ctistring.h
test_fdrtristatesub.obj:	fdrTriStateSub.h dlldefs.h fdrftpinterface.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h types.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_dbchg.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h ctidate.h
textfileinterfaceparts.obj:	precompiled.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		logger.h thread.h CtiPCPtrQueue.h TextFileInterfaceParts.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
