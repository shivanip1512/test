/*-----------------------------------------------------------------------------
    Filename:  mcsh.cpp
                    
    Programmer:  Aaron Lauinger
    
    Description: Source file for mcsh.exe
                 Simply calls Tcl_Main in tcl81.dll providing a callback
                 after initialization in order to load mccmd.dll.
                 This loads cti metering and control commands into the 
                 interpreter and attempts to make a connection to the PIL.
                          
    Initial Date:  10/16/99   
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "mcsh.h"

#include <rw/toolpro/winsock.h>

void main(int argc, char* argv[])
{   //make sure winsock is initialized
    RWWinSockInfo sockInfo;

    //Start up tcl and give it a pointer to our init proc
    //so we can load our metering and control dll
    Tcl_Main( argc, argv, MCSH_InitProc );
}

int MCSH_InitProc(Tcl_Interp* interp)
{
    // Initialize the global logger
    dout.setOutputFile("mcsh");
    dout.setWriteInterval(0);
    dout.setToStdOut(true);
    dout.start();   

    if( Mccmd_Init(interp) == TCL_ERROR )
        return TCL_ERROR;

    return TCL_OK;
}

