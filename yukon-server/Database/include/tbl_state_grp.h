
#pragma warning( disable : 4786)
#ifndef __TBL_STATE_GRP_H__
#define __TBL_STATE_GRP_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_state_grp
*
* Class:  CtiTableStateGroup
* Date:   12/29/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_state_grp.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:09 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <set>
using std::set;

#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw\thr\mutex.h>

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>


#include "dlldefs.h"
#include "mutex.h"
#include "tbl_state.h"


class IM_EX_CTIYUKONDB CtiTableStateGroup : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:

   typedef set< CtiTableState > CtiStateSet_t;

protected:

   LONG           _stateGroupID;
   string      _name;

   CtiMutex       _stateMux;
   CtiStateSet_t  _stateSet;

private:

public:

   CtiTableStateGroup(LONG id = -1);
   CtiTableStateGroup(const CtiTableStateGroup& aRef);
   virtual ~CtiTableStateGroup();

   CtiTableStateGroup& operator=(const CtiTableStateGroup& aRef);
   LONG getStateGroupID() const;
   const string& getName() const;
   const CtiStateSet_t& getStateSet() const;

   CtiTableStateGroup& setStateGroupID( const LONG id );
   CtiTableStateGroup& setName( const string &str );
   CtiTableStateGroup& setStateSet( const CtiStateSet_t& aSet );

   string getRawState(LONG rawValue);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static string getTableName();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Delete();

   virtual void DecodeDatabaseReader(RWDBReader& rdr);

   bool operator<( const CtiTableStateGroup &rhs ) const;
   bool operator==( const CtiTableStateGroup &rhs ) const;
   bool operator()(const CtiTableStateGroup& aRef) const;

   void dump() const;

};
#endif // #ifndef __TBL_STATE_GRP_H__
