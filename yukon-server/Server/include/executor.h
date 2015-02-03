#pragma once

#include "dlldefs.h"
#include "yukon.h"  //  for YukonError_t

#include <boost/scoped_ptr.hpp>

class CtiConnectionManager;
class CtiServer;
class CtiMessage;

class IM_EX_CTISVR CtiExecutor
{
   boost::scoped_ptr<CtiMessage> msg_;

public:
   CtiExecutor(CtiMessage *msg);

   CtiConnectionManager* getConnectionHandle();
   const CtiConnectionManager* getConnectionHandle() const;

   CtiMessage*  getMessage();

   virtual YukonError_t ServerExecute(CtiServer *Svr) = 0;
};
