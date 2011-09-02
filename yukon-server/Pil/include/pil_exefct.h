#pragma once

#include "collectable.h"
#include "message.h"

#include "executorfactory.h"

class CtiConnectionManager;

class IM_EX_CTIPIL CtiPILExecutorFactory : public CtiExecutorFactory
{
private:
   // Currently no data members in this class.

public:
   typedef CtiExecutorFactory Inherited;

   CtiPILExecutorFactory()
   {}
   ~CtiPILExecutorFactory()
   {}

   virtual CtiExecutor *getExecutor(CtiMessage*);
};
