
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
* REVISION     :  $Revision: 1.4.24.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <rw/thr/recursiv.h>
#include <rw\thr\mutex.h>

#include "row_reader.h"

#include "dlldefs.h"

class IM_EX_CTIYUKONDB CtiTableState
{

protected:

   LONG           _pointID;
   LONG           _stateGroupID;
   LONG           _rawState;
   string      _text;

   // LONG _foreColor;
   // LONG _bkgColor

private:
    mutable CtiMutex _classMutex;
public:

   CtiTableState(LONG id = -1, LONG raw = 0);
   CtiTableState(const CtiTableState& aRef);
   virtual ~CtiTableState();

   CtiTableState& operator=(const CtiTableState& aRef);
   LONG getStateGroupID() const;
   CtiTableState& setStateGroupID( const LONG id );

   LONG getRawState() const;
   CtiTableState& setRawState( const LONG id );

   const string& getText() const;
   CtiTableState& setText( const string &str );

   LONG getPointID();
   CtiTableState& setPointID( const LONG ptid );

   static string getTableName();
   virtual bool Restore();

   virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

   bool operator<( const CtiTableState &rhs ) const;
   bool operator==( const CtiTableState &rhs ) const;
   bool operator()(const CtiTableState& aRef) const;

   void dump() const;
};
#endif // #ifndef __TBL_STATE_H__
