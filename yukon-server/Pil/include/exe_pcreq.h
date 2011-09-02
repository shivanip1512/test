#pragma once

#include "executor.h"

// Forward Declarations
class CtiConnectionManager;
class CtiServer;


class IM_EX_CTIPIL CtiRequestExecutor : public CtiExecutor
{
private:

public:

   CtiRequestExecutor(CtiMessage *p = NULL) :
      CtiExecutor(p)
   {}

   virtual ~CtiRequestExecutor()
   {}

   int  ServerExecute(CtiServer *Svr);
};


