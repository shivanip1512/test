#pragma warning( disable : 4786)

#ifndef __EXCHANGE_H__
#define __EXCHANGE_H__

/*-----------------------------------------------------------------------------*
*
* File:   exchange
*
* Date:   5/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/exchange.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:37 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <iostream>
using namespace std;

#include <rw/rwerr.h>

#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include <rw/toolpro/portal.h>
#include <rw/toolpro/portstrm.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include <rw/defs.h>
#include <rw/pstream.h>

#include "dlldefs.h"
#include "dllbase.h"

#define MULTIBUFFEREDEXCHANGE

class IM_EX_CTIBASE CtiExchange : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
private:

   RWSocketPortal    *Portal_;           // The connection is under this!
   RWPortalStreambuf *sinbuf;
   RWPortalStreambuf *soubuf;
   RWpostream        *oStream;
   RWpistream        *iStream;

   CtiExchange();

public:
   CtiExchange(RWSocketPortal portal);
   virtual ~CtiExchange();

   RWBoolean   valid() const
   {
      LockGuard grd(monitor());

      RWBoolean bValid = Portal_->socket().valid();

      if(!bValid ||
         sinbuf  == NULL ||
         soubuf  == NULL ||
         oStream == NULL ||
         iStream == NULL )
      {
         bValid = false;
      }

      return bValid;
   }

   RWBoolean   bad() const
   {
      LockGuard grd(monitor());
      return (oStream->bad() || iStream->bad());
   }


   RWpistream & In()
   {
      LockGuard grd(monitor());
      #if 1
      if(iStream != NULL)
      {
         if(iStream->bad())
         {
            {
               RWMutexLock::LockGuard  guard(coutMux);
               cout << "BAD  Progress " << __FILE__ << " " << __LINE__ << " " << iStream << endl;
            }

            RWxmsg   err("Exchange has baggage in the inbound");
            err.raise();
         }
      }
      else
      {
         RWxmsg   err("Exchange iStream is invalid/NULL");
         err.raise();
      }
      #endif

      return *iStream;
   }

   RWpostream & Out()
   {
      LockGuard grd(monitor());
      if(oStream->bad())
      {
         {
            RWMutexLock::LockGuard  guard(coutMux);
            cout << "BAD  Progress " << __FILE__ << " " << __LINE__ << " " << oStream << endl;
         }

         RWxmsg   err("Exchange has baggage in the outbound");
         err.raise();
      }
      return *oStream;
   }

   RWInetHost getPeer() const
   {
      LockGuard grd(monitor());
      /*
       *  get the host from the RWSockAddr via a conversion to RWInetAddr
       */
      RWInetHost  iHost = RWInetAddr::as(Portal_->socket().getpeername()).host();
      return iHost;
   }

};

#endif // #ifndef __EXCHANGE_H__
