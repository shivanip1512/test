include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(RW) \
-I$(BOOST) \


.PATH.cpp = .;$(R_DISPATCH)

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
;$(TCLINC) \
;$(RW)



CALCOBJS= \
calc.obj \
calccomponent.obj \
calc_logic.obj \
pointstore.obj \
calcthread.obj \
calclogicsvc.obj \



LURKOBJS= \
lurk.obj \

LOGOBJS= \
log.obj \

NEWVALOBJS= \
newval.obj \

NEWVALRNGOBJS= \
newvalrng.obj \

WINLIBS=kernel32.lib user32.lib
SOCKSLIB=wsock32.lib

CTIPROGS=\
calc_logic.exe \
lurk.exe \
newval.exe \
newvalrng.exe \
log.exe


VGLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cmdline.lib \
$(COMPILEBASE)\lib\cticparms.lib


TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib


CALCLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
advapi32.lib



ALL:            $(CTIPROGS)

calc_logic.exe:  $(CALCOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) /Fm $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
                $(CALCOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(CALCLIBS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

#--  START TEST APPLICATIONS
lurk.exe:       $(LURKOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(LURKOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

log.exe:        $(LOGOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(LOGOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

newval.exe:     $(NEWVALOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(NEWVALOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

newvalrng.exe:     $(NEWVALRNGOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(NEWVALRNGOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)
#--  END TEST APPLICATIONS

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
    $(RWCPPINVOKE) /Fm $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################



#UPDATE#
calc.obj:	yukon.h precompiled.h ctidbgmem.h calc.h calccomponent.h \
		ctitime.h dlldefs.h ctidate.h logger.h thread.h mutex.h \
		guard.h pointstore.h hashkey.h hash_functions.h rtdb.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h pointdefs.h \
		regression.h utility.h rwutil.h boost_time.h
calccomponent.obj:	yukon.h precompiled.h ctidbgmem.h calccomponent.h \
		ctitime.h dlldefs.h ctidate.h logger.h thread.h mutex.h \
		guard.h pointstore.h hashkey.h hash_functions.h rtdb.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h pointdefs.h \
		regression.h calc.h utility.h rwutil.h boost_time.h
calclogicsvc.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h ctinexus.h netports.h id_build.h \
		message.h collectable.h rwutil.h ctitime.h boost_time.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_cmd.h msg_reg.h \
		msg_signal.h msg_ptreg.h msg_dbchg.h numstr.h pointtypes.h \
		configparms.h cparms.h logger.h thread.h utility.h \
		calclogicsvc.h connection.h exchange.h queue.h cservice.h \
		calcthread.h hashkey.h hash_functions.h calc.h \
		calccomponent.h ctidate.h pointstore.h rtdb.h regression.h \
		thread_monitor.h thread_register_data.h
calcthread.obj:	yukon.h precompiled.h ctidbgmem.h ctibase.h ctinexus.h \
		dlldefs.h netports.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h pointtypes.h message.h collectable.h \
		rwutil.h ctitime.h boost_time.h msg_multi.h msg_pdata.h \
		pointdefs.h logger.h thread.h utility.h calcthread.h \
		hashkey.h hash_functions.h calc.h calccomponent.h ctidate.h \
		pointstore.h rtdb.h regression.h thread_monitor.h queue.h \
		thread_register_data.h
calc_logic.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h CServiceConfig.h rtdb.h hashkey.h \
		hash_functions.h ctibase.h ctinexus.h netports.h cparms.h \
		rwutil.h ctitime.h boost_time.h configparms.h calclogicsvc.h \
		connection.h exchange.h logger.h thread.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cservice.h calcthread.h calc.h \
		calccomponent.h ctidate.h pointstore.h regression.h \
		thread_monitor.h thread_register_data.h
log.obj:	yukon.h precompiled.h ctidbgmem.h connection.h dlldefs.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h ctitime.h message.h \
		collectable.h rwutil.h boost_time.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h ctinexus.h \
		netports.h msg_cmd.h pointtypes.h
lurk.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h message.h collectable.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h ctinexus.h netports.h msg_cmd.h msg_dbchg.h
newval.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h ctitime.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h netports.h \
		message.h collectable.h rwutil.h boost_time.h msg_cmd.h \
		msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h connection.h pointtypes.h
newvalrng.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h ctitime.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h netports.h \
		message.h collectable.h rwutil.h boost_time.h msg_cmd.h \
		msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h connection.h pointtypes.h
pointstore.obj:	yukon.h precompiled.h ctidbgmem.h pointstore.h \
		ctitime.h dlldefs.h hashkey.h hash_functions.h rtdb.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h pointdefs.h regression.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
tstest.obj:	yukon.h precompiled.h ctidbgmem.h calc.h calccomponent.h \
		ctitime.h dlldefs.h ctidate.h logger.h thread.h mutex.h \
		guard.h pointstore.h hashkey.h hash_functions.h rtdb.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h pointdefs.h \
		regression.h
#ENDUPDATE#
