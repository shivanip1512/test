
all:
        @echo:
	@title Building in $(COMPILEBASE)\tcl
	@echo Building in $(COMPILEBASE)\tcl
        @echo:
	@%cd $(COMPILEBASE)\tcl
        @build -nologo
        echo:
        @title Building in $(COMPILEBASE)\cparms
        @echo Building in $(COMPILEBASE)\cparms
        @echo:
        @%cd $(COMPILEBASE)\cparms
        @build -nologo
        @%cd $(COMPILEBASE)\common
        # @cid .\include\id_build.h #TEMPORARILY COMMENT OUT
        @echo:
        @title Building in $(COMPILEBASE)\common
        @echo Building in $(COMPILEBASE)\common
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\message
        @echo:
        @title Building in $(COMPILEBASE)\message
        @echo Building in $(COMPILEBASE)\message
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\server
        @echo:
        @title Building in $(COMPILEBASE)\server
        @echo Building in $(COMPILEBASE)\server
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\protocol
        @echo:
        @title Building in $(COMPILEBASE)\protocol
        @echo Building in $(COMPILEBASE)\protocol
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\database
        @echo:
        @title Building in $(COMPILEBASE)\database
        @echo Building in $(COMPILEBASE)\database
	@echo:
        @build -nologo
	@%cd $(COMPILEBASE)\DeviceConfiguration
        @echo:
        @title Building in $(COMPILEBASE)\deviceconfiguration
        @echo Building in $(COMPILEBASE)\deviceconfiguration
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\rtdb
        @echo:
        @title Building in $(COMPILEBASE)\rtdb
        @echo Building in $(COMPILEBASE)\rtdb
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\dbsignal
        @echo:
        @title Building in $(COMPILEBASE)\dbsignal
        @echo Building in $(COMPILEBASE)\dbsignal
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\service
        @echo:
        @title Building in $(COMPILEBASE)\service
        @echo Building in $(COMPILEBASE)\service
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\dispatch
        @echo:
        @title Building in $(COMPILEBASE)\dispatch
        @echo Building in $(COMPILEBASE)\dispatch
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\pil
        @echo:
        @title Building in $(COMPILEBASE)\pil
        @echo Building in $(COMPILEBASE)\pil
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\porter
        @echo:
        @title Building in $(COMPILEBASE)\porter
        @echo Building in $(COMPILEBASE)\porter
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\scanner
        @echo:
        @title Building in $(COMPILEBASE)\scanner
        @echo Building in $(COMPILEBASE)\scanner
        @echo:
        @build -nologo

        @%cd $(COMPILEBASE)\Simulator
        @echo:
        @title Building in $(COMPILEBASE)\Simulator
        @echo Building in $(COMPILEBASE)\Simulator
        @echo:
        @build -nologo

        @%cd $(COMPILEBASE)\interp
        @echo:
        @title Building in $(COMPILEBASE)\interp
        @echo Building in $(COMPILEBASE)\interp
        @echo:
        @build -nologo

        @%cd $(COMPILEBASE)\mccmd
        @echo:
        @title Building in $(COMPILEBASE)\mccmd
        @echo Building in $(COMPILEBASE)\mccmd
        @echo:
        @build -nologo

        @%cd $(COMPILEBASE)\logic
        @echo:
        @title Building in $(COMPILEBASE)\logic
        @echo Building in $(COMPILEBASE)\logic
        @echo:
        @build -nologo

        @%cd $(COMPILEBASE)\macs
        @echo:
        @title Building in $(COMPILEBASE)\macs
        @echo Building in $(COMPILEBASE)\macs
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\fdr
        @echo:
        @title Building in $(COMPILEBASE)\fdr
        @echo Building in $(COMPILEBASE)\fdr
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\calc
        @echo:
        @title Building in $(COMPILEBASE)\calc
        @echo Building in $(COMPILEBASE)\calc

        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\capcontrol
        @echo:
        @title Building in $(COMPILEBASE)\capcontrol
        @echo Building in $(COMPILEBASE)\capcontrol
        @echo:
        @build -nologo
        @%cd $(COMPILEBASE)\loadmanagement
        @echo:
        @title Building in $(COMPILEBASE)\loadmanagement
        @echo Building in $(COMPILEBASE)\loadmanagement
        @echo:
        @build -nologo
        @%cd $(CWD)


allclean:
        @build clean all


