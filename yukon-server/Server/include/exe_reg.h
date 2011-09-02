#pragma once

// Forward Declarations
class CtiConnectionManager;

#include "executor.h"

class IM_EX_CTISVR CtiRegistrationExecutor : public CtiExecutor
{
private:

public:

   CtiRegistrationExecutor(CtiMessage *p = NULL);
   ~CtiRegistrationExecutor();
   INT  ServerExecute(CtiServer *);
};


