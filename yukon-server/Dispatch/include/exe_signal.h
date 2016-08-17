#pragma once

#include "executor.h"

// Forward Declarations
class CtiServer;


struct CtiSignalExecutor : public CtiExecutor
{
   CtiSignalExecutor(CtiMessage *p = NULL) :
      CtiExecutor(p);

   CtiSignalExecutor(const CtiSignalExecutor& aRef);
   virtual ~CtiSignalExecutor();

   YukonError_t ServerExecute(CtiServer *Svr);
};
