#pragma once

#include "collectable.h"
#include "message.h"

#include "executor.h"
#include "exe_cmd.h"
#include "exe_reg.h"


class IM_EX_CTISVR CtiExecutorFactory
{
public:
   CtiExecutorFactory();
   ~CtiExecutorFactory();
   virtual CtiExecutor *getExecutor(CtiMessage*);
};





