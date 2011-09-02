#pragma once

#include "executor.h"

// Forward Declarations
class CtiConnectionManager;
class CtiServer;


class IM_EX_CTIVANGOGH CtiPointChangeExecutor : public CtiExecutor
{
private:

public:

   CtiPointChangeExecutor(CtiMessage *p = NULL);// :CtiExecutor(p);
   virtual ~CtiPointChangeExecutor();
   INT  ServerExecute(CtiServer *Svr);
};


