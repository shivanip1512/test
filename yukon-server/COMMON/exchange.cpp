/*-----------------------------------------------------------------------------*
*
* File:   exchange
*
* Date:   5/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/exchange.cpp-arc  $
* REVISION     :  $Revision: 1.6.34.2 $
* DATE         :  $Date: 2008/11/13 17:23:51 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/toolpro/winsock.h>
#include <rw/toolpro/neterr.h>
#include <rw/toolpro/inetaddr.h>

#include "exchange.h"
#include "dlldefs.h"

#pragma optimize( "", off ) // Be careful with this, be sure ON is at the bottom of the file
                            // and that all header files are before this!

// Throws RWSockErr with error.errorNumber() != WSAECONNREFUSED
CtiExchange::CtiExchange(RWInetAddr sockAddress)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    RWSocket sock;
    sock.connect(sockAddress);
    Portal_ = new RWSocketPortal(sock, RWSocketPortal::Application);
    sinbuf  = new RWPortalStreambuf(*Portal_);
    soubuf  = new RWPortalStreambuf(*Portal_);
    oStream = new RWpostream(soubuf);
    iStream = new RWpistream(sinbuf);
}

CtiExchange::CtiExchange(RWSocketPortal portal) : Portal_(new RWSocketPortal(portal))
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
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
    CtiLockGuard<CtiMutex> guard(_classMutex);
    try
    {
        try
        {
            if(Portal_ != NULL)
            {
                delete Portal_;
                Portal_ = NULL;
            }
        }
        catch(RWSockErr& msg )
        {
            if(msg.errorNumber() != RWNETNOTINITIALISED)
            {
                cout << "Socket Error :" << msg.errorNumber() << " occurred" << endl;
                cout << "  " << msg.why() << endl;
            }
        }

        delete sinbuf;
        delete soubuf;
        delete oStream;
        delete iStream;

        sinbuf = NULL;
        soubuf = NULL;
        oStream = NULL;
        iStream = NULL;




    }
    catch(RWxmsg& msg )
    {
        cout << endl << "CtiExchange Deletion Failed: " ;
        cout << msg.why() << endl;
        throw;
    }
}

void CtiExchange::close()
{
    try
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        if(Portal_ != NULL)
        {
            Portal_->socket().closesocket();
        }
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

#pragma optimize( "", on )
