#pragma once

// Forward Declarations
class CtiConnectionManager;
class CtiCommandMsg;

#include "executor.h"

class IM_EX_CTISVR CtiCommandExecutor : public CtiExecutor
{
private:

public:

   CtiCommandExecutor(CtiMessage *p = NULL);
   virtual ~CtiCommandExecutor();
   INT  ServerExecute(CtiServer *);
};


