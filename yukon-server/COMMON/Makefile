include global.inc
include rwglobal.inc

all:
	 @build -nologo -f .\DLL\makedll.mak $@
         @build -nologo -f makepch.mak
         @build -nologo -f makebase.mak
	 @build -nologo -f maketest.mak


clean:
         @build -nologo -f .\DLL\makedll.mak $@
         @build -nologo -f makebase.mak $@

allclean:   clean all

copy:
	@build -nologo -f .\DLL\makedll.mak $@
        @build -nologo -f makepch.mak $@
        @build -nologo -f makebase.mak $@
	@build -nologo -f maketest.mak $@


deps:
        @build -nologo -f makebase.mak $@
	@build -nologo -f maketest.mak $@

