#ifndef __MGR_PTCLIENTS_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mgr_ptclients
*
* mgr_ptclients.h      7/7/99
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/mgr_ptclients.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __MGR_PTCLIENTS_H__

#include <list>
using namespace std;

#include <rw/db/connect.h>

#include <rw\tpslist.h>
#include <rw\thr\mutex.h>
#include <rw/thr/recursiv.h>

#include "dlldefs.h"
#include "mgr_point.h"
#include "msg_pdata.h"
#include "ptconnect.h"
#include "pt_dyn_dispatch.h"
#include "queue.h"
#include "rtdb.h"
#include "tbl_rawpthistory.h"

// Forward Declarations
class CtiSignalMsg;
class CtiConnectionManager;
class CtiPointRegistrationMsg;


class IM_EX_CTIVANGOGH CtiPointClientManager : public CtiPointManager
{
private:

   // Inherit Point "List" from Parent

public:

   typedef CtiPointManager Inherited;
   typedef RWTPtrSlist<CtiConnectionManager> ConnectionList;

   CtiPointClientManager();

   virtual ~CtiPointClientManager();
   void RefreshList(BOOL (*fn)(CtiPoint*,void*) = isAPoint, void *d = NULL);

   void DumpList(void);
   void DeleteList(void);
   int InsertConnectionManager(CtiConnectionManager* CM, const CtiPointRegistrationMsg &aReg, bool debugprint = false);
   int RemoveConnectionManager(CtiConnectionManager* CM);

   RWTime findNextNearestArchivalTime();
   void scanForArchival(const RWTime &Now, CtiQueue<CtiTableRawPointHistory, less<CtiTableRawPointHistory> > &Que);

   void validateConnections();
   void storeDirtyRecords();

   void RefreshDynamicData();

};

#endif                  // #ifndef __MGR_PTCLIENTS_H__
