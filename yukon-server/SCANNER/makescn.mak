include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

SCNDLLOBJS=\
ctiscannable.obj\
mgr_scannable.obj\
dev_welco.obj\

CTIPROGS=\
ctiscn.dll

SCNLIBS=\
$(COMPILEBASE)\lib\ctibase.lib\
$(COMPILEBASE)\lib\ctidbsrc.lib

PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)

ctiscn.dll:     $(SCNDLLOBJS) Makefile $(OBJ)\ctiscn.res
                @echo:
                @echo Compiling $@
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe$@ \
                $(SCNDLLOBJS) -link $(RWLIBS) $(SCNLIBS) $(OBJ)\ctiscn.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist $@ copy $@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist $(@B).lib copy $(@B).lib $(COMPILEBASE)\lib
                @echo:
                @echo Done building Target $@
                @echo:


clean:
        -del *.obj *.pch *.pdb *.sdb *.adb *.ilk *.exp *.lib


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) -I..\include $(INCLPATHS) /D_DLL_CTISCN -c $<


######################################################################################

#UPDATE#

#ENDUPDATE#
