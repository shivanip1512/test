#pragma once

#include "ctitime.h"

#include "dlldefs.h"
#include "pt_base.h"
#include "con_mgr.h"
#include "msg_pdata.h"
#include "server_b.h"


class IM_EX_CTIVANGOGH CtiPointConnection
{
protected:

   // Vector of managers who care about this point.
   typedef std::set< CtiServer::ptr_type > CollectionType;
   CollectionType ConnectionManagerCollection;
   mutable CtiMutex _classMutex;

public:

   CtiPointConnection();
   CtiPointConnection(const CtiPointConnection& aRef);
   virtual ~CtiPointConnection();
   void AddConnectionManager(CtiServer::ptr_type cm);
   void RemoveConnectionManager(CtiServer::ptr_type cm);
   bool HasConnection(const CtiServer::ptr_type cm);
   bool CtiPointConnection::IsEmpty();
   CtiPointConnection& operator=(const CtiPointConnection &aRef);

   CollectionType& getManagerList();
   CollectionType  getManagerList() const;

};
