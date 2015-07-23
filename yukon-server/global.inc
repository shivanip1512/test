#
# Global include for Yukon makefiles
#

OBJ             = .\obj
BIN             = .\bin

#
# Initial read time assertions
#

%IF( "$(YUKONOUTPUT)" == "" )
        @echo YUKONOUTPUT not specified.
        %Abort
%ENDIF

%IF( "$(COMPILEBASE)" == "" )
        @echo COMPILEBASE not specified.
        %Abort
%ENDIF

# Many directories are differentiated with Debug or Release
%IF !%Defined(CONFIGURATION)
%IF %Defined(DEBUG)
CONFIGURATION=Debug
%ELSE
CONFIGURATION=Release
%ENDIF
%ELSE
%IF( "$(CONFIGURATION)" == "Debug")
DEBUG=1
%ENDIF
%ENDIF

#
# Working directories.
#

PRECOMPILED     = $(COMPILEBASE)

LOADMANAGEMENT  = $(COMPILEBASE)\loadmanagement
FDR             = $(COMPILEBASE)\fdr
CALC            = $(COMPILEBASE)\calc
CAPCONTROL      = $(COMPILEBASE)\capcontrol
COMMON          = $(COMPILEBASE)\common
DISPATCH        = $(COMPILEBASE)\dispatch
DATABASE        = $(COMPILEBASE)\database
PIL             = $(COMPILEBASE)\pil
PORTER          = $(COMPILEBASE)\porter
SCANNER         = $(COMPILEBASE)\scanner
SERVER          = $(COMPILEBASE)\server
MACS            = $(COMPILEBASE)\macs
MSG             = $(COMPILEBASE)\message
PROT            = $(COMPILEBASE)\protocol
SERVICE         = $(COMPILEBASE)\service
SIGNAL          = $(COMPILEBASE)\dbsignal
RTDB            = $(COMPILEBASE)\rtdb
DEVICECONFIGURATION = $(COMPILEBASE)\deviceconfiguration
RESOURCE        = $(COMPILEBASE)\resource

CPP_LIBRARIES   = $(DEV_ENVIRONMENT)\libraries

XERCES          = $(CPP_LIBRARIES)\apache\xerces-c\3.1.2-1
XERCES_BIN      = $(XERCES)\$(CONFIGURATION)\bin
!IFDEF DEBUG
XERCES_LIB      = $(XERCES)\$(CONFIGURATION)\lib\xerces-c_3D.lib
XERCES_DLL      = xerces-c_3_1D.dll
!ELSE
XERCES_LIB      = $(XERCES)\$(CONFIGURATION)\lib\xerces-c_3.lib
XERCES_DLL      = xerces-c_3_1.dll
!ENDIF
XERCES_INCLUDE      = $(XERCES)\include

TCL             = $(CPP_LIBRARIES)\tcl\8.6.4-1
TCL_INCLUDE     = $(TCL)\include
TCL_BIN         = $(TCL)\$(CONFIGURATION)\bin
!IFDEF DEBUG
TCL_DLL         = tcl86tg.dll
TCL_LIBS = \
$(TCL)\$(CONFIGURATION)\lib\tcl86tg.lib
!ELSE
TCL_DLL         = tcl86t.dll
TCL_LIBS = \
$(TCL)\$(CONFIGURATION)\lib\tcl86t.lib
!ENDIF

BOOST           = $(CPP_LIBRARIES)\boost\1.58.0-1
BOOST_INCLUDE   = $(BOOST)\include
BOOST_BIN       = $(BOOST)\bin
!IFDEF DEBUG
BOOST_VERSION = vc120-mt-gd-1_58
!ELSE
BOOST_VERSION = vc120-mt-1_58
!ENDIF
BOOST_DLL_1 = boost_chrono-$(BOOST_VERSION).dll
BOOST_DLL_2 = boost_date_time-$(BOOST_VERSION).dll
BOOST_DLL_3 = boost_thread-$(BOOST_VERSION).dll
BOOST_DLL_4 = boost_regex-$(BOOST_VERSION).dll
BOOST_DLL_5 = boost_filesystem-$(BOOST_VERSION).dll
BOOST_DLL_6 = boost_system-$(BOOST_VERSION).dll
BOOST_DLL_7 = boost_unit_test_framework-$(BOOST_VERSION).dll
BOOST_LIBS = \
$(BOOST)\lib\boost_chrono-$(BOOST_VERSION).lib \
$(BOOST)\lib\boost_date_time-$(BOOST_VERSION).lib \
$(BOOST)\lib\boost_thread-$(BOOST_VERSION).lib   \
$(BOOST)\lib\boost_regex-$(BOOST_VERSION).lib \
$(BOOST)\lib\boost_filesystem-$(BOOST_VERSION).lib \
$(BOOST)\lib\boost_system-$(BOOST_VERSION).lib
BOOST_TEST_LIBS = \
$(BOOST)\lib\boost_unit_test_framework-$(BOOST_VERSION).lib

