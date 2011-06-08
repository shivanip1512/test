#ifndef __PTCONNECT_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   ptconnect
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/ptconnect.h-arc  $
* REVISION     :  $Revision: 1.8.4.1 $
* DATE         :  $Date: 2008/11/20 20:37:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __PTCONNECT_H__

#include "ctitime.h"
#include <rw/thr/recursiv.h>
#include <rw\thr\mutex.h>

#include "dlldefs.h"
#include "hashkey.h"
#include "pt_base.h"
#include "con_mgr.h"
#include "msg_pdata.h"
#include "server_b.h"

class CtiPointChange;
class CtiConnectionManager;


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
   int PostPointChangeToConnections(const CtiPointDataMsg& Msg);
   bool HasConnection(const CtiServer::ptr_type cm);
   bool CtiPointConnection::IsEmpty();
   CtiPointConnection& operator=(const CtiPointConnection &aRef);

   CollectionType& getManagerList();
   CollectionType  getManagerList() const;
    
};

#endif // __PTCONNECT_H__
