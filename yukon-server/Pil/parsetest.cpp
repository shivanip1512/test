/*-----------------------------------------------------------------------------*
*
* File:   parsetest
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PIL/parsetest.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:55 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <crtdbg.h>
#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>
#include <rw\cstring.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "queue.h"
#include "cmdparse.h"
#include "logger.h"


BOOL  bQuit = FALSE;


void main(int argc, char **argv)
{
    char cmd[100] = { "GetStatus This is the default case...."};

    strcpy(cmd, argv[1]);

    dout.start();     // fire up the logger thread
    dout.setOutputPath("c:\\temp\\");
    dout.setOutputFile("temp");
    dout.setToStdOut(true);
    dout.setWriteInterval(0);

    try
    {
        CtiCommandParser  parse(cmd);
        parse.Dump();


        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << parse.asString() << endl;
        }

        parse.parseAsString(parse.asString());

    }
    catch(RWxmsg &msg)
    {
        cout << "Exception: ";
        cout << msg.why() << endl;
    }

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();


    exit(0);

}
