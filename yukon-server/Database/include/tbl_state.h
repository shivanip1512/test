
#pragma warning( disable : 4786)
#ifndef __TBL_STATE_H__
#define __TBL_STATE_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_state
*
* Class:  CtiTableState
* Date:   12/29/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_state.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:46 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw\thr\mutex.h>

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\rwtime.h>
#include <rw\cstring.h>

#include "dlldefs.h"

class IM_EX_CTIYUKONDB CtiTableState : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{

protected:

   LONG           _pointID;
   LONG           _stateGroupID;
   LONG           _rawState;
   RWCString      _text;

   // LONG _foreColor;
   // LONG _bkgColor

private:

public:

   CtiTableState(LONG id = -1, LONG raw = 0);
   CtiTableState(const CtiTableState& aRef);
   virtual ~CtiTableState();

   CtiTableState& operator=(const CtiTableState& aRef);
   LONG getStateGroupID() const;
   CtiTableState& setStateGroupID( const LONG id );

   LONG getRawState() const;
   CtiTableState& setRawState( const LONG id );

   const RWCString& getText() const;
   CtiTableState& setText( const RWCString &str );

   LONG getPointID();
   CtiTableState& setPointID( const LONG ptid );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static RWCString getTableName();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Delete();

   virtual void DecodeDatabaseReader(RWDBReader& rdr);

   bool operator<( const CtiTableState &rhs ) const;
   bool operator==( const CtiTableState &rhs ) const;
   bool operator()(const CtiTableState& aRef) const;

   void dump() const;
};
#endif // #ifndef __TBL_STATE_H__
