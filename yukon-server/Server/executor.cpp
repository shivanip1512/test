#include "precompiled.h"

#include "executor.h"
#include "con_mgr.h"

CtiMessage *CtiExecutor::getMessage()
{
   return msg_.get();
}

CtiConnectionManager *CtiExecutor::getConnectionHandle()
{
   if( msg_ )
   {
      return reinterpret_cast<CtiConnectionManager *>(msg_->getConnectionHandle());
   }

   return NULL;
}

const CtiConnectionManager *CtiExecutor::getConnectionHandle() const
{
   if( msg_ )
   {
       return reinterpret_cast<const CtiConnectionManager *>(msg_->getConnectionHandle());
   }

   return NULL;
}

CtiExecutor::CtiExecutor(CtiMessage *msg) :
   msg_(msg)
{}

