/*-----------------------------------------------------------------------------
    Filename:  ctish.cpp
                    
    Programmer:  Aaron Lauinger
    
    Description: Header file for ctish.exe
                 Simply calls Tcl_Main in tcl81.dll providing a callback
                 after initialization in order to load mccmd.dll.
                          
    Initial Date:  10/16/99   
                   9/3/03
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999, 2003
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )

#ifndef __CTISH_H__
#define __CTISH_H__

#include <tcl.h>

#include "logger.h"
#include "guard.h"

int CTISH_InitProc(Tcl_Interp*);

#endif
