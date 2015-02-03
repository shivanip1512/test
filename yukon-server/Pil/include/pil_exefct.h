#pragma once

#include "executorfactory.h"

class IM_EX_CTIPIL CtiPILExecutorFactory : public CtiExecutorFactory
{
public:
   typedef CtiExecutorFactory Inherited;

   virtual CtiExecutor *getExecutor(CtiMessage*);
};
