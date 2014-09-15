#pragma once

#include "executor.h"

// Forward Declarations
class CtiServer;


class IM_EX_CTIVANGOGH CtiPointChangeExecutor : public CtiExecutor
{
private:

public:

   CtiPointChangeExecutor(CtiMessage *p = NULL);// :CtiExecutor(p);
   virtual ~CtiPointChangeExecutor();
   YukonError_t ServerExecute(CtiServer *Svr);
};