SQLAPI          = $(CPP_LIBRARIES)\sqlapi\4.1.5-1
SQLAPI_BIN      = $(SQLAPI)\bin
!IFDEF DEBUG
SQLAPI_LIB      = $(SQLAPI)\lib\sqlapid.lib
SQLAPI_DLL      = sqlapid.dll
!ELSE
SQLAPI_LIB      = $(SQLAPI)\lib\sqlapi.lib
SQLAPI_DLL      = sqlapi.dll
!ENDIF

ACTIVEMQ        = $(CPP_LIBRARIES)\apache\activemq-cpp\3.8.4-1
ACTIVEMQ_INCLUDE= $(ACTIVEMQ)\include
ACTIVEMQ_BIN    = $(ACTIVEMQ)\$(CONFIGURATION)\bin
ACTIVEMQ_LIB    = $(ACTIVEMQ)\$(CONFIGURATION)\lib\activemq-cpp.lib
!IFDEF DEBUG
ACTIVEMQ_DLL    = activemq-cppd.dll
!ELSE
ACTIVEMQ_DLL    = activemq-cpp.dll
!ENDIF

APR             = $(CPP_LIBRARIES)\apache\apr\1.5.2-1
APR_BIN         = $(APR)\$(CONFIGURATION)\bin
APR_LIB         = $(APR)\$(CONFIGURATION)\lib\libapr-1.lib
APR_DLL         = libapr-1.dll
APR_INCLUDE     = $(APR)\include

APR_UTIL        = $(CPP_LIBRARIES)\apache\apr-util\1.5.4-1
APR_UTIL_BIN    = $(APR_UTIL)\$(CONFIGURATION)\bin
APR_UTIL_LIB    = $(APR_UTIL)\$(CONFIGURATION)\lib\libaprutil-1.lib
APR_UTIL_DLL    = libaprutil-1.dll
# APR_UTIL_INCLUDE= $(APR_UTIL)\include

APR_ICONV       = $(CPP_LIBRARIES)\apache\apr-iconv\1.2.1-1
APR_ICONV_BIN   = $(APR_ICONV)\$(CONFIGURATION)\bin
APR_ICONV_LIB   = $(APR_ICONV)\$(CONFIGURATION)\lib\libapriconv-1.lib
APR_ICONV_DLL   = libapriconv-1.dll
# APR_ICONV_INCLUDE= $(APR_ICONV)\include

THRIFT          = $(CPP_LIBRARIES)\apache\thrift\0.9.2-1
THRIFT_BIN      = $(THRIFT)\$(CONFIGURATION)\bin
THRIFT_INCLUDE  = $(THRIFT)\include
THRIFT_LIB      = $(THRIFT)\$(CONFIGURATION)\lib\libthrift.lib

CAJUN           = $(CPP_LIBRARIES)\cajun\2.0.2-1
CAJUN_INCLUDE   = $(CAJUN)\include

LOG4CXX         = $(CPP_LIBRARIES)\apache\log4cxx\0.10.0-1
LOG4CXX_BIN     = $(LOG4CXX)\$(CONFIGURATION)\bin
LOG4CXX_INCLUDE = $(LOG4CXX)\include
LOG4CXX_LIB     = $(LOG4CXX)\$(CONFIGURATION)\lib\log4cxx.lib
LOG4CXX_DLL     = log4cxx.dll

MICROSOFT       = $(CPP_LIBRARIES)\microsoft
MICROSOFT_VC    = $(MICROSOFT)\vc\12.0\$(CONFIGURATION)
!IFDEF DEBUG
MICROSOFT_VCR_DLL = msvcr120d.dll
MICROSOFT_VCP_DLL = msvcp120d.dll
!ELSE
MICROSOFT_VCR_DLL = msvcr120.dll
MICROSOFT_VCP_DLL = msvcp120.dll
!ENDIF

MICROSOFT_SQL     = $(MICROSOFT)\sql-server-client-dll\2000.80.194.0
MICROSOFT_SQL_DLL = ntwdblib.dll

DBGHELP           = $(MICROSOFT)\dbghelp\6.12.2.633
DBGHELP_BIN       = $(DBGHELP)\bin
DBGHELP_DLL       = dbghelp.dll
DBGHELP_LIBS =\
$(DBGHELP)\lib\dbghelp.lib

OPENSSL         = $(CPP_LIBRARIES)\openssl\1.0.1o-1
OPENSSL_BIN     = $(OPENSSL)\$(CONFIGURATION)\bin
OPENSSL_LIB     = $(OPENSSL)\$(CONFIGURATION)\lib
OPENSSL_DLL_1   = libeay32.dll
OPENSSL_DLL_2   = ssleay32.dll
OPENSSL_LIBS = \
$(OPENSSL_LIB)\libeay32.lib \
$(OPENSSL_LIB)\ssleay32.lib
OPENSSL_INCLUDE = $(OPENSSL)\include

