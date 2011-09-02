#pragma once

#include "executor.h"

// Forward Declarations
class CtiServer;


class IM_EX_CTIVANGOGH CtiSignalExecutor : public CtiExecutor
{
protected:

private:

public:
   CtiSignalExecutor(CtiMessage *p = NULL) :
      CtiExecutor(p);

   CtiSignalExecutor(const CtiSignalExecutor& aRef);
   virtual ~CtiSignalExecutor();


   INT  ServerExecute(CtiServer *Svr);
};
