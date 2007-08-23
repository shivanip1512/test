!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(BOOST) \
-I$(RW) 

TARGET=$(BIN)\yukon.pch

ALL:	$(TARGET)

copy:	$(TARGET)

$(TARGET): 	$(COMMON)\include\yukon.h
	$(CC) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) /Yc"yukon.h" /Fp"$(BIN)\yukon.pch" /Fe"$(BIN)\precompiled.obj" -c precompiled.cpp

