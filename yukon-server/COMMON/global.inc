###########################################################
#                                                         #
#       Makefile Template for CTI Apps                    #
#                                                         #
#       $Revision: 1.10 $                               #
#       $Date: 2005/02/10 23:23:45 $                  #
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
R_PROCLOG        = $(REFPATH)\Proclog
R_SERVER         = $(REFPATH)\Server
R_CPARMS         = $(REFPATH)\Cparms
R_MACS           = $(REFPATH)\Macs
R_MCCMD          = $(REFPATH)\Mccmd
R_MSG            = $(REFPATH)\Message
R_PROT           = $(REFPATH)\Protocol
R_SERVICE        = $(REFPATH)\Service
R_SIGNAL         = $(REFPATH)\dbsignal
R_RTDB           = $(REFPATH)\rtdb


#
# Working directories.
#

LOADMANAGEMENT  = $(COMPILEBASE)\loadmanagement
FDR             = $(COMPILEBASE)\fdr
BOOST           = $(COMPILEBASE)\boost_1_31_0
CALC            = $(COMPILEBASE)\calc
CAPCONTROL      = $(COMPILEBASE)\capcontrol
COMMON          = $(COMPILEBASE)\common
DISPATCH        = $(COMPILEBASE)\dispatch
DATABASE        = $(COMPILEBASE)\database
PIL             = $(COMPILEBASE)\pil
PORTER          = $(COMPILEBASE)\porter
SCANNER         = $(COMPILEBASE)\scanner
PROCLOG         = $(COMPILEBASE)\proclog
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
TCLINC          = $(COMPILEBASE)\tcl\include
XERCESINC       = $(COMPILEBASE)\xerces\include

BOOSTLIBS = $(BOOST)\stage\lib\libboost_date_time-vc6-mt-gd-1_31.lib

!IFDEF DEBUG
CFLAGS  = /nologo /EHsc- /Zi /Yd /Od /MDd /Zm200 /FD
DLLFLAGS= /nologo /EHsc- /Zi /Yd /Od /GD /LD /MDd /Zm200 /FD 
TCL_LIB = tcl81d
XERCES_LIB = xerces-c_1D
XERCES_DLL = xerces-c_1_7_0D
!ELSE
CFLAGS  = /nologo /EHsc- /O2 /MD /Zm200 /FD
DLLFLAGS= /nologo /EHsc- /O2 /GD /LD /MD /Zm200 /FD 
TCL_LIB = tcl81
XERCES_LIB = xerces-c_1
XERCES_DLL = xerces-c_1_7_0
!ENDIF

PCHFLAGS = /Yu"yukon.h" /Fp"$(BIN)\yukon.pch"

!IFDEF YUKONPROFILE
CFLAGS  = /Fm $(CFLAGS)
DLLFLAGS= /Fm $(DLLFLAGS)
!ENDIF


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




