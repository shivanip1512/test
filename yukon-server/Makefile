
all:
        @echo:
        @title Building in $(COMPILEBASE)\tcl
        @echo Building in $(COMPILEBASE)\tcl
        @echo:
        @%cd $(COMPILEBASE)\tcl
        @$(MAKE) -nologo
        @title Building in $(COMPILEBASE)\xerces
        @echo Building in $(COMPILEBASE)\xerces
        @echo:
        @%cd $(COMPILEBASE)\xerces
        @$(MAKE) -nologo
        echo:
        @title Building in $(COMPILEBASE)\cparms
        @echo Building in $(COMPILEBASE)\cparms
        @echo:
        @%cd $(COMPILEBASE)\cparms
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\common
        @cid .\include\id_build.h
        @echo:
        @title Building in $(COMPILEBASE)\common
        @echo Building in $(COMPILEBASE)\common
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\proclog
        @echo:
        @title Building in $(COMPILEBASE)\proclog
        @echo Building in $(COMPILEBASE)\proclog
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\message
        @echo:
        @title Building in $(COMPILEBASE)\message
        @echo Building in $(COMPILEBASE)\message
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\server
        @echo:
        @title Building in $(COMPILEBASE)\server
        @echo Building in $(COMPILEBASE)\server
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\protocol
        @echo:
        @title Building in $(COMPILEBASE)\protocol
        @echo Building in $(COMPILEBASE)\protocol
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\database
        @echo:
        @title Building in $(COMPILEBASE)\database
        @echo Building in $(COMPILEBASE)\database
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\rtdb
        @echo:
        @title Building in $(COMPILEBASE)\rtdb
        @echo Building in $(COMPILEBASE)\rtdb
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\dbsignal
        @echo:
        @title Building in $(COMPILEBASE)\dbsignal
        @echo Building in $(COMPILEBASE)\dbsignal
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\service
        @echo:
        @title Building in $(COMPILEBASE)\service
        @echo Building in $(COMPILEBASE)\service
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\dispatch
        @echo:
        @title Building in $(COMPILEBASE)\dispatch
        @echo Building in $(COMPILEBASE)\dispatch
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\pil
        @echo:
        @title Building in $(COMPILEBASE)\pil
        @echo Building in $(COMPILEBASE)\pil
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\porter
        @echo:
        @title Building in $(COMPILEBASE)\porter
        @echo Building in $(COMPILEBASE)\porter
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\scanner
        @echo:
        @title Building in $(COMPILEBASE)\scanner
        @echo Building in $(COMPILEBASE)\scanner
        @echo:
        @$(MAKE) -nologo

        @%cd $(COMPILEBASE)\interp
        @echo:
        @title Building in $(COMPILEBASE)\interp
        @echo Building in $(COMPILEBASE)\interp
        @echo:
        @$(MAKE) -nologo

        @%cd $(COMPILEBASE)\mccmd
        @echo:
        @title Building in $(COMPILEBASE)\mccmd
        @echo Building in $(COMPILEBASE)\mccmd
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\macs
        @echo:
        @title Building in $(COMPILEBASE)\macs
        @echo Building in $(COMPILEBASE)\macs
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\fdr
        @echo:
        @title Building in $(COMPILEBASE)\fdr
        @echo Building in $(COMPILEBASE)\fdr
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\calc
        @echo:
        @title Building in $(COMPILEBASE)\calc
        @echo Building in $(COMPILEBASE)\calc
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\capcontrol
        @echo:
        @title Building in $(COMPILEBASE)\capcontrol
        @echo Building in $(COMPILEBASE)\capcontrol
        @echo:
        @$(MAKE) -nologo
        @%cd $(COMPILEBASE)\loadmanagement
        @echo:
        @title Building in $(COMPILEBASE)\loadmanagement
        @echo Building in $(COMPILEBASE)\loadmanagement
        @echo:
        @$(MAKE) -nologo
        @%cd $(CWD)


allclean:
        @$(MAKE) clean all


clean:
        @%cd cparms
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\protocol
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\proclog
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\server
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\database
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\rtdb
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\dbsignal
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\common
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\message
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\dispatch
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\porter
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\scanner
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\pil
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\service
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\fdr
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\calc
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\interp
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\mccmd
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\macs
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\capcontrol
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\loadmanagement
        @$(MAKE) -nologo  $@
        @%cd $(CWD)

deps:
        %cd $(COMPILEBASE)\protocol
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\proclog
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\server
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\database
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\rtdb
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\dbsignal
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\common
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\message
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\dispatch
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\porter
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\scanner
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\pil
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\service
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\fdr
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\calc
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\interp
        title %cd%
        %cd $(COMPILEBASE)\mccmd
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\macs
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\capcontrol
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(COMPILEBASE)\loadmanagement
        title %cd%
        $(MAKE) -nologo  $@
        %cd $(CWD)

copy:
        @%cd cparms
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\proclog
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\server
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\database
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\rtdb
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\common
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\message
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\protocol
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\dispatch
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\porter
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\scanner
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\pil
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\fdr
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\calc
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\service
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\interp
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\mccmd
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\macs
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\capcontrol
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\loadmanagement
        @$(MAKE) -nologo  $@
        @%cd $(COMPILEBASE)\dbsignal
        @$(MAKE) -nologo  $@
        @%cd $(CWD)


