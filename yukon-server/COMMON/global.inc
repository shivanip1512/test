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
CPARMS          = $(COMPILEBASE)\cparms
MACS            = $(COMPILEBASE)\macs
MCCMD           = $(COMPILEBASE)\mccmd
MSG             = $(COMPILEBASE)\message
PROT            = $(COMPILEBASE)\protocol
SERVICE         = $(COMPILEBASE)\service
SIGNAL          = $(COMPILEBASE)\dbsignal
RTDB            = $(COMPILEBASE)\rtdb
INTERP          = $(COMPILEBASE)\interp
DEVICECONFIGURATION = $(COMPILEBASE)\deviceconfiguration

CPP_LIBRARIES   = $(DEV_ENVIRONMENT)\libraries

XERCES          = $(CPP_LIBRARIES)\xerces\3.0.1

TCL             = $(CPP_LIBRARIES)\tcl\8.1.1
TCL_LIBS = \
$(TCL)\lib\tcl81.lib

BOOST           = $(CPP_LIBRARIES)\boost\1.37.0

SQLAPI          = $(CPP_LIBRARIES)\sqlapi\2010.06.08
ACTIVEMQ        = $(CPP_LIBRARIES)\activemq-cpp\3.0.1

MICROSOFT       = $(CPP_LIBRARIES)\microsoft
MICROSOFT_VC    = $(MICROSOFT)\vc\9.0
MICROSOFT_SQL   = $(MICROSOFT)\sql-server-client-dll\2000.80.194.0

DBGHELP         = $(MICROSOFT)\dbghelp\6.12.2.633
DBGHELP_LIBS =\
$(DBGHELP)\lib\dbghelp.lib

OPENSSL         = $(CPP_LIBRARIES)\openssl\0.9.8g
OPENSSL_LIBS = \
$(OPENSSL)\lib\libeay32.lib \
$(OPENSSL)\lib\ssleay32.lib

BOOST_ALL_DYN_LINK = 1

!IFDEF DEBUG
BOOST_VERSION = vc90-mt-gd-1_37
MICROSOFTDLL = d
!ELSE
BOOST_VERSION = vc90-mt-1_37
!ENDIF

BOOST_LIBS = \
$(BOOST)\lib\libboost_date_time-$(BOOST_VERSION).lib \
$(BOOST)\lib\libboost_thread-$(BOOST_VERSION).lib   \
$(BOOST)\lib\libboost_regex-$(BOOST_VERSION).lib

BOOST_TEST_LIBS = \
$(BOOST)\lib\libboost_unit_test_framework-$(BOOST_VERSION).lib


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

PCHFLAGS = /Yu"yukon.h" /Fp"$(BIN)\yukon.pch"

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




