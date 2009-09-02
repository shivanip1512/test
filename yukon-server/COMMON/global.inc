###########################################################
#                                                         #
#       Makefile Template for CTI Apps                    #
#                                                         #
#       $Revision: 1.18.2.4 $                             #
#       $Date: 2008/11/12 17:27:30 $                      #
#                                                         #
# Definitions                                             #
###########################################################

OBJ             = .\obj
BIN             = .\bin
SRC             = .

###########################################################
#       Initial read time defines                         #
###########################################################
# Define to allow compiling from another directory        #
###########################################################

# ALTERNATE_REFERENCE_PATH:
#       if defined, should reference a complete tree of the
#       compile stucture this is typically a pvcs reference
#       directory which is a read only directory of the most
#       recent checked in goods.
#
# REFPATH:
#       Allows the rest of the makefile know about the
#       default and the alternate
#       (whichever you have chosen to use)
#

%IF( "$(YUKONOUTPUT)" == "" )
%set YUKONOUTPUT=\yukon
%ENDIF


%IF( "$(ALTERNATE_REFERENCE_PATH)" == "" )
        %if %dir( z:\projects\yukon )
                REFPATH = z:\projects\yukon
        %else
                REFPATH = .
        %endif
%ELSE
        REFPATH=$(ALTERNATE_REFERENCE_PATH)\
%ENDIF

# COMPILEBASE:
#       This is the programmers working directory set
#
%IF( "$(COMPILEBASE)" == "" )

   COMPILEBASE = $(REFPATH)

%ENDIF

# SOURCEBASE:
#       This is one level above the compile base.
#       This is set by the build batch file.
#
%IF( "$(SOURCEBASE)" == "" )

   SOURCEBASE = ..

%ENDIF

#
# Reference directories.
#

R_LOADMANAGEMENT = $(REFPATH)\LoadManagement
R_FDR            = $(REFPATH)\Fdr
R_CALC           = $(REFPATH)\Calc
R_CAPCONTROL     = $(REFPATH)\CapControl
R_COMMON         = $(REFPATH)\Common
R_DISPATCH       = $(REFPATH)\Dispatch
R_DATABASE       = $(REFPATH)\Database
R_PIL            = $(REFPATH)\Pil
R_PORTER         = $(REFPATH)\Porter
R_SCANNER        = $(REFPATH)\Scanner
R_SERVER         = $(REFPATH)\Server
R_CPARMS         = $(REFPATH)\Cparms
R_MACS           = $(REFPATH)\Macs
R_MCCMD          = $(REFPATH)\Mccmd
R_MSG            = $(REFPATH)\Message
R_PROT           = $(REFPATH)\Protocol
R_SERVICE        = $(REFPATH)\Service
R_SIGNAL         = $(REFPATH)\dbsignal
R_RTDB           = $(REFPATH)\rtdb
R_DEVICECONFIGURATION = $(REFPATH)\DeviceConfiguration

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

TCLINC          = $(COMPILEBASE)\tcl\include

XERCESHOME      = $(YUKONBASE)\yukon-3rdparty\Xerces
XERCESINC       = $(XERCESHOME)\include

BOOST           = $(YUKONBASE)\yukon-3rdparty\boost

ACTIVEMQ        = $(YUKONBASE)\yukon-3rdparty\ActiveMQ-CPP

BOOST_ALL_DYN_LINK = 1

!IFDEF DEBUG
#echo using debug boost libs
BOOSTLIBS = $(BOOST)\lib\libboost_date_time-vc90-mt-gd-1_37.lib \
            $(BOOST)\lib\libboost_thread-vc90-mt-gd-1_37.lib   \
            $(BOOST)\lib\libboost_regex-vc90-mt-gd-1_37.lib
BOOSTTHREAD = $(BOOST)\lib\boost_thread-vc90-mt-gd-1_37.lib
BOOSTTESTLIBS = $(BOOST)\lib\\libboost_unit_test_framework-vc90-mt-gd-1_37.lib
MICROSOFTDLL = d
#echo $(BOOSTLIBS)
!ELSE
#echo using non-debug boost libs
BOOSTLIBS = $(BOOST)\lib\libboost_date_time-vc90-mt-1_37.lib \
            $(BOOST)\lib\libboost_thread-vc90-mt-1_37.lib   \
            $(BOOST)\lib\libboost_regex-vc90-mt-1_37.lib
