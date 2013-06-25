# nmake file YUKON 1.0 FDR dsm2 import Interface

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(RW) \


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
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(RW)



FDRINTERFACEOBJS=\


CTIFDRLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cti_fdr.lib \




ALL:   fdrlodestarimport_enh.dll

fdrlodestarimport_enh.dll: fdrlodestarimport_enh.obj fdrlodestarimport_enh.mak
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrlodestarimport_enh.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\fdrlodestarimport_enh.dll copy bin\fdrlodestarimport_enh.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\fdrlodestarimport_enh.lib copy bin\fdrlodestarimport_enh.lib $(COMPILEBASE)\lib


clean:
                -del fdrlodestarimport_enh.obj fdrlodestarimport_enh.dll fdrlodestarimport_enh.ilk fdrlodestarimport_enh.pdb fdrlodestarimport_enh.lib fdrlodestarimport_enh.exp


deps:
                scandeps -Output fdrlodestarimport_enh.mak fdrlodestarimport_enh.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_ENH_FDRLODESTARIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
