include $(COMPILEBASE)\global.inc

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
                $(CC) $(INCLPATHS) $(DLLFLAGS) -Fe$@ \
                $(SCNDLLOBJS) -link $(SCNLIBS) $(OBJ)\ctiscn.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist $@ copy $@ $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	       -if exist $(@B).pdb copy $(@B).pdb $(YUKONDEBUG)
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
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) -I..\include $(INCLPATHS) /D_DLL_CTISCN -c $<


######################################################################################

#UPDATE#

#ENDUPDATE#
