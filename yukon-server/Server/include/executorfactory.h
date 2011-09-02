#pragma once

#include "collectable.h"
#include "message.h"

#include "executor.h"
#include "exe_cmd.h"
#include "exe_reg.h"
// #include "exe_ptchg.h"

class CtiConnectionManager;

class IM_EX_CTISVR CtiExecutorFactory
{
private:
   // Currently no data members in this class.

public:
   CtiExecutorFactory();
   ~CtiExecutorFactory();
   virtual CtiExecutor *getExecutor(CtiMessage*);
};





