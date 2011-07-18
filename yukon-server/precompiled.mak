!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(BOOST) \
-I$(RW)

ALL:	precompiled.pch

copy:	precompiled.pch

precompiled.pch: $(COMMON)\include\precompiled.h
	$(CC) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) $(RWINCL) /Yc"precompiled.h" /Fp"precompiled.pch" -c precompiled.cpp
	@-del precompiled.obj

clean:
	@-del precompiled.pch precompiled.obj vc90.pdb

deps:
