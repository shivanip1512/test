#pragma once

#include "collectable.h"
#include "message.h"

#include "executor.h"
#include "exe_ptchg.h"
#include "executorfactory.h"

class CtiConnectionManager;

class IM_EX_CTIVANGOGH CtiVanGoghExecutorFactory : public CtiExecutorFactory
{
private:
   // Currently no data members in this class.

public:
   typedef CtiExecutorFactory Inherited;

   CtiVanGoghExecutorFactory();
   ~CtiVanGoghExecutorFactory();
   virtual CtiExecutor* getExecutor(CtiMessage*);
};
