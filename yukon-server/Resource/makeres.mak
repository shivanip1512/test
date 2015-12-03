
include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I.\include \
-I$(COMMON)\include \


.PATH.cpp = .


.PATH.H = \
.\include \
;$(COMMON)\include \


TARGET_OBJS= \
dllmain.obj


TARGET= \
yukon-resource.dll


PROGS_VERSION= \
$(TARGET)


all:    $(TARGET)


$(TARGET):  $(TARGET_OBJS) Makefile resource.res $(OBJ)\yukon-resource.res
        @echo:
        @echo Compiling $@
        @%cd $(OBJ)
        $(CC) $(DLLFLAGS) -Fe..\$@ $(TARGET_OBJS) -link resource.res yukon-resource.res
        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
	-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
        -if exist ..\bin\$(@B).pdb copy ..\bin\$(@B).pdb $(YUKONDEBUG)
        -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
        -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
        @echo:
        @echo Done building Target $@
        @echo:
        @%cd $(CWD)


resource.res:   resource.rc
        @echo Creating $@
        rc /fo $(OBJ)\$@ $<


include $(COMPILEBASE)\versioninfo.inc


copy:
        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
	-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
        -if exist bin\*.pdb copy bin\*.pdb $(YUKONDEBUG)


deps:
        scandeps -DirInc -Output makeres.mak *.cpp


clean:
        -del vc*.pdb $(OBJ)\*.* $(BIN)\*.*


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################

