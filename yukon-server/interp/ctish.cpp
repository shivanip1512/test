/*-----------------------------------------------------------------------------
    Filename:  ctish.cpp
                    
    Programmer:  Aaron Lauinger
    
    Description: Source file for ctish.exe
                 Simply calls Tcl_Main in tcl81.dll providing a callback
                 after initialization in order to load mccmd.dll.
                          
    Initial Date:  10/16/99   
                    9/3/03
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999, 2003
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "ctish.h"

#include <rw/toolpro/winsock.h>

void main(int argc, char* argv[])
{   //make sure winsock is initialized
    RWWinSockInfo sockInfo;

    //Start up tcl and give it a pointer to our init proc
    //so we can load our metering and control dll
    Tcl_Main( argc, argv, CTISH_InitProc );
}

int CTISH_InitProc(Tcl_Interp* interp)
{
  
    // Initialize the global logger
    dout.setOutputFile("ctish");
    dout.setWriteInterval(0);
    dout.setToStdOut(true);
    dout.start();   

    Tcl_Eval(interp, "load mccmd" );

    return TCL_OK;
}

