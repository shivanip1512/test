#ifndef __CON_MGR_PIL_H__
#define __CON_MGR_PIL_H__
#include <limits.h>

#include <rw/thr/mutex.h>
#include <rw/thr/thrfunc.h>
#include <rw/toolpro/sockport.h>
#include <rw/toolpro/inetaddr.h>

#include "exchange.h"
#include "message.h"

#include "con_mgr.h"
#include "pil_conmgr.h"

class IM_EX_CTIPIL CtiPILConnectionManager : public CtiConnectionManager
{
private:

   // Client connection supplied information via a Registration Message

public:

   typedef CtiConnectionManager Inherited;

   CtiPILConnectionManager()
   {
      cout << "Default Constructor may break things!" << __FILE__ << " " << __LINE__ << endl;
      cout << "**** Connection Manager!!! *****" << endl;
   }

   CtiPILConnectionManager(CtiExchange *XChg, InQ_t *MainQueue_) :
      CtiConnectionManager( XChg, MainQueue_ )
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

#endif //#ifndef __CON_MGR_PIL_H__