clean:
        @%cd cparms
        @build -nologo  $@
        @%cd $(COMPILEBASE)\protocol
        @build -nologo  $@
        @%cd $(COMPILEBASE)\server
        @build -nologo  $@
        @%cd $(COMPILEBASE)\database
	@build -nologo  $@
	@%cd $(COMPILEBASE)\deviceconfiguration
        @build -nologo  $@
        @%cd $(COMPILEBASE)\rtdb
        @build -nologo  $@
        @%cd $(COMPILEBASE)\dbsignal
        @build -nologo  $@
        @%cd $(COMPILEBASE)\common
        @build -nologo  $@
        @%cd $(COMPILEBASE)\message
        @build -nologo  $@
        @%cd $(COMPILEBASE)\dispatch
        @build -nologo  $@
        @%cd $(COMPILEBASE)\porter
        @build -nologo  $@
        @%cd $(COMPILEBASE)\scanner
        @build -nologo  $@
	@%cd $(COMPILEBASE)\Simulator
	@build -nologo  $@
        @%cd $(COMPILEBASE)\pil
        @build -nologo  $@
        @%cd $(COMPILEBASE)\service
        @build -nologo  $@
        @%cd $(COMPILEBASE)\fdr
        @build -nologo  $@
        @%cd $(COMPILEBASE)\calc
        @build -nologo  $@
        @%cd $(COMPILEBASE)\interp
        @build -nologo  $@
        @%cd $(COMPILEBASE)\mccmd
        @build -nologo  $@
        @%cd $(COMPILEBASE)\macs
        @build -nologo  $@
        @%cd $(COMPILEBASE)\logic
        @build -nologo  $@
        @%cd $(COMPILEBASE)\capcontrol
        @build -nologo  $@
        @%cd $(COMPILEBASE)\loadmanagement
        @build -nologo  $@
        @%cd $(COMPILEBASE)\lib
       -del *.obj *.dll *.ilk *.pdb *.lib *.exp
        @%cd $(COMPILEBASE)\bin
       -del *.dll *.lib *.exe *.xml *.tcl
        @%cd $(COMPILEBASE)\bin\unittests
       -del *.xml
        @%cd $(CWD)

deps:
        %cd $(COMPILEBASE)\protocol
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\server
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\database
	title %cd%
        build -nologo  $@
	%cd $(COMPILEBASE)\deviceconfiguration
	title %cd%
        build -nologo  $@
	%cd $(COMPILEBASE)\rtdb
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\dbsignal
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\common
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\message
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\dispatch
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\porter
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\scanner
        title %cd%
        build -nologo  $@
	@%cd $(COMPILEBASE)\Simulator
	title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\pil
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\service
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\fdr
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\calc
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\interp
        title %cd%
        %cd $(COMPILEBASE)\mccmd
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\macs
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\capcontrol
        title %cd%
        build -nologo  $@
        %cd $(COMPILEBASE)\loadmanagement
        title %cd%
        build -nologo  $@
        %cd $(CWD)

copy:
        @%cd cparms
        @build -nologo  $@
        @%cd $(COMPILEBASE)\server
        @build -nologo  $@
        @%cd $(COMPILEBASE)\database
        @build -nologo  $@
        @%cd $(COMPILEBASE)\deviceconfiguration
	@build -nologo  $@
	@%cd $(COMPILEBASE)\rtdb
        @build -nologo  $@
        @%cd $(COMPILEBASE)\common
        @build -nologo  $@
        @%cd $(COMPILEBASE)\message
        @build -nologo  $@
        @%cd $(COMPILEBASE)\protocol
        @build -nologo  $@
        @%cd $(COMPILEBASE)\dispatch
        @build -nologo  $@
        @%cd $(COMPILEBASE)\porter
        @build -nologo  $@
        @%cd $(COMPILEBASE)\scanner
        @build -nologo  $@
        @%cd $(COMPILEBASE)\pil
        @build -nologo  $@
        @%cd $(COMPILEBASE)\fdr
        @build -nologo  $@
        @%cd $(COMPILEBASE)\calc
        @build -nologo  $@
        @%cd $(COMPILEBASE)\service
        @build -nologo  $@
        @%cd $(COMPILEBASE)\interp
        @build -nologo  $@
        @%cd $(COMPILEBASE)\mccmd
        @build -nologo  $@
        @%cd $(COMPILEBASE)\macs
        @build -nologo  $@
        @%cd $(COMPILEBASE)\capcontrol
        @build -nologo  $@
        @%cd $(COMPILEBASE)\loadmanagement
        @build -nologo  $@
        @%cd $(COMPILEBASE)\dbsignal
        @build -nologo  $@
        @%cd $(CWD)


