include global.inc
include rwglobal.inc

all:
	 @$(MAKE) -nologo -f .\DLL\makedll.mak $@
         @$(MAKE) -nologo -f makepch.mak	
         @$(MAKE) -nologo -f makebase.mak
         @$(MAKE) -nologo -f makecmdline.mak
	 @$(MAKE) -nologo -f maketest.mak


clean:
         @$(MAKE) -nologo -f .\DLL\makedll.mak $@
         @$(MAKE) -nologo -f makebase.mak $@

allclean:   clean all

copy:
	@$(MAKE) -nologo -f .\DLL\makedll.mak $@
        @$(MAKE) -nologo -f makepch.mak $@
        @$(MAKE) -nologo -f makebase.mak $@
        @$(MAKE) -nologo -f makecmdline.mak $@
	@$(MAKE) -nologo -f maketest.mak $@


deps:
        @$(MAKE) -nologo -f makebase.mak $@
        @$(MAKE) -nologo -f makecmdline.mak $@
	@$(MAKE) -nologo -f maketest.mak $@