LIBCOAP         = $(CPP_LIBRARIES)\libcoap\4.0.1-1
LIBCOAP_INCLUDE = $(LIBCOAP)\include
!IFDEF DEBUG
LIBCOAP_LIBS = $(LIBCOAP)\$(CONFIGURATION)\lib\libcoapd.lib Ws2_32.lib
!ELSE
LIBCOAP_LIBS = $(LIBCOAP)\$(CONFIGURATION)\lib\libcoap.lib Ws2_32.lib
!ENDIF

MANIFEST_TOOL = mt.exe


%IF( "$(BUILD_VERSION)" != "" )

   D_VERSION = /DBUILD_VERSION="$(BUILD_VERSION)"

%ENDIF

%IF( "$(BUILD_VERSION_DETAILS)" != "" )

   D_VERSION_DETAILS = /DBUILD_VERSION_DETAILS="$(BUILD_VERSION_DETAILS)"

%ENDIF

D_BOOST_ENABLE_ASSERT_HANDLER = /DBOOST_ENABLE_ASSERT_HANDLER

!IFDEF DEBUG
#note on above:  apparently the "!" is not a NOT -- hence this part is the DEBUG SECTION
#echo debug build used
CFLAGS  = /nologo /Zi /Od /EHa     /MDd /Zm200 /D_DEBUG  $(D_VERSION) $(D_VERSION_DETAILS) $(D_BOOST_ENABLE_ASSERT_HANDLER)
DLLFLAGS= /nologo /Zi /Od /EHa /LD /MDd /Zm200 /D_DEBUG  $(D_VERSION) $(D_VERSION_DETAILS) $(D_BOOST_ENABLE_ASSERT_HANDLER)
!ELSE
#This part is the non-debug section a/k/a release or retail build
#echo non-debug build used
CFLAGS  = /nologo /Zi /O2 /Oy- /EHa     /MD /Zm200 $(D_VERSION) $(D_VERSION_DETAILS) $(BOOST_ENABLE_ASSERT_HANDLER)
DLLFLAGS= /nologo /Zi /O2 /Oy- /EHa /LD /MD /Zm200 $(D_VERSION) $(D_VERSION_DETAILS) $(BOOST_ENABLE_ASSERT_HANDLER)
!ENDIF


#Oy - omit frame pointers, can make debugging more difficult
#Zi - create full debugging information
#Od - disable optimization
#Eha - structured (e.g. floating-point) and unstructured (C++ throw) exceptions are caught
#Zm200 - precompiled header memory limit of 150 MB
#MDd - defines _DEBUG, _MT, and _DLL

PCHFLAGS = /Yu"precompiled.h" /Fp"$(COMPILEBASE)\precompiled.pch"
PRECOMPILED_OBJ = $(PRECOMPILED)\precompiled.obj

# /Fm Tells the linker to produce a mapfile containing a list of segments in the order in which they appear in the corresponding .exe file or DLL.
!IFDEF YUKONPROFILE
CFLAGS  = /Fm $(CFLAGS)
DLLFLAGS= /Fm $(DLLFLAGS)
!ENDIF


# Parallel Build
#       This (/MP[#]) is the number of source files that will be built in parallel.
#       Set the number to the number of cores in your CPU.  Setting the number to more than the number
#       of cores doesn't make it build faster but does have a negative effect on page file usage.
PARALLEL = /MP8


INCLPATHS=-I. -I.\include


# Command to invoke compiler.
CC=cl $(COMPILEONLY)

CCOPTS=\
-D__DLL__ \
-D_CONSOLE \
-D_REENTRANT \
-D_WINDOWS \
-D_X86_=1 \
-DWIN32 \
-DNOMINMAX \
-DBOOST_ALL_DYN_LINK

#If I am debugging this is the ticket.
!IFDEF DEBUG

CCOPTS=$(CCOPTS) \
-D_DEBUG \

!ELSE

CCOPTS=$(CCOPTS) \
-DNDEBUG \

!ENDIF

# Configuration Builder search paths
#       This is the real trick to these makefiles.  The path is searched left to right
#       the key being that you are going to look in the current dir first,
#       then look in the reference directory which was established above.

.IgnoreMissingHeader shared_ptr.hpp

.Hdr.hpp = ^^\#[ \t]*include[ \t][ \t]*\"@.*@\"
.Hdr.hpp = ^^\#[ \t]*INCLUDE[ \t][ \t]*\"@.*@\"

.PATH.obj = $(OBJ)
.PATH.exe = $(BIN)
.PATH.dll = $(BIN)


####################################################
# PROLOG - To be done before every make
####################################################
#
.PROLOG:
#        @%echo
#        @echo Anything here is done every time BEFORE any file timestamps are examined
#        @%echo

.INIT:
#        @%echo
#        @echo Anything here is done every time BEFORE something should be rebuilt
#        @%echo

.DEINIT:
#        @%echo
#        @echo Anything here is done every time AFTER something was rebuilt
#        @%echo

# Implicit dependancies and make lines




