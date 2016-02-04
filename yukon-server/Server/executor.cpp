#include "precompiled.h"

#include "executor.h"
#include "con_mgr.h"

CtiMessage *CtiExecutor::getMessage()
{
   return msg_.get();
}

CtiExecutor::CtiExecutor(CtiMessage *msg) :
   msg_(msg)
{}

