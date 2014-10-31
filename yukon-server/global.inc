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

XERCES          = $(CPP_LIBRARIES)\xerces\3.0.1

TCL             = $(CPP_LIBRARIES)\tcl\8.1.1
TCL_LIBS = \
$(TCL)\lib\tcl81.lib

BOOST           = $(CPP_LIBRARIES)\boost\1.49.0
BOOST_INCLUDE   = $(CPP_LIBRARIES)\boost\1.49.0\include\boost-1_49

SQLAPI          = $(CPP_LIBRARIES)\sqlapi\4.0.1

ACTIVEMQ        = $(CPP_LIBRARIES)\apache\activemq-cpp\3.8.2
ACTIVEMQ_INCLUDE= $(ACTIVEMQ)\include
!IFDEF DEBUG
ACTIVEMQ_LIB    = $(ACTIVEMQ)\lib\activemq-cppd.lib
ACTIVEMQ_DLL    = activemq-cppd.dll
!ELSE
ACTIVEMQ_LIB    = $(ACTIVEMQ)\lib\activemq-cpp.lib
ACTIVEMQ_DLL    = activemq-cpp.dll
!ENDIF

APR             = $(CPP_LIBRARIES)\apache\apr\1.4.6
APR_LIB         = $(APR)\lib\libapr-1.lib
APR_INCLUDE     = $(APR)\include

APR_UTIL        = $(CPP_LIBRARIES)\apache\apr-util\1.5.1
# APR_UTIL_INCLUDE= $(APR_UTIL)\include

APR_ICONV       = $(CPP_LIBRARIES)\apache\apr-iconv\1.2.1
# APR_ICONV_INCLUDE= $(APR_ICONV)\include

THRIFT          = $(CPP_LIBRARIES)\apache\thrift\0.9.0
THRIFT_INCLUDE  = $(THRIFT)\include
THRIFT_LIB      = $(THRIFT)\lib\libthrift.lib

CAJUN           = $(CPP_LIBRARIES)\cajun\2.0.2
CAJUN_INCLUDE   = $(CAJUN)\include

LOG4CXX         = $(CPP_LIBRARIES)\apache\log4cxx\0.10.0
LOG4CXX_INCLUDE = $(LOG4CXX)\include
!IFDEF DEBUG
LOG4CXX_LIB     = $(LOG4CXX)\lib\log4cxxd.lib
LOG4CXX_DLL     = log4cxxd.dll
!ELSE
LOG4CXX_LIB     = $(LOG4CXX)\lib\log4cxx.lib
LOG4CXX_DLL     = log4cxx.dll
!ENDIF

MICROSOFT       = $(CPP_LIBRARIES)\microsoft
MICROSOFT_VC    = $(MICROSOFT)\vc\9.0
MICROSOFT_SQL   = $(MICROSOFT)\sql-server-client-dll\2000.80.194.0

DBGHELP         = $(MICROSOFT)\dbghelp\6.12.2.633
DBGHELP_LIBS =\
$(DBGHELP)\lib\dbghelp.lib

OPENSSL         = $(CPP_LIBRARIES)\openssl\1.0.1g
OPENSSL_LIBS = \
!IFDEF DEBUG
$(OPENSSL)\lib\libeay32MDd.lib \
$(OPENSSL)\lib\ssleay32MDd.lib
!ELSE
$(OPENSSL)\lib\libeay32MD.lib \
$(OPENSSL)\lib\ssleay32MD.lib
!ENDIF

LIBCOAP         = $(CPP_LIBRARIES)\libcoap\4.0.1
LIBCOAP_INCLUDE = $(LIBCOAP)\include
!IFDEF DEBUG
LIBCOAP_LIBS = $(LIBCOAP)\lib\libcoapd.lib
!ELSE
LIBCOAP_LIBS = $(LIBCOAP)\lib\libcoap.lib
!ENDIF


BOOST_ALL_DYN_LINK = 1

!IFDEF DEBUG
BOOST_VERSION = vc90-mt-gd-1_49
MICROSOFTDLL = d
!ELSE
BOOST_VERSION = vc90-mt-1_49
!ENDIF

BOOST_LIBS = \
$(BOOST)\lib\libboost_date_time-$(BOOST_VERSION).lib \
$(BOOST)\lib\libboost_thread-$(BOOST_VERSION).lib   \
$(BOOST)\lib\libboost_regex-$(BOOST_VERSION).lib \
$(BOOST)\lib\libboost_filesystem-$(BOOST_VERSION).lib \
$(BOOST)\lib\libboost_system-$(BOOST_VERSION).lib

BOOST_TEST_LIBS = \
$(BOOST)\lib\libboost_unit_test_framework-$(BOOST_VERSION).lib

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
CFLAGS  = /nologo /Zc:wchar_t- /Zi /Od /EHa     /MDd /Zm200 /D_DEBUG  $(D_VERSION) $(D_VERSION_DETAILS) $(D_BOOST_ENABLE_ASSERT_HANDLER)
DLLFLAGS= /nologo /Zc:wchar_t- /Zi /Od /EHa /LD /MDd /Zm200 /D_DEBUG  $(D_VERSION) $(D_VERSION_DETAILS) $(D_BOOST_ENABLE_ASSERT_HANDLER)
XERCES_LIB = xerces-c_3D
XERCES_DLL = xerces-c_3_0D
SQLAPI_LIB = sqlapid
SQLAPI_DLL = sqlapid
!ELSE
#This part is the non-debug section a/k/a release or retail build
#echo non-debug build used
CFLAGS  = /nologo /Zc:wchar_t- /Zi /O2 /Oy- /EHa     /MD /Zm200 $(D_VERSION) $(D_VERSION_DETAILS) $(BOOST_ENABLE_ASSERT_HANDLER)
DLLFLAGS= /nologo /Zc:wchar_t- /Zi /O2 /Oy- /EHa /LD /MD /Zm200 $(D_VERSION) $(D_VERSION_DETAILS) $(BOOST_ENABLE_ASSERT_HANDLER)
XERCES_LIB = xerces-c_3
XERCES_DLL = xerces-c_3_0
SQLAPI_LIB = sqlapi
SQLAPI_DLL = sqlapi
!ENDIF

XERCES_LIBS = $(XERCES)\lib\$(XERCES_LIB).lib

PCHFLAGS = /Yu"precompiled.h" /Fp"$(COMPILEBASE)\precompiled.pch"

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




