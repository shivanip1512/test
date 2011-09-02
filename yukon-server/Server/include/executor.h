#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "message.h"
#include "dlldefs.h"

// Forward Declarations
class CtiConnectionManager;
class CtiServer;

class IM_EX_CTISVR CtiExecutor
{
private:
   CtiMessage *msg_;

public:
   CtiExecutor(CtiMessage *msg = NULL);// :msg_(msg){}

   virtual ~CtiExecutor();
   CtiConnectionManager* getConnectionHandle();
   const CtiConnectionManager* getConnectionHandle() const;

   CtiMessage*  getMessage();

   /*
    *  The ServerExecute MUST call releaseMessage() if the message is passed on back to the
    *  client.  Otherwise, the message will be deleted from the queue and a wicked cascade will
    *  occur.
    */
   void releaseMessage();


   virtual INT  ServerExecute(CtiServer *Svr) = 0;

};
