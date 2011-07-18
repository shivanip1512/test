/*-----------------------------------------------------------------------------*
*
* File:   parsetest
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PIL/parsetest.cpp-arc  $
* REVISION     :  $Revision: 1.6.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include <crtdbg.h>
#include <iostream>


#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>

#include <rw/toolpro/winsock.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "queue.h"
#include "cmdparse.h"
#include "logger.h"


using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

BOOL  bQuit = FALSE;


void main(int argc, char **argv)
{
    try
    {
        string cmd;
        for(int i=1; i<argc; i++){
            cmd += argv[i];
            cmd += " ";
        }

        cout << "parsing: " << cmd << endl;

        CtiCommandParser  parse(cmd);
        //parse.Dump();
        cout << parse.asString() << endl;


    }
    catch(RWxmsg &msg)
    {
        cout << "Exception: ";
        cout << msg.why() << endl;
    }

    exit(0);

}

