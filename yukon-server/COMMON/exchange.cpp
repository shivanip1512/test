/*-----------------------------------------------------------------------------*
*
* File:   exchange
*
* Date:   5/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/exchange.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:23:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/toolpro/winsock.h>
#include <rw/toolpro/neterr.h>
#include <rw/toolpro/inetaddr.h>
#include <rw\thr\mutex.h>

#include "exchange.h"
#include "dlldefs.h"
extern RWMutexLock coutMux;

CtiExchange::CtiExchange(RWSocketPortal portal) : Portal_(new RWSocketPortal(portal))
{
    LockGuard grd(monitor());
    try
    {
        sinbuf  = new RWPortalStreambuf(*Portal_);
        soubuf  = new RWPortalStreambuf(*Portal_);
        oStream = new RWpostream(soubuf);
        iStream = new RWpistream(sinbuf);
    }
    catch(RWxmsg& msg )
    {
        cout << "CtiExchange Creation Failed: " << msg.why() << endl;
        msg.raise();
    }
}

CtiExchange::~CtiExchange()
{
    LockGuard grd(monitor());
    try
    {
        // Attempt to keep the delete of sinbuf/soutbuf from blocking us!
        SET_NON_BLOCKING( Portal_->socket() );

        delete sinbuf;
        delete soubuf;
        delete oStream;
        delete iStream;

        sinbuf = NULL;
        soubuf = NULL;
        oStream = NULL;
        iStream = NULL;

        try
        {
            delete Portal_;
        }
        catch(RWSockErr& msg )
        {
            if(msg.errorNumber() != RWNETNOTINITIALISED)
            {
                cout << "Socket Error :" << msg.errorNumber() << " occurred" << endl;
                cout << "  " << msg.why() << endl;
            }
        }
    }
    catch(RWxmsg& msg )
    {
        cout << endl << "CtiExchange Deletion Failed: " ;
        cout << msg.why() << endl;
        throw;
    }
}


