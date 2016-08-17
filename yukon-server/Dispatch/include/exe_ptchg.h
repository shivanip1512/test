#pragma once

#include "executor.h"

// Forward Declarations
class CtiServer;


class CtiPointChangeExecutor : public CtiExecutor
{
private:

public:

   CtiPointChangeExecutor(CtiMessage *p = NULL);// :CtiExecutor(p);
   virtual ~CtiPointChangeExecutor();
   YukonError_t ServerExecute(CtiServer *Svr);
};


