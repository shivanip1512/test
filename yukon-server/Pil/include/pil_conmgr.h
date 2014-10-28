#pragma once

#include <limits.h>

#include <rw/thr/mutex.h>
#include <rw/thr/thrfunc.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "message.h"

#include "con_mgr.h"
#include "pil_conmgr.h"

#include "connection_listener.h"

class IM_EX_CTIPIL CtiPILConnectionManager : public CtiConnectionManager
{
private:

   // Client connection supplied information via a Registration Message

public:

   typedef CtiConnectionManager Inherited;

   CtiPILConnectionManager( CtiListenerConnection &listenerConn, Que_t *MainQueue_ ) :
       CtiConnectionManager( listenerConn, MainQueue_ )
   {
      // cout << "**** Connection Manager!!! *****" << endl;
   }

   virtual ~CtiPILConnectionManager()
   {
      // cout << "destructor for pil connection manager" << endl;
   }

   static unsigned hash(const CtiPILConnectionManager& aRef)
   {
      return (unsigned)&aRef;            // The address of the Object?
   }
};
