# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(TCLINC) \
-I$(RW) \

OBJS=\
ctish.obj

LIBS=\
$(COMPILEBASE)\lib\$(TCL_LIB).lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\cticparms.lib

EXECS=\
ctish.exe

ALL:            $(EXECS)

ctish.exe:     $(OBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(OBJS) -link $(LIBS) $(RWLIBS) 
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target $@
                @echo:
               -@%cd $(CWD)

copy:           $(EXECS)
               -if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)

deps:
                scandeps -Output makeexe.mak *.cpp

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


#UPDATE#
decodetextcmdfile.obj:	decodeTextCmdFile.h logger.h thread.h mutex.h \
		dlldefs.h guard.h types.h
mccmd.obj:	mccmd.h msg_pcrequest.h dlldefs.h message.h ctidbgmem.h \
		collectable.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		ctdpcptrq.h dbaccess.h sema.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configparms.h \
		netports.h msg_email.h ctibase.h ctinexus.h pointtypes.h \
		numstr.h mgr_holiday.h wpsc.h xcel.h decodetextcmdfile.h
mcs8100test.obj:	wpsc.h logger.h thread.h mutex.h dlldefs.h guard.h \
		types.h
mcsh.obj:	mcsh.h logger.h thread.h mutex.h dlldefs.h guard.h mccmd.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h ctdpcptrq.h
wpsc.obj:	wpsc.h logger.h thread.h mutex.h dlldefs.h guard.h types.h \
		numstr.h
xcel.obj:	xcel.h logger.h thread.h mutex.h dlldefs.h guard.h types.h
#ENDUPDATE#
