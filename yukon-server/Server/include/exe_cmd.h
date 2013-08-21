#pragma once

#include "executor.h"

class IM_EX_CTISVR CtiCommandExecutor : public CtiExecutor
{
private:

public:

   CtiCommandExecutor(CtiMessage *p = NULL);
   virtual ~CtiCommandExecutor();
   INT  ServerExecute(CtiServer *);
};