BOOSTTHREAD = $(BOOST)\lib\boost_thread-vc90-mt-1_37.lib
BOOSTTESTLIBS = $(BOOST)\lib\\libboost_unit_test_framework-vc90-mt-1_37.lib
#echo $(BOOSTLIBS)
!ENDIF

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
CFLAGS  = /nologo /Zc:wchar_t- /Zi /Od /EHa     /MDd /Zm200 /FD /D_DEBUG  $(D_VERSION) $(D_VERSION_DETAILS) $(D_BOOST_ENABLE_ASSERT_HANDLER)
DLLFLAGS= /nologo /Zc:wchar_t- /Zi /Od /EHa /LD /MDd /Zm200 /FD /D_DEBUG  $(D_VERSION) $(D_VERSION_DETAILS) $(D_BOOST_ENABLE_ASSERT_HANDLER)
#LINKFLAGS = /link
#LINKFLAGS = /link /FORCE /NODEFAULTLIB:MSVCRT.lib
#LINKFLAGS = /link /VERBOSE:LIB
XERCES_LIB = xerces-c_3D
XERCES_DLL = xerces-c_3_0D
!ELSE
#This part is the non-debug section a/k/a release or retail build
#echo non-debug build used
CFLAGS  = /nologo /Zc:wchar_t- /Zi /O2 /Oy- /EHa     /MD /Zm200 /FD $(VERSION) $(VERSION_DETAILS) $(BOOST_ENABLE_ASSERT_HANDLER)
DLLFLAGS= /nologo /Zc:wchar_t- /Zi /O2 /Oy- /EHa /LD /MD /Zm200 /FD $(VERSION) $(VERSION_DETAILS) $(BOOST_ENABLE_ASSERT_HANDLER)
#LINKFLAGS = /link /NODEFAULTLIB:MSVCRTD.lib
#LINKFLAGS = /link /FORCE /NODEFAULTLIB:MSVCRTD.lib
#LINKFLAGS = /link /VERBOSE:LIB
XERCES_LIB = xerces-c_3
XERCES_DLL = xerces-c_3_0
!ENDIF

TCL_LIB = tcl81

PCHFLAGS = /Yu"yukon.h" /Fp"$(BIN)\yukon.pch"

!IFDEF YUKONPROFILE
CFLAGS  = /Fm $(CFLAGS)
DLLFLAGS= /Fm $(DLLFLAGS)
!ENDIF


# Parallel Build
#       This (/MP[#]) is the number of source files that will be built in parallel.
#       Set the number to the number of cores in your CPU.  Setting the number to more than the number
#       of cores doesn't make it build faster but does have a negative effect on page file usage.
PARALLEL = /MP2


INCLPATHS=-I. -I.\include


# Configuration Builder search paths
#       This is the real trick to these makefiles.  The path is searched left to right
#       the key being that you are going to look in the current dir first,
#       then look in the reference directory which was established above.

# Must be done in the Makefiles themselves (to prevent name collisions)... .PATH.h = .;$INCPATHADDITIONS);$(INCLUDE)
# Must be done in the Makefiles themselves (to prevent name collisions)... .PATH.cpp = .;$(CPPREFDIRS)

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

#%if !%file(vcs.cfg)
# @echo REFERENCEDIR = WRITEPROTECT      *\..>vcs.cfg
# @echo ARCHIVESUFFIX = +-arc            >> vcs.cfg
# @echo DELETEWORK                       >> vcs.cfg
# @echo vcsdir = $(REFPATH)\src\vcs      >>vcs.cfg
#%endif

%if %file(make.err)
        @del make.err
%endif

.INIT:
#        @%echo
#        @echo Anything here is done every time BEFORE something should be rebuilt
#        @%echo

.DEINIT:
#        @%echo
#        @echo Anything here is done every time AFTER something was rebuilt
#        @%echo

# TYPE the error file if an error occured
%if %file(err)
        @echo .
        @type err
        @%cd $(CWD)
%endif

# Implicit dependancies and make lines




