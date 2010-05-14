# This is a !GLOM! of Makefiles from RogueWave Examples.
#
# If this doesn't work for you PLEASE PLEASE change it carefully

# Define this environment variable to make life easier.
# it should point at the workspace you are currently using up to the ...\rw directory.

!IFDEF DEBUG
RWBUILDLEVEL=15
!ELSE
RWBUILDLEVEL=12
!ENDIF

BUILDNUMBER=$(RWBUILDLEVEL)
BUILDSUFFIX=d
BUILDTYPE=$(BUILDNUMBER)$(BUILDSUFFIX)

RWOSFAMILY=PC
RWOS=WINNT
RWCOMPILER=MSVC

RWWORKSPACE=$(CPP_LIBRARIES)\roguewave\2009.04.07\$(BUILDTYPE)
RW = $(RWWORKSPACE)

# Rogue Wave header file include path.
RWINCL=-I. -I$(RWWORKSPACE)

# Command to invoke compiler.
CC=cl $(COMPILEONLY)

RWCPPINVOKE=$(CC)

# RogueWave Defines from \htmldocs\genbd\4-2.htm

# These defines cover the basics of the build types.
RWOPTS=\
-D__DLL__ \
-D_CONSOLE \
-D_REENTRANT \
-DRWDLL \
-D_WINDOWS \
-DNOMINMAX \
-D_X86_=1 \
-DWIN32 \
-DRW_NAMESPACE_COLLISION_MSVC_BUG \
-DRW_THR_OS_VERSION_WINNT=0x0400 \
-DRW_THR_USE

#If I am debugging this is the ticket.
!IFDEF DEBUG

RWOPTS=$(RWOPTS) \
-D_DEBUG \
-DRWDEBUG \
-DRW_THR_DEBUG


!ELSE

RWOPTS=$(RWOPTS) \
-DNDEBUG \
-DRW_THR_RELEASE \

!ENDIF



RWOSLIBS=user32.lib wsock32.lib


# Product specific include paths, options, and link libraries.

######################################################################
#       Standard definitions                                         #
######################################################################
RWSHELL=        command.com
RWMOVE=         move
RWCOPY=         copy
RWRM=           del

#       Compile flags:
RWCPPFLAGS=$(RWOPTS)

#       Link flags:
RWLINKFLAGS=$(RWINCL)

#       Libraries to link in
RWLIBS=\
$(RWWORKSPACE)\lib\net$(BUILDTYPE).lib \
$(RWWORKSPACE)\lib\tls$(BUILDTYPE).lib \
$(RWWORKSPACE)\lib\thr$(BUILDTYPE).lib \
$(RWWORKSPACE)\lib\int$(BUILDTYPE).lib \
$(RWWORKSPACE)\lib\dbt$(BUILDTYPE).lib \
$(RWOSLIBS)



#       Extension used by this compiler:
RWEXT=.cpp

########################### Module code ##############################

