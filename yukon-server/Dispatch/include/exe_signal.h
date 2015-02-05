#pragma once

#include "executor.h"

// Forward Declarations
class CtiServer;


struct IM_EX_CTIVANGOGH CtiSignalExecutor : public CtiExecutor
{
   CtiSignalExecutor(CtiMessage *p = NULL) :
      CtiExecutor(p);

   CtiSignalExecutor(const CtiSignalExecutor& aRef);
   virtual ~CtiSignalExecutor();

   YukonError_t ServerExecute(CtiServer *Svr);
};
