build_dirs = \
        common \
        message \
        server \
        protocol \
        database \
        deviceconfiguration \
        rtdb \
        dbsignal \
        service \
        dispatch \
        pil \
        porter \
        scanner \
        simulator \
        interp \
        logic \
        macs \
        fdr \
        calc \
        capcontrol \
        loadmanagement

all:
        @build -nologo -f precompiled.mak $@
        @build -nologo -f libraries.mak $@

        %foreach sub_dir in $(build_dirs)
                @%set build_dir = $(COMPILEBASE)\$(sub_dir)

                @%cd $(build_dir)

                @echo:
                @title Building in $(build_dir)
                @echo Building in $(build_dir)
                @echo:

                build -nologo $(build_command)
        %endfor

        @%cd $(CWD)

deps:
        @%set build_command = deps
        %do all

copy:
        @%set build_command = copy
        %do all

clean:
        @%set build_command = clean
        %do all

        %cd $(COMPILEBASE)\lib
        -del *.obj *.dll *.ilk *.pdb *.lib *.exp
        %cd $(COMPILEBASE)\bin
        -del *.dll *.lib *.exe *.xml *.tcl
        -del $(COMPILEBASE)\bin\unittests\*.xml
        %cd $(CWD)

