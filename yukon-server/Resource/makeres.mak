
include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I.\include \
-I$(COMMON)\include \


.PATH.cpp = .


.PATH.H = \
.\include \
;$(COMMON)\include \


TARGET_OBJS= \
dllmain.obj


RESOURCES_FULLBUILD = $[Filename,$(OBJ),ResourcesFullBuild,target]


TARGET= \
yukon-resource.dll


PROGS_VERSION= \
$(TARGET)


all:    $(TARGET)


$(RESOURCES_FULLBUILD):
        @touch $@
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(TARGET_OBJS)]


$(TARGET):  $(RESOURCES_FULLBUILD) $(TARGET_OBJS) Makefile resource.res $(OBJ)\yukon-resource.res
        @echo:
        @echo Compiling $@
        @%cd $(OBJ)
        $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(TARGET_OBJS) -link $(LINKFLAGS) resource.res yukon-resource.res
        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
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


deps:
        scandeps -DirInc -Output makeres.mak *.cpp


clean:
        -del vc*.pdb $(OBJ)\*.* $(BIN)\*.*


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_DEVDB -Fo$(OBJ)\ -c $<

######################################################################################


#UPDATE#
dllmain.obj:	precompiled.h
#ENDUPDATE#
