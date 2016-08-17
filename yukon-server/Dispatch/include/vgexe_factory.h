#pragma once

#include "executorfactory.h"

class CtiVanGoghExecutorFactory : public CtiExecutorFactory
{
public:
   typedef CtiExecutorFactory Inherited;

   virtual CtiExecutor* getExecutor(CtiMessage*);
};
