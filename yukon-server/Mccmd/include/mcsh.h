/*-----------------------------------------------------------------------------
    Filename:  mcsh.cpp
                    
    Programmer:  Aaron Lauinger
    
    Description: Header file for mcsh.exe
                 Simply calls Tcl_Main in tcl81.dll providing a callback
                 after initialization in order to load mccmd.dll.
                 This loads cti metering and control commands into the 
                 interpreter and attempts to make a connection to the PIL.
                          
    Initial Date:  10/16/99   
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )

#ifndef __MCSH_H__
#define __MCSH_H__

#include <tcl.h>

#include "logger.h"
#include "guard.h"

#include "mccmd.h"

int MCSH_InitProc(Tcl_Interp*);

#endif
