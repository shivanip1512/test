#pragma once

#include "executor.h"

class IM_EX_CTISVR CtiRegistrationExecutor : public CtiExecutor
{
public:

   CtiRegistrationExecutor(CtiMessage *p = NULL);
   ~CtiRegistrationExecutor();
   YukonError_t ServerExecute(CtiServer *);
};


