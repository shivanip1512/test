#Build name MUST BE FIRST!!!!

.include global.inc
.include rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(BOOST) \
-I$(RW) 
           
.PATH.cpp = .;$(R_COMMON)

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
;$(BOOST) \
;$(RW) 

TESTOBJS=\
cmdparsetestgenerator.obj \
cmdparse.obj \
test_cmdparse.obj \
test_rwutil.obj \
ctidate.obj \
ctitime.obj \
test_ctidate.obj \
test_ctitime.obj 

                 
LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib 


CMTEST=\
cmdparsetestgenerator.exe \
test_cmdparse.exe \
test_rwutil.exe \
test_ctidate.exe \
test_ctitime.exe


ALL:            $(CMTEST)


cmdparsetestgenerator.exe:    cmdparsetestgenerator.obj Makefile
                @echo.
		@echo Compiling $@
		@echo. 
		@%cd $(OBJ)
		$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) $(COMPILEBASE)\lib\cticparms.lib /Fe..\$@ \
cmdparsetestgenerator.obj -link $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS)
		-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)
                @echo.
		

test_cmdparse.exe:    $(TESTOBJS) Makefile
                @echo.
		@echo Compiling $@
		@echo. 
		@%cd $(OBJ)
		$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) $(COMPILEBASE)\lib\cticparms.lib /Fe..\$@ \
test_cmdparse.obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS)
		-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)
		@echo.
		
test_rwutil.exe:    test_rwutil.obj Makefile
                @%cd $(OBJ)
		$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) $(COMPILEBASE)\lib\cticparms.lib /Fe..\$@ \
test_rwutil.obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS)
		-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)


test_ctidate.exe:    test_ctidate.obj Makefile
                @echo.
		@echo Compiling $@
		@echo. 
		@%cd $(OBJ)
		$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$@ \
test_ctidate.obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS)
		-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)
		@echo.

test_ctitime.exe:    test_ctitime.obj Makefile
                @echo.
		@echo Compiling $@
		@echo. 
		@%cd $(OBJ)
		$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
test_ctitime.obj -link  /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS)
		-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)
		@echo.
		
		

deps:
                scandeps -Output maketest.mak *.cpp

clean:
        -del \
test*.pdb \
$(OBJ)\test*.obj \
$(BIN)\test*.pdb \
$(BIN)\test*.pch \
$(BIN)\test*.ilk \
$(BIN)\test*.exp \
$(BIN)\test*.lib \
$(BIN)\test*.dll \
$(BIN)\test*.exe


allclean:   clean test



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################
ctidate.obj:	yukon.h ctidate.h
ctitime.obj:	yukon.h ctitime.h
cmdparse.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h dlldefs.h \
		parsevalue.h cparms.h devicetypes.h logger.h thread.h mutex.h \
		guard.h numstr.h pointdefs.h utility.h dsm2.h 
test_cmdparse.obj:	cmdparse.h test_cmdparse_input.h test_cmdparse_output.h
cmdparsetestgenerator.obj:  cmdparse.h test_cmdparse_input.h 
test_ctidate.obj:  ctidate.h 
test_ctitime.obj:  ctitime.h

