/*-----------------------------------------------------------------------------*
*
* File:   exchange
*
* Date:   5/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/exchange.h-arc  $
* REVISION     :  $Revision: 1.10.10.1 $
* DATE         :  $Date: 2008/11/12 17:27:30 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __EXCHANGE_H__
#define __EXCHANGE_H__
#pragma warning( disable : 4786)



#include <iostream>


#include <rw/rwerr.h>

#include <rw/thr/recursiv.h>

#include <rw/toolpro/portal.h>
#include <rw/toolpro/portstrm.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include <rw/defs.h>
#include <rw/pstream.h>
#include <boost/utility.hpp>

#include "dlldefs.h"
#include "dllbase.h"
#include "logger.h"
#include "mutex.h"
#include "string_utility.h"

#define MULTIBUFFEREDEXCHANGE

class IM_EX_CTIBASE CtiExchange : public boost::noncopyable
{
private:

    RWSocketPortal    *Portal_;           // The connection is under this!
    RWPortalStreambuf *sinbuf;
    RWPortalStreambuf *soubuf;
    RWpostream        *oStream;
    RWpistream        *iStream;
    mutable CtiMutex _classMutex;

public:
    CtiExchange(RWSocketPortal portal);
    CtiExchange(RWInetAddr sockAddress);
    virtual ~CtiExchange();

    RWBoolean   valid() const
    {
        CtiLockGuard<CtiMutex> guard( (_classMutex) );

        RWBoolean bValid = false;

        if(Portal_)
        {
            bValid = Portal_->socket().valid();

            if(!bValid ||
               sinbuf  == NULL ||
               soubuf  == NULL ||
               oStream == NULL ||
               iStream == NULL )
            {
                bValid = false;
            }
        }

        return bValid;
    }

    RWBoolean   bad() const
    {
        CtiLockGuard<CtiMutex> guard( (_classMutex));
        return(oStream->bad() || iStream->bad());
    }

    void close();

    RWpistream & In()
    {
        CtiLockGuard<CtiMutex> guard( (_classMutex));

        if(iStream != NULL)
        {
            if(iStream->bad())
            {
                iStream->clear();

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Exchange istream has bad status " << FO(__FILE__) << " (" << __LINE__ << ") " << iStream << endl;
                }

                RWxmsg   err("Exchange has problems on the inbound stream");
                err.raise();
            }
        }
        else
        {
            RWxmsg   err("Exchange iStream is invalid/NULL");
            err.raise();
        }

        return *iStream;
    }

    RWpostream & Out()
    {
        CtiLockGuard<CtiMutex> guard( (_classMutex));
        if(oStream->bad())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " oStream has BAD status " << FO(__FILE__) << " (" << __LINE__ << ")" << endl;
            }

            RWxmsg   err("EXCEPTION: Exchange's outbound socket has a BAD status.  It will be reset.");
            err.raise();
        }
        return *oStream;
    }

    RWInetHost getPeerHost() const
    {
        CtiLockGuard<CtiMutex> guard( (_classMutex));
        /*
         *  get the host from the RWSockAddr via a conversion to RWInetAddr
         */
        RWInetHost  iHost;

        if(Portal_)
        {
            try
            {
                iHost = RWInetAddr::as(Portal_->socket().getpeername()).host();
            }
            catch ( RWxmsg& msg )
            {
                CtiLockGuard< CtiLogger > guard(dout);
                dout << "CtiExchange::getPeer() - " << msg.why() << endl;
            }
        }
        return iHost;
    }

    RWInetPort getPeerPort() const
    {
        CtiLockGuard<CtiMutex> guard( (_classMutex));
        /*
         *  get the port from the RWSockAddr via a conversion to RWInetAddr
         */
        RWInetPort iPort;

        if(Portal_)
        {
            try
            {
                iPort = RWInetAddr::as(Portal_->socket().getpeername()).port();
            }
            catch ( RWxmsg& msg )
            {
                CtiLockGuard< CtiLogger > guard(dout);
                dout << "CtiExchange::getPeerPort() - " << msg.why() << endl;
            }
        }
        return iPort;
    }

};

#endif // #ifndef __EXCHANGE_H__
