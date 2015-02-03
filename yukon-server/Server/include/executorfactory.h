#pragma once

#include "dlldefs.h"

class CtiExecutor;
class CtiMessage;

class IM_EX_CTISVR CtiExecutorFactory
{
public:
   virtual CtiExecutor *getExecutor(CtiMessage*);
};





