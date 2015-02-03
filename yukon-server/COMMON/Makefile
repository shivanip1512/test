!include $(COMPILEBASE)\global.inc

all:
         @build -nologo -f $(PRECOMPILED)\makepch.mak
         @build -nologo -f makebase.mak
	 @build -nologo -f maketest.mak


clean:
         @build -nologo -f makebase.mak $@

allclean:   clean all

copy:
        @build -nologo -f $(PRECOMPILED)\makepch.mak $@
        @build -nologo -f makebase.mak $@
	@build -nologo -f maketest.mak $@


deps:
        @build -nologo -f makebase.mak $@
	@build -nologo -f maketest.mak $@

