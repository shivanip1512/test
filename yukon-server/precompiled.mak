!include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(BOOST_INCLUDE) \

ALL:	precompiled.pch

copy:	precompiled.pch

precompiled.pch: $(COMMON)\include\precompiled.h
	$(CC) $(CCOPTS) $(DLLFLAGS) $(INCLPATHS) /Yc"precompiled.h" /Fp"precompiled.pch" -c precompiled.cpp

clean:
	@-del precompiled.pch precompiled.obj vc120.pdb

deps:
