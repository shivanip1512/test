#pragma once

#include "executorfactory.h"

class IM_EX_CTIVANGOGH CtiVanGoghExecutorFactory : public CtiExecutorFactory
{
public:
   typedef CtiExecutorFactory Inherited;

   virtual CtiExecutor* getExecutor(CtiMessage*);
};
