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
-I$(SERVICE)\include \
-I$(RW) \
-I$(BOOST) \


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
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(TCLINC) \
;$(RW)



FDROBJS= \
fdrservice.obj \
fdr.obj


WINLIBS=kernel32.lib user32.lib
SOCKSLIB=wsock32.lib


CTIPROGS=fdr.exe

FDRLIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib


# Debug testing $(COMPILEBASE)\lib\cticparms.lib \
#        $(FDROBJS) -link $(RWLIBS) $(BOOSTLIBS) $(FDRLIBS) /PROFILE /DEBUGTYPE:CV /PDB:NONE


ALL:            $(CTIPROGS)

fdr.exe:    $(FDROBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) /Fm $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
        $(FDROBJS) -link $(RWLIBS) $(BOOSTLIBS) $(FDRLIBS)
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
    $(RWCPPINVOKE) /Fm $(RWCPPFLAGS) $(CFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################



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
